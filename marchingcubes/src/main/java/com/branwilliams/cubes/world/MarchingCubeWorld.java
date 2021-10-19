package com.branwilliams.cubes.world;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Updateable;
import com.branwilliams.bundi.engine.util.Grid3i;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.cubes.GridCellMesh;
import com.branwilliams.cubes.builder.GridBuilder;
import com.branwilliams.cubes.builder.GridMeshBuilder;
import com.branwilliams.cubes.math.RaycastResult;
import org.joml.Vector3f;
import org.joml.Vector3i;


/**
 * @author Brandon
 * @since January 25, 2020
 */
public class MarchingCubeWorld<GridData extends PointData> implements Updateable {

    private final Grid3i<MarchingCubeChunk<GridData>> chunks;

    private final WorldProperties worldProperties;

    private final GridBuilder<GridData> gridBuilder;

    private final GridMeshBuilder<GridData> gridMeshBuilder;

    public MarchingCubeWorld(WorldProperties worldProperties,
                             GridBuilder<GridData> gridBuilder,
                             GridMeshBuilder<GridData> gridCellMeshBuilder) {
        this.worldProperties = worldProperties;
        this.gridBuilder = gridBuilder;
        this.gridMeshBuilder = gridCellMeshBuilder;

        this.chunks = new Grid3i<>(MarchingCubeChunk[]::new, worldProperties.getWorldDimensions().x,
                worldProperties.getWorldDimensions().y, worldProperties.getWorldDimensions().z);
    }

    public void update(Engine engine, double deltaTime) {
        for (MarchingCubeChunk<GridData> chunk : chunks) {
            if (chunk.isDirty()) {
                gridMeshBuilder.buildMesh(this, chunk.getGridData(), chunk.getOffset(),
                        chunk.getGridCellMesh());
                chunk.resetDirty();
            }
        }
    }

    @Override
    public void fixedUpdate(Engine engine, double deltaTime) {

    }

    public void loadAllChunks() {
        for (int x = 0; x < this.worldProperties.getWorldDimensions().x; x++) {
            for (int y = 0; y < this.worldProperties.getWorldDimensions().y; y++) {
                for (int z = 0; z < this.worldProperties.getWorldDimensions().z; z++) {
                    loadChunk(x, y, z);
                }
            }
        }
    }

    public void reloadChunks() {
        for (MarchingCubeChunk<GridData> chunk : chunks) {
            chunk.markDirty();
        }
    }

    public MarchingCubeChunk<GridData> getChunk(int chunkX, int chunkY, int chunkZ) {
        if (isChunkPosWithinWorld(chunkX, chunkY, chunkZ)) {
            return chunks.getValue(chunkX, chunkY, chunkZ);
        }
        return null;
    }

    public boolean loadChunk(int chunkX, int chunkY, int chunkZ) {
        if (isChunkPosWithinWorld(chunkX, chunkY, chunkZ)) {

            // chunk already exists!
            if (getChunk(chunkX, chunkY, chunkZ) != null)
                return false;

            Vector3f offset = new Vector3f(chunkX * worldProperties.getChunkDimensions().x,
                    chunkY * worldProperties.getChunkDimensions().y,
                    chunkZ * worldProperties.getChunkDimensions().z);

            Grid3i<GridData> gridData = gridBuilder.buildGrid(this, offset,
                    worldProperties.getChunkDimensions().x, worldProperties.getChunkDimensions().y,
                    worldProperties.getChunkDimensions().z);

            GridCellMesh gridCellMesh = gridMeshBuilder.initMesh(this, gridData, offset);
            MarchingCubeChunk<GridData> chunk = new MarchingCubeChunk<>(offset, gridData, gridCellMesh,
                    getChunkDimensions());
            this.chunks.setValue(chunk, chunkX, chunkY, chunkZ);
            return true;
        }
        return false;
    }

    public MarchingCubeChunk<GridData> getChunk(Vector3f position) {
        return getChunk(position.x, position.y, position.z);
    }

    public void updateNeighborChunks(GridData origin, MarchingCubeChunk<GridData> originChunk, float originX,
                                     float originY, float originZ) {
        int radius = 1;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {

                    // skip origin
                    if (x == 0 && y == 0 && z == 0)
                        continue;
                    MarchingCubeChunk<GridData> neighborChunk = this.getChunk(originX + x, originY + y,
                            originZ + z);

                    if (!originChunk.equals(neighborChunk)) {
                        neighborChunk.setDirty(true);
                    }
                }
            }
        }
    }

    /**
     * ray-triangle intersection test from
     * https://www.scratchapixel.com/lessons/3d-basic-rendering/ray-tracing-rendering-a-triangle/ray-triangle-intersection-geometric-solution
     *
     * */
    public Vector3f intersectPoint(Vector3f origin, Vector3f direction, float distance, float x, float y, float z) {
        int chunkX = toChunkX(x);
        int chunkY = toChunkY(y);
        int chunkZ = toChunkZ(z);

        MarchingCubeChunk<GridData> chunk = getChunks().getValue(chunkX, chunkY, chunkZ);
        if (chunk == null) {
            return null;
        }

        Vector3f offset = chunk.getOffset();
        GridData gridData = getGridData(x, y, z);

        if (gridData == null) {
            return null;
        }

        if (gridData.getIsoValue() >= getIsoLevel()) {
            return new Vector3f(x, y, z);
        }

//        List<Vector3f> triangles = new ArrayList<>();
//        int triangleCount = gridCell.getTriangles(this, triangles);
//
//        if (triangleCount > 0) {
//            return new Vector3f(x, y, z);
//        }
//
//        // TODO fix this ray-triangle intersection!
//        for (int i = 0; i < triangleCount; i++) {
//            Vector3f p1 = triangles.get(i * 3).add(offset);
//            Vector3f p2 = triangles.get(i * 3 + 1).add(offset);
//            Vector3f p3 = triangles.get(i * 3 + 2).add(offset);
//            double a, f, u, v;
//            Vector3f edge1 = new Vector3f(p2).sub(p1);
//            Vector3f edge2 = new Vector3f(p3).sub(p1);
//            Vector3f h = new Vector3f(direction).cross(edge2);
//            a = edge1.dot(h);
//            if (a > -0.0001D && a < 0.000D) {
//                continue;
//            }
//            f = 1D / a;
//            Vector3f s = new Vector3f(direction).sub(p1);
//            u = f * (s.dot(h));
//            if (u < 0 || u > 1) {
//                continue;
//            }
//            Vector3f q = new Vector3f(s).cross(edge1);
//            v = f * direction.dot(q);
//            if (v < 0 || u + v > 1) {
//                continue;
//            }
//            float t = (float) (f * edge2.dot(q));
//            if (t > 0.0001D) {
//                return direction.mul(t, new Vector3f()).add(origin);
//            }





//            Vector3f normal = calculateNormal(p1, p2, p3).normalize();
//
//            float nDotRayDirection = normal.dot(direction);
//
//            // check if ray and triangle plane are parallel?
//            if (Math.abs(nDotRayDirection) < 0.00001F) {
//                continue; // they are almost parallel so they don't intersect!
//            }
//
//            // distance
//            float d = normal.dot(p1);
//
//            // t
//            float t = (normal.dot(origin) + d) / nDotRayDirection;
//
//            // if the triangle is behind the ray, then no intersection...
//            if (t < 0) {
//                continue;
//            }
//
//            Vector3f point = direction.mul(t, new Vector3f()).add(origin);
//
//            Vector3f c = new Vector3f();
//            Vector3f edge = new Vector3f();
//            Vector3f vp = new Vector3f();
//
//            edge.set(p2).sub(p1);
//            vp.set(point).sub(p1);
//            c.set(edge).cross(vp);
//            if (normal.dot(c) < 0) {
//                continue;
//            }
//
//            edge.set(p3).sub(p2);
//            vp.set(point).sub(p2);
//            c.set(edge).cross(vp);
//            if (normal.dot(c) < 0) {
//                continue;
//            }
//
//            edge.set(p1).sub(p3);
//            vp.set(point).sub(p3);
//            c.set(edge).cross(vp);
//            if (normal.dot(c) < 0) {
//                continue;
//            }
//
//            return point;
//        }

        // no triangles means an empty chunk position...
        return null;
    }

    public RaycastResult raycast(Vector3f origin, Vector3f direction, float distance) {
        float directionLength = direction.length();

        if (directionLength == 0F)
            return null;

        float x = Mathf.floor(origin.x - this.getCubeSize() * 0.5F);
        float y = Mathf.floor(origin.y - this.getCubeSize() * 0.5F);
        float z = Mathf.floor(origin.z - this.getCubeSize() * 0.5F);

        float stepX = Math.signum(direction.x);
        float stepY = Math.signum(direction.y);
        float stepZ = Math.signum(direction.z);

        float tDeltaX = Mathf.abs(1F / direction.x);
        float tDeltaY = Mathf.abs(1F / direction.y);
        float tDeltaZ = Mathf.abs(1F / direction.z);

        float xdist = stepX > 0 ? (x + 1 - origin.x) : (origin.x - x);
        float ydist = stepY > 0 ? (y + 1 - origin.y) : (origin.y - y);
        float zdist = stepZ > 0 ? (z + 1 - origin.z) : (origin.z - z);

        float tMaxX = tDeltaX * xdist;
        float tMaxY = tDeltaY * ydist;
        float tMaxZ = tDeltaZ * zdist;

        Vector3f point = null;
        Vector3f face = new Vector3f();

        float t = 0F;
        boolean found = false;
        while (t < distance) {
            if ((point = intersectPoint(origin, direction, distance, x, y, z)) != null) {
                found = true;
                break;
            }

            if (tMaxX < tMaxY) {
                if (tMaxX < tMaxZ) {
                    x += stepX;
                    t = tMaxX;
                    tMaxX += tDeltaX;

                    face.x = -stepX;
                    face.y = 0;
                    face.z = 0;
                } else {
                    z += stepZ;
                    t = tMaxZ;
                    tMaxZ += tDeltaZ;

                    face.x = 0;
                    face.y = 0;
                    face.z = -stepZ;
                }
            } else {
                if (tMaxY < tMaxZ) {
                    y += stepY;
                    t = tMaxY;
                    tMaxY += tDeltaY;

                    face.x = 0;
                    face.y = -stepY;
                    face.z = 0;
                } else {
                    z += stepZ;
                    t = tMaxZ;
                    tMaxZ += tDeltaZ;

                    face.x = 0;
                    face.y = 0;
                    face.z = -stepZ;
                }
            }
        }

        if (found) {
            Vector3f hitPosition = new Vector3f(origin);
            hitPosition.add(direction.x * t, direction.y * t, direction.z * t);
            return new RaycastResult(point, hitPosition, face);
        } else {
            return null;
        }
    }

    public boolean isEdgeCell(float x, float y, float z, MarchingCubeChunk<GridData> chunk) {
        Vector3f offset = chunk.getOffset();
        int gridX = (int) (x - offset.x);
        int gridY = (int) (y - offset.y);
        int gridZ = (int) (z - offset.z);
        return gridX <= 0 || gridX >= getChunkDimensions().x - 1
                || gridY <= 0 || gridY >= getChunkDimensions().y - 1
                || gridZ <= 0 || gridZ >= getChunkDimensions().z - 1;
    }

    public MarchingCubeChunk<GridData> getChunk(float x, float y, float z) {
        int chunkX = toChunkX(x);
        int chunkY = toChunkY(y);
        int chunkZ = toChunkZ(z);
        return chunks.getValue(chunkX, chunkY, chunkZ);
    }

    public GridData getGridData(Vector3f position) {
        return getGridData(position.x, position.y, position.z);
    }

    public GridData getGridData(float x, float y, float z) {
        return getGridData(x, y, z, getChunk(x, y, z));
    }

    public GridData getGridData(Vector3f position, MarchingCubeChunk<GridData> chunk) {
        return getGridData(position.x, position.y, position.z, chunk);
    }

    public GridData getGridData(float x, float y, float z, MarchingCubeChunk<GridData> chunk) {
        if (chunk == null) {
            return null;
        }

        Vector3f offset = chunk.getOffset();
        GridData gridData = chunk.getGridData(
                (int) (x - offset.x),
                (int) (y - offset.y),
                (int) (z - offset.z));

        return gridData;
    }

    public int toChunkX(float x) {
        return ((int) x) >> worldProperties.getWidthBitshift();
    }

    public int toChunkY(float y) {
        return ((int) y) >> worldProperties.getHeightBitshift();
    }

    public int toChunkZ(float z) {
        return ((int) z) >> worldProperties.getDepthBitshift();
    }

    public boolean isChunkPosWithinWorld(int chunkX, int chunkY, int chunkZ) {
        return chunkX >= 0 && chunkX < worldProperties.getWorldDimensions().x
                && chunkY >= 0 && chunkY < worldProperties.getWorldDimensions().y
                && chunkZ >= 0 && chunkZ < worldProperties.getWorldDimensions().z;
    }

    public float getCubeSize() {
        return worldProperties.getCubeSize();
    }

    public Grid3i<MarchingCubeChunk<GridData>> getChunks() {
        return chunks;
    }

    public Vector3i getWorldDimensions() {
        return worldProperties.getWorldDimensions();
    }

    public Vector3i getChunkDimensions() {
        return worldProperties.getChunkDimensions();
    }

    public float getIsoLevel() {
        return worldProperties.getIsoLevel();
    }

    public void setIsoLevel(float isoLevel) {
        worldProperties.setIsoLevel(isoLevel);
    }

    public WorldProperties getWorldProperties() {
        return worldProperties;
    }
}

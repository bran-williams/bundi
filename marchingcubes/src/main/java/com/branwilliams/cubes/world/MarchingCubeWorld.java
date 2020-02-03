package com.branwilliams.cubes.world;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.util.Grid3f;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.cubes.GridCell;
import com.branwilliams.cubes.GridCellMesh;
import com.branwilliams.cubes.builder.GridCellGridBuilder;
import com.branwilliams.cubes.builder.GridCellMeshBuilder;
import com.branwilliams.cubes.math.RaycastResult;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;

import static com.branwilliams.bundi.engine.util.Mathf.getTwosPower;
import static com.branwilliams.bundi.engine.util.Mathf.isPowerOfTwo;
import static com.branwilliams.bundi.engine.util.MeshUtils.calculateNormal;

/**
 * @author Brandon
 * @since January 25, 2020
 */
public class MarchingCubeWorld {

    private final Grid3f<MarchingCubeChunk> chunks;

    private final WorldProperties worldProperties;

    private final GridCellGridBuilder gridCellGridBuilder;

    private final GridCellMeshBuilder gridCellMeshBuilder;

    public MarchingCubeWorld(WorldProperties worldProperties,
                             GridCellGridBuilder gridCellGridBuilder,
                             GridCellMeshBuilder gridCellMeshBuilder) {
        this.worldProperties = worldProperties;
        this.gridCellGridBuilder = gridCellGridBuilder;
        this.gridCellMeshBuilder = gridCellMeshBuilder;

        this.chunks = new Grid3f<>(MarchingCubeChunk[]::new, worldProperties.getWorldDimensions().x,
                worldProperties.getWorldDimensions().y, worldProperties.getWorldDimensions().z);
    }

    public void update(Engine engine, double deltaTime) {
        for (MarchingCubeChunk chunk : chunks) {
            if (chunk.isDirty()) {
                gridCellMeshBuilder.rebuildMesh(this, chunk.getGridCellMesh(), chunk.getGridCells());
                chunk.resetDirty();
            }
        }
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
        for (MarchingCubeChunk chunk : chunks) {
            chunk.markDirty();
        }
    }

    public MarchingCubeChunk getChunk(int chunkX, int chunkY, int chunkZ) {
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

            Grid3f<GridCell> gridCellGrid = gridCellGridBuilder.buildGridCellGrid(this, offset,
                    worldProperties.getChunkDimensions().x, worldProperties.getChunkDimensions().y,
                    worldProperties.getChunkDimensions().z);

            GridCellMesh gridCellMesh = gridCellMeshBuilder.buildMesh(this, gridCellGrid);
            MarchingCubeChunk chunk = new MarchingCubeChunk(offset, gridCellGrid, gridCellMesh);
            this.chunks.setValue(chunk, chunkX, chunkY, chunkZ);
            return true;
        }
        return false;
    }

    public MarchingCubeChunk getChunk(Vector3f position) {
        return getChunk(position.x, position.y, position.z);
    }

    public MarchingCubeChunk getChunk(float x, float y, float z) {
        int chunkX = toChunkX(x);
        int chunkY = toChunkY(y);
        int chunkZ = toChunkZ(z);
        return chunks.getValue(chunkX, chunkY, chunkZ);
    }

    public GridCell getGridCell(Vector3f position) {
        return getGridCell(position.x, position.y, position.z);
    }

    public GridCell getGridCell(float x, float y, float z) {
        MarchingCubeChunk chunk = getChunk(x, y, z);
        if (chunk == null) {
            return null;
        }

        Vector3f offset = chunk.getOffset();
        GridCell gridCell = chunk.getGridCell(
                (int) (x - offset.x),
                (int) (y - offset.y),
                (int) (z - offset.z));

        return gridCell;
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

//    /**
//     * Ray - triangle interesection test taken from
//     * <a href="https://www.scratchapixel.com/lessons/3d-basic-rendering/ray-tracing-rendering-a-triangle/ray-triangle-intersection-geometric-solution">Ray-Triangle Intersection: Geometric Solution</a>
//     * */
//    private Vector3f intersectPoint(Vector3f origin, Vector3f direction, float distance, float x, float y, float z) {
//        GridCell gridCell = getGridCell(x, y, z);
//
//        if (gridCell == null) {
//            return null;
//        }
//
//        List<Vector3f> triangles = new ArrayList<>();
//        int triangleCount = gridCell.getTriangles(isoLevel, triangles);
//        for (int i = 0; i < triangleCount; i++) {
//            Vector3f p1 = triangles.get(i * 3);
//            Vector3f p2 = triangles.get(i * 3 + 1);
//            Vector3f p3 = triangles.get(i * 3 + 2);
//            Vector3f normal = calculateNormal(p1, p2, p3);
//
//            float nDotRayDirection = normal.dot(direction);
//
//            // check if ray and triangle plane are parallel?
//            if (Math.abs(nDotRayDirection) < 0.00001F) {
//                return null; // they are almost parallel so they don't intersect!
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
//                System.out.println("triangle behind!");
//                return null;
//            }
//
//            Vector3f point = direction.mul(t, new Vector3f()).add(origin);
//
//            Vector3f c = new Vector3f();
//            Vector3f edge = new Vector3f();
//            Vector3f vp = new Vector3f();
//
//            p2.sub(p1, edge);
//            point.sub(p1, vp);
//            edge.cross(vp, c);
//            if (normal.dot(c) < 0)
//                return null;
//
//            p3.sub(p2, edge);
//            point.sub(p2, vp);
//            edge.cross(vp, c);
//            if (normal.dot(c) < 0)
//                return null;
//
//            p1.sub(p3, edge);
//            point.sub(p3, vp);
//            edge.cross(vp, c);
//            if (normal.dot(c) < 0)
//                return null;
//
//            return point;
//        }
//
//        System.out.println("empty position...");
//        // no triangles means an empty chunk position...
//        return null;
//    }
//
//    /**
//     * Performs the raycasting algorithm as described in "A Fast Voxel Traversal Algorithm for Ray Tracing"
//     * by John Amanatides and Andrew Woo, 1987
//     *
//     * <br/> <br/> References:
//     * <a href="http://www.cse.yorku.ca/~amana/research/grid.pdf">Paper</a>
//     * <a href="http://citeseer.ist.psu.edu/viewdoc/summary?doi=10.1.1.42.3443">Paper</a>
//     * <a href="http://www.cse.chalmers.se/edu/year/2010/course/TDA361/grid.pdf">Paper</a>
//     *
//     * <br/> <br/>
//     * Implementation adapted from the following two sources:
//     * <a href="https://github.com/andyhall/fast-voxel-raycast/">ref1</a>
//     * <a href="https://github.com/kpreid/cubes/blob/c5e61fa22cb7f9ba03cd9f22e5327d738ec93969/world.js#L307">ref2</a>
//     *
//     * @author andyhall
//     * @author kpreid
//     * @param origin The position this raycast will begin from.
//     * @param direction A NORMALIZED direction vector.
//     * @param distance The maximum distance of this raycast.
//     * */
//    public RaycastResult raycast(Vector3f origin, Vector3f direction, float distance) {
//        float directionLength = direction.length();
//
//        if (directionLength == 0F)
//            return null;
//
//        float x = Mathf.floor(origin.x);
//        float y = Mathf.floor(origin.y);
//        float z = Mathf.floor(origin.z);
//
//        float stepX = Math.signum(direction.x);
//        float stepY = Math.signum(direction.y);
//        float stepZ = Math.signum(direction.z);
//
//        float tDeltaX = Mathf.abs(1F / direction.x);
//        float tDeltaY = Mathf.abs(1F / direction.y);
//        float tDeltaZ = Mathf.abs(1F / direction.z);
//
//        float xdist = stepX > 0 ? (x + 1 - origin.x) : (origin.x - x);
//        float ydist = stepY > 0 ? (y + 1 - origin.y) : (origin.y - y);
//        float zdist = stepZ > 0 ? (z + 1 - origin.z) : (origin.z - z);
//
//        float tMaxX = tDeltaX * xdist;
//        float tMaxY = tDeltaY * ydist;
//        float tMaxZ = tDeltaZ * zdist;
//
//        Vector3f point = null;
//        Vector3f face = new Vector3f();
//
//        float t = 0F;
//        boolean found = false;
//        while (t < distance) {
//            if ((point = intersectPoint(origin, direction, distance, x, y, z)) != null) {
//                found = true;
//                break;
//            }
//
//            if (tMaxX < tMaxY) {
//                if (tMaxX < tMaxZ) {
//                    x += stepX;
//                    t = tMaxX;
//                    tMaxX += tDeltaX;
//
//                    face.x = -stepX;
//                    face.y = 0;
//                    face.z = 0;
//                } else {
//                    z += stepZ;
//                    t = tMaxZ;
//                    tMaxZ += tDeltaZ;
//
//                    face.x = 0;
//                    face.y = 0;
//                    face.z = -stepZ;
//                }
//            } else {
//                if (tMaxY < tMaxZ) {
//                    y += stepY;
//                    t = tMaxY;
//                    tMaxY += tDeltaY;
//
//                    face.x = 0;
//                    face.y = -stepY;
//                    face.z = 0;
//                } else {
//                    z += stepZ;
//                    t = tMaxZ;
//                    tMaxZ += tDeltaZ;
//
//                    face.x = 0;
//                    face.y = 0;
//                    face.z = -stepZ;
//                }
//            }
//        }
//
//        if (found) {
//            Vector3f hitPosition = new Vector3f(origin);
//            hitPosition.add(direction.x * t, direction.y * t, direction.z * t);
//
//            Vector3f blockPosition = new Vector3f(Mathf.floor(hitPosition.x), Mathf.floor(hitPosition.y), Mathf.floor(hitPosition.z));
//            // Small hack to fix the block position being offset wrongly.
//            if (face.x > 0)
//                blockPosition.x -= 1;
//            if (face.y > 0)
//                blockPosition.y -= 1;
//            if (face.z > 0)
//                blockPosition.z -= 1;
//
//            return new RaycastResult(point, hitPosition, face);
//        } else {
//            return null;
//        }
//    }

    public float getCubeSize() {
        return worldProperties.getCubeSize();
    }

    public Grid3f<MarchingCubeChunk> getChunks() {
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

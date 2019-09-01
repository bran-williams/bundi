package com.branwilliams.bundi.voxel.world;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.bundi.voxel.math.AABB;
import com.branwilliams.bundi.voxel.math.RaycastResult;
import com.branwilliams.bundi.voxel.render.mesh.ChunkMesh;
import com.branwilliams.bundi.voxel.voxels.VoxelFace;
import com.branwilliams.bundi.voxel.voxels.VoxelRegistry;
import com.branwilliams.bundi.voxel.world.chunk.ChunkPos;
import com.branwilliams.bundi.voxel.world.chunk.VoxelChunk;
import com.branwilliams.bundi.voxel.voxels.Voxel;
import com.branwilliams.bundi.voxel.world.generator.VoxelChunkGenerator;
import com.branwilliams.bundi.voxel.world.storage.ChunkMeshStorage;
import com.branwilliams.bundi.voxel.world.storage.ChunkStorage;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author Brandon
 * @since July 21, 2019
 */
public class VoxelWorld implements Destructible {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final VoxelRegistry voxelRegistry;

    private final VoxelChunkGenerator voxelChunkGenerator;

    private final ChunkStorage chunks;

    private ChunkMeshStorage chunkMeshStorage;

    private Set<ChunkPos> visibleChunks;

    public VoxelWorld(VoxelRegistry voxelRegistry, VoxelChunkGenerator voxelChunkGenerator, ChunkStorage chunks,
                      ChunkMeshStorage chunkMeshStorage) {
        this.voxelRegistry = voxelRegistry;
        this.voxelChunkGenerator = voxelChunkGenerator;
        this.chunks = chunks;
        this.chunkMeshStorage = chunkMeshStorage;
        this.visibleChunks = new HashSet<>();
    }

    /**
     * This function unloads any loaded mesh that is outside of the radius around the chunkX and chunkZ provided. Any
     * chunks that are not already loaded are generated and queued to load up as chunk meshes.
     * */
    public void loadChunks(float x, float z, int radius) {
        int chunkX = ChunkPos.toChunkX(x);
        int chunkZ = ChunkPos.toChunkZ(z);
        unloadChunks(chunkX, chunkZ, radius);

        for (int i = chunkX - radius; i < chunkX + radius; i++) {
            for (int j = chunkZ - radius; j < chunkZ + radius; j++) {
                VoxelChunk voxelChunk;

                if (chunks.isLoaded(i, j)) {
                    voxelChunk = chunks.getChunk(i, j);
                } else {
                    voxelChunk = voxelChunkGenerator.generateChunk(voxelRegistry, i, j);
                    chunks.loadChunk(voxelChunk.chunkPos, voxelChunk);
                }

                if (!visibleChunks.contains(voxelChunk.chunkPos)) {
                    visibleChunks.add(voxelChunk.chunkPos);
                    chunkMeshStorage.loadChunkMesh(voxelChunk.chunkPos, voxelChunk);
                }
            }
        }
    }

    /**
     * Tells the chunk mesh storage to unload any meshes not within the radius. Removes the chunk position from the
     * loaded chunks list.
     * */
    private void unloadChunks(int chunkX, int chunkZ, int radius) {
        List<ChunkPos> toUnload = new ArrayList<>();
        for (ChunkPos chunkPos : visibleChunks) {
            if (!isChunkWithinRange(chunkPos, chunkX, chunkZ, radius)) {
                toUnload.add(chunkPos);
            }
        }
        toUnload.forEach(chunkMeshStorage::unloadChunkMesh);
        visibleChunks.removeAll(toUnload);
    }

    /**
     * @return True if the provided chunk position is within the radius around the chunkX and chunkZ provided.
     * */
    private boolean isChunkWithinRange(ChunkPos chunkPos, int chunkX, int chunkZ, int radius) {
        return     chunkPos.getX() >= chunkX - radius
                && chunkPos.getX() <  chunkX + radius
                && chunkPos.getZ() >= chunkZ - radius
                && chunkPos.getZ() <  chunkZ + radius;
    }

    /**
     * Forces the mesh storage to generate all loaded chunk meshes.
     * */
    public void forceGenerateChunkMeshes() {
        for (ChunkPos chunkPos : visibleChunks) {
            chunkMeshStorage.forceGenerateMesh(this, chunks.getChunk(chunkPos));
        }
    }

    /**
     * Marks every chunk as dirty in order to reload them.
     * */
    public void reloadChunks() {
        chunks.getLoadedChunks().forEach(VoxelChunk::markDirty);
    }

    /**
     *
     * */
    public List<AABB> queryVoxelsWithinAABB(AABB aabb, Predicate<Voxel> filter) {
        List<AABB> voxels = new ArrayList<>();

        for (int x = Mathf.floor(aabb.getMinX()); x < Mathf.ceil(aabb.getMaxX()); x++) {
            for (int y = Mathf.floor(aabb.getMinY()); y < Mathf.ceil(aabb.getMaxY()); y++) {
                for (int z = Mathf.floor(aabb.getMinZ()); z < Mathf.ceil(aabb.getMaxZ()); z++) {
                    Voxel voxel = chunks.getVoxelAtPosition(x, y, z);
                    if (filter.test(voxel))
                        voxels.add(voxel.getBoundingBox(x, y, z));
                }
            }
        }

        return voxels;
    }

    /**
     * Performs the raycasting algorithm as described in "A Fast Voxel Traversal Algorithm for Ray Tracing"
     * by John Amanatides and Andrew Woo, 1987
     * <a href="http://www.cse.yorku.ca/~amana/research/grid.pdf">Paper</a>
     * <a href="http://citeseer.ist.psu.edu/viewdoc/summary?doi=10.1.1.42.3443">Paper</a>
     * <a href="http://www.cse.chalmers.se/edu/year/2010/course/TDA361/grid.pdf">Paper</a>
     *
     * <br/> <br/>
     * Implementation adapted from the following two sources:
     * <a href="https://github.com/andyhall/fast-voxel-raycast/">ref1</a>
     * <a href="https://github.com/kpreid/cubes/blob/c5e61fa22cb7f9ba03cd9f22e5327d738ec93969/world.js#L307">ref2</a>
     *
     * @author andyhall
     * @author kpreid
     * @param origin The position this raycast will begin from.
     * @param direction A NORMALIZED direction vector.
     * @param distance The maximum distance of this raycast.
     * */
    public RaycastResult raycast(Vector3f origin, Vector3f direction, float distance) {
        float directionLength = direction.length();

        if (directionLength == 0F)
            return null;

        float x = Mathf.floor(origin.x);
        float y = Mathf.floor(origin.y);
        float z = Mathf.floor(origin.z);

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

        Vector3f face = new Vector3f();

        float t = 0F;
        boolean found = false;
        while (t < distance) {

            if (!chunks.getVoxelAtPosition(x, y, z).isAir()) {
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

            Vector3f blockPosition = new Vector3f(Mathf.floor(hitPosition.x), Mathf.floor(hitPosition.y), Mathf.floor(hitPosition.z));
            // Small hack to fix the block position being offset wrongly.
            if (face.x > 0)
                blockPosition.x -= 1;
            if (face.y > 0)
                blockPosition.y -= 1;
            if (face.z > 0)
                blockPosition.z -= 1;

            return new RaycastResult(hitPosition, blockPosition, face);
        } else {
            return null;
        }
    }

    /**
     * @return The {@link ChunkMesh} for the provided chunk.
     * */
    public ChunkMesh getChunkMesh(VoxelChunk voxelChunk) {
        return chunkMeshStorage.getMesh(this, voxelChunk);
    }

    public Set<ChunkPos> getVisibleChunks() {
        return visibleChunks;
    }

    public ChunkStorage getChunks() {
        return chunks;
    }

    public ChunkMeshStorage getChunkMeshStorage() {
        return chunkMeshStorage;
    }

    @Override
    public void destroy() {
        this.chunks.getLoadedChunks().forEach(VoxelChunk::destroy);
        this.chunkMeshStorage.destroy();
    }

}

package com.branwilliams.bundi.voxel.world.storage;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.util.RateLimiter;
import com.branwilliams.bundi.voxel.render.mesh.builder.VoxelChunkMeshBuilder;
import com.branwilliams.bundi.voxel.render.mesh.ChunkMesh;
import com.branwilliams.bundi.voxel.world.VoxelWorld;
import com.branwilliams.bundi.voxel.world.chunk.ChunkPos;
import com.branwilliams.bundi.voxel.world.chunk.VoxelChunk;

import java.util.*;

/**
 * @author Brandon
 * @since August 19, 2019
 */
public class ChunkMeshStorage implements Destructible {

    private final VoxelChunkMeshBuilder voxelChunkMeshBuilder;

    private final RateLimiter meshCreationLimiter;

    private final MeshPool meshPool;

    private Map<ChunkPos, ChunkMesh> meshes;

    public ChunkMeshStorage(VoxelChunkMeshBuilder voxelChunkMeshBuilder, RateLimiter meshCreationLimiter) {
        this.voxelChunkMeshBuilder = voxelChunkMeshBuilder;
        this.meshCreationLimiter = meshCreationLimiter;
        this.meshPool = new MeshPool();
        this.meshes = new HashMap<>();
    }

    public void loadChunkMesh(ChunkPos chunkPos, VoxelChunk voxelChunk) {
        ChunkMesh mesh = meshes.get(chunkPos);

        // Get a mesh from the pool if necessary and put this mesh into the loaded map. Set its state to 'reassigned'
        // so that it will wait its turn to be created and it will be repositioned.
        if (mesh == null) {
            mesh = meshPool.borrowMesh();
            meshes.put(chunkPos, mesh);
        }
        mesh.reassign(voxelChunk);
    }

    public void unloadChunkMesh(ChunkPos chunkPos) {
        ChunkMesh mesh = meshes.get(chunkPos);

        if (mesh != null) {
            mesh.unassign();
        }
    }

    public void unloadMeshes() {
        List<ChunkPos> toRemove = new ArrayList<>();

        for (Map.Entry<ChunkPos, ChunkMesh> entry : meshes.entrySet()) {
            ChunkMesh mesh = entry.getValue();
            if (mesh.getMeshState() == ChunkMesh.MeshState.UNASSIGNED && mesh.finishedAnimation()) {
                toRemove.add(entry.getKey());
                mesh.unload();
                meshPool.returnMesh(mesh);
            }
        }

        toRemove.forEach((chunkPosition) -> meshes.remove(chunkPosition));
    }

    public ChunkMesh getMesh(VoxelWorld voxelWorld, VoxelChunk voxelChunk) {
        ChunkMesh mesh = meshes.get(voxelChunk.chunkPos);

        if (mesh != null) {
//            System.out.println("mesh state=" + mesh.getMeshState().name());
            switch (mesh.getMeshState()) {
                case REASSIGNED:
                    if (meshCreationLimiter.reached()) {
                        mesh.load();
                        voxelChunkMeshBuilder.rebuildChunkMesh(voxelWorld, voxelChunk, mesh);
                        meshCreationLimiter.reset();
                        break;
                    } else {
                        return null;
                    }
                case UNASSIGNED:
                case LOADED:
                    if (voxelChunk.isDirty()) {
                        voxelChunkMeshBuilder.rebuildChunkMesh(voxelWorld, voxelChunk, mesh);
                        voxelChunk.markClean();
                    }
                    break;
                default:
                    return null;
            }
        }

        return mesh;
    }

    /**
     * Forces the generation of this chunks mesh.
     * */
    public void forceGenerateMesh(VoxelWorld voxelWorld, VoxelChunk voxelChunk) {
        ChunkMesh mesh = meshes.get(voxelChunk.chunkPos);

        if (mesh == null) {
            mesh = meshPool.borrowMesh();
            meshes.put(voxelChunk.chunkPos, mesh);
            mesh.reassign(voxelChunk);
            meshCreationLimiter.reset();
        }

        mesh.load();
        voxelChunkMeshBuilder.rebuildChunkMesh(voxelWorld, voxelChunk, mesh);
        voxelChunk.markClean();
    }

    @Override
    public void destroy() {
        meshPool.destroy();
        meshes.values().forEach(ChunkMesh::destroy);
    }

    public Set<ChunkPos> getChunkPositionsForMeshes() {
        return meshes.keySet();
    }

    public Collection<ChunkMesh> getMeshes() {
        return meshes.values();
    }

    private static class MeshPool implements Destructible {

        private Queue<ChunkMesh> pool;

        public MeshPool() {
            this.pool = new LinkedList<>();
        }

        public ChunkMesh borrowMesh() {
            ChunkMesh mesh = pool.poll();
            if (mesh == null)
                mesh = new ChunkMesh();
            return mesh;
        }

        public boolean returnMesh(ChunkMesh chunkMesh) {
            return pool.offer(chunkMesh);
        }

        @Override
        public void destroy() {
            this.pool.forEach(ChunkMesh::destroy);
        }

        @Override
        public String toString() {
            return "MeshPool{" +
                    "pool=" + pool +
                    '}';
        }
    }

}

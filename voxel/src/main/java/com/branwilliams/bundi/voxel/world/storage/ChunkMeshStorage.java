package com.branwilliams.bundi.voxel.world.storage;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.util.RateLimiter;
import com.branwilliams.bundi.voxel.builder.VoxelMeshBuilder;
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

    private final VoxelMeshBuilder voxelMeshBuilder;

    private final RateLimiter meshCreationLimiter;

    private final MeshPool meshPool;

    private Map<ChunkPos, ChunkMesh> meshes;

    public ChunkMeshStorage(VoxelMeshBuilder voxelMeshBuilder, RateLimiter meshCreationLimiter) {
        this.voxelMeshBuilder = voxelMeshBuilder;
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
            mesh.reassign(voxelChunk);
        }
    }

    public void unloadChunkMesh(ChunkPos chunkPos) {
        ChunkMesh mesh = meshes.get(chunkPos);

        if (mesh != null) {
            meshes.remove(chunkPos);
            mesh.unassign();
            meshPool.returnMesh(mesh);
        }
    }

    public ChunkMesh getMesh(VoxelWorld voxelWorld, VoxelChunk voxelChunk) {
        ChunkMesh mesh = meshes.get(voxelChunk.chunkPos);

        if (mesh != null) {
            switch (mesh.getMeshState()) {
                case REASSIGNED:
                    if (meshCreationLimiter.reached()) {
                        mesh.onMeshChangeState(ChunkMesh.MeshState.LOADED);
                        voxelMeshBuilder.rebuildChunkMesh(voxelWorld, voxelChunk, mesh);
                        meshCreationLimiter.reset();
                        break;
                    } else {
                        return null;
                    }

                case LOADED:
                    if (voxelChunk.isDirty()) {
                        voxelMeshBuilder.rebuildChunkMesh(voxelWorld, voxelChunk, mesh);
                        voxelChunk.markClean();
                    }
                    break;
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

        mesh.onMeshChangeState(ChunkMesh.MeshState.LOADED);
        voxelMeshBuilder.rebuildChunkMesh(voxelWorld, voxelChunk, mesh);
        voxelChunk.markClean();
    }

    @Override
    public void destroy() {
        meshPool.destroy();
        meshes.values().forEach(ChunkMesh::destroy);
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
    }

}

package com.branwilliams.bundi.voxel.render.mesh;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.engine.shader.Transformation;
import com.branwilliams.bundi.engine.util.Timer;
import com.branwilliams.bundi.voxel.VoxelConstants;
import com.branwilliams.bundi.voxel.world.chunk.VoxelChunk;

/**
 * @author Brandon
 * @since August 13, 2019
 */
public class ChunkMesh implements Destructible {

    public enum MeshState {
        /** This is the state of a mesh which has not been assigned to any chunk. */
        UNASSIGNED,
        /** This is used to differentiate between player modifications making a chunk dirty vs a chunk being new. */
        REASSIGNED,
        /** The mesh has been created and should animate into the scene. */
        LOADED
    }

    private MeshState meshState;

    private VoxelChunk voxelChunk;

    private Transformable transformable;

    private Mesh solidMesh;

    /** Time (in ms) when this mesh state has been changed. */
    private long changeTime;

    public ChunkMesh() {
        this.meshState = MeshState.UNASSIGNED;
        this.transformable = new Transformation();
        this.solidMesh = new Mesh();
        initializeSolidMesh();
    }

    private void initializeSolidMesh() {
        int vertexCount = 8 * 6;
        this.solidMesh.bind();
        this.solidMesh.initializeAttribute(0, 3, vertexCount * 3);
        this.solidMesh.initializeAttribute(1, 2, vertexCount * 2);
        this.solidMesh.initializeAttribute(2, 3, vertexCount * 3);
        this.solidMesh.initializeAttribute(3, 3, vertexCount * 3);
        this.solidMesh.unbind();
    }

    /**
     * Invoked whenever this mesh has been reset to another chunk.
     * Updates the transformable of this chunk mesh to match the chunk position provided. This also resets the ownership
     * of this mesh, since a position update implies that it is now owned by another chunk.
     * */
    public void reassign(VoxelChunk voxelChunk) {
        this.voxelChunk = voxelChunk;
        onMeshChangeState(MeshState.REASSIGNED);
    }

    public void unassign() {
        this.voxelChunk = null;
        onMeshChangeState(MeshState.UNASSIGNED);
    }

    /**
     * Invoked when the mesh of this object is created for the first time for a chunk.
     * */
    public void onMeshChangeState(MeshState meshState) {
        changeTime = Timer.getSystemTime();
        this.meshState = meshState;
    }

    /**
     * @return The transformable used to render this chunk mesh.
     * */
    public Transformable getTransformable(float animationHeight) {
        float y = -animationHeight + (getAnimation() * animationHeight);
        return transformable.position(voxelChunk.chunkPos.getRealX(), y, voxelChunk.chunkPos.getRealZ());
    }

    /**
     * @return A value from 0 ~ 1 representing the time between this meshes creation to the current time.
     * */
    public float getAnimation() {
        float animation = (float) (Timer.getSystemTime() - changeTime) / (float) VoxelConstants.CHUNK_ANIMATION_TIME_MS;
        return Math.min(1F, animation);
    }

    /**
     * @return True if this chunk mesh has finished animating into view.
     * */
    public boolean finishedAnimation() {
        return getAnimation() == 1F;
    }

    /**
     *
     * */
    public boolean isRenderable() {
        return meshState == MeshState.LOADED;
    }

    public MeshState getMeshState() {
        return meshState;
    }

    public Mesh getSolidMesh() {
        return solidMesh;
    }

    public VoxelChunk getVoxelChunk() {
        return voxelChunk;
    }

    @Override
    public void destroy() {
        solidMesh.destroy();
    }

}

package com.branwilliams.bundi.voxel.render.mesh;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.engine.shader.Transformation;
import com.branwilliams.bundi.engine.shader.dynamic.VertexElements;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.util.Timer;
import com.branwilliams.bundi.voxel.VoxelConstants;
import com.branwilliams.bundi.voxel.render.mesh.builder.ChunkMeshVertex;
import com.branwilliams.bundi.voxel.util.Easings;
import com.branwilliams.bundi.voxel.world.chunk.VoxelChunk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static com.branwilliams.bundi.engine.util.MeshUtils.*;

/**
 * @author Brandon
 * @since August 13, 2019
 */
public class ChunkMesh implements Destructible {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public enum MeshState {
        /** This is the state of a mesh which has not been assigned to any chunk. */
        UNASSIGNED,
        /** This is used to differentiate between player modifications making a chunk dirty vs a chunk being new. */
        REASSIGNED,
        /** The mesh has been created and should animate into the scene. */
        LOADED,
        /** The mesh has been completely unloaded and should be released to the mesh pool. */
        UNLOADED
    }

    private MeshState meshState;

    private VoxelChunk voxelChunk;

    private Transformable transformable;

    private Mesh solidMesh;

    private List<ChunkMeshVertex> vertices;

    private List<Integer> indices;

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

    public synchronized void setMeshData(List<ChunkMeshVertex> vertices, List<Integer> indices) {
        this.vertices = vertices;
        this.indices = indices;
    }

    public synchronized void loadMeshData() {
        if (this.vertices == null || this.indices == null) {
            log.info("Unable to load mesh data: No vertices or indices set.");
            return;
        }

        this.solidMesh.setVertexFormat(VertexFormat.POSITION_UV_NORMAL_TANGENT);
        this.solidMesh.bind();
        this.solidMesh.storeAttribute(0,
                toArray3f(vertices.stream().map((v) -> v.vertex).collect(Collectors.toList())),
                VertexElements.POSITION.getSize());
        this.solidMesh.storeAttribute(1,
                toArray2f(vertices.stream().map((v) -> v.uv).collect(Collectors.toList())),
                VertexElements.UV.getSize());
        this.solidMesh.storeAttribute(2,
                toArray3f(vertices.stream().map((v) -> v.normal).collect(Collectors.toList())),
                VertexElements.NORMAL.getSize());
        this.solidMesh.storeAttribute(3,
                toArray3f(vertices.stream().map((v) -> v.tangent).collect(Collectors.toList())),
                VertexElements.TANGENT.getSize());
        this.solidMesh.storeIndices(toArrayi(indices));
        this.solidMesh.unbind();

        this.vertices = null;
        this.indices = null;
    }

    /**
     * Invoked whenever this mesh has been reset to another chunk.
     * Updates the transformable of this chunk mesh to match the chunk position provided. This also resets the ownership
     * of this mesh, since a position update implies that it is now owned by another chunk.
     * */
    public void reassign(VoxelChunk voxelChunk) {
        this.voxelChunk = voxelChunk;
        setMeshState(MeshState.REASSIGNED);
    }

    public void unassign() {
        setMeshState(MeshState.UNASSIGNED);
    }

    public void unload() {
        this.voxelChunk = null;
        setMeshState(MeshState.UNLOADED);
    }

    public void load() {
        setMeshState(MeshState.LOADED);
    }

    public void setMeshState(MeshState meshState) {
        boolean changed = this.meshState != meshState;
        this.meshState = meshState;
        if (changed)
            onMeshChangeState(meshState);
    }

    /**
     * Invoked when the mesh of this object is created for the first time for a chunk.
     * */
    public void onMeshChangeState(MeshState meshState) {
        changeTime = Timer.getSystemTime();
    }

    /**
     * @return The transformable used to render this chunk mesh.
     * */
    public Transformable getTransformable(float animationHeight) {
        float y = -animationHeight + (getAnimation() * animationHeight);
        if (meshState == MeshState.UNASSIGNED) {
            y = (getAnimation() * -animationHeight);
        }

        return transformable.position(voxelChunk.chunkPos.getRealX(), y, voxelChunk.chunkPos.getRealZ());
    }

    /**
     * @return A value from 0 ~ 1 representing the time between this meshes creation to the current time.
     * */
    public float getAnimation() {
        float animation = (float) (Timer.getSystemTime() - changeTime) / (float) VoxelConstants.CHUNK_ANIMATION_TIME_MS;
        animation = Math.min(1F, animation);
        float eased = Math.min(1F, Easings.cubicEaseIn(animation, 0.645F, 0.045F, 0.355F));
        return eased;
//        return animation;
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
        return meshState == MeshState.LOADED || meshState == MeshState.UNASSIGNED;
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

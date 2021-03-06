package com.branwilliams.bundi.voxel.render.pipeline.passes;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.material.Material;
import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.voxel.render.mesh.ChunkMesh;
import com.branwilliams.bundi.voxel.VoxelScene;
import com.branwilliams.bundi.voxel.render.pipeline.VoxelRenderContext;
import com.branwilliams.bundi.voxel.render.pipeline.shaders.ChunkShaderProgram;
import com.branwilliams.bundi.voxel.world.VoxelWorld;
import com.branwilliams.bundi.voxel.world.chunk.ChunkPos;
import com.branwilliams.bundi.voxel.world.chunk.VoxelChunk;

import static org.lwjgl.opengl.GL11.*;


/**
 * Created by Brandon Williams on 9/25/2018.
 */
public class ChunkRenderPass extends RenderPass<VoxelRenderContext> {

    private static final float CHUNK_ANIMATION_HEIGHT = 50;

    private final VoxelScene scene;

    private ChunkShaderProgram chunkShaderProgram;

    public ChunkRenderPass(VoxelScene scene) {
        this.scene = scene;
    }

    @Override
    public void init(VoxelRenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            chunkShaderProgram = new ChunkShaderProgram(engine.getContext());
        } catch (ShaderUniformException | ShaderInitializationException e) {
            System.err.println("Unable to create shader programs!");
            throw new InitializationException(e);
        }
    }

    /**
     * 1. Binds geometry shader program and sets the projection and view matrix.
     * 2. Render each object within the scene.
     * 3. Set polygon mode to fill if wireframe is enabled. Terrain shader set this to lines if wireframe is true.
     * */
    @Override
    public void render(VoxelRenderContext renderContext, Engine engine, Window window, double deltaTime) {
        glEnable(GL_DEPTH_TEST);
        Material material = scene.getTexturePack().getMaterial();

        // Bind mesh shader program.
        this.chunkShaderProgram.bind();
        this.chunkShaderProgram.setProjectionMatrix(renderContext.getProjection());
        this.chunkShaderProgram.setViewMatrix(scene.getCamera());
        this.chunkShaderProgram.setMaterial(material);
        this.chunkShaderProgram.setLight(scene.getSun());
        this.chunkShaderProgram.setAtmosphere(scene.getAtmosphere());

        renderContext.getFrustum().update(renderContext.getProjection(), scene.getCamera());

        VoxelWorld world = scene.getVoxelWorld();
        world.getChunkMeshStorage().unloadMeshes();

        for (ChunkPos chunkPos : world.getChunkMeshStorage().getChunkPositionsForMeshes()) {
            VoxelChunk chunk = world.getChunks().getChunk(chunkPos);
            ChunkMesh chunkMesh = world.getChunkMesh(chunk);

            if (shouldRenderMesh(renderContext, chunkMesh, chunk)) {
                this.chunkShaderProgram.setModelMatrix(chunkMesh.getTransformable(CHUNK_ANIMATION_HEIGHT));
                Mesh mesh = chunkMesh.getSolidMesh();
                MeshRenderer.bind(mesh, material);
                MeshRenderer.render(mesh);
                MeshRenderer.unbind(mesh, material);
            }
        }
    }

    private boolean shouldRenderMesh(VoxelRenderContext renderContext, ChunkMesh chunkMesh, VoxelChunk voxelChunk) {
        return chunkMesh != null && chunkMesh.isRenderable() &&
                renderContext.getFrustum().insideFrustumAABB(voxelChunk.getAABB());
    }

    @Override
    public void destroy() {
        super.destroy();
        this.chunkShaderProgram.destroy();
    }
}

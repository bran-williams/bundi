package com.branwilliams.bundi.voxel.render.pipeline.passes;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.ecs.IComponentMatcher;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicVAO;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.voxel.components.PlayerState;
import com.branwilliams.bundi.voxel.math.AABB;
import com.branwilliams.bundi.voxel.VoxelScene;
import com.branwilliams.bundi.voxel.render.pipeline.VoxelRenderContext;
import com.branwilliams.bundi.voxel.util.RenderUtils;
import com.branwilliams.bundi.voxel.world.chunk.VoxelChunk;

import static org.lwjgl.opengl.GL11.GL_LINES;

public class ChunkBorderRenderPass extends RenderPass<VoxelRenderContext> {

    private final VoxelScene scene;

    private IComponentMatcher playerMatcher;

    private DynamicShaderProgram shaderProgram;

    private DynamicVAO dynamicVAO;

    public ChunkBorderRenderPass(VoxelScene scene) {
        this.scene = scene;
        this.playerMatcher = scene.getEs().matcher(Transformable.class, PlayerState.class);
    }

    @Override
    public void init(VoxelRenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            shaderProgram = new DynamicShaderProgram(VertexFormat.POSITION_COLOR, DynamicShaderProgram.VIEW_MATRIX);
        } catch (ShaderInitializationException | ShaderUniformException e) {
            e.printStackTrace();
        }

        dynamicVAO = new DynamicVAO(VertexFormat.POSITION_COLOR);
    }

    @Override
    public void render(VoxelRenderContext renderContext, Engine engine, Window window) {
        shaderProgram.bind();
        shaderProgram.setProjectionMatrix(renderContext.getProjection());
        shaderProgram.setViewMatrix(scene.getCamera());
        shaderProgram.setModelMatrix(Transformable.empty());


        Transformable playerTransform = scene.getPlayer().getComponent(Transformable.class);
        VoxelChunk playerChunk = scene.getVoxelWorld().getChunkAtPosition(playerTransform.getPosition());

        dynamicVAO.begin();
        RenderUtils.addAABB(dynamicVAO, playerChunk.getAABB(), 1F, 0F, 0F, 1F);

        for (IEntity entity : scene.getEs().getEntities(playerMatcher)) {
            PlayerState playerState = entity.getComponent(PlayerState.class);
            RenderUtils.addAABB(dynamicVAO, playerState.getBoundingBox(), 0F, 1F, 0F, 1F);
        }

        dynamicVAO.draw(GL_LINES);
        ShaderProgram.unbind();
    }
}

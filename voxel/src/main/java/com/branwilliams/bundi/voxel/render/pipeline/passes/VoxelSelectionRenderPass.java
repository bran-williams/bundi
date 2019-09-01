package com.branwilliams.bundi.voxel.render.pipeline.passes;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;

import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicVAO;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.voxel.math.AABB;
import com.branwilliams.bundi.voxel.math.RaycastResult;
import com.branwilliams.bundi.voxel.VoxelScene;
import com.branwilliams.bundi.voxel.render.pipeline.VoxelRenderContext;
import com.branwilliams.bundi.voxel.util.RenderUtils;
import com.branwilliams.bundi.voxel.voxels.Voxel;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Brandon
 * @since August 10, 2019
 */
public class VoxelSelectionRenderPass extends RenderPass<VoxelRenderContext> {

    private Vector4f SELECTION_OUTLINE_COLOR = new Vector4f(0F, 0F, 0F, 1F);
    private Vector4f DIRECTION_VECTOR_COLOR = new Vector4f(0F, 1F, 0F, 1F);

    private final VoxelScene scene;

    private DynamicShaderProgram shaderProgram;

    private DynamicVAO dynamicVAO;

    public VoxelSelectionRenderPass(VoxelScene scene) {
        this.scene = scene;
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
        if (scene.getPlayerState().getRaycast() == null) {
            return;
        }

        RaycastResult selectedVoxel = scene.getPlayerState().getRaycast();
        Vector3f voxelPos = selectedVoxel.blockPosition;
        Voxel voxel = scene.getVoxelWorld().getChunks().getVoxelAtPosition(voxelPos);
        AABB voxelAABB = voxel.getBoundingBox((int) voxelPos.x, (int) voxelPos.y, (int) voxelPos.z);
        Vector3f voxelCenter = voxelAABB.getCenter();

        shaderProgram.bind();
        shaderProgram.setProjectionMatrix(renderContext.getProjection());
        shaderProgram.setViewMatrix(scene.getCamera());
        shaderProgram.setModelMatrix(Transformable.empty());

        dynamicVAO.begin();
        RenderUtils.addAABB(dynamicVAO, voxelAABB, SELECTION_OUTLINE_COLOR);

        dynamicVAO.position(voxelCenter.x, voxelCenter.y, voxelCenter.z).color(DIRECTION_VECTOR_COLOR).endVertex();
        dynamicVAO.position(voxelCenter.x + selectedVoxel.face.x, voxelCenter.y + selectedVoxel.face.y, voxelCenter.z + selectedVoxel.face.z).color(DIRECTION_VECTOR_COLOR).endVertex();

        dynamicVAO.draw(GL_LINES);

        ShaderProgram.unbind();
    }
}

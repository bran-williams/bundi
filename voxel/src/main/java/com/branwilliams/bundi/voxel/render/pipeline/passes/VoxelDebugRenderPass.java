package com.branwilliams.bundi.voxel.render.pipeline.passes;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.font.BasicFontRenderer;
import com.branwilliams.bundi.engine.font.FontRenderer;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicVAO;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.texture.Texture;
import com.branwilliams.bundi.engine.texture.TextureData;
import com.branwilliams.bundi.engine.util.TextureUtils;
import com.branwilliams.bundi.voxel.VoxelScene;
import com.branwilliams.bundi.voxel.components.MovementComponent;
import com.branwilliams.bundi.voxel.components.PlayerState;
import com.branwilliams.bundi.voxel.render.pipeline.VoxelRenderContext;
import com.branwilliams.bundi.voxel.voxels.Voxel;
import com.branwilliams.bundi.voxel.voxels.VoxelFace;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

/**
 * @author Brandon
 * @since August 10, 2019
 */
public class VoxelDebugRenderPass extends RenderPass<VoxelRenderContext> implements Window.WindowListener {

    private final VoxelScene scene;

    private DynamicShaderProgram dynamicShaderProgram;

    private FontRenderer fontRenderer;

    public VoxelDebugRenderPass(VoxelScene scene) {
        scene.addWindowListener(this);
        this.scene = scene;
    }

    @Override
    public void init(VoxelRenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            dynamicShaderProgram = new DynamicShaderProgram();
        } catch (ShaderInitializationException | ShaderUniformException e) {
            e.printStackTrace();
        }
        fontRenderer = new BasicFontRenderer();
        fontRenderer.setFont(new Font("Roboto", Font.PLAIN, 18), true);

    }

    @Override
    public void render(VoxelRenderContext renderContext, Engine engine, Window window, double deltaTime) {
        if (scene.isLocked())
            return;

        glDisable(GL_DEPTH_TEST);
        dynamicShaderProgram.bind();
        dynamicShaderProgram.setProjectionMatrix(renderContext.getOrthoProjection());
        dynamicShaderProgram.setModelMatrix(Transformable.empty());

        PlayerState playerState = scene.getPlayerState();
        Transformable playerTransform = scene.getPlayer().getComponent(Transformable.class);
        MovementComponent movementComponent = scene.getPlayer().getComponent(MovementComponent.class);
        int y = 2;

        if (playerState.isOnGround()) {
            fontRenderer.drawStringWithShadow("onGround=True", 2, y, 0xFF339900);
        } else {
            fontRenderer.drawStringWithShadow("onGround=False", 2, y, 0xFF990000);
        }

        y += fontRenderer.getFontData().getFontHeight();

        y = drawVector("Position", playerTransform.getPosition(), 2, y, 0xFFFFFFFF);
        y = drawVector("Velocity", movementComponent.getVelocity(), 2, y, 0xFFFFFFFF);
        y = drawVector("Acceleration", movementComponent.getAcceleration(), 2, y, 0xFFFFFFFF);

        ShaderProgram.unbind();
    }

    private int drawVector(String title, Vector3f vector, int x, int y, int color) {
        fontRenderer.drawStringWithShadow(title, x, y, color);
        y += fontRenderer.getFontData().getFontHeight();

        fontRenderer.drawStringWithShadow(String.format("\tx=%.3f y=%.3f z=%.3f", vector.x, vector.y, vector.z), x, y, color);
        y += fontRenderer.getFontData().getFontHeight();

        return y;
    }

    @Override
    public void resize(Window window, int width, int height) {
    }

    @Override
    public void destroy() {
        super.destroy();
        this.dynamicShaderProgram.destroy();
        this.fontRenderer.destroy();
    }

}

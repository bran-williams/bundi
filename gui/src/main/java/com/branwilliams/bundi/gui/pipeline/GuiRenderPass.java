package com.branwilliams.bundi.gui.pipeline;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.gui.screen.GuiScreenManager;

import java.util.function.Supplier;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Brandon
 * @since November 24, 2019
 */
public class GuiRenderPass <CurrentContext extends RenderContext> extends RenderPass<CurrentContext> implements Window.WindowListener {

    private final Supplier<GuiScreenManager> guiScreenManager;

    private DynamicShaderProgram dynamicShaderProgram;

    private Projection orthoProjection;

    public GuiRenderPass(Scene scene, Supplier<GuiScreenManager> guiScreenManager) {
        this.guiScreenManager = guiScreenManager;
        scene.addWindowListener(this);

    }

    public GuiRenderPass(Scene scene, GuiScreenManager guiScreenManager) {
        this(scene, () -> guiScreenManager);
    }

    @Override
    public void init(CurrentContext renderContext, Engine engine, Window window) throws InitializationException {
        orthoProjection = new Projection(window);
        try {
            dynamicShaderProgram = new DynamicShaderProgram();
        } catch (ShaderInitializationException | ShaderUniformException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(CurrentContext renderContext, Engine engine, Window window, double deltaTime) {
        glDisable(GL_DEPTH_TEST);
        GuiScreenManager guiScreenManager = this.guiScreenManager.get();
        if (guiScreenManager.getGuiScreen() != null) {
            dynamicShaderProgram.bind();
            dynamicShaderProgram.setProjectionMatrix(orthoProjection);
            dynamicShaderProgram.setModelMatrix(Transformable.empty());
            guiScreenManager.getGuiScreen().render();
        }
        glEnable(GL_DEPTH_TEST);

    }

    @Override
    public void destroy() {
        dynamicShaderProgram.destroy();
    }

    @Override
    public void resize(Window window, int width, int height) {
        orthoProjection.update();
    }
}

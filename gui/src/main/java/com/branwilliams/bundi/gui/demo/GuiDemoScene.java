package com.branwilliams.bundi.gui.demo;

import com.branwilliams.bundi.engine.core.AbstractScene;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.KeyListener;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.shader.Projection;
import com.branwilliams.bundi.gui.pipeline.GuiRenderPass;
import com.branwilliams.bundi.gui.screen.GuiScreenManager;
import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

/**
 * @author Brandon
 * @since February 05, 2020
 */
public class GuiDemoScene extends AbstractScene {

    private static final String UI_FILE = "ui/demo-menu.xml";

    private static final Map<String, Object> UI_ENVIRONMENT = createUIEnvironment();

    private static Map<String, Object> createUIEnvironment() {
        Map<String, Object> env = new HashMap<>();
        env.put("taco", true);
        env.put("frito", false);
        env.put("taco_text", "this is a label for tacos");
        env.put("taco_color", "#449900");
        env.put("taco_checkbox", "Yuh");

        List<String> myList = Lists.asList("one", "two", new String[] { "three" });
        env.put("myList", myList);
        return env;
    }

    private final GuiScreenManager guiScreenManager;

    public GuiDemoScene() {
        super("gui_demo");
        this.addKeyListener(this);
        this.guiScreenManager = new GuiScreenManager(this);
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        this.guiScreenManager.init(engine, window);
        this.guiScreenManager.loadAsGuiScreen(UI_FILE, UI_ENVIRONMENT);

        Projection orthoProjection = new Projection(window);
        RenderContext renderContext = new RenderContext(orthoProjection);

        RenderPipeline<RenderContext> renderPipeline = new RenderPipeline<>(renderContext);
        renderPipeline.addLast(new GuiRenderPass<>(this, this::getGuiScreenManager));
        // Add render passes here.
        GuiDemoRenderer<RenderContext> renderer = new GuiDemoRenderer<>(this, renderPipeline);
        setRenderer(renderer);
    }

    @Override
    public void play(Engine engine) {

    }

    @Override
    public void pause(Engine engine) {

    }

    @Override
    public void update(Engine engine, double deltaTime) {
        super.update(engine, deltaTime);
        this.guiScreenManager.update();
    }

    @Override
    public void keyPress(Window window, int key, int scancode, int mods) {
        super.keyPress(window, key, scancode, mods);
        if (key == GLFW_KEY_ESCAPE) {
            this.guiScreenManager.setGuiScreen(null);
            this.guiScreenManager.loadAsGuiScreen(UI_FILE, UI_ENVIRONMENT);
        }
    }

    public GuiScreenManager getGuiScreenManager() {
        return guiScreenManager;
    }

}

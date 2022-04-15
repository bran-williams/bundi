package com.branwilliams.bundi.voxel.gui;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.gui.api.ContainerManager;
import com.branwilliams.bundi.gui.screen.ContainerScreen;
import com.branwilliams.bundi.gui.screen.GuiScreen;
import com.branwilliams.bundi.gui.screen.GuiScreenManager;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

public abstract class VoxelGuiScreen <SceneType extends Scene> extends ContainerScreen<SceneType> {

    protected final Supplier<GuiScreenManager<SceneType>> screenManager;

    protected final String resourcePath;

    protected final Supplier<Map<String, Object>> env;

    protected GuiScreen<SceneType> previous;

    private boolean initialized = false;

    public VoxelGuiScreen(Supplier<GuiScreenManager<SceneType>> screenManager, String resourcePath) {
        this(screenManager, null, resourcePath, HashMap::new);
    }

    public VoxelGuiScreen(Supplier<GuiScreenManager<SceneType>> screenManager, GuiScreen<SceneType> previous, String resourcePath) {
        this(screenManager, previous, resourcePath, HashMap::new);
    }

    public VoxelGuiScreen(Supplier<GuiScreenManager<SceneType>> screenManager, GuiScreen<SceneType> previous, String resourcePath,
                          Supplier<Map<String, Object>> env) {
        this.screenManager = screenManager;
        this.previous = previous;
        this.resourcePath = resourcePath;
        this.env = env;
    }

    @Override
    public void initialize(SceneType scene, Engine engine, Window window) {
        reloadUIResource();
        super.initialize(scene, engine, window);
        initialized = true;
    }

    @Override
    public void close(SceneType scene, Engine engine, Window window) {
        super.close(scene, engine, window);
    }

    @Override
    public void keyPress(Window window, int key, int scancode, int mods) {
        super.keyPress(window, key, scancode, mods);

        if (initialized && shouldEscape() && key == GLFW_KEY_ESCAPE) {
            screenManager.get().setGuiScreen(previous);
        }
    }

    @Override
    public void resize(Window window, int width, int height) {
        super.resize(window, width, height);
        reloadUIResource();
    }

    /**
     * Invoked when this screen's ContainerManager is loaded. This is done by {@link VoxelGuiScreen#reloadUIResource()}.
     * This occurs once initially and every time the screen size changes.
     * */
    protected abstract void onLoadUI(ContainerManager containerManager);

    protected void reloadUIResource() {
        ContainerManager containerManager = createContainerManager(resourcePath, env.get());
        setContainerManager(containerManager);
        onLoadUI(containerManager);
    }

    protected ContainerManager createContainerManager(String resourcePath, Map<String, Object> env) {
        return screenManager.get().loadFromResources(resourcePath, env);
    }

    public boolean shouldEscape() {
        return true;
    }

}

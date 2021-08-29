package com.branwilliams.bundi.gui.screen;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.font.FontCache;
import com.branwilliams.bundi.engine.font.FontData;
import com.branwilliams.bundi.gui.api.Container;
import com.branwilliams.bundi.gui.api.ContainerManager;
import com.branwilliams.bundi.gui.api.Toolbox;
import com.branwilliams.bundi.gui.api.loader.UILoader;
import com.branwilliams.bundi.gui.impl.BasicRenderer;
import com.branwilliams.bundi.gui.impl.BasicToolbox;
import com.branwilliams.bundi.gui.impl.ColorPack;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.branwilliams.bundi.gui.impl.Pointers.FONT_TOOLTIP;

/**
 * Created by Brandon Williams on 10/1/2019.
 */
public class GuiScreenManager <SceneType extends Scene> {

    private final SceneType scene;

    private Engine engine;

    private Window window;

    private FontCache fontCache;

    private UILoader uiLoader;

    private Toolbox toolbox;

    private BasicRenderer renderManager;

    private GuiScreen<SceneType> guiScreen;

    public GuiScreenManager(SceneType scene) {
        this.scene = scene;
    }

    public void init(Engine engine, Window window) {
        this.engine = engine;
        this.window = window;
        fontCache = new FontCache();
        toolbox = new BasicToolbox(engine, window);
        uiLoader = new UILoader(fontCache, toolbox);

        ColorPack colorPack = ColorPack.random();
        colorPack.apply(toolbox);

        renderManager = new BasicRenderer(toolbox);

        FontData tooltipFont = fontCache.createFont("Verdana", Font.PLAIN, 18, true);
        renderManager.getFontRenderer().setFontData(tooltipFont);
        toolbox.put(FONT_TOOLTIP, tooltipFont);
    }

    public void update() {
        if (this.guiScreen != null) {
            this.guiScreen.update();
        }
    }

    public ContainerManager loadFromResources(String ui, Map<String, Object> env) {
        return load(() -> uiLoader.loadUIFromResources(ui, env));
    }

    public ContainerManager loadFromAssetDirectory(String ui, Map<String, Object> env) {
        return load(() -> uiLoader.loadUIFromAssetDirectory(ui, env));
    }

    public ContainerManager loadFromFileContents(String fileContents, Map<String, Object> env) {
        return load(() -> uiLoader.loadUIFromFileContents(fileContents, env));
    }

    private ContainerManager load(Supplier<List<Container>> loadUI) {
        List<Container> containers = loadUI.get();
        if (containers == null)
            return null;

        ContainerManager containerManager = new ContainerManager(scene, window, renderManager, toolbox);
        for (Container container : containers)
            containerManager.add(container);
        return containerManager;
    }

    public ContainerManager loadAsGuiScreen(String ui) {
        return loadAsGuiScreen(ui, new HashMap<>());
    }

    public ContainerManager loadAsGuiScreen(String fileContents, Map<String, Object> env) {
        return loadAsGuiScreen(loadFromFileContents(fileContents, env));
    }

    public ContainerManager loadAsGuiScreen(ContainerManager containerManager) {
        if (containerManager != null)
            setGuiScreen(new AbstractContainerScreen<>(containerManager));
        return containerManager;
    }

    public GuiScreen<SceneType> getGuiScreen() {
        return guiScreen;
    }

    public void setGuiScreen(GuiScreen<SceneType> guiScreen) {
        if (this.guiScreen != null) {
            this.guiScreen.close(scene, engine, window);
            this.guiScreen.destroy();
        }
        if (guiScreen != null) {
            guiScreen.initialize(scene, engine, window);
        }
        this.guiScreen = guiScreen;
    }

    public FontCache getFontCache() {
        return fontCache;
    }

    public UILoader getUiLoader() {
        return uiLoader;
    }

    public Toolbox getToolbox() {
        return toolbox;
    }

    public BasicRenderer getRenderManager() {
        return renderManager;
    }
}

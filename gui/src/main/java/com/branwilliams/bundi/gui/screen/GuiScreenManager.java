package com.branwilliams.bundi.gui.screen;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.font.FontCache;
import com.branwilliams.bundi.engine.font.FontData;
import com.branwilliams.bundi.gui.api.Container;
import com.branwilliams.bundi.gui.api.ContainerManager;
import com.branwilliams.bundi.gui.api.Toolbox;
import com.branwilliams.bundi.gui.api.loader.UILoader;
import com.branwilliams.bundi.gui.impl.BasicRenderer;
import com.branwilliams.bundi.gui.impl.BasicToolbox;
import com.branwilliams.bundi.gui.impl.ColorPack;
import com.google.common.collect.Lists;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ContainerManager load(String ui) {
        return load(ui, new HashMap<>());
    }

    public ContainerManager load(String ui, Map<String, Object> env) {
        try {
            ContainerManager containerManager = new ContainerManager(scene, window, renderManager, toolbox);
            List<Container> containers = uiLoader.loadUI(ui, env);
            for (Container container : containers)
                containerManager.add(container);
            return containerManager;
        } catch (IOException | SAXException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ContainerManager loadAsGuiScreen(String ui) {
        return loadAsGuiScreen(ui, new HashMap<>());
    }

    public ContainerManager loadAsGuiScreen(String ui, Map<String, Object> env) {
        ContainerManager containerManager = load(ui, env);
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

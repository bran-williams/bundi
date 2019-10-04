package com.branwilliams.bundi.gui.screen;

import com.branwilliams.bundi.engine.core.Scene;

/**
 * Created by Brandon Williams on 10/1/2019.
 */
public class GuiScreenManager {

    private final Scene scene;

    private GuiScreen guiScreen;

    public GuiScreenManager(Scene scene) {
        this.scene = scene;
    }

    public GuiScreen getGuiScreen() {
        return guiScreen;
    }

    public void setGuiScreen(GuiScreen guiScreen) {
        if (this.guiScreen != null) {
            this.guiScreen.close(scene);
            this.guiScreen.destroy();
        }
        if (guiScreen != null) {
            guiScreen.initialize(scene);
        }
        this.guiScreen = guiScreen;
    }
}

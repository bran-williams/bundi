package com.branwilliams.bundi.gui.api;

import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.window.WindowListener;

public class ScreenWidget extends Container implements WindowListener {

    public ScreenWidget(Scene scene, Window window) {
        super();
        this.setWidth(window.getWidth());
        this.setHeight(window.getHeight());
        scene.addWindowListener(this);
    }

    @Override
    public void resize(Window window, int width, int height) {
        this.setWidth(width);
        this.setHeight(height);
    }

    @Override
    public void update() {

    }
}

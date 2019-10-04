package com.branwilliams.bundi.gui.screen;

import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.gui.api.ContainerManager;

/**
 * @author Brandon
 * @since August 17, 2019
 */
public abstract class ContainerScreen implements GuiScreen {

    public ContainerManager containerManager;

    public ContainerScreen(ContainerManager containerManager) {
        this.containerManager = containerManager;
    }

    @Override
    public void render() {
        containerManager.render();
    }

    @Override
    public void update() {
        containerManager.update();
    }

    @Override
    public void resize(Window window, int width, int height) {

    }

    @Override
    public void move(Window window, float newMouseX, float newMouseY, float oldMouseX, float oldMouseY) {
        containerManager.move(window, newMouseX, newMouseY, oldMouseX, oldMouseY);
    }

    @Override
    public void press(Window window, float mouseX, float mouseY, int buttonId) {
        containerManager.press(window, mouseX, mouseY, buttonId);
    }

    @Override
    public void release(Window window, float mouseX, float mouseY, int buttonId) {
        containerManager.release(window, mouseX, mouseY, buttonId);
    }

    @Override
    public void wheel(Window window, double xoffset, double yoffset) {
        containerManager.wheel(window, xoffset, yoffset);
    }

    @Override
    public void keyPress(Window window, int key, int scancode, int mods) {
        containerManager.keyPress(window, key, scancode, mods);
    }

    @Override
    public void keyRelease(Window window, int key, int scancode, int mods) {
        containerManager.keyRelease(window, key, scancode, mods);
    }

    @Override
    public void charTyped(Window window, String characters) {
        containerManager.charTyped(window, characters);
    }

    @Override
    public void destroy() {
        containerManager.destroy();
    }

}

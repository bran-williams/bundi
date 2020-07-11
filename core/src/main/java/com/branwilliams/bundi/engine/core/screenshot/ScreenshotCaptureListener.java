package com.branwilliams.bundi.engine.core.screenshot;

import com.branwilliams.bundi.engine.core.window.KeyListener;
import com.branwilliams.bundi.engine.core.window.Window;

public class ScreenshotCaptureListener implements KeyListener {

    private final ScreenshotCapturer screenshotCapturer;

    private final int keyCode;

    public ScreenshotCaptureListener(ScreenshotCapturer screenshotCapturer, int keyCode) {
        this.screenshotCapturer = screenshotCapturer;
        this.keyCode = keyCode;
    }

    @Override
    public void keyPress(Window window, int key, int scancode, int mods) {
        if (key == keyCode) {
            screenshotCapturer.screenshot();
        }
    }

    @Override
    public void keyRelease(Window window, int key, int scancode, int mods) {

    }
}

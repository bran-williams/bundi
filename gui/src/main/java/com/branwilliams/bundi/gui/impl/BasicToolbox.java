package com.branwilliams.bundi.gui.impl;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Nameable;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.gui.api.Toolbox;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.HashMap;
import java.util.Map;

/**
 * Basic implementation of the tool box. <br/>
 * Created by Brandon Williams on 3/13/2017.
 */
public class BasicToolbox implements Toolbox {

    private final Map<String, Object> objects = new HashMap<>();

    private final Engine engine;

    private final Window window;

    public BasicToolbox(Engine engine, Window window) {
        this.engine = engine;
        this.window = window;
    }

    @Override
    public Window getWindow() {
        return window;
    }

    @Override
    public Engine getEngine() {
        return engine;
    }

    @Override
    public int getMouseX() {
        return (int) window.getMouseX();
    }

    @Override
    public int getMouseY() {
        return (int) window.getMouseY();
    }

    @Override
    public int getWidth() {
        return window.getWidth();
    }

    @Override
    public int getHeight() {
        return window.getHeight();
    }

    @Override
    public String getClipboard() {
        try {
            Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);

            if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String) transferable.getTransferData(DataFlavor.stringFlavor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    @Override
    public boolean isPointInside(int x, int y, int[] rect) {
        return x > rect[0] && y > rect[1] && x < rect[0] + rect[2] && y < rect[1] + rect[3];
    }

    @Override
    public <O> O get(String location) {
        //noinspection unchecked
        return (O) objects.get(location);
    }

    @Override
    public <O> O get(Nameable location) {
        //noinspection unchecked
        return (O) objects.get(location.getName());
    }

    @Override
    public void put(String location, Object object) {
        objects.put(location, object);
    }

    @Override
    public void put(Nameable location, Object object) {
        objects.put(location.getName(), object);
    }
}

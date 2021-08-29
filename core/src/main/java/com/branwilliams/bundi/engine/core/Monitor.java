package com.branwilliams.bundi.engine.core;

import com.branwilliams.bundi.engine.core.window.Window;

import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;

public class Monitor implements Nameable {

    private final Window window;

    private final long id;

    public Monitor(Window window, long id) {
        this.window = window;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @Override
    public String getName() {
        return glfwGetMonitorName(id);
    }

    @Override
    public String toString() {
        return "Monitor{"
                + "name=" + getName()
                + ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Monitor monitor = (Monitor) o;
        return id == monitor.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

package com.branwilliams.bundi.gui.api;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.window.CharacterListener;
import com.branwilliams.bundi.engine.core.window.KeyListener;
import com.branwilliams.bundi.engine.core.window.MouseListener;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.gui.api.actions.*;
import com.branwilliams.bundi.gui.api.render.RenderManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages containers. <br/>
 * Created by Brandon Williams on 1/23/2017.
 */
public class ContainerManager implements Destructible, MouseListener, KeyListener, CharacterListener {

    private final Container window;

    private final List<Container> containers = new ArrayList<>();

    private RenderManager renderManager;

    private Toolbox toolbox;

    private PopupContainer tooltip = null;

    // Enable layering of components when true.
    private boolean layering = true;

    public ContainerManager(Scene scene, Window window, RenderManager renderManager, Toolbox toolbox) {
        this.window = new ScreenWidget(scene, window);
        this.renderManager = renderManager;
        this.toolbox = toolbox;
    }

    public <T extends Component> T getByTag(String tag) {
        for (Container container : containers) {
            T res = container.getByTag(tag);
            if (res != null)
                return res;
        }
        return null;
    }

    public List<Container> getContainers() {
        return containers;
    }

    /**
     * Invoked to render the containers.
     * */
    public void render() {
        for (Container container : containers) {
            renderManager.render(container);
        }
        if (tooltip != null) {
            tooltip.updatePosition(toolbox.getMouseX(), toolbox.getMouseY());
            renderManager.render(tooltip);
        }
//        renderManager.getPopupRenderer().drawTooltip(tooltip, toolbox.getMouseX(), toolbox.getMouseY());
    }

    /**
     * Updates all containers.
     * */
    public void update() {
        for (Container container : containers) {
            container.update();
        }
    }

    public boolean isLayering() {
        return layering;
    }

    public void setLayering(boolean layering) {
        this.layering = layering;
    }

    /**
     * Adds the container to this manager.
     * */
    public boolean add(Container container) {
        container.setToolbox(toolbox);
        container.setParent(window);
        return this.containers.add(container);
    }

    public boolean remove(Container container) {
        return this.containers.remove(container);
    }

    public void clear() {
        this.containers.clear();
    }

    public boolean contains(String tag) {
        for (Container container : this.containers) {
            if (container.getTag().equals(tag)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void move(Window window, float newMouseX, float newMouseY, float oldMouseX, float oldMouseY) {
        // Used to ensure only one container is updated with a true hover state.
        boolean hover = true;

        // Reset the tool tip, since we will be calculating the hover state of each component.
        this.tooltip = null;

        for (int i = containers.size() - 1; i >= 0; i--) {
            Container container = containers.get(i);
            // Set hover true if no other container has been set hovered = true and this container has the mouse over it.
            if (hover && container.isPointInside((int) newMouseX, (int) newMouseY)) {
                this.tooltip = container.getTooltip();
                container.setHovered(true);
                hover = false;
            } else
                container.setHovered(false);
        }
    }

    @Override
    public void press(Window window, float mouseX, float mouseY, int buttonId) {
        ClickEvent clickEvent = new ClickEvent(ClickEvent.MouseClickAction.MOUSE_PRESS, (int) mouseX, (int) mouseY, buttonId);

        for (int i = containers.size() - 1; i >= 0; i--) {
            Container container = containers.get(i);
            if (container.isActivated(ClickEvent.class, clickEvent)) {
                // Layer this container.
                if (layering) {
                    containers.remove(container);
                    containers.add(container);
                }
                break;
            }
        }
    }

    @Override
    public void release(Window window, float mouseX, float mouseY, int buttonId) {
        ClickEvent clickEvent = new ClickEvent(ClickEvent.MouseClickAction.MOUSE_RELEASE, (int) mouseX, (int) mouseY, buttonId);
        for (int i = containers.size() - 1; i >= 0; i--) {
            Container container = containers.get(i);
            if (container.isActivated(ClickEvent.class, clickEvent))
                break;
        }
    }

    @Override
    public void wheel(Window window, double xoffset, double yoffset) {
        for (Container container : containers) {
            if (container.isActivated(MouseWheelDirection.class, yoffset > 0 ? MouseWheelDirection.UP : MouseWheelDirection.DOWN))
                break;
        }
    }

    @Override
    public void keyPress(Window window, int key, int scancode, int mods) {
        KeystrokeEvent keystrokeEvent = new KeystrokeEvent(key, scancode, mods,
                KeystrokeEvent.KeystrokeAction.KEY_PRESS);
        for (Container container : containers) {
            if (container.isActivated(KeystrokeEvent.class, keystrokeEvent))
                break;
        }
    }

    @Override
    public void keyRelease(Window window, int key, int scancode, int mods) {
        KeystrokeEvent keystrokeEvent = new KeystrokeEvent(key, scancode, mods,
                KeystrokeEvent.KeystrokeAction.KEY_RELEASE);
        for (Container container : containers) {
            if (container.isActivated(KeystrokeEvent.class, keystrokeEvent))
                break;
        }
    }

    @Override
    public void keyHeld(Window window, int key, int scancode, int mods) {
        KeystrokeEvent keystrokeEvent = new KeystrokeEvent(key, scancode, mods,
                KeystrokeEvent.KeystrokeAction.KEY_HELD);
        for (Container container : containers) {
            if (container.isActivated(KeystrokeEvent.class, keystrokeEvent))
                break;
        }
    }

    @Override
    public void charTyped(Window window, String characters) {
        CharTypedEvent charTypedEvent = new CharTypedEvent(characters);
        for (Container container : containers) {
            if (container.isActivated(CharTypedEvent.class, charTypedEvent))
                break;
        }
    }

    @Override
    public void destroy() {

    }

    @Override
    public String toString() {
        return "ContainerManager{" +
                "containers=" + containers +
                ", renderManager=" + renderManager +
                ", toolbox=" + toolbox +
                ", tooltip='" + tooltip + '\'' +
                ", layering=" + layering +
                '}';
    }
}

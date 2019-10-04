package com.branwilliams.bundi.gui.api;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.gui.api.actions.Actions;
import com.branwilliams.bundi.gui.api.actions.ClickAction;
import com.branwilliams.bundi.gui.api.actions.Direction;
import com.branwilliams.bundi.gui.api.actions.KeystrokeAction;
import com.branwilliams.bundi.gui.api.render.RenderManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages containers. <br/>
 * Created by Brandon Williams on 1/23/2017.
 */
public class ContainerManager implements Destructible, Window.MouseListener, Window.KeyListener, Window.CharacterListener {

    private final List<Container> containers = new ArrayList<>();

    private RenderManager renderManager;

    private Toolbox toolbox;

    private String tooltip = null;

    // Enable layering of components when true.
    private boolean layering = true;

    public ContainerManager(RenderManager renderManager, Toolbox toolbox) {
        this.renderManager = renderManager;
        this.toolbox = toolbox;
    }

    /**
     * Invoked to render the containers.
     * */
    public void render() {
        for (Container container : containers) {
            renderManager.render(container);
        }
        renderManager.getPopupRenderer().drawTooltip(tooltip, toolbox.getMouseX(), toolbox.getMouseY());
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
        ClickAction clickAction = new ClickAction((int) mouseX, (int) mouseY, buttonId);

        for (int i = containers.size() - 1; i >= 0; i--) {
            Container container = containers.get(i);
            if (container.isActivated(Actions.MOUSE_PRESS, clickAction)) {
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
        ClickAction clickAction = new ClickAction((int) mouseX, (int) mouseY, buttonId);
        for (int i = containers.size() - 1; i >= 0; i--) {
            Container container = containers.get(i);
            if (container.isActivated(Actions.MOUSE_RELEASE, clickAction))
                break;
        }
    }

    @Override
    public void wheel(Window window, double xoffset, double yoffset) {
        for (Container container : containers) {
            if (container.isActivated(Actions.MOUSEWHEEL, yoffset < 0 ? Direction.UP : Direction.DOWN))
                break;
        }
    }

    @Override
    public void keyPress(Window window, int key, int scancode, int mods) {
        KeystrokeAction keystrokeAction = new KeystrokeAction(key, scancode, mods);
        for (Container container : containers) {
            if (container.isActivated(Actions.KEY_PRESS, keystrokeAction))
                break;
        }
    }

    @Override
    public void keyRelease(Window window, int key, int scancode, int mods) {
        KeystrokeAction keystrokeAction = new KeystrokeAction(key, scancode, mods);
        for (Container container : containers) {
            if (container.isActivated(Actions.KEY_RELEASE, keystrokeAction))
                break;
        }
    }

    @Override
    public void charTyped(Window window, String characters) {
        for (Container container : containers) {
            if (container.isActivated(Actions.CHARACTER_TYPED, characters))
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

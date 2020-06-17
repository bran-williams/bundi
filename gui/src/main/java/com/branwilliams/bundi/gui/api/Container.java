package com.branwilliams.bundi.gui.api;

import com.branwilliams.bundi.gui.api.actions.Actions;
import com.branwilliams.bundi.gui.api.layouts.PaddedLayout;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple component which contains and handles other components. <br/>
 * Created by Brandon Williams on 1/15/2017.
 */
public class Container extends Component {

    protected final List<Component> components = new ArrayList<>();

    private Layout layout = new PaddedLayout();

    // Enable layering of components when true.
    private boolean layering = true;

    // Forces this container to take the dimensions of the layout after it's been laid out.
    private boolean useLayoutSize = false;

    // Forces this container to automatically layout after updating.
    private boolean autoLayout = false;

    // Always calls 'alwaysLayoutChildren' when layout is invoked.
    private boolean alwaysLayoutChildren = true;

    private float opacity = 1F;

    private Color backgroundColor;

    @Override
    public void update() {
        // Used to ensure only one component is updated with a true hover state. When this container has a hover state of false, no component will receive a true hover state.
        boolean hover = isHovered();

        // Reset the tool tip, since we will be calculating the hover state of each component.
        this.tooltip = null;

        // Iterate backwards through each component and update their hovered state along with running their update
        // function.
        for (int i = components.size() - 1; i >= 0; i--) {
            Component component = components.get(i);
            // Set hover true if no other component has been set hovered = true and this component has the mouse over it.
            if (hover && component.isPointInside(toolbox.getMouseX(), toolbox.getMouseY())) {
                this.tooltip = component.getTooltip();
                component.setHovered(true);
                hover = false;
            } else
                component.setHovered(false);
            component.update();
        }
        if (autoLayout)
            layout();
    }

    @Override
    public <T> boolean isActivated(Class<T> type, T data) {
        // Update all component's mouse clicks.
        for (int i = components.size() - 1; i >= 0; i--) {
            Component component = components.get(i);
            if (component.isActivated(type, data)) {
                // Layer this component.
                if (layering) {
                    components.remove(component);
                    components.add(component);
                }
                return true;
            }
        }
        return super.isActivated(type, data);
    }

    /**
     * Lays out each component within the container according to the layout specified.
     * */
    public void layout() {
        if (alwaysLayoutChildren)
            this.layoutChildren();
        int[] size = this.layout.layout(this, components);
        if (useLayoutSize) {
            this.setSize(size[0], size[1]);
            //System.out.println(getTag() + " getting size " + size[0] + ", " + size[1]);
        }
    }

    /**
     * Lays out any child components which are containers.
     * */
    public void layoutChildren() {
        for (Component component : components) {
            if (component instanceof Container) {
                ((Container) component).layout();
            }
        }
    }

    /**
     *
     * */
    public <T extends Component> T getByTag(String tag) {
        if (tag.equalsIgnoreCase(getTag())) {
            return (T) this;
        }

        for (Component component : components) {
            if (tag.equalsIgnoreCase(component.getTag())) {
                return (T) component;
            }
            if (component instanceof Container) {
                T res = ((Container) component).getByTag(tag);
                if (res != null) {
                    return res;
                }
            }
        }
        return null;
    }

    /**
     * Adds this component to this container.
     * */
    public boolean add(Component component) {
        // Update this component with the new gui utility.
        component.setToolbox(toolbox);
        component.setParent(this);
        return components.add(component);
    }

    @Override
    protected void setToolbox(Toolbox toolbox) {
        super.setToolbox(toolbox);
        // Update every child with the new gui utility.
        for (Component child : components) {
            child.setToolbox(toolbox);
        }
    }

    public Layout getLayout() {
        return layout;
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
        // Ensure no negative layout.
        if (this.layout == null)
            this.layout = new PaddedLayout();
    }

    @Override
    public boolean isPointInside(int x, int y) {
        if (super.isPointInside(x, y)) {
            return true;
        }

        for (int i = components.size() - 1; i >= 0; i--) {
            Component component = components.get(i);
            if (component.isPointInside(x, y)) {
                return true;
            }
        }

        return false;
    }

    public List<Component> getComponents() {
        return components;
    }

    public boolean remove(Component component) {
        return components.remove(component);
    }

    public void clear() {
        components.clear();
    }

    public void setLayering(boolean layering) {
        this.layering = layering;
    }

    public boolean isLayering() {
        return layering;
    }

    public boolean isAutoLayout() {
        return autoLayout;
    }

    public void setAutoLayout(boolean autoLayout) {
        this.autoLayout = autoLayout;
    }

    public boolean isUseLayoutSize() {
        return useLayoutSize;
    }

    public void setUseLayoutSize(boolean useLayoutSize) {
        this.useLayoutSize = useLayoutSize;
    }

    public boolean isAlwaysLayoutChildren() {
        return alwaysLayoutChildren;
    }

    public void setAlwaysLayoutChildren(boolean alwaysLayoutChildren) {
        this.alwaysLayoutChildren = alwaysLayoutChildren;
    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}

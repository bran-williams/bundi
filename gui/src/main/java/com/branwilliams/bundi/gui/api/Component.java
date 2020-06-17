package com.branwilliams.bundi.gui.api;

import com.branwilliams.bundi.gui.api.actions.ActionListener;
import com.branwilliams.bundi.gui.api.actions.Actions;
import com.branwilliams.bundi.engine.font.FontData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Simple component class which stores a position and a size along with data associated with the component. <br/>
 * Created by Brandon Williams on 1/15/2017.
 */
public abstract class Component extends BasicWidget {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private Component parent;

    /**
     * True when this component contains the mouse. False otherwise.
     * */
    private boolean hovered = false;

    /**
     * This tag is used as an identifier for this component.
     * */
    private String tag;

    /**
     * Utility used within this component to access mouse information and screen information. Includes helper functions as well.
     * */
    protected Toolbox toolbox;

    /**
     * Each component is going to have it's own font data associated with it.
     * */
    protected FontData font = new FontData();

    protected PopupContainer tooltip = null;

    /**
     * Each activator will be mapped to it's respective action.
     * */
    private final Map<Class<?>, List<ActionListener>> listeners = new HashMap<>();

    /**
     * Determines if this component is visible or not.
     * */
    private boolean visible = true;

    /**
     * This is the minimum width this component should have.
     * */
    private int minimumWidth;

    /**
     * This is the minimum height this component should have.
     * */
    private int minimumHeight;

    protected Component() {
        this(null);
    }

    protected Component(String tag) {
        this.tag = tag;
    }


    /**
     * Invoked to update the component.
     * */
    public abstract void update();

    @Override
    public void destroy() {
        if (font != null)
            font.destroy();
    }

    /**
     * Invoked when some action occurs.
     * @return True if this component was activated and halts the activation of other components within it's parent.
     * @param type
     * @param data
     * */
    public <T> boolean isActivated(Class<T> type, T data) {
        List<ActionListener> actionListeners = this.listeners.get(type);

        boolean activated = false;

        if (actionListeners != null) {
            for (int i = 0; i < actionListeners.size(); i++) {
                ActionListener<T> actionListener = actionListeners.get(i);
                actionListener.onAction(data);
            }
        }

        return activated;
    }

    /**
     * @return The tooltip associated with this component.
     * */
    public PopupContainer getTooltip() {
        return tooltip;
    }

    public void setTooltip(PopupContainer tooltip) {
        this.tooltip = tooltip;
    }

//    public String getTooltip() {
//        return tooltip;
//    }

//    public void setTooltip(String tooltip) {
//        this.tooltip = tooltip;
//    }

    /**
     * Adds an actionListener to this component. Will update them with action information.
     * */
    public <T> void addListener(Class<T> action, ActionListener<T> actionListener) {
        // Grab the actions object from within our listeners map and use this optional's 'isPresent' value to determine if we
        // Must update our listeners list for this action type or create that list.
        if (listeners.containsKey(action)) {
            List<ActionListener> listeners = this.listeners.get(action);
            listeners.add(actionListener);
        } else {
            List<ActionListener> listeners = new ArrayList<>();
            listeners.add(actionListener);
            this.listeners.put(action, listeners);
        }
    }

    public List<ActionListener> clearActionListeners(Class<?> action) {
        return this.listeners.remove(action);
    }

    /**
     * Will remove this component from it's parent.
     * */
    public void dispose() {
        if (parent instanceof Container) {
            ((Container) parent).remove(this);
            this.parent = null;
        } else {
            log.info("Unable to dispose component=[{}]: No parent", toString());
        }
    }

    public int getMinimumWidth() {
        return minimumWidth;
    }

    public void setMinimumWidth(int minimumWidth) {
        this.minimumWidth = minimumWidth;
    }

    public int getMinimumHeight() {
        return minimumHeight;
    }

    public void setMinimumHeight(int minimumHeight) {
        this.minimumHeight = minimumHeight;
    }

    public boolean hasTag() {
        return tag != null;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Component getParent() {
        return parent;
    }

    public boolean isHovered() {
        return hovered;
    }

    protected void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    /**
     * @return True if this component has no parent.
     * */
    public boolean hasParent() {
        return parent != null;
    }

    /**
     * Setter for the parent which can only be accessed from a parent component.
     * */
    protected void setParent(Component parent) {
        this.parent = parent;
    }

    /**
     * Setter for the utility which should only be accessed from a parent component.
     * */
    protected void setToolbox(Toolbox toolbox) {
        this.toolbox = toolbox;
    }

    public FontData getFont() {
        return font;
    }

    public void setFont(FontData font) {
        this.font = font;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}

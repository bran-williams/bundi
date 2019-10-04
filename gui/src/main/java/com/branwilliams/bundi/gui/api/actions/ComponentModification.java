package com.branwilliams.bundi.gui.api.actions;

import com.branwilliams.bundi.gui.api.Component;

public class ComponentModification {

    private final ModificationType type;

    private final Component component;

    private boolean cancel = false;

    public ComponentModification(ModificationType type, Component component) {
        this.type = type;
        this.component = component;
    }

    public ModificationType getType() {
        return type;
    }

    public Component getComponent() {
        return component;
    }

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public enum ModificationType {
        ADD, REMOVE
    }
}

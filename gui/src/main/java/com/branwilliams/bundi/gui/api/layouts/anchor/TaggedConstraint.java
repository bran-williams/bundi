package com.branwilliams.bundi.gui.api.layouts.anchor;

import com.branwilliams.bundi.gui.api.Component;
import com.branwilliams.bundi.gui.api.Container;

public class TaggedConstraint <ContainerType extends Container, ComponentType extends Component>
        implements Constraint<ContainerType, ComponentType> {

    private final Constraint<ContainerType, ComponentType> delegate;

    private final String tag;

    public TaggedConstraint(String tag, Constraint<ContainerType, ComponentType> delegate) {
        this.tag = tag;
        this.delegate = delegate;
    }

    @Override
    public void apply(ContainerType container, ComponentType component) {
        delegate.apply(container, component);
    }

    @Override
    public boolean shouldApply(ContainerType container, ComponentType component) {
        return tag.equalsIgnoreCase(component.getTag());
    }
}

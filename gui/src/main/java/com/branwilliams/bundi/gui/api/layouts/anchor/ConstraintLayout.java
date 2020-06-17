package com.branwilliams.bundi.gui.api.layouts.anchor;

import com.branwilliams.bundi.gui.api.Widget;
import com.branwilliams.bundi.gui.api.Layout;

import java.util.ArrayList;
import java.util.List;

public class ConstraintLayout implements Layout<Widget, Widget> {

    private final List<Constraint> constraints;

    public ConstraintLayout() {
        this(new ArrayList<>());
    }

    public ConstraintLayout(List<Constraint> constraints) {
        this.constraints = constraints;
    }

    @Override
    public int[] layout(Widget container, List<Widget> components) {
        for (Widget component : components) {
            for (Constraint constraint : constraints) {
                if (constraint.shouldApply(container, component))
                    constraint.apply(container, component);
            }
        }

        return new int[] { container.getWidth(), container.getHeight() };
    }

    public void addConstraint(Constraint constraint) {
        constraints.add(constraint);
    }
}

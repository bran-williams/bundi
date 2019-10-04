package com.branwilliams.bundi.gui.api.layouts.anchor;

import com.branwilliams.bundi.gui.api.Widget;
import com.branwilliams.bundi.gui.api.Layout;

import java.util.ArrayList;
import java.util.List;

public class ConstraintLayout implements Layout<Widget> {

    private final List<Constraint> constraints = new ArrayList<>();

    @Override
    public int[] layout(Widget container, List<Widget> list) {
        for (Constraint constraint : constraints) {

        }
        return new int[] { container.getWidth(), container.getHeight() };
    }

    public void addConstraint(Constraint constraint) {
        constraints.add(constraint);
    }
}

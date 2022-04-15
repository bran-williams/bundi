package com.branwilliams.bundi.gui.api.loader.factory;

import com.branwilliams.bundi.gui.api.Toolbox;
import com.branwilliams.bundi.gui.api.layouts.anchor.Constraint;
import com.branwilliams.bundi.gui.api.layouts.anchor.ConstraintLayout;
import com.branwilliams.bundi.gui.api.loader.UIElementFactory;
import com.branwilliams.bundi.gui.api.loader.UIElementType;
import com.branwilliams.bundi.gui.util.XmlUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Brandon Williams on 10/1/2019.
 */
public class ConstraintLayoutFactory implements UIElementFactory<ConstraintLayout> {

    private Map<String, UIElementFactory<Constraint>> constraintFactories = new HashMap<>();

    public ConstraintLayoutFactory() {
        this.constraintFactories = constraintFactories;
        initConstraintFactories();
    }

    public void initConstraintFactories() {
//        this.constraintFactories.put("keepinside", new KeepChildrenInsideFactory());
    }

    @Override
    public ConstraintLayout createElement(Toolbox toolbox, Node node, NamedNodeMap attributes, int parentWidth, int parentHeight) {
        List<Constraint> constraints = new ArrayList<>();
        XmlUtils.forChildren(node, (c) -> {

        });
        ConstraintLayout constraintLayout = new ConstraintLayout(constraints);
        return constraintLayout;
    }

    @Override
    public UIElementType getType() {
        return UIElementType.LAYOUT;
    }

    @Override
    public String getName() {
        return "constraintlayout";
    }
}

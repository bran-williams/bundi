package com.branwilliams.bundi.gui.impl;

import com.branwilliams.bundi.engine.font.FontRenderer;
import com.branwilliams.bundi.gui.impl.render.ShapeRenderer;
import com.branwilliams.bundi.gui.api.Component;
import com.branwilliams.bundi.gui.api.Toolbox;
import com.branwilliams.bundi.gui.api.render.ComponentRenderer;
import com.branwilliams.bundi.gui.api.render.ImageRenderer;

/**
 * Implementation of the component renderer which simply forces the constructor to accept a toolbox. <br/>
 * Created by Brandon Williams on 3/15/2017.
 */
public abstract class AbstractComponentRenderer<T extends Component> implements ComponentRenderer<T> {

    protected final ShapeRenderer shapeRenderer;

    protected final Toolbox toolbox;

    protected final FontRenderer fontRenderer;

    protected final ImageRenderer imageRenderer;

    public AbstractComponentRenderer(ShapeRenderer shapeRenderer, Toolbox toolbox, FontRenderer fontRenderer, ImageRenderer imageRenderer) {
        this.shapeRenderer = shapeRenderer;
        this.toolbox = toolbox;
        this.fontRenderer = fontRenderer;
        this.imageRenderer = imageRenderer;
    }

    @Override
    public void pre(T component) {

    }

    @Override
    public void post(T component) {

    }
}

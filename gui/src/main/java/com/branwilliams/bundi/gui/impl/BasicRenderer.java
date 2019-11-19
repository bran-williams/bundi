package com.branwilliams.bundi.gui.impl;

import com.branwilliams.bundi.engine.font.BasicFontRenderer;
import com.branwilliams.bundi.engine.font.FontRenderer;
import com.branwilliams.bundi.gui.api.ShapeRenderer;
import com.branwilliams.bundi.gui.api.Container;
import com.branwilliams.bundi.gui.api.Toolbox;
import com.branwilliams.bundi.gui.api.components.*;
import com.branwilliams.bundi.gui.api.containers.Frame;
import com.branwilliams.bundi.gui.api.containers.ScrollableContainer;
import com.branwilliams.bundi.gui.impl.render.*;
import com.branwilliams.bundi.gui.api.render.ImageRenderer;
import com.branwilliams.bundi.gui.api.render.RenderManager;

/**
 * Basic implementation of the renderer for the GUI. <br/>
 * Created by Brandon Williams on 2/13/2017.
 */
public class BasicRenderer extends RenderManager {

    private final FontRenderer fontRenderer = new BasicFontRenderer();
    
    private final ImageRenderer imageRenderer = new BasicImageRenderer();

    private final Toolbox toolbox;

    private final ShapeRenderer shapeRenderer;

    public BasicRenderer(Toolbox toolbox) {
        super();
        this.toolbox = toolbox;
        this.shapeRenderer = new ShapeRenderer();

        this.setPopupRenderer(new BasicPopupRenderer(shapeRenderer, toolbox, fontRenderer, imageRenderer));
        this.setRenderer(Button.class, new ButtonRenderer(shapeRenderer, toolbox, fontRenderer, imageRenderer));
        this.setRenderer(Checkbox.class, new CheckboxRenderer(shapeRenderer, toolbox, fontRenderer, imageRenderer));
        this.setRenderer(Container.class, false, new ContainerRenderer(shapeRenderer, toolbox, fontRenderer, imageRenderer));
        this.setRenderer(ScrollableContainer.class, false, new ScrollableContainerRenderer(shapeRenderer, toolbox, fontRenderer, imageRenderer));
        this.setRenderer(Slider.class, new SliderRenderer(shapeRenderer, toolbox, fontRenderer, imageRenderer));
        this.setRenderer(Label.class, new LabelRenderer(shapeRenderer, toolbox, fontRenderer, imageRenderer));
        this.setRenderer(TextField.class, new TextFieldRenderer(shapeRenderer, toolbox, fontRenderer, imageRenderer));
        this.setRenderer(ComboBox.class, new ComboBoxRenderer(shapeRenderer, toolbox, fontRenderer, imageRenderer));
        this.setRenderer(Frame.class, true, new FrameRenderer(shapeRenderer, toolbox, fontRenderer, imageRenderer));
    }

    public FontRenderer getFontRenderer() {
        return fontRenderer;
    }

    public ImageRenderer getImageRenderer() {
        return imageRenderer;
    }

    public Toolbox getToolbox() {
        return toolbox;
    }
}

package com.branwilliams.bundi.gui.api.components;

import com.branwilliams.bundi.gui.api.Component;
import com.branwilliams.bundi.gui.api.actions.ClickEvent;

import java.util.function.BiFunction;

/**
 * Created by Brandon Williams on 9/9/2019.
 */
public class TreeView extends Component {

//    private Tree text;

    private boolean highlight;

    private boolean pressed = false;

    private BiFunction<TreeView, ClickEvent, Boolean> pressFunction;

    public TreeView(String tag, String text) {
        this(tag, text, false);
    }

    public TreeView(String tag, String text, boolean highlight) {
        super(tag);
        this.highlight = highlight;
    }

    @Override
    public void update() {

    }

    /**
     * Invokes the given function when this button is pressed.
     * */
    public void onPressed(BiFunction<TreeView, ClickEvent, Boolean> pressFunction) {
        this.pressFunction = pressFunction;
    }

    public boolean isHighlight() {
        return highlight;
    }

    public void setHighlight(boolean highlight) {
        this.highlight = highlight;
    }

    public boolean isPressed() {
        return pressed;
    }

    @Override
    public String toString() {
        return "TreeView{" +
                "highlight=" + highlight +
                ", font=" + font +
                ", tooltip='" + tooltip + '\'' +
                ", x=" + getX() +
                ", y=" + getY() +
                ", width=" + getWidth() +
                ", height=" + getHeight() +
                '}';
    }
}

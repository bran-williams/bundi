package com.branwilliams.bundi.engine.font;

import java.util.function.Supplier;

public class Text {

    private Supplier<String> text;

    private boolean dirty;

    // Only true when the text has been initialized with just a string.
    private boolean static_;

    public Text(Supplier<String> text) {
        this.setText(text);
    }

    public Text(String text) {
        this.setText(text);
    }

    /**
     * Sets the supplier for this texts string.
     * */
    public void setText(Supplier<String> text) {
        this.text = text;
        this.static_ = false;
    }

    /**
     * Sets the supplier for this texts string.
     *
     * */
    public void setText(String text) {
        this.text = () -> text;
        this.static_ = true;
    }

    public String getText() {
        return text.get();
    }

    public boolean isDirty() {
        return !static_ && dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}

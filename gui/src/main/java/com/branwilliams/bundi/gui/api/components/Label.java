package com.branwilliams.bundi.gui.api.components;

import com.branwilliams.bundi.engine.font.FontData;
import com.branwilliams.bundi.gui.api.Component;

import java.awt.*;

/**
 * Simple label implementation. <br/>
 * Created by Brandon Williams on 1/15/2017.
 */
public class Label extends Component {

    public enum LabelAlignment {
        LEFT, CENTER, RIGHT;

        public static LabelAlignment fromOrDefault(String text, LabelAlignment defaultValue) {
            LabelAlignment alignment = from(text);

            if (alignment == null)
                return defaultValue;
            else
                return alignment;
        }

        public static LabelAlignment from(String text) {
            for (LabelAlignment alignment : values()) {
                if (alignment.name().equalsIgnoreCase(text)) {
                    return alignment;
                }
            }
            return null;
        }

    }

    public static final int NO_TEXT_SIZE = -1;

    private String text;

    private Color color;

    private int textWidth = NO_TEXT_SIZE;

    private int textHeight = NO_TEXT_SIZE;

    private LabelAlignment alignment = LabelAlignment.LEFT;

    public Label(String tag, String text) {
        this(tag, text, new FontData(), Color.WHITE);
    }

    public Label(String tag, String text, FontData font) {
        this(tag, text, font, Color.WHITE);
    }

    public Label(String tag, String text, FontData font, Color color) {
        super(tag);
        this.font = font;
        this.text = text;
        this.color = color;
        this.update();
    }

    @Override
    public void update() {
        // TODO Unecessary??
        if (this.font.hasFont() && !this.hasTextSize()) {
            updateTextSize();
        }
    }

    private void updateTextSize() {
        textWidth = font.getStringWidth(text);
        textHeight = font.getFontHeight();
//        this.setWidth(textWidth);
//        this.setHeight(textHeight);
    }

    /**
     * @return True if this label has it's text size calculated.
     * */
    private boolean hasTextSize() {
        return textWidth == NO_TEXT_SIZE || textHeight == NO_TEXT_SIZE;
    }

    /**
     * Resets this labels text sizing.
     * */
    private void resetTextSize() {
        textWidth = NO_TEXT_SIZE;
        textHeight = NO_TEXT_SIZE;
    }

    public LabelAlignment getAlignment() {
        return alignment;
    }

    public void setAlignment(LabelAlignment alignment) {
        this.alignment = alignment;
    }

    public int getTextWidth() {
        return textWidth;
    }

    public int getTextHeight() {
        return textHeight;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void setFont(FontData font) {
        super.setFont(font);

        if (font == null)
            this.resetTextSize();
        else
            this.updateTextSize();
    }


}

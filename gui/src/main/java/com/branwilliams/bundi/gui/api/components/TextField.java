package com.branwilliams.bundi.gui.api.components;

import com.branwilliams.bundi.gui.api.Component;
import com.branwilliams.bundi.gui.api.actions.*;

import java.awt.*;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Simple text field implementation. <br/>
 * Created by Brandon Williams on 1/15/2017.
 */
public class TextField extends Component {

    /**
     * Every character which is allowed to be typed into this text field.
     * */
    private String validCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-=[]\\;',./`~!@#$%^&*()_+{}|:\"<>?; ";

    private Color color = Color.WHITE;

    private String text, defaultText;

    private int pointer = 0, maxLength = 50;

    // This index is stored separately from the pointer to ensure
    private int renderIndex = 0;

    // This is the minimum amount of characters needed to be present before the pointer.
    // Used when calculating the render index.
    private int renderOffset = 4;

    private boolean typing = false;

    public TextField(String tag, String text) {
        this(tag, text, text);
    }

    public TextField(String tag, String text, String defaultText) {
        super(tag);
        this.setText(text);
        this.defaultText = defaultText;

        this.addListener(ClickEvent.class, (ClickEvent.ClickActionListener)
                event ->
                        typing = (event.mouseClickAction == ClickEvent.MouseClickAction.MOUSE_PRESS
                                && isHovered()
                                && isPointInside(event.x, event.y) && event.buttonId == 0));


        this.addListener(CharTypedEvent.class, (CharacterTypedActionListener) charTypedEvent -> {
            if (typing) {
                if (validCharacters.contains(charTypedEvent.character))
                    this.append(charTypedEvent.character);
                return true;
            }

            return false;
        });

        this.addListener(KeystrokeEvent.class, (KeystrokeEvent.KeystrokeActionListener) this::keystrokeAction);
    }

    private boolean keystrokeAction(KeystrokeEvent event) {
        if (typing && event.keystrokeAction != KeystrokeEvent.KeystrokeAction.KEY_RELEASE) {
            switch (event.keystroke.key) {
                case GLFW_KEY_BACKSPACE:
                    backSpace();
                    break;
                case GLFW_KEY_HOME:
                    move(0);
                    break;
                case GLFW_KEY_LEFT:
                    this.moveDirection(-1);
                    break;
                case GLFW_KEY_RIGHT:
                    this.moveDirection(1);
                    break;
                case GLFW_KEY_END:
                    move(this.text.length());
                    break;
                case GLFW_KEY_DELETE:
                    forwardSpace();
                    break;
                case GLFW_KEY_V:
                    if (toolbox.getWindow().isKeyPressed(GLFW_KEY_LEFT_CONTROL)
                            || toolbox.getWindow().isKeyPressed(GLFW_KEY_RIGHT_CONTROL))
                        this.append(toolbox.getClipboard());
                default:
                    break;
            }
            return true;
        }
        return false;
    }

    /**
     * Appends this string to this textfield's text.
     * */
    public void append(String string) {
        // If the current text + the given string are less than or equal to the max length.
        if (text.concat(string).length() <= maxLength) {
            // If the pointer is at the end of the text, throw the new string onto the end.
            if (pointer >= this.text.length()) {
                this.text = this.text + string;
            // If the pointer is not at the beginning of the text, throw the new string into the middle of our text.
            } else if (pointer > 0) {
                this.text = this.text.substring(0, pointer) + string + this.text.substring(pointer, this.text.length());
            // Otherwise put the new string at the beginning of our text.
            } else if (pointer == 0) {
                this.text = string + this.text;
            }
            // Increase our pointer.
            pointer += string.length();
        }
    }

    /**
     * Appends this character to this textfield's text.
     * */
    private void append(char c) {
        append(Character.toString(c));
    }

    @Override
    public void update() {
        calculateRenderIndex();
    }

    /**
     * Calculates the render index for this text field.
     * */
    private void calculateRenderIndex() {

        // LEFT CHECK
        // If the pointer is greater than or equal to the render offset and if the distance between
        // the pointer and the render index is less than the offset, we must move the render index left.
        if (pointer >= renderOffset && pointer - renderIndex < renderOffset) {
            // Set the render index to the pointer minus the offset.
            renderIndex = Math.max(0, pointer - renderOffset);
        } else {
            // Calculate the trimmed string starting at the render index.
            char[] trimmed = font.trim(text.substring(renderIndex), this.getWidth()).toCharArray();

            // RIGHT CHECK
            // In order to compare the pointer to the length of the trimmed text,
            // the render index must be added.
            if (renderIndex + trimmed.length < text.length() && (renderIndex + trimmed.length) - pointer < renderOffset) {
                renderIndex = Math.max(0, (pointer + renderOffset) - trimmed.length);
            }
        }
    }


    /**
     * Performs the backspace operation on the text with the current selection position.
     * */
    private void backSpace() {
        if (!this.text.isEmpty()) {
            if (pointer >= this.text.length()) {
                this.text = this.text.substring(0, this.text.length() - 1);
            } else if (pointer > 0) {
                this.text = this.text.substring(0, pointer - 1) + this.text.substring(pointer);
            }
            moveDirection(-1);
        }
    }

    /**
     * Performs the forward space operation on the text with the current selection position.
     * */
    private void forwardSpace() {
        if (!this.text.isEmpty()) {
            if (pointer <= 0) {
                this.text = this.text.substring(1, this.text.length());
            } else if (pointer < this.text.length()) {
                this.text = this.text.substring(0, pointer) + this.text.substring(pointer + 1, this.text.length());
            }
        }
    }

    /**
     * Moves the selection position to the given index.
     * */
    private void move(int index) {
        pointer = index;
        keepSafe();
    }

    /**
     * Moves the selection position into the direction given.
     * */
    private void moveDirection(int direction) {
        pointer += direction;
        keepSafe();
    }

    /**
     * Keeps the selection position within 0 and the length of the text.
     * */
    private void keepSafe() {
        if (pointer > this.text.length()) {
            pointer = this.text.length();
        } else if (pointer < 0) {
            pointer = 0;
        }
        if (pointer == 0) {
            renderIndex = pointer;
        }
        this.calculateRenderIndex();
    }

    /**
     * @return A string that can be rendered.
     * */
    public String getRenderText(boolean showPlacement) {
        String text = showPlacement ? this.text.substring(renderIndex, pointer) + "|" + this.text.substring(pointer) : this.text.substring(renderIndex);
        return font.trim(text, this.getWidth());
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        this.pointer = text.length();
        this.calculateRenderIndex();
    }

    /**
     * @return True if this textfield has text.
     * */
    public boolean hasText() {
        return !text.isEmpty();
    }

    public String getDefaultText() {
        return defaultText;
    }

    public void setDefaultText(String defaultText) {
        this.defaultText = defaultText;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public boolean isTyping() {
        return typing;
    }

    public String getValidCharacters() {
        return validCharacters;
    }

    public void setValidCharacters(String validCharacters) {
        this.validCharacters = validCharacters;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
package com.branwilliams.bundi.gui.api.components;

import com.branwilliams.bundi.gui.api.Component;
import com.branwilliams.bundi.gui.api.actions.Actions;
import com.branwilliams.bundi.gui.api.actions.ClickEvent;

/**
 * Simple combo box implementation. <br/>
 * Created by Brandon Williams on 3/5/2017.
 */
public class ComboBox<I> extends Component {

    private static final int ITEM_NAME_PADDING = 2;

    private I[] items;

    private boolean expanded = false;

    private int selected = 0;

    private int itemHeight = 15;

    public ComboBox(I... items) {
        super();
        this.items = items;
        addListener(ClickEvent.class, (ClickEvent.ClickActionListener) event -> {
            switch (event.mouseClickAction) {
                case MOUSE_PRESS:
                    return isHovered() && isPointInside(event.x, event.y) && event.buttonId == 0;

                case MOUSE_RELEASE:
                    if (isHovered() && isPointInside(event.x, event.y) && event.buttonId == 0) {
                        if (expanded) {
                            // Find the hovered item and set it selected.
                            int index = getHoveredItem(event.x, event.y);
                            if (index != -1) {
                                setSelected(index);
                            }
                            expanded = false;
                        } else {
                            // Expand me!
                            expanded = true;
                        }
                    } else {
                        expanded = false;
                    }
                    return false;

                default:
                    return false;
            }
        });
        this.setHeight(itemHeight);
    }

    @Override
    public void update() {
        itemHeight = ITEM_NAME_PADDING + font.getFontHeight() + ITEM_NAME_PADDING;
        // Update the height with the font height + font height * items length.
        setHeight(itemHeight + (expanded ? itemHeight * items.length : 0));
    }
    @Override
    public boolean isPointInside(int x, int y) {
        return super.isPointInside(x, y) || getHoveredItem(x, y) != -1;
    }

    /**
     * @return -1 for no items.
     * */
    public int getHoveredItem(int x, int y) {
        if (expanded) {
            for (int i = 0; i < items.length; i++) {
                if (toolbox.isPointInside(x, y, getItemArea(i))) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * @return The area of the item at the index given.
     * */
    public int[] getItemArea(int index) {
        return new int[] { getX(), getY() + itemHeight + index * itemHeight, getWidth(), itemHeight };
    }

    public int getItemHeight() {
        return itemHeight;
    }

    public void setItemHeight(int itemHeight) {
        this.itemHeight = itemHeight;
    }

    public I[] getItems() {
        return items;
    }

    public void setItems(I[] items) {
        this.items = items;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
        if (this.selected < 0)
            this.selected = 0;
        if (this.selected >= items.length)
            this.selected = items.length - 1;
    }

    public I getSelectedItem() {
        return items[selected];
    }
}

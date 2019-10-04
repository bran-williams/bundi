package com.branwilliams.bundi.gui.api.layouts;

import com.branwilliams.bundi.gui.api.Widget;
import com.branwilliams.bundi.gui.api.Layout;

import java.util.List;

/**
 * Layout which places each component into their own row and column. <br/>
 * Created by Brandon Williams on 2/19/2017.
 */
public class GridLayout implements Layout<Widget> {

    private final int rows, columns;

    private final int verticalPadding;

    private final int horizontalPadding;

    private final int componentPadding;

    public GridLayout(int rows, int columns) {
        this(rows, columns, 0);
    }

    public GridLayout(int rows, int columns, int padding) {
        this(rows, columns, padding, padding);
    }

    public GridLayout(int rows, int columns, int containerPadding, int componentPadding) {
        this(rows, columns, containerPadding, containerPadding, componentPadding);
    }

    public GridLayout(int rows, int columns, int verticalPadding, int horizontalPadding, int componentPadding) {
        if (rows <= 0 || columns <= 0) {
            throw new IllegalArgumentException("Rows and columns both must be greater than zero!");
        }
        this.rows = rows;
        this.columns = columns;
        this.verticalPadding = verticalPadding;
        this.horizontalPadding = horizontalPadding;
        this.componentPadding = componentPadding;
    }

    @Override
    public int[] layout(Widget container, List<Widget> components) {
        if (components.size() > columns * rows) {
            throw new IllegalStateException("Container contains too many components for GridLayout "
                    + "maxComponents: " + columns * rows + " components: " + components.size());
        }

        int[] rowHeight = new int[rows];
        int[] columnWidth = new int[columns];

        for (int i = 0; i < components.size(); i++) {
            Widget component = components.get(i);

            // Calculate this component's row and column.
            int column = i % columns;
            int row = (int) Math.floor((float) i / (float) columns);

            // Increase this cell width if necessary.
            if (component.getWidth() > columnWidth[column])
                columnWidth[column] = component.getWidth();

            // Increase this cell height if necessary.
            if (component.getHeight() > rowHeight[row])
                rowHeight[row] = component.getHeight();
        }

        int x = 0, y = 0, width = 0, height = 0;

        for (int i = 0; i < components.size(); i++) {
            Widget component = components.get(i);

            // Calculate this component's row and column.
            int column = i % columns;
            int row = (int) Math.floor((float) i / (float) columns);

            component.setX(container.getX() + horizontalPadding + x);
            component.setY(container.getY() + verticalPadding + y);

            // Increment our width and height
            if (x + component.getWidth() + horizontalPadding * 2 > width)
                width = x + component.getWidth() + horizontalPadding * 2;

            if (y + component.getHeight() + verticalPadding * 2 > height)
                height = y + component.getHeight() + verticalPadding * 2;

            // If we have a next component
            if ((i + 1) < components.size()) {
                int nextColumn = (i + 1) % columns;
                int nextRow = (int) Math.floor((float) (i + 1) / (float) columns);

                // if the next component is within the next column, move our position right.
                if (nextColumn > column) {
                    x += columnWidth[column] + componentPadding;
                }
                // if the next component is within a new row, reset the x position and move downward.
                if (nextRow > row) {
                    y += rowHeight[row] + componentPadding;
                    x = 0;
                }
            }
        }

        return new int[] { width, height };
    }
}

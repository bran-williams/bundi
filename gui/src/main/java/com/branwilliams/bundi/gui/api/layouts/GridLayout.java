package com.branwilliams.bundi.gui.api.layouts;

import com.branwilliams.bundi.gui.api.Widget;
import com.branwilliams.bundi.gui.api.Layout;

import java.util.List;

/**
 * Layout which places each component into their own row and column. <br/>
 * Created by Brandon Williams on 2/19/2017.
 */
public class GridLayout implements Layout<Widget, Widget> {

    private final int rows, columns;

    private final int verticalPadding;

    private final int horizontalPadding;

    private final int componentPadding;

    private final boolean forcedSize;

    public GridLayout(int rows, int columns) {
        this(rows, columns, 0);
    }

    public GridLayout(int rows, int columns, int padding) {
        this(rows, columns, padding, padding);
    }

    public GridLayout(int rows, int columns, int containerPadding, int componentPadding) {
        this(rows, columns, containerPadding, containerPadding, componentPadding, true);
    }

    public GridLayout(int rows, int columns, int verticalPadding, int horizontalPadding, int componentPadding,
                      boolean forcedSize) {
        if (rows <= 0 || columns <= 0) {
            throw new IllegalArgumentException("Rows and columns both must be greater than zero!");
        }
        this.rows = rows;
        this.columns = columns;
        this.verticalPadding = verticalPadding;
        this.horizontalPadding = horizontalPadding;
        this.componentPadding = componentPadding;
        this.forcedSize = forcedSize;
    }

    @Override
    public int[] layout(Widget container, List<Widget> components) {
        if (components.size() > columns * rows) {
            throw new IllegalStateException("Container contains too many components for GridLayout "
                    + "maxComponents: " + columns * rows + " components: " + components.size());
        }

        int[] rowHeight = new int[rows];
        int[] columnWidth = new int[columns];

//        // Initialize the gridlayout to make each cell height uniform..
//        int defaultRowHeight = container.getHeight() / (rows);
//        for (int i = 0; i < rows; i++)
//            rowHeight[i] = defaultRowHeight;
//
//        // Initialize the gridlayout to make each cell width uniform..
//        int defaultColumnWidth = container.getWidth() / (columns);
//        for (int i = 0; i < columns; i++)
//            columnWidth[i] = defaultColumnWidth;

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

        int width = 0;
        int height = 0;

        // TODO uniform column sizes and uniform row sizes?
//        int minimumWidth = horizontalPadding * 2;
//        for (int i = 0; i < columns; i++) {
//            boolean isLastComponent = i == columns - 1;
//            minimumWidth += columnWidth[i] + (isLastComponent ? 0 : componentPadding);
//        }
//        if (minimumWidth < container.getWidth()) {
//        }

        int x = 0, y = 0;

        for (int i = 0; i < components.size(); i++) {
            Widget component = components.get(i);

            // Calculate this component's row and column.
            int column = i % columns;
            int row = (int) Math.floor((float) i / (float) columns);
            int thisColumnsWidth = columnWidth[column];
            int thisRowsHeight = rowHeight[row];

            component.setX(container.getX() + horizontalPadding + x);
            component.setY(container.getY() + verticalPadding + y);



            if (forcedSize) {
                if (component.getWidth() < thisColumnsWidth) {
                    component.setWidth(thisColumnsWidth - horizontalPadding);
                }
                if (component.getHeight() < thisRowsHeight) {
                    component.setHeight(thisRowsHeight - verticalPadding);
                }
            } else {
                thisColumnsWidth = component.getWidth();
                thisRowsHeight = component.getHeight();
            }

            // Increment our width and height
            if (x + thisColumnsWidth + horizontalPadding * 2 > width)
                width = x + thisColumnsWidth + horizontalPadding * 2;

            if (y + thisRowsHeight + verticalPadding * 2 > height)
                height = y + thisRowsHeight + verticalPadding * 2;

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

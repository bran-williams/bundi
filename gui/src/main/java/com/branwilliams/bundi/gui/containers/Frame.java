package com.branwilliams.bundi.gui.containers;

import com.branwilliams.bundi.gui.BasicWidget;
import com.branwilliams.bundi.gui.Widget;
import com.branwilliams.bundi.gui.Layout;
import com.branwilliams.bundi.gui.layouts.PaddedLayout;

import java.util.List;

/**
 *
 * */
public class Frame extends MoveableContainer {

    public static final int FRAME_TITLE_HEIGHT = 32;

    private Widget titleWidget;

    private String title;

    public Frame(String tag, String title) {
        super(tag);
        this.title = title;
        this.titleWidget = new FrameTitleWidget(this, FRAME_TITLE_HEIGHT);
        this.setMoveFunction((frame, toolbox) -> {
            frame.layout();
            return true;
        });
        this.setLayout(new PaddedLayout());
    }

    @Override
    public boolean isPointInsideMoveableArea(int x, int y) {
        return titleWidget.isPointInside(x, y);
    }

    @Override
    public void setLayout(Layout layout) {
        if (!(layout instanceof FrameLayout)) {
            layout = new FrameLayout(layout);
        }
        super.setLayout(layout);
    }

    public Widget getTitleWidget() {
        return titleWidget;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Frame{" +
                "title='" + title + '\'' +
                ", font=" + font +
                ", tooltip='" + tooltip + '\'' +
                ", x=" + getX() +
                ", y=" + getY() +
                ", width=" + getWidth() +
                ", height=" + getHeight() +
                '}';
    }

    /**
     * This layout wraps around a layout and applies the size of the title to this frame.
     * */
    public class FrameLayout implements Layout {

        private final Layout layout;

        public FrameLayout(Layout layout) {
            this.layout = layout;
        }

        @Override
        public int[] layout(Widget container, List components) {
            container = new BasicWidget(container.getX(), container.getY() + titleWidget.getHeight(), container.getWidth(), container.getHeight() - titleWidget.getHeight());
            int[] dimensions = layout == null ? new int[] { container.getWidth(), container.getHeight() } : layout.layout(container, components);
            dimensions[1] += titleWidget.getHeight();
            return dimensions;
        }
    }

    /**
     * Special dimension made to contain the frame title's dimensions.
     * This is actually a bit stupid right now, but I plan on making this just
     * another component. Like a container itself!
     * */
    public class FrameTitleWidget implements Widget {

        private final Frame frame;

        private int height;

        public FrameTitleWidget(Frame frame, int height) {
            this.frame = frame;
            this.height = height;
        }

        @Override
        public int getX() {
            return frame.getX();
        }

        @Override
        public void setX(int x) {

        }

        @Override
        public int getY() {
            return frame.getY();
        }

        @Override
        public void setY(int y) {

        }

        @Override
        public int getWidth() {
            return frame.getWidth();
        }

        @Override
        public void setWidth(int width) {

        }

        @Override
        public int getHeight() {
            return height;
        }

        @Override
        public void setHeight(int height) {
            this.height = height;
        }

        @Override
        public boolean isPointInside(int x, int y) {
            return x > this.getX() && y > this.getY() && x < this.getX() + this.getWidth() && y < this.getY() + this.getHeight();
        }

        @Override
        public int[] getArea() {
            return new int[] { getX(), getY(), getWidth(), getHeight() };
        }

    }
}

package com.branwilliams.bundi.gui.api;

public class PopupContainer extends Container {

    public PopupContainer() {
        super();
    }

    @Override
    public void update() {
        super.update();
    }

    public void updatePosition(int x, int y) {
        this.setPosition(x, y);
        if (isAutoLayout())
            layout();
        this.setPosition(x, y - this.getHeight());
        if (isAutoLayout())
            layout();
    }

//    @Override
//    public void setPosition(int x, int y) {
//
//    }
//
//    @Override
//    public void setSize(int width, int height) {
//
//    }
}

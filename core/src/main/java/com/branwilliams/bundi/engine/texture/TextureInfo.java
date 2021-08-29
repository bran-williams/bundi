package com.branwilliams.bundi.engine.texture;

public class TextureInfo {
    private int width;
    private int height;
    private int components;

    public TextureInfo(int width, int height, int components) {
        this.width = width;
        this.height = height;
        this.components = components;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getComponents() {
        return components;
    }
}

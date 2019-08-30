package com.branwilliams.bundi.gui;

import com.branwilliams.bundi.engine.shader.dynamic.DynamicVAO;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.texture.Texture;

import java.awt.*;

/**
 * Used by UI components to draw shapes.
 * @author Brandon
 * @since May 15, 2019
 */
public class ShapeRenderer {

    private final DynamicVAO dynamicVAO;

    private final Texture whiteTexture;

    public ShapeRenderer() {
        dynamicVAO = new DynamicVAO(VertexFormat.POSITION_UV_COLOR);
        whiteTexture = new Texture(255, 255, 255, 255, 1, 1, false);
    }

    public void drawRect(int[] area, Color color) {
        drawRect(area, color.getRGB());
    }

    public void drawRect(int[] area, int color) {
        whiteTexture.bind();
        dynamicVAO.begin();
        addRect(area, color);
        dynamicVAO.draw();
    }

    public void drawRect(int x, int y, int x1, int y1, Color color) {
        drawRect(x, y, x1, y1, color.getRGB());
    }

    public void drawRect(int x, int y, int x1, int y1, int color) {
        whiteTexture.bind();
        dynamicVAO.begin();
        dynamicVAO.addRect(x, y, x1, y1, 0F, 0F, 1F, 1F, color);
        dynamicVAO.draw();
    }

    public void addRect(int[] area, Color color) {
        addRect(area, color.getRGB());
    }

    public void addRect(int[] area,  int color) {
        dynamicVAO.addRect(area[0], area[1], area[0] + area[2], area[1] + area[3], 0F, 0F, 1F, 1F, color);
    }

    public DynamicVAO getDynamicVAO() {
        return dynamicVAO;
    }
}

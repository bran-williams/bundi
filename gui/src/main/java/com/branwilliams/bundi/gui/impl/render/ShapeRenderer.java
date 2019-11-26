package com.branwilliams.bundi.gui.impl.render;

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

    public void drawTriangle(int x, int y, int x1, int y1, int color) {
        float z = 0F;
        whiteTexture.bind();
        dynamicVAO.begin();
        dynamicVAO.position(x1, y, z).texture(1, 0).color(color).endVertex();
        dynamicVAO.position(x, y, z).texture(0, 0).color(color).endVertex();
        dynamicVAO.position((x + x1) * 0.5F, y1, z).texture(0, 1).color(color).endVertex();
        dynamicVAO.draw();
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

    public void drawLine(int x, int y, int x1, int y1, Color color) {
        drawLine(x, y, x1, y1, color.getRGB());
    }

    public void drawLine(int x, int y, int x1, int y1, int color) {
        whiteTexture.bind();
        dynamicVAO.begin();
        dynamicVAO.addLine(x, y, x1, y1, 0F, 0F, 1F, 1F, color);
        dynamicVAO.draw();
    }

    public void drawRect(int x, int y, int x1, int y1, Color color) {
        drawRect(whiteTexture, x, y, x1, y1, color.getRGB());
    }

    public void drawRect(int x, int y, int x1, int y1, int color) {
        drawRect(whiteTexture, x, y, x1, y1, color);
    }

    public void drawRect(Texture texture, int x, int y, float scale, Color color) {
        drawRect(texture, x, y, scale, color.getRGB());
    }

    public void drawRect(Texture texture, int x, int y, float scale, int color) {
        drawRect(texture, x, y, (int) (texture.getWidth() * scale), (int) (texture.getHeight() * scale), color);
    }

    public void drawRect(Texture texture, int x, int y, int x1, int y1, int color) {
        texture.bind();
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

package com.branwilliams.terrain.generator;

import com.branwilliams.bundi.engine.texture.TextureData;

import java.nio.ByteBuffer;

/**
 * Created by Brandon Williams on 10/31/2018.
 */
public class HeightmapHeightGenerator implements HeightGenerator {

    public static final int GRAYSCALE_MAX_COLOR = 0x1000000;

    private final int[] pixels;

    // the largest value each pixel can have. This is used to create a value from 0 ~ 1 for the height calculation.
    private final int maxColor;

    private int width;

    private int height;

    public HeightmapHeightGenerator(TextureData textureData) {
        this(textureData.getData(), textureData.getWidth(), textureData.getHeight(), textureData.getChannels(), GRAYSCALE_MAX_COLOR);
    }

    public HeightmapHeightGenerator(TextureData textureData, int maxColor) {
        this(textureData.getData(), textureData.getWidth(), textureData.getHeight(), textureData.getChannels(), maxColor);
    }

    public HeightmapHeightGenerator(int[] pixels, int width, int height) {
        this(pixels, width, height, GRAYSCALE_MAX_COLOR);
    }

    public HeightmapHeightGenerator(int[] pixels, int width, int height, int maxColor) {
        this.pixels = pixels;
        this.width = width;
        this.height = height;
        this.maxColor = maxColor;
    }

    public HeightmapHeightGenerator(ByteBuffer buffer, int width, int height, int channels, int maxColor) {
        if (buffer.capacity() % channels != 0) {
            throw new IllegalStateException("ByteBuffer must be in the formats: rgba or rgb");
        }
        pixels = new int[width * height];
        this.width = width;
        this.height = height;
        this.maxColor = maxColor;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int i = (x + y * width) * channels;

                int r = 0,g = 0,b = 0,a = 0;

                switch (channels) {
                    case 1:
                        r = buffer.get(i) & 0xFF;
                        break;
                    case 2:
                        r = buffer.get(i) & 0xFF;
                        a = buffer.get(i + 1) & 0xFF;
                        break;
                    case 3:
                        r = buffer.get(i) & 0xFF;
                        g = buffer.get(i + 1) & 0xFF;
                        b = buffer.get(i + 2) & 0xFF;
                        break;
                    case 4:
                        r = buffer.get(i) & 0xFF;
                        g = buffer.get(i + 1) & 0xFF;
                        b =  buffer.get(i + 2) & 0xFF;
                        a = buffer.get(i + 3) & 0xFF;
                        break;
                }

                setPixel(x, y, (a << 24) | (r << 16) | (g << 8) | b);
            }
        }
    }

    @Override
    public float[][] generateHeight(float x, float z, int vertexCountX, int vertexCountZ, float amplitude) {
        if (vertexCountX * vertexCountZ != this.pixels.length) {
            System.out.println();
            throw new IllegalStateException(String.format("The vertex count must match this heightmaps pixel count. pixel count: %d", this.pixels.length));
        }
        float[][] heights = new float[vertexCountX][vertexCountZ];
        for (int i = 0; i < vertexCountX; i++) {
            heights[i] = new float[vertexCountZ];
            for (int j = 0; j < vertexCountZ; j++) {
                int pixel = getPixel(i, j);
                pixel += maxColor * 0.5F;
                float normalizedPixel = (float) pixel / (maxColor * 0.5F);
                heights[i][j] = normalizedPixel * amplitude;
            }
        }
        return heights;
    }

    public int getPixel(int x, int y) {
        return pixels[x + y * width];
    }


    public void setPixel(int x, int y, int color) {
        this.pixels[x + y * width] = color;
    }
}

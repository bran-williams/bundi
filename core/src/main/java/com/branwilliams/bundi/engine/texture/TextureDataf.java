package com.branwilliams.bundi.engine.texture;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

/**
 * Represents texture data where each rgba value is a byte.
 * */
public class TextureDataf extends AbstractTextureData<Float, FloatBuffer> {

    public TextureDataf(int width, int height, int channels, int format) {
        this(width, height, channels, format, generateTextureBuffer(width, height, channels));
    }

    public static FloatBuffer generateTextureBuffer(int width, int height, int channels) {
        return MemoryUtil.memAllocFloat(width * height * channels);
    }

    public TextureDataf(int width, int height, int channels, int format, FloatBuffer data) {
        super(width, height, channels, format, data);
    }

    @Override
    protected Float red(int index) {
        return data.get(index);
    }

    @Override
    protected Float green(int index) {
        return data.get(index);
    }

    @Override
    protected Float blue(int index) {
        return data.get(index);
    }

    @Override
    protected Float alpha(int index) {
        return data.get(index);
    }

    @Override
    protected void putRed(int index, Float red) {
        data.put(index, red);
    }

    @Override
    protected void putGreen(int index, Float green) {
        data.put(index, green);
    }

    @Override
    protected void putBlue(int index, Float blue) {
        data.put(index, blue);
    }

    @Override
    protected void putAlpha(int index, Float alpha) {
        data.put(index, alpha);
    }

    @Override
    public String toString() {
        return "TextureDataf{" +
                "width=" + width +
                ", height=" + height +
                ", channels=" + channels +
                '}';
    }

}

package com.branwilliams.bundi.engine.texture;

import com.branwilliams.bundi.engine.util.ColorUtils;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

/**
 * Represents texture data where each rgba value is a byte.
 * */
public class TextureData extends AbstractTextureData<Byte, ByteBuffer> {

    public TextureData(int width, int height, int channels, int format) {
        this(width, height, channels, format, generateTextureBuffer(width, height, channels));
    }

    public static ByteBuffer generateTextureBuffer(int width, int height, int channels) {
        return MemoryUtil.memAlloc(width * height * channels);
    }

    public TextureData(int width, int height, int channels, int format, ByteBuffer data) {
        super(width, height, channels, format, data);
    }

    @Override
    protected Byte red(int index) {
        return data.get(index);
    }

    @Override
    protected Byte green(int index) {
        return data.get(index);
    }

    @Override
    protected Byte blue(int index) {
        return data.get(index);
    }

    @Override
    protected Byte alpha(int index) {
        return data.get(index);
    }

    @Override
    protected void putRed(int index, Byte red) {
        data.put(index, red);
    }

    @Override
    protected void putGreen(int index, Byte green) {
        data.put(index, green);
    }

    @Override
    protected void putBlue(int index, Byte blue) {
        data.put(index, blue);
    }

    @Override
    protected void putAlpha(int index, Byte alpha) {
        data.put(index, alpha);
    }

    public int getPixel(int x, int y) {
        return getPixel(x, y, (r, g, b, a) -> ColorUtils.toARGB(a, r, g, b));
    }

    @Override
    public String toString() {
        return "ByteTextureData{" +
                "width=" + width +
                ", height=" + height +
                ", channels=" + channels +
                '}';
    }

}

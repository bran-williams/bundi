package com.branwilliams.bundi.engine.texture;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.util.ColorUtils;
import com.branwilliams.bundi.engine.util.Mathf;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

/**
 * Represents an images data. This object holds the data for an image. This format is perfect for uploading to
 * OpenGl.
 * */
public class TextureData implements Destructible {

    public interface PixelConsumer {
        void consume(TextureData src, int x, int y, int pixel);
    }

    private final int width;

    private final int height;

    /** The number of channels this image data contains for each pixel.
     *  e.g. channels = 4 for ARGB and 3 for RGB. */
    private final int channels;

    /** The format of this image data. Used to tell OpenGL what format to expect the data. */
    private final int format;

    private ByteBuffer data;

    public TextureData(int width, int height, int channels, int format) {
        this(width, height, channels, format, generateTextureBuffer(width, height, channels));
    }

    public static ByteBuffer generateTextureBuffer(int width, int height, int channels) {
        ByteBuffer buffer = MemoryUtil.memAlloc(width * height * channels);
        return buffer;
    }

    public TextureData(int width, int height, int channels, int format, ByteBuffer data) {
        this.width = width;
        this.height = height;
        this.channels = channels;
        this.format = format;
        this.data = data;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getChannels() {
        return channels;
    }

    public int getFormat() {
        return format;
    }

    /**
     * Calculates the bytebuffer index for the x,y pixel position. For an image formatted in rgba format, the values
     * are going to be formmated in the following: <br/>
     * <pre>
     * index = getIndex(pixelX, pixelY);
     * byte r = index;
     * byte g = index + 1;
     * byte b = index + 2;
     * byte a = index + 3;
     * </pre>
     * All pixel coordinates are clamped to the edge of this texture data.
     * @return The index for the provided x and y pixel coordinates.
     * */
    public int getIndex(int x, int y) {
        return getIndex(width, height, channels, x, y);
    }

    public static int getIndex(int width, int height, int channels, int x, int y) {
        // Clamp pixel coordinates to the edges
        x = Mathf.clamp(x, 0, width - 1);
        y = Mathf.clamp(y, 0, height - 1);
        return (x + y * width) * channels;
    }

    /**
     * Gets the pixel information at the x,y value within this image.
     * This is formatted as a hexadecimal color, e.g. 0xAARRGGBB.
     * @return The color of the pixel at the given x, y value.
     * */
    public int getPixel(int x, int y) {
        byte r = 0;
        byte g = 0;
        byte b = 0;
        byte a = 0;
        int index = getIndex(x, y);
        switch (channels) {
            case 1:
                r = data.get(index);
                break;
            case 2:
                r = data.get(index);
                a = data.get(index + 1);
                break;
            case 3:
                r = data.get(index);
                g = data.get(index + 1);
                b = data.get(index + 2);
                break;
            case 4:
                r = data.get(index);
                g = data.get(index + 1);
                b = data.get(index + 2);
                a = data.get(index + 3);
                break;
        }
        return ColorUtils.toARGB(a, r, g, b);
    }

    /**
     * Sets the r,g,b,a values for the pixel located in the x,y position.
     * <br/> <br/>
     * The x position will be clamped to 0 ~ width - 1
     * <br/>
     * The y position will be clamped to 0 ~ height - 1
     * <br/> <br/>
     * If this texture contains one channel, then the red value is updated and if it has two channels, then the r, a
     * values are updated.. and so on and so forth.
     * */
    public void setPixel(int x, int y, byte r, byte g, byte b, byte a) {
        // Clamp pixel coordinates to the edges
        x = Mathf.clamp(x, 0, width - 1);
        y = Mathf.clamp(y, 0, height - 1);

        int index = getIndex(x, y);
        switch (channels) {
            case 1:
                data.put(index, r);
                break;
            case 2:
                data.put(index, r);
                data.put(index + 1, a);
                break;
            case 3:
                data.put(index, r);
                data.put(index + 1, g);
                data.put(index + 2, b);
                break;
            case 4:
                data.put(index, r);
                data.put(index + 1, g);
                data.put(index + 2, b);
                data.put(index + 3, a);
                break;
        }
    }

    public ByteBuffer getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ImageData{" +
                "width=" + width +
                ", height=" + height +
                ", channels=" + channels +
                '}';
    }

    @Override
    public void destroy() {
        MemoryUtil.memFree(data);
        data = null;
    }

    public void forEachPixel(PixelConsumer consumer) {
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                consumer.consume(this, x, y, getPixel(x, y));
            }
        }
    }
}

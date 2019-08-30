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

    private final int width;

    private final int height;

    /** The number of channels this image data contains for each pixel.
     *  e.g. channels = 4 for ARGB and 3 for RGB. */
    private final int channels;

    /** The format of this image data. Used to tell OpenGL what format to expect the data. */
    private final int format;

    private ByteBuffer data;

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
        // Clamp pixel coordinates to the edges
        x = Mathf.clamp(x, width - 1, 0);
        y = Mathf.clamp(y, height - 1, 0);
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
}

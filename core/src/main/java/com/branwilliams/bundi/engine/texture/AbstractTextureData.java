package com.branwilliams.bundi.engine.texture;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.util.ColorUtils;
import com.branwilliams.bundi.engine.util.Mathf;
import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * Represents an images data. This object holds the data for an image. This format is perfect for uploading to
 * OpenGl.
 * */
public abstract class AbstractTextureData <PixelType extends Number, BufferType extends Buffer>
        implements Destructible {

    public interface PixelConsumer <PixelType extends Number, BufferType extends Buffer> {
        void consume(AbstractTextureData<PixelType, BufferType> src, int x, int y, PixelType r, PixelType g,
                     PixelType b, PixelType a);
    }

    public interface PixelTransformer <T, PixelType extends Number> {
        T transform(PixelType r, PixelType g, PixelType b, PixelType a);
    }

    protected final int width;

    protected final int height;

    /** The number of channels this image data contains for each pixel.
     *  e.g. channels = 4 for ARGB and 3 for RGB. */
    protected final int channels;

    /** The format of this image data. Used to tell OpenGL what format to expect the data. */
    protected final int format;

    protected BufferType data;

    public AbstractTextureData(int width, int height, int channels, int format, BufferType data) {
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

    protected abstract PixelType red(int index);

    protected abstract PixelType green(int index);

    protected abstract PixelType blue(int index);

    protected abstract PixelType alpha(int index);

    protected abstract void putRed(int index, PixelType red);

    protected abstract void putGreen(int index, PixelType green);

    protected abstract void putBlue(int index, PixelType blue);

    protected abstract void putAlpha(int index, PixelType alpha);

    /**
     * Gets the pixel information at the x,y value within this image.
     * This is formatted as a hexadecimal color, e.g. 0xAARRGGBB.
     * @return The color of the pixel at the given x, y value.
     * */
    public void getPixel(int x, int y, PixelConsumer<PixelType, BufferType> consumer) {
        PixelType r = null;
        PixelType g = null;
        PixelType b = null;
        PixelType a = null;
        int index = getIndex(x, y);
        switch (channels) {
            case 1:
                r = red(index);
                break;
            case 2:
                r = red(index);
                a = alpha(index + 1);
                break;
            case 3:
                r = red(index);
                g = green(index + 1);
                b = blue(index + 2);
                break;
            case 4:
                r = red(index);
                g = green(index + 1);
                b = blue(index + 2);
                a = alpha(index + 3);
                break;
        }
        consumer.consume(this, x, y, r, g, b, a);
    }

    /**
     * Gets the pixel information at the x,y value within this image.
     * This is formatted as a hexadecimal color, e.g. 0xAARRGGBB.
     * @return The color of the pixel at the given x, y value.
     * */
    public <T> T getPixel(int x, int y, PixelTransformer<T, PixelType> transformer) {
        PixelType r = null;
        PixelType g = null;
        PixelType b = null;
        PixelType a = null;
        int index = getIndex(x, y);
        switch (channels) {
            case 1:
                r = red(index);
                break;
            case 2:
                r = red(index);
                a = alpha(index + 1);
                break;
            case 3:
                r = red(index);
                g = green(index + 1);
                b = blue(index + 2);
                break;
            case 4:
                r = red(index);
                g = green(index + 1);
                b = blue(index + 2);
                a = alpha(index + 3);
                break;
        }

        return transformer.transform(r, g, b, a);
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
    public void setPixel(int x, int y, PixelType r, PixelType g, PixelType b, PixelType a) {
        // Clamp pixel coordinates to the edges
        x = Mathf.clamp(x, 0, width - 1);
        y = Mathf.clamp(y, 0, height - 1);

        int index = getIndex(x, y);
        switch (channels) {
            case 1:
                putRed(index, r);
                break;
            case 2:
                putRed(index, r);
                putAlpha(index + 1, a);
                break;
            case 3:
                putRed(index, r);
                putGreen(index + 1, g);
                putBlue(index + 2, b);
                break;
            case 4:
                putRed(index, r);
                putGreen(index + 1, g);
                putBlue(index + 2, b);
                putAlpha(index + 3, a);
                break;
        }
    }

    public void forEachPixel(PixelConsumer<PixelType, BufferType> consumer) {
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                getPixel(x, y, consumer);
            }
        }
    }

    public BufferType getData() {
        return data;
    }


    @Override
    public String toString() {
        return "AbstractTextureData{" +
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

package com.branwilliams.bundi.engine.util;

import com.branwilliams.bundi.engine.texture.TextureData;
import com.branwilliams.bundi.engine.texture.DownsampledTextureData;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.branwilliams.bundi.engine.util.ColorUtils.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author Brandon
 * @since August 04, 2019
 */
public enum TextureUtils {
    INSTANCE;

    /**
     * Stitches the provided {@link TextureData ImageDatas} horizontally with no padding.
     *
     * @see TextureUtils#stitchedTextures(int, TextureData...)
     * */
    public static TextureData stitchedTextures(TextureData... pngs) {
        return stitchedTextures(0, pngs);
    }

    /**
     * Stitches the provided {@link TextureData ImageDatas} horizontally into one image.
     * The output image is RGBA format with 4 channels.
     *
     * @param padding The padding between each image in the output image.
     * @param images The images to stitch together.
     * @return An {@link TextureData} with the images provided stitched horizontally.
     * */
    public static TextureData stitchedTextures(int padding, TextureData... images) {
        if (images == null || images.length < 2) {
            throw new IllegalArgumentException("The number of pngs must be at least two!");
        }

        int totalWidth = 0;
        int totalHeight = 0;
        int channels = images[0].getChannels();

        // Find a total width and height for the stitched png.
        for (TextureData img : images) {
            if (img.getChannels() < 3) {
                throw new IllegalArgumentException("All pngs must have at least 3 channels!");
            }
            totalWidth += img.getWidth() + padding;
            if (img.getHeight() > totalHeight) {
                totalHeight = img.getHeight();
            }
        }

        int xoffset = 0;
        int outputChannels = 4;
        ByteBuffer buffer = MemoryUtil.memAlloc(outputChannels * totalWidth * totalHeight);

        for (TextureData img : images) {
            ByteBuffer imageBuffer = img.getData();

            for (int x = 0; x < img.getWidth(); x++) {
                for (int y = 0; y < img.getHeight(); y++) {
                    int imageIndex = (x + y * img.getWidth()) * img.getChannels();
                    int stitchedIndex = ((xoffset + x) + y * totalWidth) * channels;

                    byte r = imageBuffer.get(imageIndex);
                    byte g = imageBuffer.get(imageIndex + 1);
                    byte b = imageBuffer.get(imageIndex + 2);
                    byte a = (byte) 255;
                    if (img.getChannels() == 4)
                        a = imageBuffer.get(imageIndex + 3);

                    buffer.put(stitchedIndex, r);
                    buffer.put(stitchedIndex + 1, g);
                    buffer.put(stitchedIndex + 2, b);
                    buffer.put(stitchedIndex + 3, a);
                }
            }
            // Put padding
            for (int y = 0; y < totalHeight; y++) {
                for (int i = 0; i < padding; i++) {
                    int stitchedIndex = ((xoffset + (img.getWidth() - 1) + i) + y * totalWidth) * channels;
                    buffer.put(stitchedIndex, (byte) 0);
                    buffer.put(stitchedIndex + 1, (byte) 0);
                    buffer.put(stitchedIndex + 2, (byte) 0);
                    buffer.put(stitchedIndex + 3, (byte) 0);
                }
            }
            xoffset += img.getWidth() + padding;
        }
        buffer.clear();
        return new TextureData(totalWidth, totalHeight, outputChannels, getFormatFromChannels(outputChannels), buffer);
    }

//    public static TextureData flip(TextureData textureData) {
//        if (textureData == null) {
//            throw new IllegalArgumentException("ImageData must not be null!");
//        }
//
//        ByteBuffer buffer = MemoryUtil.memAlloc(textureData.getChannels() * textureData.getWidth() * textureData.getHeight());
//
//        int pixelIndex;
//
//        for (int x = textureData.getWidth() - 1; x >= 0; x--) {
//            for (int y = textureData.getHeight() - 1; y >= 0; y--) {
//
//                // Calculate the index and r,g,b,a values for the image.
//                pixelIndex = textureData.getIndex(x, y);
//
//                byte r = textureData.getData().get(pixelIndex);
//                byte g = textureData.getData().get(pixelIndex + 1);
//                byte b = textureData.getData().get(pixelIndex + 2);
//                byte a = (byte) 0;
//                if (textureData.getChannels() == 4)
//                    a = textureData.getData().get(pixelIndex + 3);
//
//                // imageIndex = (x + y * imageData.getWidth()) * imageData.getChannels();
//
//                buffer.put(r);
//                buffer.put(g);
//                buffer.put(b);
//                if (textureData.getChannels() == 4)
//                    buffer.put(a);
//            }
//        }
//        buffer.flip();
//
//        return new TextureData(textureData.getWidth(), textureData.getHeight(), textureData.getChannels(),
//                getFormatFromChannels(textureData.getChannels()), buffer);
//    }
//
//
//    public static TextureData flipHorizontal(TextureData textureData) {
//        if (textureData == null) {
//            throw new IllegalArgumentException("ImageData must not be null!");
//        }
//
//        ByteBuffer buffer = MemoryUtil.memAlloc(textureData.getChannels() * textureData.getWidth() * textureData.getHeight());
//
//        int pixelIndex;
//        for (int x = textureData.getWidth() - 1; x >= 0; x--) {
//            for (int y = 0; y < textureData.getHeight(); y++) {
//
//                // Calculate the index and r,g,b,a values for the image.
//                pixelIndex = textureData.getIndex(x, y);
//                byte r = textureData.getData().get(pixelIndex);
//                byte g = textureData.getData().get(pixelIndex + 1);
//                byte b = textureData.getData().get(pixelIndex + 2);
//                byte a = (byte) 0;
//                if (textureData.getChannels() == 4)
//                    a = textureData.getData().get(pixelIndex + 3);
//
//                putARGB(buffer, pixelIndex, r, g, b, a, textureData.getChannels());
//
//            }
//        }
//        buffer.flip();
//        return new TextureData(textureData.getWidth(), textureData.getHeight(), textureData.getChannels(),
//                getFormatFromChannels(textureData.getChannels()), buffer);
//    }
//
//    public static TextureData flipVertical(TextureData textureData) {
//        if (textureData == null) {
//            throw new IllegalArgumentException("ImageData must not be null!");
//        }
//
//        ByteBuffer buffer = MemoryUtil.memAlloc(textureData.getChannels() * textureData.getWidth() * textureData.getHeight());
//
//        int pixelIndex;
//        for (int x = 0; x < textureData.getWidth(); x++) {
//            for (int y = textureData.getHeight() - 1; y >= 0; y--) {
//
//                // Calculate the index and r,g,b,a values for the image.
//                pixelIndex = textureData.getIndex(x, y);
//
//                byte r = textureData.getData().get(pixelIndex);
//                byte g = textureData.getData().get(pixelIndex + 1);
//                byte b = textureData.getData().get(pixelIndex + 2);
//                byte a = (byte) 0;
//                if (textureData.getChannels() == 4)
//                    a = textureData.getData().get(pixelIndex + 3);
//
//                putARGB(buffer, pixelIndex, r, g, b, a, textureData.getChannels());
//            }
//        }
//        buffer.flip();
//        return new TextureData(textureData.getWidth(), textureData.getHeight(), textureData.getChannels(),
//                getFormatFromChannels(textureData.getChannels()), buffer);
//    }

    /**
     * Creates a new {@link TextureData} with each pixel replaced by the function modifier. Note: the input texture data
     * will not be destroyed, so take special consideration of this when using this function.
     *
     *
     * @param textureData The texture data to modify.
     * @param modifier The function which takes input ARGB values and outputs new ARGB values.
     * @return A new, modified version of the input texture data.
     * */
    public static TextureData modify(TextureData textureData, Function<Integer, Integer> modifier) {
        if (textureData == null) {
            throw new IllegalArgumentException("ImageData must not be null!");
        }

        ByteBuffer buffer = MemoryUtil.memAlloc(textureData.getChannels() * textureData.getWidth() * textureData.getHeight());
        int channels = textureData.getChannels();

        int pixelIndex;
        for (int x = 0; x < textureData.getWidth(); x++) {
            for (int y = 0; y < textureData.getHeight(); y++) {
                // Calculate the index and r,g,b,a values for the image.
                int pixelARGB = textureData.getPixel(x, y);

                // Take  the argb formatted values from first and second and produce one output color.
                int modifiedARGB = modifier.apply(pixelARGB);

                pixelIndex = textureData.getIndex(x, y);
                putARGB(buffer, pixelIndex, modifiedARGB, channels);
            }
        }
        buffer.flip();
        return new TextureData(textureData.getWidth(), textureData.getHeight(), textureData.getChannels(),
                getFormatFromChannels(textureData.getChannels()), buffer);
    }


    /**
     * Combines the image data between two {@link TextureData}s to produce one ImageData.
     * @param first The first ImageData that is used.
     * @param second The second ImageData that is used.
     * @param combiner This BiFunction produces one ARGB value given two ARGB values from the two images.
     * */
    public static TextureData combine(TextureData first, TextureData second, int desiredChannels, BiFunction<Integer, Integer, Integer> combiner) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("ImageData must not be null!");
        }
        if (first.getWidth() != second.getWidth() && first.getHeight() != second.getHeight()) {
            throw new IllegalArgumentException("Both ImageData must have the same dimensions!");
        }

        int channels = Math.max(desiredChannels, Math.max(first.getChannels(), second.getChannels()));
        ByteBuffer buffer = MemoryUtil.memAlloc(channels * first.getWidth() * first.getHeight());

        int pixelIndex;
        for (int x = 0; x < first.getWidth(); x++) {
            for (int y = 0; y < first.getHeight(); y++) {

                // Calculate the index and r,g,b,a values for the first image.
                int firstARGB = first.getPixel(x, y);

                // Calculate the index and r,g,b,a values for the second image.
                int secondARGB = second.getPixel(x, y);

                // Take the argb formatted values from first and second and produce one output color.
                int combinedARGB = combiner.apply(firstARGB, secondARGB);

                // Potentially a new number of channels, so the index must be calculated manually.
                pixelIndex = (x + y * first.getWidth()) * channels;

                putARGB(buffer, pixelIndex, combinedARGB, channels);
            }
        }
        buffer.flip();
        return new TextureData(first.getWidth(), first.getHeight(), channels, getFormatFromChannels(channels), buffer);
    }

    /**
     *
     * @return The OpenGL internal format used for the number of channels provided. 0 if the channels are not within the
     * range of 1 <= channels <= 4
     * */
    public static int getFormatFromChannels(int channels) {
        int format = 0;
        switch (channels) {
            case 1:
                format = GL_RED;
                break;
            case 2:
                format = GL30.GL_RG;
                break;
            case 3:
                format = GL_RGB;
                break;
            case 4:
                format = GL_RGBA;
                break;
        }
        return format;
    }

    /**
     * @return The number of channels given an OpenGL format.
     * */
    public static int getChannelsFromFormat(int format) {
        switch (format) {
            case GL_RED:
                return 1;
            case GL30.GL_RG:
                return 2;
            case GL_RGB:
                return 3;
            case GL_RGBA:
                return 4;
        }
        return -1;
    }

    public static void putARGB(ByteBuffer buffer, int index, int argb, int channels) {
        putARGB(buffer, index, red(argb), green(argb), blue(argb), alpha(argb), channels);
    }

    /**
     * Puts the red, green blue, and alpha values into the bytebuffer at the index provided with special consideration
     * of the channels.
     * */
    public static void putARGB(ByteBuffer buffer, int index, byte r, byte g, byte b, byte a, int channels) {
        // always red
        buffer.put(index, r);

        switch (channels) {
            case 1:
                break;
            case 2:
                buffer.put(index + 1, g);
                break;
            case 3:
                buffer.put(index + 1, g);
                buffer.put(index + 2, b);
                break;
            case 4:
                buffer.put(index + 1, g);
                buffer.put(index + 2, b);
                buffer.put(index + 3, a);
        }
    }

    /**
     * @return The max mipmaps levels possible given some images width, height, and depth.
     * */
    public static int getMaxMipMaps(int width, int height, int depth) {
        return 1 + Mathf.floor(Mathf.log(2, Math.max(Math.max(width, height), depth)));
    }

    /**
     * @return The max mipmaps levels possible given some images width, height, and depth.
     * */
    public static int getMaxMipMaps(TextureData textureData) {
        return getMaxMipMaps(textureData.getWidth(), textureData.getHeight(), 1);
    }

    public static DownsampledTextureData downsample(TextureData textureData) {
        return downsample(textureData, getMaxMipMaps(textureData));
    }

    public static DownsampledTextureData downsample(TextureData textureData, int maxSamples) {
        return downsample(textureData, maxSamples, Image.SCALE_AREA_AVERAGING);
    }

    /**
     *
     * */
    public static DownsampledTextureData downsample(TextureData textureData, int maxSamples, int scaleHint) {
        BufferedImage img = toBufferedImage(textureData);

        List<TextureData> downsampled = new ArrayList<>();

        int halfWidth = textureData.getWidth();
        int halfHeight = textureData.getHeight();

        int samples = 0;
        while (samples < maxSamples) {
            // If the dimensions are already reduced to 1x1, then stop the loop
            if (halfWidth <= 1 && halfHeight <= 1) {
                break;
            }
            // Half the width if possible
            if (halfWidth > 1)
                halfWidth = Mathf.floor(halfWidth * 0.5F);

            // Half the height if possible
            if (halfHeight > 1)
                halfHeight = Mathf.floor(halfHeight * 0.5F);

            ByteBuffer downsampledBuffer = toByteBuffer(img.getScaledInstance(halfWidth, halfHeight, scaleHint));

            downsampled.add(new TextureData(halfWidth, halfHeight, textureData.getChannels(), textureData.getFormat(), downsampledBuffer));
            samples++;
        }

        return new DownsampledTextureData(textureData, downsampled.toArray(new TextureData[0]));
    }

    /**
     * Creates a buffered image from the provided TextureData.
     * */
    public static BufferedImage toBufferedImage(TextureData textureData) {
        BufferedImage img = new BufferedImage(textureData.getWidth(), textureData.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < textureData.getWidth(); x++) {
            for (int y = 0; y < textureData.getHeight(); y++) {
                img.setRGB(x, y, textureData.getPixel(x, y));
            }
        }

        return img;
    }

    /**
     * Converts the array of pixels to a byte buffer for use by a texture.
     * Assuming the pixels are RGBA.
     * */
    public static ByteBuffer toByteBuffer(int[] pixels, int width, int height) {
        ByteBuffer buffer = MemoryUtil.memAlloc(width * height * 4);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = pixels[y * width + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                buffer.put((byte) (pixel & 0xFF));
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }
        buffer.flip();

        return buffer;
    }

    /**
     * Creates a bytebuffer with the r, g, b, and a values.
     * */
    public static ByteBuffer toByteBuffer(int r, int g, int b, int a, int width, int height) {
        ByteBuffer buffer = MemoryUtil.memAlloc(width * height * 4);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                buffer.put((byte) r);
                buffer.put((byte) g);
                buffer.put((byte) b);
                buffer.put((byte) a);
            }
        }
        buffer.flip();
        return buffer;
    }

    public static ByteBuffer toByteBuffer(Image img) {
        return toByteBuffer(toBufferedImage(img));
    }

    public static ByteBuffer toByteBuffer(BufferedImage img) {
        ByteBuffer buffer = MemoryUtil.memAlloc(img.getWidth() * img.getHeight() * img.getColorModel().getNumComponents());

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int argb = img.getRGB(x, y);
                buffer.put(red(argb));
                buffer.put(green(argb));
                buffer.put(blue(argb));
                buffer.put(alpha(argb));
            }
        }

        buffer.flip();
        return buffer;
    }

    /**
     * Converts a given Image into a BufferedImage
     *
     * @param img The Image to be converted
     * @return The converted BufferedImage
     */
    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }
}

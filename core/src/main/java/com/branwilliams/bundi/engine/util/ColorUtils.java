package com.branwilliams.bundi.engine.util;

import org.joml.Vector3f;
import org.joml.Vector4f;

import java.awt.*;
import java.util.Random;

public enum ColorUtils {
    INSTANCE;

    private static final Random random = new Random();

    private ColorUtils() {}

    /**
     * @return A {@link Color} object with effects applied to it based on the boolean values given.
     * */
    public static Color getColorWithEffects(Color color, boolean mouseOver, boolean mouseDown) {
        return mouseOver ? (mouseDown ? color.darker() : color.brighter()) : color;
    }

    /**
     * @return A hexadecimal color with effects applied to it based on the boolean values given.
     * */
    public static int getColorWithEffects(int color, boolean mouseOver, boolean mouseDown) {
        return mouseOver ? (mouseDown ? darker(color, 0.2F) : brighter(color, 0.2F)) : color;
    }

    /**
     * @return A hexadecimal color that is darkened by the scale amount provided.
     */
    public static int darker(int color, float scale) {
        int red = (color >> 16 & 255), green =  (color >> 8 & 255), blue = (color & 255), alpha = (color >> 24 & 0xff);
        red = (int) (red - red * scale);
        red = Math.min(red, 255);
        green = (int) (green - green * scale);
        green = Math.min(green, 255);
        blue = (int) (blue - blue * scale);
        blue = Math.min(blue, 255);
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    /**
     * @return A hexadecimal color that is brightened by the scale amount provided.
     * */
    public static int brighter(int color, float scale) {
        int red = (color >> 16 & 255), green =  (color >> 8 & 255), blue = (color & 255), alpha = (color >> 24 & 0xff);
        red = (int) (red + red * scale);
        red = Math.min(red, 255);
        green = (int) (green + green * scale);
        green = Math.min(green, 255);
        blue = (int) (blue + blue * scale);
        blue = Math.min(blue, 255);
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    /**
     * This isn't mine, but it's the most beautiful random color generator I've seen. Reminds me of easter.
     * */
    public static Color getRandomColor(int saturationRandom, float luminance) {
        final float hue = random.nextFloat();
        final float saturation = (random.nextInt(saturationRandom) + (float) saturationRandom) / (float) saturationRandom + (float) saturationRandom;
        return Color.getHSBColor(hue, saturation, luminance);
    }

    /**
     * This isn't mine, but it's the most beautiful random color generator I've seen. Reminds me of easter.
     * */
    public static Color getRandomColor() {
        return new Color(random.nextFloat(), random.nextFloat(), random.nextFloat());
    }

    /**
     * Creates a random pastel color.
     * */
    public static Color getRandomPastelColor() {
        return getRandomColor(1000, 0.6F);
    }


    /**
     * Converts the provided color to a vector3f. The values are divided by 255F to convert the rgb values to the range
     * 0 ~ 1. <br/> <br/>
     *
     * The color values are mapped as follows: <br/>
     * vector3f.x = color.r <br/>
     * vector3f.y = color.g <br/>
     * vector3f.z = color.b <br/>
     * vector3f.w = color.a
     *
     * */
    public static Vector4f toVector4(Color color) {
        return new Vector4f((float) color.getRed() / 255F, (float) color.getGreen() / 255F, (float) color.getBlue() / 255F, (float) color.getAlpha() / 255F);
    }

    /**
     * Converts the provided color to a vector3f. The values are divided by 255F to convert the rgb values to the range
     * 0 ~ 1. <br/> <br/>
     *
     * The color values are mapped as follows: <br/>
     * vector3f.x = color.r <br/>
     * vector3f.y = color.g <br/>
     * vector3f.z = color.b
     *
     * */
    public static Vector3f toVector3(Color color) {
        return new Vector3f((float) color.getRed() / 255F, (float) color.getGreen() / 255F, (float) color.getBlue() / 255F);
    }

    /**
     * Converts the provided color to a vector3f. The values are divided by 255F to convert the rgb values to the range
     * 0 ~ 1. <br/> <br/>
     *
     * The color values are mapped as follows: <br/>
     * vector3f.x = color.r <br/>
     * vector3f.y = color.g <br/>
     * vector3f.z = color.b
     * @param multiplier The r, g, b values are multiplied by this value before being divided by the value 255F.
     * */
    public static Vector3f toVector3(Color color, float multiplier) {
        return new Vector3f((color.getRed() * multiplier) / 255F, (color.getGreen() * multiplier) / 255F, (color.getBlue() * multiplier) / 255F);
    }


    /**
     * @return A hexadecimal representation of the rgba color values specified (between 0 - 1)
     * */
    public static int toRGB(float r, float g, float b) {
        return ((int) (r * 255F) << 16) | ((int) (g * 255F) << 8) | (int) (b * 255F);
    }


    /**
     * Calculates a hexadecimal representation of the provided rgba color values. This format is in the following:
     * 0xAARRGGBB. <br/>
     * The rgba values are assumed to be between 0~1.
     * @return A hexadecimal representation of the rgba color values specified (between 0 - 1).
     * */
    public static int toARGB(double r, double g, double b, double a) {
        return ((int) (a * 255F) << 24) | ((int) (r * 255F) << 16) | ((int) (g * 255F) << 8) | (int) (b * 255F);
    }

    /**
     * Calculates a hexadecimal representation of the provided rgba color values. This format is in the following:
     * 0xAARRGGBB. <br/>
     * The rgba values are assumed to be between 0~1.
     * @return A hexadecimal representation of the rgba color values specified (between 0 - 1).
     * */
    public static int toARGB(float r, float g, float b, float a) {
        return ((int) (a * 255F) << 24) | ((int) (r * 255F) << 16) | ((int) (g * 255F) << 8) | (int) (b * 255F);
    }


    /**
     * Calculates a hexadecimal representation of the provided rgba color values. This format is in the following:
     * 0xAARRGGBB. <br/>
     * */
    public static int toARGB(byte a, byte r, byte g, byte b) {
        return ((0xFF & a) << 24) | ( (0xFF & r) << 16) | ((0xFF & g) << 8) | (0xFF & b);
    }

    /**
     * Retrieves the red component of an integer whose hex representation is
     * 0xAARRGGBB.
     * */
    public static byte red(int color) {
        return (byte) ((color >> 16) & 0xFF);
    }

    /**
     * Retrieves the green component of an integer whose hex representation is
     * 0xAARRGGBB.
     * */
    public static byte green(int color) {
        return (byte) ((color >> 8) & 0xFF);
    }

    /**
     * Retrieves the blue component of an integer whose hex representation is
     * 0xAARRGGBB.
     * */
    public static byte blue(int color) {
        return (byte) (color & 0xFF);
    }

    /**
     * Retrieves the alpha component of an integer whose hex representation is
     * 0xAARRGGBB.
     * */
    public static byte alpha(int color) {
        return (byte) ((color >> 24) & 0xFF);
    }

    /**
     * Retrieves the red component of an integer whose hex representation is
     * 0xAARRGGBB.
     * */
    public static float redf(int color) {
        return (float) ((color >> 16) & 0xFF);
    }

    /**
     * Retrieves the green component of an integer whose hex representation is
     * 0xAARRGGBB.
     * */
    public static float greenf(int color) {
        return (float) ((color >> 8) & 0xFF);
    }

    /**
     * Retrieves the blue component of an integer whose hex representation is
     * 0xAARRGGBB.
     * */
    public static float bluef(int color) {
        return (float) (color & 0xFF);
    }

    /**
     * Retrieves the alpha component of an integer whose hex representation is
     * 0xAARRGGBB.
     * */
    public static float alphaf(int color) {
        return (float) ((color >> 24) & 0xFF);
    }
}

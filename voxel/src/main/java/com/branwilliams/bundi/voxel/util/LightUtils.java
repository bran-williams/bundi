package com.branwilliams.bundi.voxel.util;

import java.util.function.BiFunction;

import static com.branwilliams.bundi.voxel.VoxelConstants.*;

/**
 * Light is stored as follows in 16 bits:
 * <br/> <br/>
 * <pre>
 * 0000 rrrr gggg bbbb
 * </pre>
 *
 * This allows 0-15 values for R, G, and B channels.
 * */
public final class LightUtils {

    private LightUtils() {}

   public static int pack(int r, int g, int b) {
        return Math.min(MAX_LIGHT_RED, Math.max(MIN_LIGHT, r)) << 8
                | Math.min(MAX_LIGHT_GREEN, Math.max(MIN_LIGHT, g)) << 4
                | Math.min(MAX_LIGHT_BLUE, Math.max(MIN_LIGHT, b));
    }

    public static int unpackRed(int light) {
        return (light & 0x000F00) >>> 8;
    }

    public static int unpackGreen(int light) {
        return (light & 0x0000F0) >> 4;
    }

    public static int unpackBlue(int light) {
        return light & 0x00000F;
    }

    public static boolean hasLight(int light) {
        return  light > ZERO_LIGHT;
    }

    public static boolean hasNoLight(int light) {
        return  light == ZERO_LIGHT;
    }

    public static int addRGBToLight(int light, int r, int g, int b) {
        return pack(unpackRed(light) + r, unpackGreen(light) + g, unpackBlue(light) + b);
    }

    public static int decrementByAmount(int light, int amount) {
        return addRGBToLight(light, -amount, -amount, -amount);
    }

    /**
     * Returns the opposite of zeroChannels - will compare brightest with toZero and will set the r, g, and b values if
     * brightest has a value lower than toZero.
     * */
    public static int getRemovedChannels(int lightToRemove, int light) {
        int r = unpackRed(light);
        int g = unpackGreen(light);
        int b = unpackBlue(light);

        if (unpackRed(lightToRemove) < r) {
            r = MIN_LIGHT;
        }
        if (unpackGreen(lightToRemove) < g) {
            g = MIN_LIGHT;
        }
        if (unpackBlue(lightToRemove) < b) {
            b = MIN_LIGHT;
        }

        return pack(r, g, b);
    }

    /**
     * Removes rgb values from 'light' if 'lightToRemove' has larger rgb values
     * */
    public static int removeLight(int lightToRemove, int light) {
        int r = unpackRed(light);
        int g = unpackGreen(light);
        int b = unpackBlue(light);

        if (unpackRed(lightToRemove) >= r) {
            r = MIN_LIGHT;
        }
        if (unpackGreen(lightToRemove) >= g) {
            g = MIN_LIGHT;
        }
        if (unpackBlue(lightToRemove) >= b) {
            b = MIN_LIGHT;
        }

        return pack(r, g, b);
    }

    public static int maxLight(int light, int otherLight) {
        return pack(Math.max(unpackRed(light), unpackRed(otherLight)),
                Math.max(unpackGreen(light), unpackGreen(otherLight)),
                Math.max(unpackBlue(light), unpackBlue(otherLight)));
    }

    public static boolean isAnyChannelBrighterThan(int light, int otherLight) {
        return isAnyChannelBlank((l, o) -> l > o, light, otherLight);
    }

    public static boolean isAnyChannelBrighterOrEqualTo(int light, int otherLight) {
        return isAnyChannelBlank((l, o) -> l >= o, light, otherLight);
    }

    public static boolean isAnyChannelBlank(BiFunction<Integer, Integer, Boolean> comparator, int light,
                                            int otherLight) {
        return comparator.apply(unpackRed(light), unpackRed(otherLight))
                || comparator.apply(unpackGreen(light), unpackGreen(otherLight))
                || comparator.apply(unpackBlue(light), unpackBlue(otherLight));
    }

}

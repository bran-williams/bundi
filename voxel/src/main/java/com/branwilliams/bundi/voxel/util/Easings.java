package com.branwilliams.bundi.voxel.util;

/**
 * @author Brandon
 * @since January 05, 2020
 */
public class Easings {

    public static float cubicEaseInOut (float t, float b , float c, float d) {
        if ((t/=d/2) < 1) return c/2*t*t*t + b;
        return c/2*((t-=2)*t*t + 2) + b;
    }

    public static float cubicEaseIn(float t, float b, float c, float d) {
        return c*(t/=d)*t*t + b;
    }

}

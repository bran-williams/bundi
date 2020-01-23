package com.branwilliams.bundi.engine.util.noise;

/**
 * @author Brandon
 * @since January 23, 2020
 */
public interface Noise {

    double noise(double x, double y);

    double noise(double x, double y, double z);
}

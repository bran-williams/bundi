package com.branwilliams.bundi.engine.util.noise;

import com.branwilliams.bundi.engine.util.Grid2i;
import com.branwilliams.bundi.engine.util.Grid3i;
import org.joml.Vector2d;
import org.joml.Vector3d;

/**
 * @author Brandon
 * @since February 12, 2020
 */
public class LayeredNoise implements Noise {

    // When rateOfChange = 2, the layers are considered octaves.
    public static final double DEFAULT_LACUNARITY = 2D;

    public static final double DEFAULT_GAIN = 0.5D;

    private final Noise delegate;

    private int layers;

    private double lacunarity;

    private double gain;

    public LayeredNoise(Noise delegate, int layers) {
        this(delegate, layers, DEFAULT_LACUNARITY, DEFAULT_GAIN);
    }

    public LayeredNoise(Noise delegate, int layers, double lacunarity, double gain) {
        this.delegate = delegate;
        this.layers = layers;
        this.lacunarity = lacunarity;
        this.gain = gain;
    }

    @Override
    public double noise(double x, double y) {
        return fBm(x, y);
    }

    @Override
    public double noise(double x, double y, double z) {
        return fBm(x, y, z);
    }

    @Override
    public Grid2i<Double> noiseGrid2i(Vector2d origin, double noiseScale, int width, int height) {
        Grid2i<Double> grid2i = Noise.super.noiseGrid2i(origin, noiseScale, width, height);

        // Find the max noise value since this layered noise may produce noise values above 1.0.
        double maxNoiseValue = 0;
        for (double noiseValue : grid2i) {
            if (noiseValue > maxNoiseValue) {
                maxNoiseValue = noiseValue;
            }
        }

        // Normalize each value.
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                grid2i.setValue(grid2i.getValue(i, j) / maxNoiseValue, i, j);
            }
        }
        return grid2i;
    }

    @Override
    public Grid3i<Double> noiseGrid3i(Vector3d origin, double noiseScale, int width, int height, int depth) {
        Grid3i<Double> grid3i = Noise.super.noiseGrid3i(origin, noiseScale, width, height, depth);

        // Find the max noise value since this layered noise may produce noise values above 1.0.
        double maxNoiseValue = 0;
        for (double noiseValue : grid3i) {
            if (noiseValue > maxNoiseValue) {
                maxNoiseValue = noiseValue;
            }
        }

        // Normalize each value.
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                for (int k = 0; k < depth; k++) {
                    grid3i.setValue(grid3i.getValue(i, j, k) / maxNoiseValue, i, j, k);
                }
            }
        }
        return grid3i;
    }

    protected double fBm(double x, double y) {
        double noiseSum = 0;
        double amplitude = 1;
        for (int i = 0; i < layers; ++i) {
            // change in frequency and amplitude
            noiseSum += delegate.noise(x, y) * amplitude;
            x *= lacunarity;
            y *= lacunarity;
            amplitude *= gain;
        }

        return noiseSum;
    }

    protected double fBm(double x, double y, double z) {
        double noiseSum = 0;
        double amplitude = 1;
        for (int i = 0; i < layers; ++i) {
            // change in frequency and amplitude
            noiseSum += delegate.noise(x, y, z) * amplitude;
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            amplitude *= gain;
        }

        return noiseSum;
    }

    public int getLayers() {
        return layers;
    }

    public void setLayers(int layers) {
        this.layers = layers;
    }

    public double getLacunarity() {
        return lacunarity;
    }

    public void setLacunarity(double lacunarity) {
        this.lacunarity = lacunarity;
    }

    public double getGain() {
        return gain;
    }

    public void setGain(double gain) {
        this.gain = gain;
    }

    public Noise getDelegate() {
        return delegate;
    }
}

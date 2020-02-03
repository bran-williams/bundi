package com.branwilliams.cubes.builder.evaluators;

import com.branwilliams.bundi.engine.util.noise.Noise;

/**
 * @author Brandon
 * @since January 26, 2020
 */
public class NoiseIsoEvaluator implements IsoEvaluator {

    private final Noise noise;

    private float noiseScale;

    public NoiseIsoEvaluator(Noise noise, float noiseScale) {
        this.noise = noise;
        this.noiseScale = noiseScale;
    }

    @Override
    public float evaluate(float x, float y, float z, float isoValue) {
        return (float) noise.noise(x * noiseScale, y * noiseScale, z * noiseScale);
    }
}

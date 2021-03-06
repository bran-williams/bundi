package com.branwilliams.terrain.generator;

import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.bundi.engine.util.MeshUtils;
import com.branwilliams.bundi.engine.util.noise.Noise;
import com.branwilliams.bundi.engine.util.noise.OpenSimplexNoise;

import static com.branwilliams.bundi.engine.util.MeshUtils.arrayOfSize;

/**
 * Created by Brandon Williams on 10/29/2018.
 */
public class NoiseGenerator implements HeightGenerator {

    public static final float DEFAULT_NOISE_SCALE = 0.01F;

    private final Noise noise;

    private final float[] frequencies;
    private final float[] percentages;
    private final float[] noiseScale;

    public NoiseGenerator(float[] frequencies, float[] percentages) {
        this(frequencies, percentages, arrayOfSize(frequencies.length, DEFAULT_NOISE_SCALE));
    }

    public NoiseGenerator(float[] frequencies, float[] percentages, float noiseScale) {
        this(frequencies, percentages, arrayOfSize(frequencies.length, noiseScale));
    }

    public NoiseGenerator(float[] frequencies, float[] percentages, float[] noiseScale) {
        this.noise = new OpenSimplexNoise();
        this.frequencies = frequencies;
        this.percentages = percentages;
        this.noiseScale = noiseScale;
    }

    public NoiseGenerator(long seed, float[] frequencies, float[] percentages) {
        this(seed, frequencies, percentages, DEFAULT_NOISE_SCALE);
    }

    public NoiseGenerator(long seed, float[] frequencies, float[] percentages, float noiseScale) {
        this(seed, frequencies, percentages, arrayOfSize(frequencies.length, noiseScale));
    }

    public NoiseGenerator(long seed, float[] frequencies, float[] percentages, float[] noiseScale) {
        this.noise = new OpenSimplexNoise(seed);
        this.frequencies = frequencies;
        this.percentages = percentages;
        this.noiseScale = noiseScale;
    }

    @Override
    public float[][] generateHeight(float x, float z, int vertexCountX, int vertexCountZ, float amplitude) {
        if (frequencies.length != percentages.length) {
            throw new IllegalArgumentException("The number of frequencies must equal the number of percentages!");
        }
        // Ensure the edges of each tile matches.
        x = x - x * (1F / vertexCountX);
        z = z - z * (1F / vertexCountZ);

        float[][] heights = new float[vertexCountX][vertexCountZ];

        for (int i = 0; i < vertexCountX; i++) {
            heights[i] = new float[vertexCountZ];
            // Add one to ensure no zero values are given to the noise function.
            float nx = (x * vertexCountX + i + 1F);
            for (int j = 0; j < vertexCountZ; j++) {
                // Add one to ensure no zero values are given to the noise function.
                float ny = (z * vertexCountZ + j + 1F);

                float e = 0F;
                for (int k = 0; k < frequencies.length; k++) {
                    float frequency = frequencies[k];
                    float noiseX = frequency * nx * noiseScale[k];
                    float noiseY = frequency * ny * noiseScale[k];
                    e += (float) noise.noise(noiseX, noiseY, 0F) * percentages[k];
                }

                heights[i][j] = ((Mathf.clamp(e, 1F) + 1F) * 0.5F) * amplitude;
            }
        }
        return heights;
    }

}

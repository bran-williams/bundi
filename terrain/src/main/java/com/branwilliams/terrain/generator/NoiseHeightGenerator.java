package com.branwilliams.terrain.generator;

import com.branwilliams.bundi.engine.util.Grid2i;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.bundi.engine.util.noise.Noise;
import com.branwilliams.bundi.engine.util.noise.OpenSimplexNoise;
import org.joml.Vector2d;

import static com.branwilliams.bundi.engine.util.MeshUtils.arrayOfSize;

/**
 * Created by Brandon Williams on 10/29/2018.
 */
public class NoiseHeightGenerator implements HeightGenerator {

    private final Noise noise;

    private float noiseScale;

    public NoiseHeightGenerator(Noise noise, float noiseScale) {
        this.noise = noise;
        this.noiseScale = noiseScale;
    }

    @Override
    public float[][] generateHeight(float x, float z, int vertexCountX, int vertexCountZ, float amplitude) {
        // Ensure the edges of each tile matches.
        x = x - x * (1F / vertexCountX);
        z = z - z * (1F / vertexCountZ);

        Vector2d origin = new Vector2d(x * vertexCountX + 1F, z * vertexCountZ + 1F);
        Grid2i<Double> noiseGrid = noise.noiseGrid2i(origin, noiseScale, vertexCountX, vertexCountZ);

        float[][] heights = new float[vertexCountX][vertexCountZ];

        for (int i = 0; i < vertexCountX; i++) {
            heights[i] = new float[vertexCountZ];
            for (int j = 0; j < vertexCountZ; j++) {
                float e = noiseGrid.getValue(i, j).floatValue();
                heights[i][j] = ((e + 1F) * 0.5F) * amplitude;

//                heights[i][j] = ((Mathf.clamp(e, 1F) + 1F) * 0.5F) * amplitude;
            }
        }
        return heights;
    }

//    @Override
//    public float[][] generateHeight(float x, float z, int vertexCountX, int vertexCountZ, float amplitude) {
//        // Ensure the edges of each tile matches.
//        x = x - x * (1F / vertexCountX);
//        z = z - z * (1F / vertexCountZ);
//
//        float[][] heights = new float[vertexCountX][vertexCountZ];
//
//        float maxNoise = 0F;
//
//        for (int i = 0; i < vertexCountX; i++) {
//            heights[i] = new float[vertexCountZ];
//
//            // Add one to ensure no zero values are given to the noise function.
//            float nx = (x * vertexCountX + i + 1F);
//
//            for (int j = 0; j < vertexCountZ; j++) {
//                // Add one to ensure no zero values are given to the noise function.
//                float ny = (z * vertexCountZ + j + 1F);
//
//                float e = (float) noise.noise(nx * noiseScale, ny * noiseScale, 0F);
//                heights[i][j] = ((Mathf.clamp(e, 1F) + 1F) * 0.5F);
//
//                if (heights[i][j] > maxNoise)
//                    maxNoise = heights[i][j];
//            }
//        }
//
//        for (int i = 0; i < vertexCountX; i++) {
//            for (int j = 0; j < vertexCountZ; j++) {
//                heights[i][j] /= maxNoise;
//                heights[i][j] *= amplitude;
//            }
//        }
//        return heights;
//    }

    public Noise getNoise() {
        return noise;
    }

    public float getNoiseScale() {
        return noiseScale;
    }

    public void setNoiseScale(float noiseScale) {
        this.noiseScale = noiseScale;
    }
}

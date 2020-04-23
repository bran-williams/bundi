package com.branwilliams.bundi.engine.util.noise;

import com.branwilliams.bundi.engine.util.Grid2i;
import com.branwilliams.bundi.engine.util.Grid3i;
import org.joml.Vector2d;
import org.joml.Vector3d;

/**
 * @author Brandon
 * @since January 23, 2020
 */
public interface Noise {

    double noise(double x, double y);

    double noise(double x, double y, double z);

    default Grid2i<Double> noiseGrid2i(Vector2d origin, double noiseScale, int width, int height) {
        Grid2i<Double> grid2i = new Grid2i<>(Double[]::new, width, height);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double noiseValue = noise(origin.x + i * noiseScale, origin.y + j * noiseScale);
                grid2i.setValue(noiseValue, i, j);
            }
        }

        return grid2i;
    }

    default Grid3i<Double> noiseGrid3i(Vector3d origin, double noiseScale, int width, int height, int depth) {
        Grid3i<Double> grid3i = new Grid3i<>(Double[]::new, width, height, depth);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                for (int k = 0; k < depth; k++) {
                    double noiseValue = noise(origin.x + i * noiseScale,
                            origin.y + j * noiseScale,
                            origin.z + k * noiseScale);
                    grid3i.setValue(noiseValue, i, j, k);
                }
            }
        }

        return grid3i;
    }
}

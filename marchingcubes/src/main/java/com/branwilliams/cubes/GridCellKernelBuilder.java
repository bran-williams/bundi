package com.branwilliams.cubes;

import com.branwilliams.bundi.engine.util.PerlinNoise;

public class GridCellKernelBuilder {

    private static final float NOISE_SCALE = 0.1F;

    private final PerlinNoise perlinNoise;

    public GridCellKernelBuilder() {
        this.perlinNoise = new PerlinNoise();
    }

    public GridCellKernel<Boolean> buildGridCell(int width, int height, int depth, float isoLevel) {
        GridCellKernel<Boolean> gridCellKernel = new GridCellKernel<>(Boolean[]::new, width, height, depth);
        return rebuildGridCell(gridCellKernel, isoLevel);
    }

    public GridCellKernel<Boolean> rebuildGridCell(GridCellKernel<Boolean> gridCellKernel, float isoLevel) {

        for (int i = 0; i < gridCellKernel.getWidth(); i++) {
            for (int j = 0; j < gridCellKernel.getHeight(); j++) {
                for (int k = 0; k < gridCellKernel.getDepth(); k++) {
                    float x = i * NOISE_SCALE;
                    float y = j * NOISE_SCALE;
                    float z = k * NOISE_SCALE;
                    double e = perlinNoise.noise(x, y, z);

                    gridCellKernel.setValue(e > isoLevel, i, j, k);
                }
            }
        }

        return gridCellKernel;
    }

}

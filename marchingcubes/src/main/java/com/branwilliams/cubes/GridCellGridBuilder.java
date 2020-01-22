package com.branwilliams.cubes;

import com.branwilliams.bundi.engine.util.PerlinNoise;
import org.joml.Vector3f;

/**
 * @author Brandon
 * @since January 21, 2020
 */
public class GridCellGridBuilder {
    private static final float NOISE_SCALE = 0.1F;

    private final PerlinNoise perlinNoise;

    private final float cellSize;

    public GridCellGridBuilder(float cellSize) {
        this.perlinNoise = new PerlinNoise();
        this.cellSize = cellSize;
    }

    public Grid3f<GridCell> buildGridCellGrid(Vector3f offset, int width, int height, int depth) {
        Grid3f<GridCell> grid3f = new Grid3f<>(GridCell[]::new, width, height, depth);
        return rebuildGridCellGrid(offset, grid3f);
    }

    public Grid3f<GridCell> rebuildGridCellGrid(Vector3f offset, Grid3f<GridCell> grid3f) {
        for (int i = 0; i < grid3f.getWidth(); i++) {
            for (int j = 0; j < grid3f.getHeight(); j++) {
                for (int k = 0; k < grid3f.getDepth(); k++) {
                    GridCell gridCell = new GridCell(new Vector3f(i, j, k), cellSize);
                    Vector3f[] points = gridCell.getPoints();

                    // Set iso values for each point.
                    for (int l = 0; l < points.length; l++) {
                        float x = (offset.x + points[l].x) * NOISE_SCALE;
                        float y = (offset.y + points[l].y) * NOISE_SCALE;
                        float z = (offset.z + points[l].z) * NOISE_SCALE;
                        float e = (float) perlinNoise.noise(x, y, z);
                        gridCell.getIsoValues()[l] = e;
                    }

                    grid3f.setValue(gridCell, i, j, k);
                }
            }
        }

        return grid3f;
    }

}

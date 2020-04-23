package com.branwilliams.cubes.builder;

import com.branwilliams.bundi.engine.util.Grid3i;
import com.branwilliams.cubes.GridCell;
import com.branwilliams.cubes.builder.evaluators.IsoEvaluator;
import com.branwilliams.cubes.world.MarchingCubeWorld;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Brandon
 * @since January 21, 2020
 */
public class GridCellGridBuilderImpl implements GridCellGridBuilder {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final IsoEvaluator evaluator;

    public GridCellGridBuilderImpl(IsoEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public Grid3i<GridCell> buildGridCellGrid(MarchingCubeWorld world, Vector3f offset, int width, int height, int depth) {
        Grid3i<GridCell> grid3i = new Grid3i<>(GridCell[]::new, width, height, depth);
        return rebuildGridCellGrid(world, offset, grid3i);
    }

    @Override
    public Grid3i<GridCell> rebuildGridCellGrid(MarchingCubeWorld world, Vector3f offset, Grid3i<GridCell> grid3i) {
        for (int i = 0; i < grid3i.getWidth(); i++) {
            for (int j = 0; j < grid3i.getHeight(); j++) {
                for (int k = 0; k < grid3i.getDepth(); k++) {
                    GridCell gridCell = grid3i.getValue(i, j, k);

                    if (gridCell == null) {
                        gridCell = new GridCell(new Vector3f(i, j, k), world.getCubeSize());
                        grid3i.setValue(gridCell, i, j, k);
                    }

                    Vector3f[] points = gridCell.getPoints();

                    // Set iso values for each point.
                    for (int l = 0; l < points.length; l++) {
                        float x = (offset.x + points[l].x);
                        float y = (offset.y + points[l].y);
                        float z = (offset.z + points[l].z);
                        float isoValue = gridCell.getIsoValues()[l];
                        gridCell.getIsoValues()[l] = evaluator.evaluate(x, y, z, isoValue);
                    }
                }
            }
        }

        return grid3i;
    }

}

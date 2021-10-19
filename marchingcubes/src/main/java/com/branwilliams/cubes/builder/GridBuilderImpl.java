package com.branwilliams.cubes.builder;

import com.branwilliams.bundi.engine.util.Grid3i;
import com.branwilliams.cubes.builder.evaluators.IsoEvaluator;
import com.branwilliams.cubes.world.MarchingCubeWorld;
import com.branwilliams.cubes.world.PointData;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * @author Brandon
 * @since January 21, 2020
 */
public class GridBuilderImpl <GridData extends PointData> implements GridBuilder<GridData> {

    public interface PointDataBuilder <GridData extends PointData> {
        GridData build(float x, float y, float z);
    }

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final IsoEvaluator evaluator;

    private final PointDataBuilder<GridData> gridDataBuilder;

    private final Function<Integer, GridData[]> gridArrayInitializer;

    public GridBuilderImpl(IsoEvaluator evaluator, PointDataBuilder<GridData> gridDataBuilder,
                           Function<Integer, GridData[]> gridArrayInitializer) {
        this.evaluator = evaluator;
        this.gridDataBuilder = gridDataBuilder;
        this.gridArrayInitializer = gridArrayInitializer;
    }

    public Grid3i<GridData> buildGrid(MarchingCubeWorld<GridData> world, Vector3f offset, int width, int height,
                                      int depth) {
        Grid3i<GridData> grid3i = new Grid3i<>(gridArrayInitializer, width, height, depth);
        return rebuildGrid(world, offset, grid3i);
    }

    public Grid3i<GridData> rebuildGrid(MarchingCubeWorld<GridData> world, Vector3f offset, Grid3i<GridData> grid3i) {
        for (int i = 0; i < grid3i.getWidth(); i++) {
            for (int j = 0; j < grid3i.getHeight(); j++) {
                for (int k = 0; k < grid3i.getDepth(); k++) {
                    GridData gridData = grid3i.getValue(i, j, k);
                    float x = (offset.x + i * world.getCubeSize());
                    float y = (offset.y + j * world.getCubeSize());
                    float z = (offset.z + k * world.getCubeSize());

                    if (gridData == null) {
                        gridData = gridDataBuilder.build(x, y, z);
                        grid3i.setValue(gridData, i, j, k);
                    }

                    gridData.setIsoValue(evaluator.evaluate(x, y, z, gridData.getIsoValue()));
                }
            }
        }

        return grid3i;
    }

}

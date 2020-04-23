package com.branwilliams.cubes.builder;

import com.branwilliams.bundi.engine.util.Grid3i;
import com.branwilliams.cubes.GridCell;
import com.branwilliams.cubes.world.MarchingCubeWorld;
import org.joml.Vector3f;

/**
 * @author Brandon
 * @since January 21, 2020
 */
public interface GridCellGridBuilder {
    Grid3i<GridCell> buildGridCellGrid(MarchingCubeWorld world, Vector3f offset, int width, int height, int depth);

    Grid3i<GridCell> rebuildGridCellGrid(MarchingCubeWorld world, Vector3f offset, Grid3i<GridCell> grid3i);
}

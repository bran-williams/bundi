package com.branwilliams.cubes.builder;

import com.branwilliams.bundi.engine.util.Grid3i;
import com.branwilliams.cubes.world.MarchingCubeWorld;
import com.branwilliams.cubes.world.PointData;
import org.joml.Vector3f;

public interface GridBuilder <GridData extends PointData> {

    Grid3i<GridData> buildGrid(MarchingCubeWorld<GridData> world, Vector3f offset, int width, int height,
                               int depth);

    Grid3i<GridData> rebuildGrid(MarchingCubeWorld<GridData> world, Vector3f offset, Grid3i<GridData> grid3i);
}

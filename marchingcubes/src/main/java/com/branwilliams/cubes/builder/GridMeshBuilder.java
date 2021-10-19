package com.branwilliams.cubes.builder;

import com.branwilliams.bundi.engine.util.Grid3i;
import com.branwilliams.cubes.GridCellMesh;
import com.branwilliams.cubes.world.MarchingCubeWorld;
import com.branwilliams.cubes.world.PointData;
import org.joml.Vector3f;

public interface GridMeshBuilder <GridData extends PointData> {

    GridCellMesh initMesh(MarchingCubeWorld<GridData> world, Grid3i<GridData> grid, Vector3f offset);

    GridCellMesh buildMesh(MarchingCubeWorld<GridData> world, Grid3i<GridData> grid, Vector3f offset,
                           GridCellMesh gridCellMesh);
}
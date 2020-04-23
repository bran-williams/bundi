package com.branwilliams.cubes.builder;

import com.branwilliams.bundi.engine.util.Grid3i;
import com.branwilliams.cubes.GridCell;
import com.branwilliams.cubes.GridCellMesh;
import com.branwilliams.cubes.world.MarchingCubeWorld;

import static com.branwilliams.bundi.engine.util.MeshUtils.toArray3f;

public interface GridCellMeshBuilder {

    GridCellMesh buildMesh(MarchingCubeWorld world, Grid3i<GridCell> gridCellGrid);

    GridCellMesh rebuildMesh(MarchingCubeWorld world, GridCellMesh gridCellMesh, Grid3i<GridCell> gridCellGrid);

}

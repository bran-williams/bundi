package com.branwilliams.cubes.builder;

import com.branwilliams.bundi.engine.util.Grid3f;
import com.branwilliams.bundi.engine.util.MeshUtils;
import com.branwilliams.cubes.GridCell;
import com.branwilliams.cubes.GridCellMesh;
import com.branwilliams.cubes.world.MarchingCubeWorld;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static com.branwilliams.bundi.engine.util.MeshUtils.toArray3f;

public interface GridCellMeshBuilder {

    GridCellMesh buildMesh(MarchingCubeWorld world, Grid3f<GridCell> gridCellGrid);

    GridCellMesh rebuildMesh(MarchingCubeWorld world, GridCellMesh gridCellMesh, Grid3f<GridCell> gridCellGrid);

}

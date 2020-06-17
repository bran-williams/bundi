package com.branwilliams.cubes.builder;

import com.branwilliams.bundi.engine.shader.dynamic.VertexElements;
import com.branwilliams.bundi.engine.util.Grid3i;
import com.branwilliams.bundi.engine.util.MeshUtils;
import com.branwilliams.cubes.GridCell;
import com.branwilliams.cubes.GridCellMesh;
import com.branwilliams.cubes.world.MarchingCubeWorld;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static com.branwilliams.bundi.engine.util.MeshUtils.toArray3f;

public class GridCellMeshBuilderImpl implements GridCellMeshBuilder {

    @Override
    public GridCellMesh buildMesh(MarchingCubeWorld world, Grid3i<GridCell> gridCellGrid) {
        GridCellMesh gridCellMesh = new GridCellMesh();
        gridCellMesh.init();
        return rebuildMesh(world, gridCellMesh, gridCellGrid);
    }

    @Override
    public GridCellMesh rebuildMesh(MarchingCubeWorld world, GridCellMesh gridCellMesh, Grid3i<GridCell> gridCellGrid) {
        List<Vector3f> positions = new ArrayList<>();
        for (GridCell gridCell : gridCellGrid) {
            gridCell.getTriangles(world.getIsoLevel(), positions);
        }

        List<Vector3f> normals = new ArrayList<>();
        for (int i = 0; i < positions.size(); i += 3) {
            Vector3f p1 = positions.get(i  );
            Vector3f p2 = positions.get(i+1);
            Vector3f p3 = positions.get(i+2);

            Vector3f normal = MeshUtils.calculateNormal(p1, p2, p3);

            normals.add(normal);
            normals.add(normal);
            normals.add(normal);
        }

        gridCellMesh.getMesh().bind();
        gridCellMesh.getMesh().storeAttribute(0, toArray3f(positions), VertexElements.POSITION.getSize());
        gridCellMesh.getMesh().storeAttribute(1, toArray3f(normals), VertexElements.NORMAL.getSize());
        gridCellMesh.getMesh().setVertexCount(positions.size());
        gridCellMesh.getMesh().unbind();

        return gridCellMesh;
    }

}

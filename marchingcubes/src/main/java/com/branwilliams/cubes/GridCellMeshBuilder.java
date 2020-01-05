package com.branwilliams.cubes;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static com.branwilliams.bundi.engine.util.MeshUtils.toArray3f;

public class GridCellMeshBuilder {

    public GridCellMesh buildMesh(GridCell gridCell) {
        GridCellMesh gridCellMesh = new GridCellMesh();
        gridCellMesh.init();
        return rebuildMesh(gridCellMesh, gridCell);
    }

    public GridCellMesh rebuildMesh(GridCellMesh gridCellMesh, GridCell gridCell) {
        GridCellKernel<Boolean> pointKernel = gridCell.getPointKernel();

        List<Vector3f> positions = new ArrayList<>();

        for (int i = 0; i < pointKernel.getWidth(); i++) {
            for (int j = 0; j < pointKernel.getHeight(); j++) {
                for (int k = 0; k < pointKernel.getDepth(); k++) {
                    if (pointKernel.getValue(i, j, k))
                        positions.add(new Vector3f(i, j, k));
                }
            }
        }

        gridCellMesh.getMesh().bind();
        gridCellMesh.getMesh().storeAttribute(0, toArray3f(positions), 3);
        gridCellMesh.getMesh().setVertexCount(positions.size());
        gridCellMesh.getMesh().unbind();

        return gridCellMesh;
    }

}

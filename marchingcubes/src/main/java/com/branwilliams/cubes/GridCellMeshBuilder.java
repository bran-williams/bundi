package com.branwilliams.cubes;

import com.branwilliams.bundi.engine.util.Grid3f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static com.branwilliams.bundi.engine.util.MeshUtils.toArray3f;

public class GridCellMeshBuilder {

    public GridCellMesh buildMesh(Grid3f<GridCell> gridCellGrid) {
        GridCellMesh gridCellMesh = new GridCellMesh();
        gridCellMesh.init();
        return rebuildMesh(gridCellMesh, gridCellGrid);
    }

    public GridCellMesh rebuildMesh(GridCellMesh gridCellMesh, Grid3f<GridCell> gridCellGrid) {

        List<Vector3f> positions = new ArrayList<>();
        for (GridCell gridCell : gridCellGrid) {
            gridCell.getTriangles(0.25F, positions);
        }

        List<Vector3f> normals = new ArrayList<>();
        for (int i = 0; i < positions.size(); i += 3) {
            Vector3f p1 = positions.get(i  );
            Vector3f p2 = positions.get(i+1);
            Vector3f p3 = positions.get(i+2);

            Vector3f normal = calculateNormal(p1, p2, p3);

            normals.add(normal);
            normals.add(normal);
            normals.add(normal);
        }

        gridCellMesh.getMesh().bind();
        gridCellMesh.getMesh().storeAttribute(0, toArray3f(positions), 3);
        gridCellMesh.getMesh().storeAttribute(1, toArray3f(normals), 3);
        gridCellMesh.getMesh().setVertexCount(positions.size());
        gridCellMesh.getMesh().unbind();

        return gridCellMesh;
    }

    private Vector3f calculateNormal(Vector3f p1, Vector3f p2, Vector3f p3) {
        Vector3f normal = new Vector3f();
        Vector3f u = new Vector3f(p2).sub(p1);
        Vector3f v = new Vector3f(p3).sub(p1);

        normal.x = (u.y * v.z) - (u.z * v.y);
        normal.y = (u.z * v.x) - (u.x * v.z);
        normal.z = (u.x * v.y) - (u.y * v.x);
        return normal;
    }

}

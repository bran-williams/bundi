package com.branwilliams.cubes.builder;

import com.branwilliams.bundi.engine.shader.dynamic.VertexElements;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.util.Grid3i;
import com.branwilliams.bundi.engine.util.MeshUtils;
import com.branwilliams.cubes.GridCellMesh;
import com.branwilliams.cubes.utils.MarchingCubeUtils;
import com.branwilliams.cubes.world.MarchingCubeWorld;
import com.branwilliams.cubes.world.PointData;
import org.joml.Vector3f;

import java.util.*;

import static com.branwilliams.bundi.engine.util.MeshUtils.toArray3f;

public class GridMeshBuilderImpl <GridData extends PointData> implements GridMeshBuilder<GridData> {

    @Override
    public GridCellMesh initMesh(MarchingCubeWorld<GridData> world, Grid3i<GridData> grid, Vector3f offset) {
        GridCellMesh gridCellMesh = new GridCellMesh();
        gridCellMesh.init();
        return gridCellMesh;
    }

    @Override
    public GridCellMesh buildMesh(MarchingCubeWorld<GridData> world, Grid3i<GridData> grid, Vector3f offset,
                                  GridCellMesh gridCellMesh) {
        List<Vector3f> positions = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();

        int numberOfCellsX = grid.getWidth();
        int numberOfCellsY = grid.getHeight();
        int numberOfCellsZ = grid.getDepth();

        for (int i = 0; i < numberOfCellsX; i++) {
            for (int j = 0; j < numberOfCellsY; j++) {
                for (int k = 0; k < numberOfCellsZ; k++) {

                    GridData gridData0 = getGridData(world, grid, offset, i, j, k + 1);
                    GridData gridData1 = getGridData(world, grid, offset, i + 1, j, k + 1);
                    GridData gridData2 = getGridData(world, grid, offset, i + 1, j, k);
                    GridData gridData3 = getGridData(world, grid, offset, i, j, k);

                    GridData gridData4 = getGridData(world, grid, offset, i, j + 1, k + 1);
                    GridData gridData5 = getGridData(world, grid, offset, i + 1, j + 1, k + 1);
                    GridData gridData6 = getGridData(world, grid, offset, i + 1, j + 1, k);
                    GridData gridData7 = getGridData(world, grid, offset, i, j + 1, k);

                    Vector3f pos0 = toCubePos(world, i, j, k + 1);
                    Vector3f pos1 = toCubePos(world, i + 1, j, k + 1);
                    Vector3f pos2 = toCubePos(world, i + 1, j, k);
                    Vector3f pos3 = toCubePos(world, i, j, k);

                    Vector3f pos4 = toCubePos(world, i, j + 1, k + 1);
                    Vector3f pos5 = toCubePos(world, i + 1, j + 1, k + 1);
                    Vector3f pos6 = toCubePos(world, i + 1, j + 1, k);
                    Vector3f pos7 = toCubePos(world, i, j + 1, k);


                    float isoLevel = world.getIsoLevel();

                    int cubeIndex = MarchingCubeUtils.getCubeIndex(isoLevel, gridData0.getIsoValue(),
                            gridData1.getIsoValue(), gridData2.getIsoValue(), gridData3.getIsoValue(),
                            gridData4.getIsoValue(), gridData5.getIsoValue(), gridData6.getIsoValue(),
                            gridData7.getIsoValue());

                    Vector3f[] vertexList = MarchingCubeUtils.buildVertices(cubeIndex, isoLevel, pos0, pos1, pos2,
                            pos3, pos4, pos5, pos6, pos7, gridData0.getIsoValue(), gridData1.getIsoValue(),
                            gridData2.getIsoValue(), gridData3.getIsoValue(), gridData4.getIsoValue(),
                            gridData5.getIsoValue(), gridData6.getIsoValue(), gridData7.getIsoValue());

                    for (int m = 0; MarchingCubeUtils.TRI_TABLE[cubeIndex][m] != -1; m += 3) {
                        Vector3f p1 = vertexList[MarchingCubeUtils.TRI_TABLE[cubeIndex][m]];
                        Vector3f p2 = vertexList[MarchingCubeUtils.TRI_TABLE[cubeIndex][m + 1]];
                        Vector3f p3 = vertexList[MarchingCubeUtils.TRI_TABLE[cubeIndex][m + 2]];

                        Vector3f normal = MeshUtils.calculateNormal(p1, p2, p3).normalize();

                        positions.add(p1);
                        positions.add(p2);
                        positions.add(p3);

                        normals.add(normal);
                        normals.add(normal);
                        normals.add(normal);
                    }
                }
            }
        }

        gridCellMesh.getMesh().bind();
        gridCellMesh.getMesh().storeAttribute(0, toArray3f(positions), VertexElements.POSITION.getSize());
        gridCellMesh.getMesh().storeAttribute(1, toArray3f(normals), VertexElements.NORMAL.getSize());
        gridCellMesh.getMesh().setVertexCount(positions.size());
        gridCellMesh.getMesh().setVertexFormat(VertexFormat.POSITION_NORMAL);
        gridCellMesh.getMesh().unbind();

        return gridCellMesh;
    }

    private GridData getGridData(MarchingCubeWorld<GridData> world, Grid3i<GridData> grid, Vector3f offset, int x,
                                 int y, int z) {
        if (grid.withinBounds(x, y, z)) {
            return grid.getValue(x, y, z);
        } else {
            return world.getGridData(toWorldPos(world, offset, x, y, z));
        }
    }

    private Vector3f toWorldPos(MarchingCubeWorld<GridData> world, Vector3f offset, int i, int j, int k) {
        float x = offset.x + i * world.getCubeSize();
        float y = offset.y + j * world.getCubeSize();
        float z = offset.z + k * world.getCubeSize();
        return new Vector3f(x, y, z);
    }

    private Vector3f toCubePos(MarchingCubeWorld<GridData> world, int i, int j, int k) {
        float x = i * world.getCubeSize();
        float y = j * world.getCubeSize();
        float z = k * world.getCubeSize();
        return new Vector3f(x, y, z);
    }

}

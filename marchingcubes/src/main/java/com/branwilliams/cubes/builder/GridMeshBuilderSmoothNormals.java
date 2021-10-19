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
import java.util.stream.Collectors;

import static com.branwilliams.bundi.engine.util.MeshUtils.toArray3f;
import static com.branwilliams.bundi.engine.util.MeshUtils.toArrayi;
import static com.branwilliams.cubes.utils.MarchingCubeUtils.TRI_TABLE;

public class GridMeshBuilderSmoothNormals<GridData extends PointData> implements GridMeshBuilder<GridData> {

    @Override
    public GridCellMesh initMesh(MarchingCubeWorld<GridData> world, Grid3i<GridData> grid, Vector3f offset) {
        GridCellMesh gridCellMesh = new GridCellMesh();
        gridCellMesh.init();
        return gridCellMesh;
//        return rebuildMesh(world, grid, offset, gridCellMesh);
    }

    @Override
    public GridCellMesh buildMesh(MarchingCubeWorld<GridData> world, Grid3i<GridData> grid, Vector3f offset,
                                  GridCellMesh gridCellMesh) {
        Map<GridMeshVertex, GridMeshVertex> positions = new HashMap<>();
        List<Integer> indices = new ArrayList<>();

        int numberOfCellsX = grid.getWidth();
        int numberOfCellsY = grid.getHeight();
        int numberOfCellsZ = grid.getDepth();

        int indexCounter = 0;
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

                    for (int m = 0; TRI_TABLE[cubeIndex][m] != -1; m += 3) {
                        Vector3f p1 = vertexList[TRI_TABLE[cubeIndex][m]];
                        Vector3f p2 = vertexList[TRI_TABLE[cubeIndex][m + 1]];
                        Vector3f p3 = vertexList[TRI_TABLE[cubeIndex][m + 2]];
                        Vector3f normal = MeshUtils.calculateNormal(p1, p2, p3).normalize();

                        GridMeshVertex gv1 = new GridMeshVertex(p1, normal);
                        GridMeshVertex gv2 = new GridMeshVertex(p2, normal);
                        GridMeshVertex gv3 = new GridMeshVertex(p3, normal);

                        if (positions.containsKey(gv1)) {
                            gv1 = positions.get(gv1);
                            gv1.getNormal().add(normal);
                        } else {
                            positions.put(gv1, gv1);
                            gv1.setIndex(indexCounter);
                            indexCounter++;
                        }

                        if (positions.containsKey(gv2)) {
                            gv2 = positions.get(gv2);
                            gv2.getNormal().add(normal);
                        } else {
                            positions.put(gv2, gv2);
                            gv2.setIndex(indexCounter);
                            indexCounter++;
                        }

                        if (positions.containsKey(gv3)) {
                            gv3 = positions.get(gv3);
                            gv3.getNormal().add(normal);
                        } else {
                            positions.put(gv3, gv3);
                            gv3.setIndex(indexCounter);
                            indexCounter++;
                        }

                        indices.add(gv1.getIndex());
                        indices.add(gv2.getIndex());
                        indices.add(gv3.getIndex());
                    }
                }
            }
        }

        List<GridMeshVertex> vertices = positions.keySet().stream()
                .sorted(Comparator.comparingInt(GridMeshVertex::getIndex))
                .collect(Collectors.toList());

        gridCellMesh.getMesh().bind();
        gridCellMesh.getMesh().storeAttribute(0, toArray3f(vertices, GridMeshVertex::getPosition),
                VertexElements.POSITION.getSize());
        gridCellMesh.getMesh().storeAttribute(1, toArray3f(vertices, (v) -> v.getNormal().normalize()),
                VertexElements.NORMAL.getSize());
        gridCellMesh.getMesh().storeIndices(toArrayi(indices));
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

    public static class GridMeshVertex {
        private Vector3f position;
        private Vector3f normal;
        private Integer index;

        public GridMeshVertex() {
            this(new Vector3f(), new Vector3f());
        }

        public GridMeshVertex(Vector3f position) {
            this(position, new Vector3f());
        }

        public GridMeshVertex(Vector3f position, Vector3f normal) {
            this.position = position;
            this.normal = normal;
        }

        public Vector3f getPosition() {
            return position;
        }

        public void setPosition(Vector3f position) {
            this.position = position;
        }

        public Vector3f getNormal() {
            return normal;
        }

        public void setNormal(Vector3f normal) {
            this.normal = normal;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GridMeshVertex that = (GridMeshVertex) o;
            return position.equals(that.position, 0.0005F);
        }

        @Override
        public int hashCode() {
            return Objects.hash(position);
        }
    }
}

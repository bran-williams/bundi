package com.branwilliams.cubes.world;

import com.branwilliams.bundi.engine.shape.AABB3f;
import com.branwilliams.bundi.engine.util.Grid3i;
import com.branwilliams.cubes.GridCellMesh;
import org.joml.Vector3f;
import org.joml.Vector3i;

/**
 * @author Brandon
 * @since January 25, 2020
 */
public class MarchingCubeChunk <GridData extends PointData> {

    private final Vector3f offset;

    private Grid3i<GridData> gridData;

    private GridCellMesh gridCellMesh;

    private AABB3f bounds;

    private boolean dirty;

    public MarchingCubeChunk(Vector3f offset, Grid3i<GridData> gridData, GridCellMesh gridCellMesh,
                             Vector3i chunkDimensions) {
        this.offset = offset;
        this.gridData = gridData;
        this.gridCellMesh = gridCellMesh;
        this.bounds = new AABB3f(offset.x, offset.y, offset.z,
                offset.x + chunkDimensions.x, offset.y + chunkDimensions.y,
                offset.z + chunkDimensions.z);
        this.dirty = true;
    }

    public Vector3f getOffset() {
        return offset;
    }

    public GridData getGridData(int gridX, int gridY, int gridZ) {
        return gridData.getValue(gridX, gridY, gridZ);
    }

    public Grid3i<GridData> getGridData() {
        return gridData;
    }

    public void setGridData(Grid3i<GridData> gridData) {
        this.gridData = gridData;
    }

    public GridCellMesh getGridCellMesh() {
        return gridCellMesh;
    }

    public void setGridCellMesh(GridCellMesh gridCellMesh) {
        this.gridCellMesh = gridCellMesh;
    }

    public void markDirty() {
        dirty = true;
    }

    public void resetDirty() {
        dirty = false;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public AABB3f getBounds() {
        return bounds;
    }

    @Override
    public String toString() {
        return "MarchingCubeChunk{" +
                "offset=" + offset +
                ", gridCellMesh=" + gridCellMesh +
                ", bounds=" + bounds +
                '}';
    }
}

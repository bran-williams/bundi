package com.branwilliams.cubes.world;

import com.branwilliams.bundi.engine.util.Grid3i;
import com.branwilliams.cubes.GridCell;
import com.branwilliams.cubes.GridCellMesh;
import org.joml.Vector3f;

/**
 * @author Brandon
 * @since January 25, 2020
 */
public class MarchingCubeChunk {

    private final Vector3f offset;

    private Grid3i<GridCell> gridCells;

    private GridCellMesh gridCellMesh;

    private boolean dirty;

    public MarchingCubeChunk(Vector3f offset, Grid3i<GridCell> gridCells, GridCellMesh gridCellMesh) {
        this.offset = offset;
        this.gridCells = gridCells;
        this.gridCellMesh = gridCellMesh;
    }

    public Vector3f getOffset() {
        return offset;
    }

    public GridCell getGridCell(int gridX, int gridY, int griZ) {
        return gridCells.getValue(gridX, gridY, griZ);
    }

    public Grid3i<GridCell> getGridCells() {
        return gridCells;
    }

    public void setGridCells(Grid3i<GridCell> gridCells) {
        this.gridCells = gridCells;
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
}

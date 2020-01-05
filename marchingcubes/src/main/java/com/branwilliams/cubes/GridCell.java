package com.branwilliams.cubes;

import java.util.function.Function;

public class GridCell {

    private GridCellKernel<Byte> edgeKernel;

    private GridCellKernel<Boolean> pointKernel;

    public GridCell(int width, int height, int depth) {
        this.edgeKernel = new GridCellKernel<>(Byte[]::new, width, height, depth);
        this.pointKernel = new GridCellKernel<>(Boolean[]::new, width, height, depth);
    }

    public GridCellKernel<Byte> getEdgeKernel() {
        return edgeKernel;
    }

    public void setEdgeKernel(GridCellKernel<Byte> edgeKernel) {
        this.edgeKernel = edgeKernel;
    }

    public GridCellKernel<Boolean> getPointKernel() {
        return pointKernel;
    }

    public void setPointKernel(GridCellKernel<Boolean> pointKernel) {
        this.pointKernel = pointKernel;
    }
}

package com.branwilliams.cubes;

import com.branwilliams.bundi.engine.util.Mathf;

import java.util.function.Function;

/**
 * @author Brandon
 * @since November 30, 2019
 */
public class GridCellKernel <T> {

    private T[] kernel;

    private final int width;

    private final int height;

    private final int depth;

    private boolean dirty;

    public GridCellKernel(Function<Integer, T[]> kernelBuilder, int width, int height, int depth) {
        this.kernel = kernelBuilder.apply(width * height * depth);
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.dirty = true;
    }

    public void setValue(T value, int x, int y, int z) {
        kernel[toFlatIndex(x, y, z)] = value;
        this.dirty = true;
    }

    public T getValue(int x, int y, int z) {
        return kernel[toFlatIndex(x, y, z)];
    }

    public int toFlatIndex(int x, int y, int z) {
        x = Mathf.clamp(x, 0, width - 1);
        y = Mathf.clamp(y, 0, height - 1);
        z = Mathf.clamp(z, 0, depth - 1);

        return x + width * (y + height * z);
    }

    public T[] getKernel() {
        return kernel;
    }

    public void setKernel(T[] kernel) {
        this.kernel = kernel;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDepth() {
        return depth;
    }

    public boolean isDirty() {
        return dirty;
    }
}

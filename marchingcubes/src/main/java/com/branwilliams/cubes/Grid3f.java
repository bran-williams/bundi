package com.branwilliams.cubes;

import com.branwilliams.bundi.engine.util.Mathf;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.function.Function;

/**
 * @author Brandon
 * @since November 30, 2019
 */
public class Grid3f<T> implements Iterable<T> {

    private T[] kernel;

    private final int width;

    private final int height;

    private final int depth;

    private boolean dirty;

    public Grid3f(Function<Integer, T[]> kernelBuilder, int width, int height, int depth) {
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

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            private int i;

            @Override
            public boolean hasNext() {
                return i < Grid3f.this.getKernel().length;
            }

            @Override
            public T next() {
                if (hasNext()) {
                    T val = Grid3f.this.getKernel()[i];
                    i++;
                    return val;
                }
                return null;
            }
        };
    }
}

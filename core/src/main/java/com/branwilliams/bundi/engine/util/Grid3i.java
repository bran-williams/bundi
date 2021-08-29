package com.branwilliams.bundi.engine.util;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.function.Function;

/**
 * @author Brandon
 * @since November 30, 2019
 */
public class Grid3i<T> implements Iterable<T> {

    private T[] kernel;

    private final int width;

    private final int height;

    private final int depth;

    private boolean dirty;

    public Grid3i(Function<Integer, T[]> kernelBuilder, int width, int height, int depth) {
        this(kernelBuilder.apply(width * height * depth), width, height, depth);
    }

    public Grid3i(T[] kernel, int width, int height, int depth) {
        if (width * height * depth != kernel.length)
            throw new IllegalArgumentException("Kernel must have the dimensions of width * height * depth");
        this.kernel = kernel;
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
        class Grid3fIterator implements Iterator<T> {

            private int i;

            private Grid3fIterator() {
                while (hasNext() && Grid3i.this.getKernel()[i] == null) {
                    i++;
                }
            }

            @Override
            public boolean hasNext() {
                return i < Grid3i.this.getKernel().length;
            }

            @Override
            public T next() {
                if (hasNext()) {
                    T val = Grid3i.this.getKernel()[i];

                    do {
                        i++;
                    } while (hasNext() && Grid3i.this.getKernel()[i] == null);

                    return val;
                }
                return null;
            }
        }
        return new Grid3fIterator();
    }
}

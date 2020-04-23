package com.branwilliams.bundi.engine.util;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.function.Function;

/**
 * @author Brandon
 * @since November 30, 2019
 */
public class Grid2i<T> implements Iterable<T> {

    private T[] kernel;

    private final int width;

    private final int height;

    private boolean dirty;

    public Grid2i(Function<Integer, T[]> kernelBuilder, int width, int height) {
        this(kernelBuilder.apply(width * height), width, height);
    }

    public Grid2i(T[] kernel, int width, int height) {
        if (width * height != kernel.length)
            throw new IllegalArgumentException("Kernel must have the dimensions of width * height");
        this.kernel = kernel;
        this.width = width;
        this.height = height;
        this.dirty = true;
    }

    public void setValue(T value, int x, int y) {
        kernel[toFlatIndex(x, y)] = value;
        this.dirty = true;
    }

    public T getValue(int x, int y) {
        return kernel[toFlatIndex(x, y)];
    }

    public int toFlatIndex(int x, int y) {
        x = Mathf.clamp(x, 0, width - 1);
        y = Mathf.clamp(y, 0, height - 1);
        return x * width + y;
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

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        class Grid2fIterator implements Iterator<T> {

            private int i;

            private Grid2fIterator() {
                if (hasNext() && Grid2i.this.getKernel()[i] == null) {
                    do {
                        i++;
                    } while (hasNext() && Grid2i.this.getKernel()[i] == null);
                }
            }

            @Override
            public boolean hasNext() {
                return i < Grid2i.this.getKernel().length;
            }

            @Override
            public T next() {
                if (hasNext()) {
                    T val = Grid2i.this.getKernel()[i];

                    do {
                        i++;
                    } while (hasNext() && Grid2i.this.getKernel()[i] == null);

                    return val;
                }
                return null;
            }
        }
        return new Grid2fIterator();
    }
}

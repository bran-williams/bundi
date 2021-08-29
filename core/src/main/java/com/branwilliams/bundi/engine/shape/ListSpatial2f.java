package com.branwilliams.bundi.engine.shape;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;

import java.util.*;
import java.util.stream.Collectors;

public class ListSpatial2f <Shape extends Shape2f, T> implements Spatial2f<Shape, T> {

    private List<ListElement> shapes;

    public ListSpatial2f() {
        this.shapes = new ArrayList<>();
    }

    @Override
    public boolean add(Shape shape2f, T element) {
        return shapes.add(new ListElement(shape2f, element));
    }

    @Override
    public boolean remove(Shape shape2f, T element) {
        return shapes.remove(new ListElement(shape2f, element));
    }

    @Override
    public boolean removeByShape(Shape shape2f) {
        Optional<ListElement> listElement = shapes.stream()
                .filter((e -> e.shape.equals(shape2f)))
                .findFirst();
        return listElement.isPresent() && shapes.remove(listElement.get());
    }

    @Override
    public boolean removeByElement(T element) {
        Optional<ListElement> listElement = shapes.stream()
                .filter((e -> e.element.equals(element)))
                .findFirst();
        return listElement.isPresent() && shapes.remove(listElement.get());
    }

    @Override
    public int size() {
        return shapes.size();
    }

    @Override
    public void clear() {
        shapes.clear();
    }

    @Override
    public Iterable<Shape> query(Shape shape2f) {
        return shapes.stream()
                .filter(e -> !shape2f.equals(e.shape) && e.shape.collides(shape2f))
                .map(e -> e.shape)
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Shape> queryVector(Vector2f vector2f) {
        return shapes.stream()
                .filter(e -> e.shape.contains(vector2f))
                .map(e -> e.shape)
                .collect(Collectors.toList());
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return shapes.stream().map(e -> e.element).iterator();
    }

    private class ListElement {

        private Shape shape;

        private T element;

        public ListElement(Shape shape, T element) {
            this.shape = shape;
            this.element = element;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ListElement that = (ListElement) o;
            return shape.equals(that.shape) &&
                    element.equals(that.element);
        }

        @Override
        public int hashCode() {
            return Objects.hash(shape, element);
        }
    }
}

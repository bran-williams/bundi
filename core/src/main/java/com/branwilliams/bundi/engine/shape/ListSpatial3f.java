package com.branwilliams.bundi.engine.shape;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class ListSpatial3f<T> implements Spatial<Shape3f, T> {

    private List<ListElement> shapes;

    public ListSpatial3f() {
        this.shapes = new ArrayList<>();
    }

    @Override
    public boolean add(Shape3f shape3f, T element) {
        return shapes.add(new ListElement(shape3f, element));
    }

    @Override
    public boolean remove(Shape3f shape3f, T element) {
        return shapes.remove(new ListElement(shape3f, element));
    }

    @Override
    public boolean removeByShape(Shape3f shape3f) {
        Optional<ListElement> listElement = shapes.stream()
                .filter((e -> e.shape.equals(shape3f)))
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
    public Iterable<Shape3f> query(Shape3f shape3f) {
        return shapes.stream()
                .filter(e -> e.shape.collides(shape3f))
                .map(e -> e.shape)
                .collect(Collectors.toList());
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return shapes.stream().map(e -> e.element).iterator();
    }

    private class ListElement {

        private Shape3f shape;

        private T element;

        public ListElement(Shape3f shape, T element) {
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

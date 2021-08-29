package com.branwilliams.bundi.engine.shape;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Simple quadtree implementation.
 *
 * Created by Brandon Williams on 10/3/2018.
 */
public class QuadTree <Shape extends Shape2f, T> implements Spatial<Shape, Vector2f, T> {

    private TreeNode<T> root;

    private final int maxSize;

    public QuadTree(int maxSize, float minX, float minY, float maxX, float maxY) {
        this(maxSize, new AABB2f(minX, minY, maxX, maxY));
    }

    public QuadTree(int maxSize, AABB2f bounds) {
        this.maxSize = maxSize;
        this.root = new TreeNode<>(bounds, maxSize);
    }

    /**
     * Adds the provided element to this QuadTree. Returns false if the shape could not fit inside this trees
     * shape, true if it was able to add the element to this tree.
     * @throws NullPointerException If the provided element and/or shape is null.
     * @return True if the element was added to this tree.
     * */
    @Override
    public boolean add(Shape shape, T element) {
        if (element == null || shape == null) {
            throw new IllegalArgumentException("Element and shape must not be null!");
        }
        return root.add(new TreeElement<>(shape, element));
    }

    /**
     *
     * */
    @Override
    public boolean remove(Shape shape, T element) {
        if (element == null || shape == null) {
            throw new IllegalArgumentException("Element and shape must not be null!");
        }
        return root.remove(shape, element);
    }

    /**
     * Forcibly removes this element within the provided bounds.
     * */
    public boolean removeByElement(T element) {
        if (element == null) {
            throw new IllegalArgumentException("Element must not be null!");
        }
        return root.removeByElement(element);
    }

    /**
     * Forcibly removes this element within the provided bounds.
     * */
    @Override
    public boolean removeByShape(Shape shape) {
        if (shape == null) {
            throw new IllegalArgumentException("Shape must not be null!");
        }
        return root.remove(shape);
    }

    public List<T> rangeQuery(Vector2f vector2f) {
        return rangeQuery((node) -> node.boundaries.contains(vector2f));
    }

    public List<T> rangeQuery(Shape shape) {
        return rangeQuery((node) -> node.boundaries.collides(shape));
    }

    public List<T> rangeQuery(Function<TreeNode<T>, Boolean> doesIntersect) {
        final List<T> elements = new ArrayList<>();

        // This consumer accepts the elements which shape collides with.
        Consumer<TreeNode<T>> elementCollector = (node) -> {
                elements.addAll(node.getElements());
        };

        root.traverse(doesIntersect, elementCollector);

        return elements;
    }

    public void visitPreorder(Consumer<TreeNode<T>> visitFunction) {
        List<TreeNode<T>> queue = new ArrayList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            TreeNode<T> node = queue.remove(0);

            visitFunction.accept(node);

            if (!node.isLeaf()) {
                queue.add(node.nw);
                queue.add(node.ne);
                queue.add(node.sw);
                queue.add(node.se);
            }
        }
    }

    /**
     * @return A List of every element within this tree.
     * */
    public Set<T> getElements() {
        return root.getElements(true);
    }

    @Override
    public int size() {
        return getElements().size();
    }

    /**
     * Clears all elements from this tree, including children.
     * */
    public void clear() {
        root.clear();
    }

    @Override
    public Iterable<Shape> query(Shape shape) {
        return (Iterable<Shape>) rangeQuery(shape);
    }

    @Override
    public Iterable<Shape> queryVector(Vector2f vector2f) {
        return (Iterable<Shape>) rangeQuery(vector2f);
    }

//    /**
//     * Removes any children who have no children.
//     * */
//    public boolean prune() {
//        boolean pruned = false;
//        if (hasChildren()) {
//            if (nw.elements.isEmpty() && nw.hasChildren() && nw.prune()) {
//                pruned = true;
//            }
//
//            if (ne.elements.isEmpty() && ne.hasChildren() && ne.prune()) {
//                pruned = true;
//            }
//
//            if (sw.elements.isEmpty() && sw.hasChildren() && sw.prune()) {
//                pruned = true;
//            }
//
//            if (se.elements.isEmpty() && se.hasChildren() && se.prune()) {
//                pruned = true;
//            }
//
//            // If all children are empty, set them to null.
//            if (hasEmptyChildren()) {
//                nw = null;
//                ne = null;
//                sw = null;
//                se = null;
//            }
//        }
//        return pruned;
//    }

    @Override
    public String toString() {
        return "QuadTreeNew{" +
                "root=" + root +
                ", maxSize=" + maxSize +
                '}';
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return root.getElements().iterator();
    }

    /**
     * Contains the shape of a node and its elements.
     *
     * Each node is divided into four quadrants, north west (nw), north east (me), south west (sw), and south east (se).
     *
     * */
    public static class TreeNode <T> {

        private final AABB2f boundaries;

        private final int maxSize;

        private List<TreeElement<T>> elements = new ArrayList<>();

        private TreeNode<T> nw, ne, sw, se;

        public TreeNode(AABB2f boundaries, int maxSize) {
            this.boundaries = boundaries;
            this.maxSize = maxSize;
        }

        /**
         * Adds the provided element to this node. Creates children if the node exceeds its max capacity.
         * */
        public boolean add(TreeElement<T> element) {
            if (isLeaf()) {
                elements.add(element);

                if (isOverFilled()) {
                    createChildren();
                    addElementsToChildren(elements);
                    elements.clear();
                }
            } else {
                addElementToChildren(element);
            }
            return true;
        }

        public boolean remove(Function<TreeNode<T>, Boolean> doesIntersect,
                              Function<TreeElement<T>, Boolean> shouldRemove) {
            class TempClass {
                boolean hasRemoved;
            }
            TempClass tempClass = new TempClass();
            traverse(doesIntersect,
                    (node) -> tempClass.hasRemoved = node.removeByElement(shouldRemove) || tempClass.hasRemoved);
            return tempClass.hasRemoved;
        }

        /**
         * */
        public boolean remove(Function<TreeNode<T>, Boolean> doesIntersect, T element) {
            return remove(doesIntersect, (e) -> e.getElement().equals(element));
        }

        /**
         * Removes any shape colliding with this shape and only if the element matches this element.
         * */
        public boolean remove(Shape2f shape, T element) {
            return remove((node) -> node.boundaries.collides(shape), element);
        }

        /**
         * Removes any elements which collides with this shape.
         * */
        public boolean remove(Shape2f shape) {
            return remove((node) -> node.boundaries.collides(shape), (element) -> true);
        }

        /**
         * Remove the element from this quadtree.
         * */
        public boolean removeByElement(T element) {
            return removeByElement((e) -> e.getElement().equals(element));
        }


        public boolean removeByElement(Function<TreeElement<T>, Boolean> shouldRemove) {
            if (isLeaf()) {
                return elements.removeIf(shouldRemove::apply);
            } else {
                boolean removed = nw.removeByElement(shouldRemove);
                removed = ne.removeByElement(shouldRemove) || removed;
                removed = sw.removeByElement(shouldRemove) || removed;
                removed = se.removeByElement(shouldRemove) || removed;
                return removed;
            }
        }

            /**
             * Clears the list of elements within this tree node. If it is not a leaf, then the children will be cleared and
             * this node will become a leaf.
             * */
        public void clear() {
            if (isLeaf()) {
                elements.clear();
            } else {
                nw.clear();
                nw = null;
                ne.clear();
                ne = null;
                sw.clear();
                sw = null;
                se.clear();
                se = null;
            }
        }

        /**
         * Traverses through the nodes, beginning with this node.
         *
         * @param doesIntersect The function which calculates whether a node intersects
         * @param nodeConsumer The consumer which accepts the nodes intersecting
         * */
        public void traverse(Function<TreeNode<T>, Boolean> doesIntersect, Consumer<TreeNode<T>> nodeConsumer) {
            // No collision, then no reason to traverse any further.
            if (!doesIntersect.apply(this))
                return;

            // If this node is a leaf, then the consumer must consume.
            if (isLeaf()) {
                nodeConsumer.accept(this);
                return;
            }
            // Otherwise, we find children suitable and continue traversing.
            TreeNode<T> child = findChildContaining(doesIntersect);

            if (child != null) {
                child.traverse(doesIntersect, nodeConsumer);
            } else {
                nw.traverse(doesIntersect, nodeConsumer);
                ne.traverse(doesIntersect, nodeConsumer);
                sw.traverse(doesIntersect, nodeConsumer);
                se.traverse(doesIntersect, nodeConsumer);
            }
        }

        public void traverse(Vector2f vector2f, Consumer<TreeNode<T>> nodeConsumer) {
            traverse((node) -> node.boundaries.contains(vector2f), nodeConsumer);
        }
        /**
         * Traverses through the nodes, beginning with this node.
         * */
        public void traverse(Shape2f shape, Consumer<TreeNode<T>> nodeConsumer) {
            traverse((node) -> node.boundaries.collides(shape), nodeConsumer);
        }

        /**
         * Creates the children for this tree node. This node will no longer be considered a leaf node.
         * */
        private void createChildren() {
            float minX = boundaries.getMinX();
            float minY = boundaries.getMinY();
            float maxX = boundaries.getMaxX();
            float maxY = boundaries.getMaxY();

            float halfWidth = boundaries.getHalfX();
            float halfHeight = boundaries.getHalfY();

            nw = new TreeNode<>(new AABB2f(minX, minY, minX + halfWidth, minY + halfHeight), maxSize);

            ne = new TreeNode<>(new AABB2f(minX + halfWidth, minY, maxX, minY + halfHeight), maxSize);

            sw = new TreeNode<>(new AABB2f(minX, minY + halfHeight, minX + halfWidth, maxY), maxSize);

            se = new TreeNode<>(new AABB2f(minX + halfWidth, minY + halfHeight, maxX, maxY), maxSize);
        }


        /**
         * @return The child tree node which contains this shape fully. If no child contains the shape fully, then it
         * will return null.
         * */
        private TreeNode<T> findChildContaining(Function<TreeNode<T>, Boolean> doesIntersect) {
            if (doesIntersect.apply(nw)) {
                return nw;
            } else if (doesIntersect.apply(ne)) {
                return ne;
            } else if (doesIntersect.apply(sw)) {
                return sw;
            } else if (doesIntersect.apply(se)) {
                return se;
            }
            return null;
        }

        private TreeNode<T> findChildContaining(Vector2f vector2f) {
            return findChildContaining((node) -> node.boundaries.contains(vector2f));
        }

        /**
         * @return The child tree node which contains this shape fully. If no child contains the shape fully, then it
         * will return null.
         * */
        private TreeNode<T> findChildContaining(Shape2f boundary) {
            return findChildContaining((node) -> node.boundaries.contains(boundary));
        }

        /**
         * Fills up the children of this node. This is done once a node reaches max capacity and the children must be
         * created.
         * */
        private void addElementsToChildren(Iterable<TreeElement<T>> elements) {
            for (TreeElement<T> element : elements) {
                addElementToChildren(element);
            }
        }

        private void addElementToChildren(TreeElement<T> element) {
            traverse(element.getShape(), node -> node.add(element));
        }


        /**
         * @return True if this tree node does not contain children.
         * */
        public boolean isLeaf() {
            return nw == null && ne == null && sw == null && se == null;
        }

        /**
         * @return Every element within this node. This list will be empty if this node is not a leaf.
         * */
        public Set<T> getElements() {
            return elements.stream().map((e) -> e.element).collect(Collectors.toSet());
        }

        /**
         * @return Every element within this node and optionally every element of children nodes.
         * @param deep When true, the elements within the children of this node will also be returned.
         * */
        public Set<T> getElements(boolean deep) {
            Set<T> elements = getElements();

            if (deep && !isLeaf()) {
                elements.addAll(nw.getElements(true));
                elements.addAll(ne.getElements(true));
                elements.addAll(sw.getElements(true));
                elements.addAll(se.getElements(true));
            }

            return elements;
        }

        /**
         * @return True if this tree node breaches the max capacity.
         * */
        private boolean isOverFilled() {
            return elements.size() > maxSize;
        }

        public List<TreeElement<T>> getTreeElements() {
            return elements;
        }

        public AABB2f getBoundaries() {
            return boundaries;
        }

        @Override
        public String toString() {
            return "TreeNode{"
                    + (!isLeaf() ? getChildrenString() : "")
                    + ", elements=" + elements
                    + ", boundaries=" + boundaries
                    + "}";
        }

        /**
         * @return A nicely formatted string representing the children of this node. This is for the toString function.
         * */
        private String getChildrenString() {
            return      "nw=" + (nw == null ? "null" : (nw.isLeaf() && nw.elements.isEmpty() ? "empty" : nw))
                    + ", ne=" + (ne == null ? "null" : (ne.isLeaf() && ne.elements.isEmpty() ? "empty" : ne))
                    + ", sw=" + (sw == null ? "null" : (sw.isLeaf() && sw.elements.isEmpty() ? "empty" : sw))
                    + ", se=" + (se == null ? "null" : (se.isLeaf() && se.elements.isEmpty() ? "empty" : se));
        }
    }

    /**
     * Contains the shape used to insert some element into a quadtree.
     * */
    public static class TreeElement<T> {

        private Shape2f shape;

        private T element;

        public TreeElement(Shape2f shape, T element) {
            this.shape = shape;
            this.element = element;
        }

        public Shape2f getShape() {
            return shape;
        }

        public void setShape(Shape2f shape) {
            this.shape = shape;
        }

        public T getElement() {
            return element;
        }

        public void setElement(T element) {
            this.element = element;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TreeElement<?> that = (TreeElement<?>) o;
            return Objects.equals(shape, that.shape) &&
                    Objects.equals(element, that.element);
        }

        @Override
        public int hashCode() {
            return Objects.hash(shape, element);
        }
    }
}
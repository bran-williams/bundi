package com.branwilliams.bundi.engine.shape;

import org.joml.Vector2f;

import java.util.*;
import java.util.function.Consumer;

public class SeparatingAxis {

    public interface ConvexShape {

        /**
         * @return The number of vertices of this shape.
         * */
        int getVertexCount();

        /**
         * @return The vertex at this index. (starting from 0 to vertexCount)
         * */
        Vector2f getVertex(int index);

        /**
         * Calculates the edge vectors of this shape.
         * @return An array of all edges of this shape.
         * */
        default Vector2f[] getEdges() {
            int vertexCount = getVertexCount();
            Vector2f[] edges = new Vector2f[vertexCount];
            for (int i = 0; i < vertexCount; i++) {
                Vector2f edge = getVertex((i + 1) % vertexCount);
                edge.sub(getVertex(i));
                edges[i] = edge;
            }
            return edges;
        }

        /**
         * Computes the geometric center of this shape. This is useful if the center vector is not the actual center of this
         * shape.
         * @return The geometric center of this shape.
         * */
        default Vector2f getGeometricCenter() {
            int vertexCount = getVertexCount();

            Vector2f sum = getVertex(0);
            for (int i = 1; i < vertexCount; i++) {
                sum.add(getVertex(i));
            }
            sum.x /= vertexCount;
            sum.y /= vertexCount;
            return sum;
        }

        /**
         * Determines if this shape is convex assuming it is a simple polygon that is not self-intersecting.
         * See <a href="https://stackoverflow.com/a/25304159">this stackoverflow post</a>.
         * @return True if this shape is convex.
         * */
        static boolean isConvex(ConvexShape shape) {
            if (shape.getVertexCount() < 4)
                return true;

            boolean sign = false;
            int n = shape.getVertexCount();

            for (int i = 0; i < n; i++) {
                double dx1 = shape.getVertex((i + 2) % n).x - shape.getVertex((i + 1) % n).x;
                double dy1 = shape.getVertex((i + 2) % n).y - shape.getVertex((i + 1) % n).y;
                double dx2 = shape.getVertex(i).x - shape.getVertex((i + 1) % n).x;
                double dy2 = shape.getVertex(i).y - shape.getVertex((i + 1) % n).y;
                double zcrossproduct = dx1 * dy2 - dy1 * dx2;

                if (i == 0)
                    sign = zcrossproduct > 0;
                else if (sign != (zcrossproduct > 0))
                    return false;
            }
            return true;
        }

    }

    public static class SimpleConvexShape implements ConvexShape {

        private Vector2f center;

        private Vector2f[] vertices;

        public SimpleConvexShape(Vector2f... vertices) {
            this.center = new Vector2f();
            this.vertices = vertices;
            if (!ConvexShape.isConvex(this)) {
                throw new IllegalArgumentException("Vertices are not convex: " + Arrays.toString(vertices));
            }
        }

        public Vector2f getCenter() {
            return center;
        }

        public void setCenter(Vector2f center) {
            this.center = center;
        }

        public SimpleConvexShape center(Vector2f center) {
            this.setCenter(center);
            return this;
        }

        public Vector2f[] getVertices() {
            return vertices;
        }

        public void setVertices(Vector2f[] vertices) {
            this.vertices = vertices;
        }

        /**
         * Creates a new vector of the i-th vertex of this shape, offset by the center vector.
         * @return The i-th vertex of this shape, offset by the center vector.
         * */
        @Override
        public Vector2f getVertex(int index) {
            return new Vector2f(center).add(vertices[index]);
        }

        @Override
        public int getVertexCount() {
            return vertices.length;
        }

    }

    /**
     * Determines if two convex shapes collide. If they collide, the push-vector (to push shape2 outside of shape1) will
     * be provided to a consumer to handle the collision appropriately.
     * This is computed via the Separating Axis Theorem.
     * @return True if the two shapes collide.
     * */
    public static boolean collide(ConvexShape shape1, ConvexShape shape2, Consumer<Vector2f> pushVectorConsumer) {
        Vector2f minPushVector = calculateMinPushVectorForEdges(null, shape1.getEdges(), shape1, shape2);
        minPushVector = calculateMinPushVectorForEdges(minPushVector, shape2.getEdges(), shape1, shape2);

        if (minPushVector != null) {
            Vector2f d = shape1.getGeometricCenter().sub(shape2.getGeometricCenter());
            if (d.dot(minPushVector) > 0) {
                minPushVector.negate();
            }
            pushVectorConsumer.accept(minPushVector);
        }
        return true;
    }

    /**
     * Iterates over every edge provided, calculates the perpendicular vector for the given edge,
     * and performs the separating axis test to determine the minimum push vector.
     * */
    private static Vector2f calculateMinPushVectorForEdges(Vector2f minPushVector, Vector2f[] edges, ConvexShape shape1,
                                            ConvexShape shape2) {
        float minPushVectorDotProduct = minPushVector == null ? Float.MAX_VALUE : minPushVector.dot(minPushVector);

        // Find the push vectors for each axis.
        // If there is a single axis without a push vector, then SAT failed.
        for (Vector2f edge : edges) {
            Vector2f orthogonal = new Vector2f(-edge.y, edge.x);
            Vector2f pushVector = separatingAxis(orthogonal, shape1, shape2);
            // this means there is a separating axis! no collision
            if (pushVector == null) {
                return null;
            } else if (pushVector.dot(pushVector) < minPushVectorDotProduct) {
                minPushVector = pushVector;
                minPushVectorDotProduct = minPushVector.dot(minPushVector);
            }
        }
        return minPushVector;
    }

    /**
     * Projects all vertices onto the plane described by orthogonal and determines if there is an overlap. If so,
     * the push vector to solve this overlap is computed and return. otherwise, null is returned if no overlap.
     * */
    public static Vector2f separatingAxis(Vector2f orthogonal, ConvexShape shape, ConvexShape shape1) {
        float min1 = Float.MAX_VALUE, min2 = Float.MAX_VALUE;
        float max1 = Float.MIN_VALUE, max2 = Float.MIN_VALUE;

        // project shape 1 onto the axis
        for (int i = 0; i < shape.getVertexCount(); i++) {
            float projection = shape.getVertex(i).dot(orthogonal);
            min1 = Math.min(min1, projection);
            max1 = Math.max(max1, projection);
        }

        // project shape 2 onto the axis
        for (int i = 0; i < shape1.getVertexCount(); i++) {
            float projection = shape1.getVertex(i).dot(orthogonal);
            min2 = Math.min(min2, projection);
            max2 = Math.max(max2, projection);
        }

        // overlapping projections means not separating axis
        if (max1 >= min2 && max2 >= min1) {
            float d = Math.min(max2 - min1, max1 - min2);
            Vector2f pushVector = new Vector2f(orthogonal);
            pushVector.mul(d / orthogonal.dot(orthogonal) + 1E-10F);
            return pushVector;
        }
        return null;
    }

//    /**
//     * Determines if two convex shapes collide. If they collide, the push-vector (to push shape2 outside of shape1) will
//     * be provided to a consumer to handle the collision appropriately.
//     * This is computed via the Separating Axis Theorem.
//     * @return True if the two shapes collide.
//     * */
//    public static boolean collide(ConvexShape shape1, ConvexShape shape2, Consumer<Vector2f> pushVectorConsumer) {
//        Vector2f[] edges = concat(shape1.getEdges(), shape2.getEdges());
//        List<Vector2f> pushVectors = new ArrayList<>();
//
//        // Find the push vectors for each axis.
//        // If there is a single axis without a push vector, then SAT failed.
//        for (int i = 0; i < edges.length; i++) {
//            Vector2f orthogonal = new Vector2f(-edges[i].y, edges[i].x);
//            Vector2f pushVector = separatingAxis(orthogonal, shape1, shape2);
//            if (pushVector == null) {
//                return false;
//            } else {
//                pushVectors.add(pushVector);
//            }
//        }
//        Optional<Vector2f> minPushVector = pushVectors.stream()
//                .min(Comparator.comparingDouble((v) -> v.dot(v)));
//
//        if (minPushVector.isPresent()) {
//            Vector2f d = shape1.getGeometricCenter().sub(shape2.getGeometricCenter());
//            if (d.dot(minPushVector.get()) > 0) {
//                minPushVector.get().negate();
//            }
//            pushVectorConsumer.accept(minPushVector.get());
//        }
//        return true;
//    }

//    private static <T> T[] concat(T[] first, T[] second) {
//        T[] output = Arrays.copyOf(first, first.length + second.length);
//
//        for (int i = 0; i < second.length; i++) {
//            output[first.length + i] = second[i];
//        }
//        return output;
//    }

    public static ConvexShape rectangle(float minX, float minY, float maxX, float maxY) {
        return new SimpleConvexShape(
                new Vector2f(minX, maxY),
                new Vector2f(minX, minY),
                new Vector2f(maxX, minY),
                new Vector2f(maxX, maxY)
        );
    }

}

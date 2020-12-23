package com.branwilliams.bundi.engine.shape;

import org.joml.Vector2f;

/**
 *
 * Created by Brandon Williams on 10/15/2018.
 */
public interface Shape2f {

    /**
     * @return True if this object collides with the other object.
     * */
    boolean collides(Shape2f other);

    /**
     * Determines if there is an intersection between this Shape2f and another Shape2f.
     * Returns the 'push vector' for an intersection between this shape and some other shape.
     * <pre>
     *         |---------|
     *         | other   |
     * |-------|---|     |
     * | this  |<- |     |
     * |       |<- |     |
     * |-------|---|-----|
     * </pre>
     * @return The Vector2f intersection vector from the other Shape2f to this Shape2f.
     * */
    Vector2f intersection(Shape2f other);

    /**
     * @return True if this object fully contains the other object.
     * */
    boolean contains(Shape2f other);

    /**
     * @return True if this object contains the point given.
     * */
    boolean contains(Vector2f point);

    /**
     * Centers this shape to the provided position.
     * */
    Shape2f center(float x, float y);

    /**
     * Centers this shape to the provided position.
     * */
    default Shape2f center(Vector2f position) {
        return center(position.x, position.y);
    }

    /**
     * Centers this shape to the provided position.
     * */
    Vector2f getCenter();

    /**
     * Creates a copy of this object. This will be a new instance.
     * @return A new copy of this shape.
     * */
    Shape2f copy();

}

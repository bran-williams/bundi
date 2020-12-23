package com.branwilliams.bundi.engine.shape;

import org.joml.Vector3f;

/**
 *
 * Created by Brandon Williams on 10/15/2018.
 */
public interface Shape3f {

    /**
     * @return True if this object collides with the other object.
     * */
    boolean collides(Shape3f other);

    /**
     * Determines if there is an intersection between this Shape3f and another Shape3f.
     * @return The Vector3f intersection vector from the other Shape3f to this Shape3f.
     * */
    Vector3f intersection(Shape3f other);

    /**
     * @return True if this object fully contains the other object.
     * */
    boolean contains(Shape3f other);

    /**
     * @return True if this object contains the point given.
     * */
    boolean contains(Vector3f point);

    /**
     * Centers this collidable to the provided position.
     * */
    default void center(float x, float y, float z) {
        getCenter().set(x, y, z);
    }

    /**
     * Centers this collidable to the provided position.
     * */
    default void center(Vector3f position) {
        getCenter().set(position);
    }

    /**
     * Centers this collidable to the provided position.
     * */
    Vector3f getCenter();

}

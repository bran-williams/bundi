package com.branwilliams.bundi.engine.shader;

import org.joml.Vector3f;

/**
 * A transformable object is something that can be translated, rotated, and scaled. This object is useful for
 * representing an object within 3D space. Using
 * Current implementation does not use quaternions for rotation, so TODO use quaternions for rotation.
 * */
public interface Transformable extends Cloneable {

    /**
     * @return The position of this transformable.
     * */
    Vector3f getPosition();

    /**
     * @return The X position of this transformable.
     * */
    default float x() {
        return getPosition().x;
    }

    /**
     * @return The Y position of this transformable.
     * */
    default float y() {
        return getPosition().y;
    }

    /**
     * @return The Z position of this transformable.
     * */
    default float z() {
        return getPosition().z;
    }

    /**
     * Sets the position of this transformable.
     * */
    void setPosition(Vector3f position);

    /**
     * Sets the position of this transformable.
     * */
    void setPosition(float x, float y, float z);

    /**
     * Sets the position of this transformable.
     * */
    default Transformable position(Vector3f position) {
        this.setPosition(position);
        return this;
    }

    /**
     * Sets the position of this transformable.
     * */
    default Transformable position(float x, float y, float z) {
        this.setPosition(x, y, z);
        return this;
    }

    /**
     * Adds the provided position to this position.
     * */
    default Transformable transform(Vector3f position) {
        this.getPosition().add(position);
        return this;
    }

    default Transformable transform(float x, float y, float z) {
        this.getPosition().add(x, y, z);
        return this;
    }

    /**
     * @return The rotation of this transformable (in euler angles).
     * */
    Vector3f getRotation();

    /**
     * @return The X rotation of this transformable.
     * */
    default float rX() {
        return getRotation().x;
    }

    /**
     * @return The Y rotation of this transformable.
     * */
    default float rY() {
        return getRotation().y;
    }

    /**
     * @return The Z rotation of this transformable.
     * */
    default float rZ() {
        return getRotation().z;
    }

    /**
     * Sets the rotation of this transformable.
     * */
    void setRotation(Vector3f rotation);

    /**
     * Sets the rotation of this transformable.
     * */
    void setRotation(float x, float y, float z);

    /**
     * Sets the rotation of this transformable.
     * */
    default Transformable rotate(Vector3f rotation) {
        this.setRotation(rotation);
        return this;
    }

    /**
     * Sets the rotation of this transformable.
     * */
    default Transformable rotate(float x, float y, float z) {
        this.setRotation(x, y, z);
        return this;
    }

    /**
     * @return The scale of this transformable.
     * */
    float getScale();

    /**
     * Sets the scale of this transformable.
     * */
    void setScale(float scale);

    /**
     * Sets the scale of this transformable.
     * */
    default Transformable scale(float scale) {
        this.setScale(scale);
        return this;
    }

    /**
     * Produces a copy of this transformable.
     * */
    Transformable copy();

    /**
     * Creates a transformable who has the position and rotation of {0, 0, 0} and scale of 1. The transform is also
     * immutable.
     * @return An empty Transformable object.
     * */
    static Transformable empty() {
        return ImmutableEmptyTransformable.instance;
    }

    final class ImmutableEmptyTransformable implements Transformable {

        private static final ImmutableEmptyTransformable instance = new ImmutableEmptyTransformable();

        private final Vector3f zero = new Vector3f(0, 0, 0);

        private ImmutableEmptyTransformable() {

        }

        @Override
        public Vector3f getPosition() {
            return zero;
        }

        @Override
        public void setPosition(Vector3f position) {

        }

        @Override
        public void setPosition(float x, float y, float z) {

        }

        @Override
        public Vector3f getRotation() {
            return zero;
        }

        @Override
        public void setRotation(Vector3f rotation) {

        }

        @Override
        public void setRotation(float x, float y, float z) {

        }

        @Override
        public float getScale() {
            return 1;
        }

        @Override
        public void setScale(float scale) {

        }

        @Override
        public Transformable copy() {
            return this;
        }
    }

}

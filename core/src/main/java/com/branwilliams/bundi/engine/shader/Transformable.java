package com.branwilliams.bundi.engine.shader;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
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
    default Transformable move(Vector3f position) {
        this.getPosition().add(position);
        return this;
    }

    default Transformable move(float x, float y, float z) {
        this.getPosition().add(x, y, z);
        return this;
    }

    /**
     * @return The rotation of this transformable (as a quaternion).
     * */
    Quaternionf getRotation();

    /**
     * Sets the rotation of this transformable.
     * */
    void setRotation(Quaternionf rotation);

    /**
     * Sets the rotation of this transformable.
     * */
    void setRotation(float x, float y, float z, float w);

    /**
     * @return The rotation of this transformable (in euler angles).
     * */
    default Vector3f getRotationAsEuler() {
        return getRotation().getEulerAnglesXYZ(new Vector3f());
    }

    /**
     * @return The X rotation of this transformable.
     * */
    default float eulerX() {
        return getRotationAsEuler().x;
    }

    /**
     * @return The Y rotation of this transformable.
     * */
    default float eulerY() {
        return getRotationAsEuler().y;
    }

    /**
     * @return The Z rotation of this transformable.
     * */
    default float eulerZ() {
        return getRotationAsEuler().z;
    }

    /**
     * Sets the rotation of this transformable.
     * */
    void setRotationFromEuler(Vector3f rotation);

    /**
     * Sets the rotation of this transformable.
     * */
    void setRotationFromEuler(float x, float y, float z);

    /**
     * Sets the rotation of this transformable.
     * */
    default Transformable rotateFromEuler(Vector3f rotation) {
        this.setRotationFromEuler(rotation);
        return this;
    }

    /**
     * Sets the rotation of this transformable.
     * */
    default Transformable rotateFromEuler(float x, float y, float z) {
        this.setRotationFromEuler(x, y, z);
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
     * Copies this transform and adds the provided transform to it.
     * */
    default Transformable add(Transformable transform) {
        Transformable res = copy();

        res.getPosition().add(transform.getPosition());
        res.getRotationAsEuler().add(transform.getRotationAsEuler());
        res.setScale(res.getScale() + transform.getScale());

        return res;
    }

    /**
     * This will create a matrix representation of this transformable. For the transformable of an object within a
     * scene, this is considered the model matrix.
     *
     * @return A matrix representation of this transformable.
     * */
    default Matrix4f toMatrix(Matrix4f matrix) {
        return matrix.identity()
                .translate(getPosition())
                .rotate(getRotation())
                .scale(getScale());
    }

    /**
     * Creates a transformable who has the position and rotation of {0, 0, 0} and scale of 1. The transform is also
     * immutable.
     * @return An empty Transformable object.
     * */
    static Transformable empty() {
        return ImmutableEmptyTransformable.instance;
    }

    /**
     * An immutable transformable whose position is (0,0,0) and whose rotation is (0,0,0,1), and whose scale is 1.
     * */
    final class ImmutableEmptyTransformable implements Transformable {

        private static final ImmutableEmptyTransformable instance = new ImmutableEmptyTransformable();

        private final Vector3f zero = new Vector3f(0, 0, 0);

        private final Quaternionf zeroQ = new Quaternionf();

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
        public Quaternionf getRotation() {
            return zeroQ;
        }

        @Override
        public void setRotation(Quaternionf rotation) {

        }

        @Override
        public void setRotation(float x, float y, float z, float w) {

        }

        @Override
        public Vector3f getRotationAsEuler() {
            return zero;
        }

        @Override
        public void setRotationFromEuler(Vector3f rotation) {

        }

        @Override
        public void setRotationFromEuler(float x, float y, float z) {

        }

        @Override
        public float getScale() {
            return 1F;
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

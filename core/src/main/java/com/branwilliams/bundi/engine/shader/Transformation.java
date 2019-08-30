package com.branwilliams.bundi.engine.shader;

import org.joml.Vector3f;

/**
 * Basic implementation of the {@link Transformable} interface. Uses euler angles for the representation of the
 * rotation.
 * TODO Quaternion angle representation.
 * Created by Brandon Williams on 6/29/2018.
 */
public class Transformation implements Transformable {

    private Vector3f position, rotation;

    private float scale;

    public Transformation(Vector3f position, Vector3f rotation, float scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public Transformation(Vector3f position, Vector3f rotation) {
        this(position, rotation, 1.0F);
    }

    public Transformation(Vector3f position) {
        this(position, new Vector3f());
    }

    public Transformation() {
        this(new Vector3f());
    }

    public Transformation(Transformable transformable) {
        this.position = new Vector3f(transformable.getPosition());
        this.rotation = new Vector3f(transformable.getRotation());
        this.scale = transformable.getScale();
    }

    @Override
    public Vector3f getPosition() {
        return position;
    }

    @Override
    public void setPosition(Vector3f position) {
        this.position = position;
    }

    @Override
    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    @Override
    public Vector3f getRotation() {
        return rotation;
    }

    @Override
    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    @Override
    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    @Override
    public float getScale() {
        return scale;
    }

    @Override
    public void setScale(float scale) {
        this.scale = scale;
    }

    @Override
    public Transformable copy() {
        return new Transformation(this);
    }

    @Override
    public String toString() {
        return "[position=" + position.toString() + ", rotation=" + rotation.toString() + ", scale=" + scale + "]";
    }
}

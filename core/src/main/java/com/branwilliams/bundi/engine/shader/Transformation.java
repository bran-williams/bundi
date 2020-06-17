package com.branwilliams.bundi.engine.shader;

import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Basic implementation of the {@link Transformable} interface. Uses euler angles for the representation of the
 * rotation.
 * Created by Brandon Williams on 6/29/2018.
 */
public class Transformation implements Transformable {

    private Vector3f position;

    private Quaternionf rotation;

    private float scale;

    public Transformation(Vector3f position, Quaternionf rotation, float scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public Transformation(Vector3f position, Quaternionf rotation) {
        this(position, rotation, 1.0F);
    }

    public Transformation(Vector3f position) {
        this(position, new Quaternionf());
    }

    public Transformation() {
        this(new Vector3f());
    }

    public Transformation(Transformable transformable) {
        this.position = new Vector3f(transformable.getPosition());
        this.rotation = new Quaternionf(transformable.getRotation());
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
    public Quaternionf getRotation() {
        return this.rotation;
    }

    @Override
    public void setRotation(Quaternionf rotation) {
        this.rotation = rotation;
    }

    @Override
    public void setRotation(float x, float y, float z, float w) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
        this.rotation.w = w;
    }

    @Override
    public void setRotationFromEuler(Vector3f rotation) {
        this.setRotationFromEuler(rotation.x, rotation.y, rotation.z);
    }

    @Override
    public void setRotationFromEuler(float x, float y, float z) {
        this.rotation.rotateXYZ(x, y, z);
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

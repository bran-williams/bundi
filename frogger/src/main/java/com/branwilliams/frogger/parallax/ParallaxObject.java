package com.branwilliams.frogger.parallax;

import org.joml.Vector2f;

public class ParallaxObject <T> {

    private Vector2f offset = new Vector2f();;

    private Vector2f velocity = new Vector2f();

    private ParallaxMovementType movementType;

    private Vector2f size;

    private T object;

    public ParallaxObject() {
    }

    public Vector2f getOffset() {
        return offset;
    }

    public void setOffset(Vector2f offset) {
        this.offset = offset;
    }

    public Vector2f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2f velocity) {
        this.velocity = velocity;
    }

    public ParallaxMovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(ParallaxMovementType movementType) {
        this.movementType = movementType;
    }

    public Vector2f getSize() {
        return size;
    }

    public void setSize(Vector2f size) {
        this.size = size;
    }


    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

}

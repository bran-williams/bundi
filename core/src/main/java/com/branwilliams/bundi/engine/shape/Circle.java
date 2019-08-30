package com.branwilliams.bundi.engine.shape;

import org.joml.Vector2f;

/**
 * Created by Brandon Williams on 10/15/2018.
 */
public class Circle implements Shape2f {

    private float radius;

    private Vector2f center;

    public Circle(float radius) {
        this(radius, new Vector2f());
    }

    public Circle(float radius, Vector2f center) {
        this.radius = radius;
        this.center = center;
    }

    @Override
    public boolean collides(Shape2f other) {
        if (other instanceof Circle) {
            Circle otherCircle = (Circle) other;
            float dist = otherCircle.getCenter().distance(this.getCenter());
            return dist <= otherCircle.getRadius() + this.getRadius();
        } else if (other instanceof AABB) {
            AABB otherAABB = (AABB) other;
            float nearestX = Math.max(otherAABB.getMinX(), Math.min(getCenter().x, otherAABB.getMaxX()));
            float nearestY = Math.max(otherAABB.getMinY(), Math.min(getCenter().y, otherAABB.getMaxY()));
            float dx = getCenter().x - nearestX;
            float dy = getCenter().y - nearestY;
            return dx * dx + dy * dy <= this.getRadius() * this.getRadius();
        }
        return false;
    }

    @Override
    public Vector2f intersection(Shape2f other) {
        Vector2f intersection = null;

        if (other instanceof AABB) {
            Vector2f boxClosestPoint = new Vector2f();

            intersection = new Vector2f(center);
            Vector2f pointMinusC = boxClosestPoint.sub(center);
            intersection.add(pointMinusC.mul(radius / (pointMinusC.length())));
        }
        return intersection;
    }

    @Override
    public boolean contains(Shape2f other) {
        if (other instanceof Circle) {
            Circle otherCircle = (Circle) other;
            float dist = otherCircle.getCenter().distance(this.getCenter());
            return this.getRadius() >= dist + otherCircle.getRadius();
        } else if (other instanceof AABB) {
            AABB otherAABB = (AABB) other;
            return this.getCenter().x - this.getRadius() <= otherAABB.getMinX() &&
                    this.getCenter().x + this.getRadius() >= otherAABB.getMaxX() &&
                    this.getCenter().y - this.getRadius() <= otherAABB.getMinY() &&
                    this.getCenter().y + this.getRadius() >= otherAABB.getMaxY();
        }
        return false;
    }

    @Override
    public boolean contains(Vector2f point) {
        return false;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Override
    public Vector2f getCenter() {
        return center;
    }

    @Override
    public Shape2f copy() {
        return new Circle(radius).center(this.center);
    }

    @Override
    public Shape2f center(Vector2f center) {
        this.center = center;
        return this;
    }

    @Override
    public Shape2f center(float x, float y) {
        this.center.x = x;
        this.center.y = y;
        return this;
    }
}

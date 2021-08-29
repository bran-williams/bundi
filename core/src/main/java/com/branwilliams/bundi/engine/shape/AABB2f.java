package com.branwilliams.bundi.engine.shape;

import org.joml.Vector2f;

/**
 *
 * AABB = Axis Aligned Bounding Box.
 *
 * Created by Brandon Williams on 10/9/2018.
 */
public class AABB2f implements Shape2f, SeparatingAxis.ConvexShape {

    private static final int VERTEX_COUNT = 4;

    private Vector2f min, max;

    private Vector2f center;

    public AABB2f(float minX, float minY, float maxX, float maxY) {
        this.min = new Vector2f(minX, minY);
        this.max = new Vector2f(maxX, maxY);
        this.center = new Vector2f();
    }

    public AABB2f(Vector2f min, Vector2f max) {
        this.min = min;
        this.max = max;
        this.center = new Vector2f();
    }

    /**
     * Centers the aabb to the position provided.
     * */
    @Override
    public Shape2f center(Vector2f center) {
        this.center = center;
        return this;
    }

    @Override
    public Shape2f center(float x, float y) {
        this.center.set(x, y);
        return this;
    }

    /**
     * @return True if the provided aabb collides with this one.
     * */
    public boolean collides(Shape2f other) {
        if (other instanceof AABB2f) {
            AABB2f otherAABB2f = (AABB2f) other;
            return (getMaxX() > otherAABB2f.getMinX() && getMinX() < otherAABB2f.getMaxX()) &&
                    (getMaxY() > otherAABB2f.getMinY() && getMinY() < otherAABB2f.getMaxY());
        } else if (other instanceof Circle) {
            Circle otherCircle = (Circle) other;
            float nearestX = Math.max(getMinX(), Math.min(otherCircle.getCenter().x, getMaxX()));
            float nearestY = Math.max(getMinY(), Math.min(otherCircle.getCenter().y, getMaxY()));
            float dx = otherCircle.getCenter().x - nearestX;
            float dy = otherCircle.getCenter().y - nearestY;
            return dx * dx + dy * dy <= otherCircle.getRadius() * otherCircle.getRadius();
        }
        return false;
    }

    @Override
    public Vector2f intersection(Shape2f other) {
        Vector2f intersection = new Vector2f();

        // Ripped from https://noonat.github.io/intersect/.
        // TODO Will provide a better set of intersection tests, most likely SAT theorem and a small physics engine.
        if (other instanceof AABB2f) {
            AABB2f otherAABB2f = (AABB2f) other;
            float dx = otherAABB2f.getCenter().x - this.getCenter().x;
            float px = (otherAABB2f.getHalfX() + this.getHalfX()) - Math.abs(dx);
            if (px <= 0) {
                return intersection;
            }

            float dy = otherAABB2f.getCenter().y - this.getCenter().y;
            float py = (otherAABB2f.getHalfY() + this.getHalfY()) - Math.abs(dy);
            if (py <= 0) {
                return intersection;
            }

            if (px < py) {
                float sx = Math.signum(dx);
                intersection.x = px * sx;
                //hit.normal.x = sx;
                //hit.pos.x = this.pos.x + (this.half.x * sx);
                //hit.pos.y = box.pos.y;
            } else {
                float sy = Math.signum(dy);
                intersection.y = py * sy;
                //hit.normal.y = sy;
                //hit.pos.x = box.pos.x;
                //hit.pos.y = this.pos.y + (this.half.y * sy);
            }
        }
        return intersection;
    }

    /**
     * @return True if this aabb completely contains the other aabb.
     * */
    @Override
    public boolean contains(Shape2f other) {
        if (other instanceof AABB2f) {
            AABB2f otherAABB2f = (AABB2f) other;
            return (getMinX() <= otherAABB2f.getMinX() && getMaxX() >= otherAABB2f.getMaxX()) &&
                    (getMinY() <= otherAABB2f.getMinY() && getMaxY() >= otherAABB2f.getMaxY());
        } else if (other instanceof Circle) {
            Circle otherCircle = (Circle) other;
            float centerX = otherCircle.getCenter().x;
            float centerY = otherCircle.getCenter().y;
            float radius = otherCircle.getRadius();
            return this.getMinX() <= centerX - radius && this.getMaxX() >= centerX + radius &&
                    this.getMinY() <= centerY - radius && this.getMaxY() >= centerY + radius;
        }
        return false;
    }

    /**
     * @return True if the provided point is within this aabb.
     * */
    public boolean contains(Vector2f point) {
        return contains(point.x, point.y);
    }

    public boolean contains(float x, float y) {
        return (x >= getMinX() && x <= getMaxX()) &&
                (y >= getMinY() && y <= getMaxY());
    }

    public float getHalfX() {
        return (max.x - min.x) / 2F;
    }

    public float getHalfY() {
        return (max.y - min.y) / 2F;
    }

    public float getMinX() {
        return center.x + min.x;
    }

    public float getMinY() {
        return center.y + min.y;
    }

    public float getMaxX() {
        return center.x + max.x;
    }

    public float getMaxY() {
        return center.y + max.y;
    }

    @Override
    public Vector2f getCenter() {
        return center;
    }

    @Override
    public Shape2f copy() {
        return new AABB2f(this.min, this.max).center(this.center);
    }

    public void setMin(Vector2f min) {
        this.min = min;
    }

    public Vector2f getMin() {
        return min;
    }

    public void setMax(Vector2f max) {
        this.max = max;
    }

    public Vector2f getMax() {
        return max;
    }

    @Override
    public String toString() {
        return "AABB2f{" +
                "minX=" + getMinX() +
                ", minY=" + getMinY() +
                ", maxX=" + getMaxX() +
                ", maxY=" + getMaxY() +
                ", center=" + getCenter() +
                '}';
    }

    @Override
    public int getVertexCount() {
        return VERTEX_COUNT;
    }

    @Override
    public Vector2f getVertex(int index) {
        Vector2f pos = new Vector2f(center);
        switch (index) {
            case 0:
                pos.add(min.x, max.y);
                break;
            case 1:
                pos.add(min);
                break;
            case 2:
                pos.add(max.x, min.y);
                break;
            case 4:
                pos.add(max);
                break;
        }
        return pos;
    }
}

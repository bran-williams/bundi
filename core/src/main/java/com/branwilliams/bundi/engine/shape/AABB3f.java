package com.branwilliams.bundi.engine.shape;

import org.joml.Vector3f;

public class AABB3f implements Shape3f {

    private Vector3f center;

    private float minX, maxX;

    private float minY, maxY;

    private float minZ, maxZ;

    public AABB3f() {
        this(0.5F);
    }

    public AABB3f(float halfSize) {
        this(halfSize, halfSize, halfSize, halfSize, halfSize, halfSize);
    }

    public AABB3f(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        this.center = new Vector3f();
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.minZ = minZ;
        this.maxZ = maxZ;
    }

    @Override
    public boolean collides(Shape3f other) {
        if (other instanceof AABB3f) {
            AABB3f otherAABB = (AABB3f) other;
            return (this.minX <= otherAABB.maxX && this.maxX >= otherAABB.minX) &&
                    (this.minY <= otherAABB.maxY && this.maxY >= otherAABB.minY) &&
                    (this.minZ <= otherAABB.maxZ && this.maxZ >= otherAABB.minZ);
        }
        return false;
    }

    @Override
    public Vector3f intersection(Shape3f other) {
        // TODO AABB3f intersection test
        return null;
    }

    @Override
    public boolean contains(Shape3f other) {
        if (other instanceof AABB3f) {
            AABB3f otherAABB = (AABB3f) other;
            return (this.minX < otherAABB.minX && this.maxX > otherAABB.maxX) &&
                    (this.minY < otherAABB.minY && this.maxY > otherAABB.maxY) &&
                    (this.minZ < otherAABB.minZ && this.maxZ > otherAABB.maxZ);
        }
        return false;
    }

    @Override
    public boolean contains(Vector3f point) {
        return (point.x >= center.x + minX && point.x <= center.x + maxX) &&
                (point.y >= center.y + minY && point.y <= center.y + maxY) &&
                (point.z >= center.z + minZ && point.z <= center.z + maxZ);
    }

    @Override
    public Vector3f getCenter() {
        return center;
    }

    public float getMinX() {
        return minX;
    }

    public void setMinX(float minX) {
        this.minX = minX;
    }

    public float getMaxX() {
        return maxX;
    }

    public void setMaxX(float maxX) {
        this.maxX = maxX;
    }

    public float getMinY() {
        return minY;
    }

    public void setMinY(float minY) {
        this.minY = minY;
    }

    public float getMaxY() {
        return maxY;
    }

    public void setMaxY(float maxY) {
        this.maxY = maxY;
    }

    public float getMinZ() {
        return minZ;
    }

    public void setMinZ(float minZ) {
        this.minZ = minZ;
    }

    public float getMaxZ() {
        return maxZ;
    }

    public void setMaxZ(float maxZ) {
        this.maxZ = maxZ;
    }
}

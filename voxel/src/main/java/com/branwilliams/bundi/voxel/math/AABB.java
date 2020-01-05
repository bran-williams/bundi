package com.branwilliams.bundi.voxel.math;

import com.branwilliams.bundi.engine.util.Mathf;
import org.joml.Vector3f;

/**
 * @author Brandon
 * @since August 12, 2019
 */
public class AABB {

    private final float epsilon = 0F;

    private float minX, maxX;

    private float minY, maxY;

    private float minZ, maxZ;

    public AABB(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.minZ = minZ;
        this.maxZ = maxZ;
    }

    public AABB expand(Vector3f offset) {
        return expand(offset.x, offset.y, offset.z);
    }

    public AABB expand(float xOffset, float yOffset, float zOffset) {
        float minX = this.minX;
        float minY = this.minY;
        float minZ = this.minZ;
        float maxX = this.maxX;
        float maxY = this.maxY;
        float maxZ = this.maxZ;

        if (xOffset < 0.0F) minX += xOffset;
        if (xOffset > 0.0F) maxX += xOffset;

        if (yOffset < 0.0F) minY += yOffset;
        if (yOffset > 0.0F) maxY += yOffset;

        if (zOffset < 0.0F) minZ += zOffset;
        if (zOffset > 0.0F) maxZ += zOffset;

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public void move(float x, float y, float z) {
        this.minX += x;
        this.maxX += x;

        this.minY += y;
        this.maxY += y;

        this.minZ += z;
        this.maxZ += z;
    }

    public float clipXCollide(AABB c, float xa) {
        if ((c.maxY <= this.minY) || (c.minY >= this.maxY)) return xa;
        if ((c.maxZ <= this.minZ) || (c.minZ >= this.maxZ)) return xa;

        if ((xa > 0.0F) && (c.maxX <= this.minX)) {
            float max = this.minX - c.maxX - this.epsilon;
            if (max < xa) xa = max;
        }

        if ((xa < 0.0F) && (c.minX >= this.maxX)) {
            float max = this.maxX - c.minX + this.epsilon;
            if (max > xa) xa = max;

        }
        return xa;
    }

    public float clipYCollide(AABB c, float ya) {
        if ((c.maxX <= this.minX) || (c.minX >= this.maxX)) return ya;
        if ((c.maxZ <= this.minZ) || (c.minZ >= this.maxZ)) return ya;

        if ((ya > 0.0F) && (c.maxY <= this.minY)) {
            float max = this.minY - c.maxY - this.epsilon;
            if (max < ya) ya = max;
        }

        if ((ya < 0.0F) && (c.minY >= this.maxY)) {
            float max = this.maxY - c.minY + this.epsilon;
            if (max > ya) ya = max;

        }
        return ya;
    }

    public float clipZCollide(AABB c, float za) {
        if ((c.maxX <= this.minX) || (c.minX >= this.maxX)) return za;
        if ((c.maxY <= this.minY) || (c.minY >= this.maxY)) return za;

        if ((za > 0.0F) && (c.maxZ <= this.minZ)) {
            float max = this.minZ - c.maxZ - this.epsilon;
            if (max < za) za = max;
        }

        if ((za < 0.0F) && (c.minZ >= this.maxZ)) {
            float max = this.maxZ - c.minZ + this.epsilon;
            if (max > za) za = max;
        }
        return za;
    }

    public boolean intersects(AABB aabb) {
        return (minX <= aabb.maxX && maxX >= aabb.minX) &&
                (minY <= aabb.maxY && maxY >= aabb.minY) &&
                (minZ <= aabb.maxZ && maxZ >= aabb.minZ);
    }

    public void set(float minX, float maxX, float minY, float maxY, float minZ, float maxZ) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.minZ = minZ;
        this.maxZ = maxZ;
    }

    public Vector3f getCenter() {
        return new Vector3f(minX + getLengthX() * 0.5F, minY + getLengthY() * 0.5F, minZ + getLengthZ() * 0.5F);
    }

    public float horizontalDistance(Vector3f point) {
        return horizontalDistance(point.x, point.z);
    }

    public float horizontalDistance(float x, float z) {
        float dx = Math.max(0, Math.max(minX - x, x - maxX));
        float dz = Math.max(0, Math.max(minZ - z, z - maxZ));
        return Mathf.sqrt(dx*dx + dz*dz);
    }

    public float getMinX() {
        return minX;
    }

    public float getLengthX() {
        return maxX - minX;
    }

    public float getLengthY() {
        return maxY - minY;
    }

    public float getLengthZ() {
        return maxZ - minZ;
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

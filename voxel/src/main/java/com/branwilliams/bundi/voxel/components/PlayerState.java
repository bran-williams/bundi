package com.branwilliams.bundi.voxel.components;

import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.voxel.inventory.Inventory;
import com.branwilliams.bundi.voxel.inventory.ItemRegistry;
import com.branwilliams.bundi.voxel.math.AABB;
import com.branwilliams.bundi.voxel.math.RaycastResult;
import com.branwilliams.bundi.voxel.voxels.Voxel;
import com.branwilliams.bundi.voxel.voxels.Voxels;
import org.joml.Vector3f;

/**
 * @author Brandon
 * @since August 11, 2019
 */
public class PlayerState {

    private Inventory inventory;

    private RaycastResult raycast;

    private float reachDistance;

    private AABB boundingBox;

    private Vector3f eyeOffset;

    private boolean onGround;

    private boolean noClip;

    private float width = 0.6F;

    private float halfWidth = width * 0.5F;

    private float height = 1.8F;

    public PlayerState() {
        inventory = new Inventory();
        reachDistance = 6F;
        eyeOffset = new Vector3f(0F, 1.62F, 0F);
        boundingBox = new AABB(-halfWidth, 0, -halfWidth, halfWidth, height, halfWidth);
    }

    public Vector3f getEyePosition(Transformable transformable) {
        return new Vector3f(transformable.getPosition()).add(eyeOffset);
    }

    public void updateBoundingBox(Transformable transformable) {
        float x = transformable.getPosition().x;
        float y = transformable.getPosition().y;
        float z = transformable.getPosition().z;
        boundingBox.set(x - halfWidth, x + halfWidth, y, y + height, z - halfWidth, z + halfWidth);
    }

    public boolean isNoClip() {
        return noClip;
    }

    public void setNoClip(boolean noClip) {
        this.noClip = noClip;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public AABB getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(AABB boundingBox) {
        this.boundingBox = boundingBox;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public RaycastResult getRaycast() {
        return raycast;
    }

    public void setRaycast(RaycastResult raycast) {
        this.raycast = raycast;
    }

    public float getReachDistance() {
        return reachDistance;
    }

    public void setReachDistance(float reachDistance) {
        this.reachDistance = reachDistance;
    }
}

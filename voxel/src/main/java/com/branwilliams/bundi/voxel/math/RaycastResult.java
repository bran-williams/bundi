package com.branwilliams.bundi.voxel.math;

import org.joml.Vector3f;

/**
 * @author Brandon
 * @since August 10, 2019
 */
public class RaycastResult {

    public final Vector3f position;

    public final Vector3f blockPosition;

    public final Vector3f face;

    public RaycastResult(Vector3f position, Vector3f blockPosition, Vector3f face) {
        this.position = position;
        this.blockPosition = blockPosition;
        this.face = face;
    }
}

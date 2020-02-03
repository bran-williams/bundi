package com.branwilliams.cubes.math;

import org.joml.Vector3f;

/**
 * @author Brandon
 * @since August 10, 2019
 */
public class RaycastResult {

    public final Vector3f position;

    public final Vector3f chunkPosition;

    public final Vector3f face;

    public RaycastResult(Vector3f position, Vector3f chunkPosition, Vector3f face) {
        this.position = position;
        this.chunkPosition = chunkPosition;
        this.face = face;
    }
}

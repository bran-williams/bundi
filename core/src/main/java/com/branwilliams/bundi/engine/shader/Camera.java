package com.branwilliams.bundi.engine.shader;

import com.branwilliams.bundi.engine.util.Mathf;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Simple camera implementation which contains a position and a rotation.
 * The x values in the camera's rotation correspond to yaw values and the y
 * values correspond to pitch.
 * */
public class Camera {

    private final Matrix4f viewMatrix = new Matrix4f();

    private Vector3f position;

    private Vector3f rotation;

    // This is used to by the view matrix method to ensure that the view matrix is not calculated too often.
    private boolean dirty;

    public Camera(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
        this.dirty = true;
    }

    public Camera(Vector3f position) {
        this(position, new Vector3f(0, 0, 0));
    }

    public Camera() {
        this(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
    }

    public void lookAt(Vector3f position) {
        lookAt(position.x, position.y, position.z);
    }

    public void lookAt(float x, float y, float z) {
        double dx = position.x - x;
        double dy = position.y - y;
        double dz = position.z - z;
        double theta = (double) Mathf.sqrt(dx * dx + dz * dz);

        float yaw = (float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) ((Math.atan2(dy, theta) * 180.0D / Math.PI));

        rotation.set(pitch, yaw, rotation.z);
        dirty = true;
    }

    /**
     * Moves this camera object in the directions provided
     * */
    public void moveDirection(float forward, float strafe) {

        // forward movement.
        float x = (float) Mathf.sin(Mathf.toRadians(rotation.y)) * forward;
        float z = (float) -Mathf.cos(Mathf.toRadians(rotation.y)) * forward;

        // strafing movement.
        x += (float) Mathf.sin(Mathf.toRadians(rotation.y - 90)) * strafe;
        z -= (float) Mathf.cos(Mathf.toRadians(rotation.y - 90)) * strafe;
        move(x, 0F, z);
    }

    /**
     * Moves this camera object by the x, y, and z values provided.
     * */
    public void move(float x, float y, float z) {
        this.position.x += x;
        this.position.y += y;
        this.position.z += z;
        this.dirty = true;
    }

    /**
     * Offsets this camera's rotation by the provided x, y, and z values.
     * */
    public void rotate(float x, float y, float z) {
        rotation.x += x;
        rotation.y += y;
        rotation.z += z;
        dirty = true;
        clampAngles();
    }

    /**
     * Clamps this camera's rotation angles.
     * Y rotation (yaw) is clamped to be between 0 ~ 360.
     * X rotation (pitch) is clamped to be between -90 ~ 90.
     * */
    public void clampAngles() {
        if (rotation.y > 360)
            rotation.y %= 360;
        if (rotation.y < 0)
            rotation.y = 360 + rotation.y;

        if (rotation.x > 89)
            rotation.x = 89;
        if (rotation.x < -89)
            rotation.x = -89;
    }

    public Vector3f getDirection() {
        Vector3f direction = new Vector3f();

        float yaw   = Mathf.toRadians(rotation.y);
        float pitch = Mathf.toRadians(rotation.x);

        direction.x = Mathf.cos(pitch) * Mathf.sin(yaw);
        direction.y = -Mathf.sin(pitch);
        direction.z = -Mathf.cos(pitch) * Mathf.cos(yaw);

        return direction.normalize();
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
        this.dirty = true;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
        this.dirty = true;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
        this.dirty = true;
        clampAngles();
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
        this.dirty = true;
        clampAngles();
    }

    /**
     * The 'pitch' of the camera is the rotation along the x-axis. This is capped between -90 and 90.
     * This function inverts whatever value this camera currently has.
     * */
    public void invertPitch() {
        this.rotation.x = -this.rotation.x;
        this.dirty = true;
    }

    @Override
    public String toString() {
        return "Camera{" +
                "viewMatrix=" + viewMatrix +
                ", position=" + position +
                ", rotation=" + rotation +
                '}';
    }

    public Matrix4f toViewMatrix() {
        if (dirty) {
            Mathf.toViewMatrix(viewMatrix, this);
            dirty = false;
        }
        return viewMatrix;
    }
}

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

    private static final float DEFAULT_YAW = 0F;

    private static final float DEFAULT_PITCH = 0F;

    private static final Vector3f DEFAULT_UP = new Vector3f(0F, 1F, 0F);

    private static final Vector3f DEFAULT_FRONT = new Vector3f(0F, 0F, -1F);

    private final Matrix4f viewMatrix = new Matrix4f();

    /**
     * World-space up vector. Default is (0, 1, 0).
     *
     * */
    private Vector3f worldUp;

    /**
     * Position of this camera.
     * */
    private Vector3f position;

    /**
     * Up vector of this camera.
     * */
    private Vector3f up;

    /**
     * Front vector of this camera.
     * */
    private Vector3f front;

    /**
     * Right vector of this camera.
     * */
    private Vector3f right;

    /**
     * The yaw (x-axis rotation) of this camera.
     * */
    private float yaw;

    /**
     * The pitch (y-axis rotation) of this camera.
     * */
    private float pitch;

    /**
     * This is used to by the view matrix method to ensure that the view matrix is not calculated too often.
     * */
    private boolean dirty;

    public Camera(Vector3f position, float yaw, float pitch) {
        this.worldUp = new Vector3f(DEFAULT_UP);
        this.up = new Vector3f(DEFAULT_UP);
        this.front = new Vector3f(DEFAULT_FRONT);

        this.position = position;
        this.yaw = yaw;
        this.pitch = pitch;
        updateCameraVectors();
        this.dirty = true;
    }

    public Camera(Vector3f position) {
        this(position, DEFAULT_YAW, DEFAULT_PITCH);
    }

    public Camera() {
        this(new Vector3f());
    }

    public void lookAt(Vector3f position) {
        lookAt(position.x, position.y, position.z);
    }

    /**
     * Forces this camera to look at the (world-space) position provided.
     * */
    public void lookAt(float x, float y, float z) {
        double dx = position.x - x;
        double dy = position.y - y;
        double dz = position.z - z;
        double theta = Math.sqrt(dx * dx + dz * dz);

        yaw = (float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
        pitch = (float) ((Math.atan2(dy, theta) * 180.0D / Math.PI));
        updateCameraVectors();
        dirty = true;
    }

    /**
     * Moves this camera object in the directions provided
     * */
    public void moveDirection(float forward, float strafe) {
        Vector3f movement = this.front.mul(forward, new Vector3f());
        movement.add(this.right.mul(strafe, new Vector3f()));
        move(movement.x, movement.y, movement.z);
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
     * Offsets this camera's rotation by the provided yaw and pitch.
     * */
    public void rotate(float dYaw, float dPitch) {
        this.yaw += dYaw;
        this.pitch += dPitch;
        clampAngles();
        updateCameraVectors();
        dirty = true;
    }

    /**
     * Clamps this camera's rotation angles.
     * Y rotation (yaw) is clamped to be between 0 ~ 360.
     * X rotation (pitch) is clamped to be between -90 ~ 90.
     * */
    public void clampAngles() {
        if (yaw > 360)
            yaw %= 360;
        if (yaw < 0)
            yaw = 360 + yaw;

        if (pitch > 89)
            pitch = 89;
        if (pitch < -89)
            pitch = -89;
    }

    /**
     * @return The direction this camera is facing.
     * */
    public Vector3f getFacingDirection() {
        return new Vector3f(front);
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

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    /**
     * Updates the front and up vectors from the yaw and pitch.
     * */
    protected void updateCameraVectors() {
        // Calculate the new Front vector
        Vector3f front = new Vector3f();
        front.x = Mathf.cos(Mathf.toRadians(yaw)) * Mathf.cos(Mathf.toRadians(pitch));
        front.y = Mathf.sin(Mathf.toRadians(pitch));
        front.z = Mathf.sin(Mathf.toRadians(yaw)) * Mathf.cos(Mathf.toRadians(pitch));
        this.front = front.normalize();

        // Also re-calculate the Right and Up vector
        this.right = front.cross(worldUp, new Vector3f()).normalize();
        this.up = right.cross(front, new Vector3f()).normalize();
    }

    public Matrix4f toViewMatrix() {
        if (dirty) {
            updateCameraVectors();
            viewMatrix.setLookAt(position, position.add(front, new Vector3f()), up);
            dirty = false;
        }
        return viewMatrix;
    }

    public Vector3f getFront() {
        return front;
    }

    public Vector3f getUp() {
        return up;
    }

    public Vector3f getRight() {
        return right;
    }

    public Vector3f getWorldUp() {
        return worldUp;
    }

    @Override
    public String toString() {
        return "Camera{" +
                "viewMatrix=" + viewMatrix +
                ", position=" + position +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                '}';
    }


}

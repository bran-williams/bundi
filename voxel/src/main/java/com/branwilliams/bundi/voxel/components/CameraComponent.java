package com.branwilliams.bundi.voxel.components;

import com.branwilliams.bundi.engine.shader.Camera;

/**
 * Created by Brandon Williams on 7/11/2018.
 */
public class CameraComponent {

    private final Camera camera;

    private float cameraSpeed;

    private boolean locked = false;

    private boolean isLockable = true;

    public CameraComponent(Camera camera, float cameraSpeed) {
        this.camera = camera;
        this.cameraSpeed = cameraSpeed;
    }

    public Camera getCamera() {
        return camera;
    }

    public float getCameraSpeed() {
        return cameraSpeed;
    }

    public void setCameraSpeed(float cameraSpeed) {
        this.cameraSpeed = cameraSpeed;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        if (isLockable)
            this.locked = locked;
    }

    public boolean isLockable() {
        return isLockable;
    }

    public void setLockable(boolean lockable) {
        isLockable = lockable;
    }
}

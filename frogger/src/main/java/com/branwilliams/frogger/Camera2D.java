package com.branwilliams.frogger;

import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.shape.AABB2f;
import com.branwilliams.bundi.engine.util.Mathf;
import org.joml.Vector2f;

public class Camera2D {

    public static final float FOCALPOINT_MOVEMENT_EPSILON = 0.5F;

    private Vector2f focalPoint;

    private Vector2f targetFocalPoint;

    private Vector2f screenSize;

    private AABB2f screenAABB;

    public Camera2D(int width, int height) {
        this.focalPoint = new Vector2f();
        this.targetFocalPoint = new Vector2f(focalPoint);
        this.screenSize = new Vector2f();
        this.screenAABB = new AABB2f(0, 0, width, height);
        this.updateScreen(width, height);
    }

    /**
     * Updates the screenSize and screenAABB.
     * */
    public void updateScreen(int width, int height) {
        this.screenSize.set(width, height);
        screenAABB.getMin().set(focalPoint.x, focalPoint.y);
        screenAABB.getMax().set(focalPoint.x + width, focalPoint.y + height);
    }

    public Vector2f getMouseRelativeToFocalPoint(Window window) {
        return getMouseRelativeToFocalPoint(window.getMouseX(), window.getMouseY());
    }

    public Vector2f getMouseRelativeToFocalPoint(float mouseX, float mouseY) {
        return new Vector2f(mouseX + focalPoint.x, mouseY + focalPoint.y);
    }

    public boolean isMoving() {
        return !Mathf.equalsWithEpsilon(targetFocalPoint, focalPoint, FOCALPOINT_MOVEMENT_EPSILON);
    }

    public Vector2f getFocalPoint() {
        return focalPoint;
    }

    public void setFocalPoint(Vector2f focalPoint) {
        this.focalPoint = focalPoint;
    }

    public Vector2f getTargetFocalPoint() {
        return targetFocalPoint;
    }

    public void setTargetFocalPoint(Vector2f targetFocalPoint) {
        this.targetFocalPoint = targetFocalPoint;
    }

    public AABB2f getScreenAABB() {
        return screenAABB;
    }

    public void setScreenAABB(AABB2f screenAABB) {
        this.screenAABB = screenAABB;
    }

    public Vector2f getScreenSize() {
        return screenSize;
    }

    public void setScreenSize(Vector2f screenSize) {
        this.screenSize = screenSize;
    }
}

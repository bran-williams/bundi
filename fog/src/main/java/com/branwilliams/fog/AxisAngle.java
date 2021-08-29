package com.branwilliams.fog;

import com.branwilliams.bundi.engine.util.Mathf;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class AxisAngle {

    private Vector3f axis;

    private float angle;

    public AxisAngle(Vector3f axis, float angle) {
        this.axis = axis;
        this.angle = angle;
    }

    public Quaternionf toQuaternion() {
        return toQuaternion(new Quaternionf());
    }

    public Quaternionf toQuaternion(Quaternionf dest) {
        dest.x = axis.x * Mathf.sin(angle / 2);
        dest.y = axis.y * Mathf.sin(angle / 2);
        dest.z = axis.z * Mathf.sin(angle / 2);
        dest.w = Mathf.cos(angle / 2);
        return dest;
    }

    public void setAngleDeg(float angleDeg) {
        this.angle = Mathf.toRadians(angleDeg);
    }

    public Vector3f getAxis() {
        return axis;
    }

    public void setAxis(Vector3f axis) {
        this.axis = axis;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }
}

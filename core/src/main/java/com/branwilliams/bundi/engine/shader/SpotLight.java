package com.branwilliams.bundi.engine.shader;

import com.branwilliams.bundi.engine.util.Mathf;
import org.joml.Vector3f;

/**
 * Represents a positional light. <br/>
 * Created by Brandon Williams on 12/27/2017.
 */
public class SpotLight {

    private Vector3f position;

    private Vector3f direction;

    private float cutOff;

    private float outerCutOff;

    private Vector3f color;

    private float ambiance;

    private LinearAttenuation attenuation;

    public SpotLight(Vector3f position, Vector3f direction, float cutOff, float outerCutOff, Vector3f color,
                     float ambiance, LinearAttenuation attenuation) {
        this.position = position;
        this.direction = direction;
        this.cutOff = cutOff;
        this.outerCutOff = outerCutOff;
        this.color = color;
        this.ambiance = ambiance;
        this.attenuation = attenuation;
    }

    /**
     * @return The radius this point light will affect.
     * */
    public float getRadius() {
        float maxChannel = Math.max(Math.max(color.x, color.y), color.z);

        return (-attenuation.getLinear() + (float) Math.sqrt(attenuation.getLinear() * attenuation.getLinear() -
                4 * attenuation.getQuadratic() * (attenuation.getConstant() - (256F/5F) * maxChannel)))
                /
                (2 * attenuation.getQuadratic());
    }

//    public float getRadius() {
//        return getRadius(0.005F);
//    }

    /**
     * quadratic = 1.0 / (radius*radius * minLight)
     * */
    public float getRadius(float minLight) {
        return Mathf.sqrt(1 / (attenuation.getQuadratic() * minLight));
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public float getCutOff() {
        return cutOff;
    }

    public void setCutOff(float cutOff) {
        this.cutOff = cutOff;
    }

    public float getOuterCutOff() {
        return outerCutOff;
    }

    public void setOuterCutOff(float outerCutOff) {
        this.outerCutOff = outerCutOff;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public float getAmbiance() {
        return ambiance;
    }

    public void setAmbiance(float ambiance) {
        this.ambiance = ambiance;
    }

    public LinearAttenuation getAttenuation() {
        return attenuation;
    }

    public void setAttenuation(LinearAttenuation attenuation) {
        this.attenuation = attenuation;
    }
}

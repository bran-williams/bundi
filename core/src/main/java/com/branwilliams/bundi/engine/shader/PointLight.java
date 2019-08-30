package com.branwilliams.bundi.engine.shader;

import com.branwilliams.bundi.engine.util.Mathf;
import org.joml.Vector3f;

/**
 * Represents a positional light. <br/>
 * Created by Brandon Williams on 12/27/2017.
 */
public class PointLight {

    private Vector3f position;

    private Vector3f color;

    private float ambiance;

    private LinearAttenuation attenuation;

    public PointLight(Vector3f position, Vector3f color, float ambiance, LinearAttenuation attenuation) {
        this.position = position;
        this.color = color;
        this.ambiance = ambiance;
        this.attenuation = attenuation;
    }


    public PointLight(Vector3f position, Vector3f color, float ambiance) {
        this(position, color, ambiance, new LinearAttenuation(1.0F, 0.09F, 0.032F));
    }


    public PointLight(Vector3f position, Vector3f color) {
        this(position, color, 0.0005F);
    }

    public PointLight(Vector3f position) {
        this(position, new Vector3f(1F, 1F, 1F));
    }

    public PointLight(Vector3f color, float radius) {
        this(new Vector3f(), color, 0F, new LinearAttenuation(1.0F, 0.09F, 1.0F / (radius*radius * 0.01F)));
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

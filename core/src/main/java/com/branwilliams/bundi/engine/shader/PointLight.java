package com.branwilliams.bundi.engine.shader;

import com.branwilliams.bundi.engine.util.Mathf;
import org.joml.Vector3f;

/**
 * Represents a positional light. <br/>
 * Created by Brandon Williams on 12/27/2017.
 */
public class PointLight {

    private Vector3f position;

    private Vector3f ambient;

    private Vector3f diffuse;

    private Vector3f specular;


    private float ambiance;

    private LinearAttenuation attenuation;

    public PointLight(Vector3f position, Vector3f ambient, Vector3f diffuse, Vector3f specular, float ambiance,
                      LinearAttenuation attenuation) {
        this.position = position;
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.ambiance = ambiance;
        this.attenuation = attenuation;
    }


    public PointLight(Vector3f position, Vector3f ambient, Vector3f diffuse, Vector3f specular, float ambiance) {
        this(position, ambient, diffuse, specular, ambiance, new LinearAttenuation(1.0F, 0.09F, 0.032F));
    }


    public PointLight(Vector3f position, Vector3f ambient, Vector3f diffuse, Vector3f specular) {
        this(position, ambient, diffuse, specular, 0.0005F);
    }

    public PointLight(Vector3f position) {
        this(position, new Vector3f(0.05F, 0.05F, 0.05F), new Vector3f(0.8F), new Vector3f(1F));
    }

    public PointLight(Vector3f ambient, Vector3f diffuse, Vector3f specular, float radius) {
        this(new Vector3f(), ambient, diffuse, specular, 0F, new LinearAttenuation(1.0F, 0.09F, 1.0F / (radius*radius * 0.01F)));
    }

    /**
     * @return The radius this point light will affect.
     * */
    public float getRadius() {
        float maxChannel = Math.max(Math.max(diffuse.x, diffuse.y), diffuse.z);

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

    public Vector3f getAmbient() {
        return ambient;
    }

    public void setAmbient(Vector3f ambient) {
        this.ambient = ambient;
    }

    public Vector3f getDiffuse() {
        return diffuse;
    }

    public void setDiffuse(Vector3f diffuse) {
        this.diffuse = diffuse;
    }

    public Vector3f getSpecular() {
        return specular;
    }

    public void setSpecular(Vector3f specular) {
        this.specular = specular;
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

    public static String getStructName() {
        return "PointLight";
    }

    public static String toGLSLStruct() {
        return "struct PointLight {\n"
                + "    vec3 position;\n"
                + "\n"
                + "    float constant;\n"
                + "    float linear;\n"
                + "    float quadratic;\n"
                + "\n"
                + "    vec3 ambient;\n"
                + "    vec3 diffuse;\n"
                + "    vec3 specular;\n"
                + "};\n";
    }
}

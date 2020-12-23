package com.branwilliams.bundi.engine.shader;

import org.joml.Vector3f;

/**
 * Represents a directional light. It has no position, just a direction. <br/>
 * Created by Brandon Williams on 12/27/2017.
 */
public class DirectionalLight {

    private Vector3f direction;

    private Vector3f ambient;

    private Vector3f diffuse;

    private Vector3f specular;

    public DirectionalLight(Vector3f direction, Vector3f ambient, Vector3f diffuse, Vector3f specular) {
        this.direction = direction;
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
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

    public static String getStructName() {
        return "DirLight";
    }

    public static String toGLSLStruct() {
        return "struct DirLight {\n"
                + "    vec3 direction;\n"
                + "\n"
                + "    vec3 ambient;\n"
                + "    vec3 diffuse;\n"
                + "    vec3 specular;\n"
                + "};\n";
    }
}

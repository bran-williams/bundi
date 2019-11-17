package com.branwilliams.terrain.generator;

import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * @author Brandon
 * @since November 17, 2019
 */
public class TerrainVertex {

    private Vector3f position;

    private Vector3f normal;

    private Vector2f uvs;

    private Vector3f tangent;

    private Vector3f bitangent;

    public TerrainVertex(Vector3f position, Vector3f normal, Vector2f uvs, Vector3f tangent, Vector3f bitangent) {
        this.position = position;
        this.normal = normal;
        this.uvs = uvs;
        this.tangent = tangent;
        this.bitangent = bitangent;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getNormal() {
        return normal;
    }

    public void setNormal(Vector3f normal) {
        this.normal = normal;
    }

    public Vector2f getUvs() {
        return uvs;
    }

    public void setUvs(Vector2f uvs) {
        this.uvs = uvs;
    }

    public Vector3f getTangent() {
        return tangent;
    }

    public void setTangent(Vector3f tangent) {
        this.tangent = tangent;
    }

    public Vector3f getBitangent() {
        return bitangent;
    }

    public void setBitangent(Vector3f bitangent) {
        this.bitangent = bitangent;
    }
}

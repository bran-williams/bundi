package com.branwilliams.bundi.pbr.pipeline.material;

import com.branwilliams.bundi.engine.material.Material;
import com.branwilliams.bundi.engine.texture.TextureLoader;
import com.branwilliams.bundi.pbr.util.PbrUtils;

import java.io.IOException;

/**
 * @author Brandon
 * @since August 31, 2019
 */
public class PbrMaterial {

    private String diffuse;

    private String normal;

    private String metallic;

    private String roughness;

    public PbrMaterial(String diffuse, String normal, String metallic, String roughness) {
        this.diffuse = diffuse;
        this.normal = normal;
        this.metallic = metallic;
        this.roughness = roughness;
    }

    /**
     * @return The {@link Material} object defined by this pbr material.
     * */
    public Material createMaterial(TextureLoader textureLoader) throws IOException {
        return PbrUtils.createPbrMaterial(textureLoader, diffuse, metallic, normal, roughness);
    }

    public String getDiffuse() {
        return diffuse;
    }

    public void setDiffuse(String diffuse) {
        this.diffuse = diffuse;
    }

    public String getNormal() {
        return normal;
    }

    public void setNormal(String normal) {
        this.normal = normal;
    }

    public String getMetallic() {
        return metallic;
    }

    public void setMetallic(String metallic) {
        this.metallic = metallic;
    }

    public String getRoughness() {
        return roughness;
    }

    public void setRoughness(String roughness) {
        this.roughness = roughness;
    }

    @Override
    public String toString() {
        return "PbrMaterial{" +
                "diffuse='" + diffuse + '\'' +
                ", normal='" + normal + '\'' +
                ", metallic='" + metallic + '\'' +
                ", roughness='" + roughness + '\'' +
                '}';
    }
}

package com.branwilliams.terrain.component;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.texture.TextureData;
import com.branwilliams.bundi.engine.texture.TextureLoader;

import java.io.IOException;

/**
 * @author Brandon
 * @since January 09, 2020
 */
public class TerrainTextureData implements Destructible {

    private String diffuse;
    private TextureData diffuseTextureData;

    private String normal;
    private TextureData normalTextureData;

    public TerrainTextureData(String diffuse, String normal) {
        this.diffuse = diffuse;
        this.diffuseTextureData = diffuseTextureData;
        this.normal = normal;
        this.normalTextureData = normalTextureData;
    }

    public void load(TextureLoader textureLoader) throws IOException {
        diffuseTextureData = textureLoader.loadTexture(diffuse);
        normalTextureData = textureLoader.loadTexture(normal);
    }

    public String getDiffuse() {
        return diffuse;
    }

    public void setDiffuse(String diffuse) {
        this.diffuse = diffuse;
    }

    public TextureData getDiffuseTextureData() {
        return diffuseTextureData;
    }

    public String getNormal() {
        return normal;
    }

    public void setNormal(String normal) {
        this.normal = normal;
    }

    public TextureData getNormalTextureData() {
        return normalTextureData;
    }

    @Override
    public String toString() {
        return "TerrainTexture{" +
                "diffuse='" + diffuse + '\'' +
                ", diffuseTextureData=" + diffuseTextureData +
                ", normal='" + normal + '\'' +
                ", normalTextureData=" + normalTextureData +
                '}';
    }

    @Override
    public void destroy() {
        if (diffuseTextureData != null)
            diffuseTextureData.destroy();

        if (normalTextureData != null)
            normalTextureData.destroy();
    }
}

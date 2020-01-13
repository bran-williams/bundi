package com.branwilliams.terrain.component;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.core.Nameable;
import com.branwilliams.bundi.engine.texture.TextureLoader;

import java.io.IOException;

/**
 * @author Brandon
 * @since January 09, 2020
 */
public class TerrainTexture implements Nameable, Destructible {

    private String name;

    private TerrainTextureData textureData;

    public TerrainTexture(String name, TerrainTextureData textureData) {
        this.name = name;
        this.textureData = textureData;
    }

    public void load(TextureLoader textureLoader) throws IOException {
        textureData.load(textureLoader);
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TerrainTextureData getTextureData() {
        return textureData;
    }

    public void setTextureData(TerrainTextureData textureData) {
        this.textureData = textureData;
    }

    @Override
    public String toString() {
        return "TerrainTexture{" +
                "name='" + name + '\'' +
                ", textureData=" + textureData +
                '}';
    }

    @Override
    public void destroy() {
        if (textureData != null)
            textureData.destroy();
    }
}

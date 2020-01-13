package com.branwilliams.terrain.component;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.texture.TextureData;
import com.branwilliams.bundi.engine.texture.TextureLoader;

import java.io.IOException;
import java.util.List;

/**
 * @author Brandon
 * @since January 09, 2020
 */
public class TerrainMaterial implements Destructible {

    private List<TerrainTexture> textures;

    private String blendmap;
    private TextureData blendmapTextureData;

    private String heightmap;
    private TextureData heightmapTextureData;

    public TerrainMaterial(List<TerrainTexture> textures, String blendmap, String heightmap) {
        this.textures = textures;
        this.blendmap = blendmap;
        this.heightmap = heightmap;
    }

    public void load(TextureLoader textureLoader) throws IOException {
        for (TerrainTexture terrainTexture : textures) {
            terrainTexture.load(textureLoader);
        }

        if (blendmap != null)
            this.blendmapTextureData = textureLoader.loadTexture(blendmap);

        if (heightmap != null)
            this.heightmapTextureData = textureLoader.loadTexture(heightmap);
    }

    public List<TerrainTexture> getTextures() {
        return textures;
    }

    public void setTextures(List<TerrainTexture> textures) {
        this.textures = textures;
    }

    public String getBlendmap() {
        return blendmap;
    }

    public void setBlendmap(String blendmap) {
        this.blendmap = blendmap;
    }

    public TextureData getBlendmapTextureData() {
        return blendmapTextureData;
    }

    public void setBlendmapTextureData(TextureData blendmapTextureData) {
        this.blendmapTextureData = blendmapTextureData;
    }

    public String getHeightmap() {
        return heightmap;
    }

    public void setHeightmap(String heightmap) {
        this.heightmap = heightmap;
    }

    public TextureData getHeightmapTextureData() {
        return heightmapTextureData;
    }

    public void setHeightmapTextureData(TextureData heightmapTextureData) {
        this.heightmapTextureData = heightmapTextureData;
    }

    @Override
    public void destroy() {
        textures.forEach(TerrainTexture::destroy);

        if (blendmapTextureData != null)
            blendmapTextureData.destroy();

        if (heightmapTextureData != null)
            heightmapTextureData.destroy();
    }
}

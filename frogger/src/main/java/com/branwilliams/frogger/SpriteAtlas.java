package com.branwilliams.frogger;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.texture.ArrayTexture;
import com.branwilliams.bundi.engine.texture.Texture;
import com.branwilliams.bundi.engine.texture.TextureData;
import com.branwilliams.bundi.engine.texture.TextureLoader;
import org.joml.Vector2f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpriteAtlas implements Destructible {

    private Vector2f spriteSize;

    private Map<String, SpriteData> atlas;

    private Map<String, Integer> arrayIndices;

    private int maxSpriteIndex;

    private ArrayTexture texture;

    public SpriteAtlas(Vector2f spriteSize, Map<String, SpriteData> atlas) {
        this.spriteSize = spriteSize;
        this.atlas = atlas;
    }

    public void load(TextureLoader textureLoader) throws IOException {
        if (atlas.isEmpty())
            return;

        this.arrayIndices = new HashMap<>();

        List<TextureData> textureData = new ArrayList<>();
        int i = 0;

        for (String spriteIndex : atlas.keySet()) {

            SpriteData spriteData = atlas.get(spriteIndex);
            spriteData.load(textureLoader);

            textureData.add(spriteData.diffuseTextureData);

            arrayIndices.put(spriteIndex, i);
            i++;
        }
        maxSpriteIndex = i - 1;

        this.texture = new ArrayTexture(Texture.TextureType.COLOR8, textureData.toArray(new TextureData[0]));
        this.texture.nearestFilter().repeatEdges();
        Texture.unbind(this.texture);

        atlas.clear();
    }

    public int getMaxSpriteIndex() {
        return maxSpriteIndex;
    }

    public ArrayTexture getTexture() {
        return texture;
    }

    public Vector2f getSpriteSize() {
        return spriteSize;
    }

    public void setSpriteSize(Vector2f spriteSize) {
        this.spriteSize = spriteSize;
    }

    public Map<String, SpriteData> getAtlas() {
        return atlas;
    }

    public void setAtlas(Map<String, SpriteData> atlas) {
        this.atlas = atlas;
    }

    @Override
    public void destroy() {
        if (this.texture != null)
            this.texture.destroy();
    }

    public static class SpriteData implements Destructible {

        private String diffuse;
        private TextureData diffuseTextureData;

        public SpriteData(String diffuse) {
            this.diffuse = diffuse;
        }

        public void load(TextureLoader textureLoader) throws IOException {
            if (diffuse != null)
                this.diffuseTextureData = textureLoader.loadTexture(diffuse);
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

        public void setDiffuseTextureData(TextureData diffuseTextureData) {
            this.diffuseTextureData = diffuseTextureData;
        }

        @Override
        public void destroy() {
            if (diffuseTextureData != null)
                diffuseTextureData.destroy();
        }
    }
}

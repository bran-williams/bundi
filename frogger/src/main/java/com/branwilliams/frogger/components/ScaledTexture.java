package com.branwilliams.frogger.components;

import com.branwilliams.bundi.engine.texture.Texture;
import com.branwilliams.bundi.engine.texture.TextureData;
import com.branwilliams.bundi.engine.texture.TextureLoader;

import java.io.IOException;

public class ScaledTexture {

    private String texture;

    private float scale;

    private Texture textureObject;

    public ScaledTexture(String texture, float scale) {
        this.texture = texture;
        this.scale = scale;
    }

    public void load(TextureLoader textureLoader) throws IOException {
        if (texture != null) {
            TextureData textureData = textureLoader.loadTexture(texture);
            textureObject  = new Texture(textureData, false);
            textureObject.bind().nearestFilter().repeatEdges();
            Texture.unbind(textureObject);
        }
    }

    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Texture getTextureObject() {
        return textureObject;
    }

    public void setTextureObject(Texture textureObject) {
        this.textureObject = textureObject;
    }

}

package com.branwilliams.bundi.gui.api.components;

import com.branwilliams.bundi.engine.texture.Texture;
import com.branwilliams.bundi.gui.api.Component;

import java.awt.*;

public class Image extends Component {

    private String altText;

    private Texture texture;

    private Color textureColor = Color.WHITE;

    public Image(Texture texture) {
        this("None", texture);
    }

    public Image(String altText, Texture texture) {
        super();
        this.altText = altText;
        this.texture = texture;
    }

    @Override
    public void update() {

    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Color getTextureColor() {
        return textureColor;
    }

    public void setTextureColor(Color textureColor) {
        this.textureColor = textureColor;
    }

    @Override
    public String toString() {
        return "Image{" +
                "altText='" + altText + '\'' +
                ", texture=" + texture +
                '}';
    }
}
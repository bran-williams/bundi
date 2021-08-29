package com.branwilliams.bundi.engine.material;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.texture.Texture;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Brandon Williams on 11/21/2017.
 */
public class Material implements Destructible {

    private MaterialFormat materialFormat;

    /**
     * Each texture is mapped to a texture unit between 0-31.
     * */
    private Texture[] textures;

    private Map<String, Object> properties;

    public Material(Texture... textures) {
        this.textures = textures;
        this.properties = new HashMap<>();
    }

    public Material() {
        this((Texture[]) null);
    }

    public Material(MaterialFormat materialFormat) {
        this();
        this.materialFormat = materialFormat;
    }

    public Texture getElementAsTexture(MaterialElement materialElement) {
        if (hasMaterialFormat() && materialFormat.hasElement(materialElement)) {
            MaterialFormat.MaterialEntry materialEntry = materialFormat.getElement(materialElement);
            if (materialEntry.elementType.isSampler) {
                return getTexture(materialEntry.textureIndex);
            }
        }
        return null;
    }

    private boolean hasMaterialFormat() {
        return materialFormat != null;
    }

    public <T> void setProperty(String name, T value) {
        properties.put(name, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getProperty(String name) {
        return (T) properties.get(name);
    }

    @SuppressWarnings("unchecked")
    public <T> T getPropertyOrDefault(String name, T defaultProperty) {
        return (T) properties.getOrDefault(name, defaultProperty);
    }

    public boolean hasProperty(String name) {
        return properties.containsKey(name);
    }

    /**
     * @return The texture which used to be in that position.
     * */
    public Texture setTexture(int id, Texture texture) {
        if (texture == null)
            throw new NullPointerException("Texture cannot be null!");

        // No textures
        if (this.textures == null) {
            this.textures = new Texture[id + 1];
            this.textures[id] = texture;
            return null;
        // Fits within our array
        } else if (id >= 0 && id < this.textures.length) {
            Texture old = this.textures[id];
            this.textures[id] = texture;
            return old;
        // Does not fit within our array
        } else {
            this.textures = Arrays.copyOf(textures, id + 1);
            this.textures[id] = texture;
            return null;
        }
    }

    /**
     * Puts the provided texture in the 0th position for this material.
     * */
    public Texture setTexture(Texture texture) {
        if (this.textures == null) {
            this.textures = new Texture[] { texture };
            return null;
        } else {
            Texture old = this.textures[0];
            this.textures[0] = texture;
            return old;
        }
    }

    /**
     * Sets the textures of this material.
     * */
    public void setTextures(Texture... textures) {
        this.textures = textures;
    }

    public Texture[] getTextures() {
        return textures;
    }

    public Texture getTexture(int id) {
        if (id > 0 && id < textures.length) {
            return textures[id];
        }
        return null;
    }
    /**
     * @return True if this material has any textures.
     * */
    public boolean hasTextures() {
        return textures != null;
    }

    public void setMaterialFormat(MaterialFormat materialFormat) {
        this.materialFormat = materialFormat;
    }

    public MaterialFormat getMaterialFormat() {
        return materialFormat;
    }

    @Override
    public void destroy() {
        if (textures != null) {
            Arrays.stream(textures).forEach(Texture::destroy);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Material)) return false;
        Material material = (Material) o;
        return Arrays.equals(textures, material.textures) &&
                Objects.equals(properties, material.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(textures, properties);
    }

    @Override
    public String toString() {
        return "[type=material, texture=" + Arrays.toString(textures) + ", properties=" + properties.toString() + "]";
    }
}

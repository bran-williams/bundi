package com.branwilliams.mcskin.steve;

import com.branwilliams.bundi.engine.shader.Material;
import com.branwilliams.bundi.engine.texture.Texture;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brandon
 * @since November 26, 2019
 */
public class MCModel {

    private Material material;

    private List<ModelPart> modelParts;

    public MCModel(Material material) {
        this.material = material;
        this.modelParts = new ArrayList<>();
    }

    public List<ModelPart> getModelParts() {
        return modelParts;
    }

    public void addModelPart(ModelPart modelPart) {
        this.modelParts.add(modelPart);
    }

    public Material getMaterial() {
        return material;
    }

    public boolean hasTexture() {
        return material.hasTextures() && material.getTextures()[0] != null;
    }

    public void setTexture(Texture texture) {
        if (hasTexture())
            this.material.getTextures()[0].destroy();

        this.material.setTexture(0, texture);
    }
}

package com.branwilliams.bundi.voxel.voxels.model;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.texture.TextureData;
import com.branwilliams.bundi.engine.texture.TextureLoader;

import java.io.IOException;
import java.util.Objects;

/** Contains the texture paths for each texture mapped to a voxel face.
 *
 * Each voxel face must have at least one texture (diffuse) and can have at most four (diffuse, specular, normal, and
 * emission). This object represents the mapping of textures for an arbitrary voxel face. It is capable of loading the
 * textures given a texture loader, mostly for the purpose of creating the default textures mapped to other voxel faces
 * who do not have mappings for the following textures: specular, normal, and emission.
 *
 *
 * @author Brandon
 * @since August 17, 2019
 */
public class VoxelFaceTexture implements Destructible {

    private String diffuse;
    private TextureData diffuseTextureData;

    private String specular;
    private TextureData specularTextureData;

    private String normal;
    private TextureData normalTextureData;

    private String emission;
    private TextureData emissionTextureData;

    public VoxelFaceTexture(String diffuse, String specular, String normal, String emission) {
        this.diffuse = diffuse;
        this.specular = specular;
        this.normal = normal;
        this.emission = emission;
    }

    public void load(TextureLoader textureLoader) throws IOException {
        diffuseTextureData = textureLoader.loadTexture(diffuse);

        if (specular != null)
            specularTextureData = textureLoader.loadTexture(specular);

        if (normal != null)
            normalTextureData = textureLoader.loadTexture(normal);

        if (emission != null)
            emissionTextureData = textureLoader.loadTexture(emission);
    }

    public boolean hasSpecular() {
        return specularTextureData != null;
    }

    public boolean hasNormal() {
        return normalTextureData != null;
    }

    public boolean hasEmission() {
        return emissionTextureData != null;
    }

    public String getDiffusePath() {
        return diffuse;
    }

    public TextureData getDiffuse() {
        return diffuseTextureData;
    }

    public String getSpecularPath() {
        return specular;
    }

    public TextureData getSpecular() {
        return specularTextureData;
    }

    public String getNormalPath() {
        return normal;
    }

    public TextureData getNormal() {
        return normalTextureData;
    }

    public String getEmissionPath() {
        return emission;
    }

    public TextureData getEmission() {
        return emissionTextureData;
    }

    @Override
    public void destroy() {
        if (diffuseTextureData != null)
            diffuseTextureData.destroy();

        if (specularTextureData != null)
            specularTextureData.destroy();

        if (normalTextureData != null)
            normalTextureData.destroy();

        if (emissionTextureData != null)
            emissionTextureData.destroy();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VoxelFaceTexture that = (VoxelFaceTexture) o;
        return diffuse.equals(that.diffuse) &&
                Objects.equals(specular, that.specular) &&
                Objects.equals(normal, that.normal) &&
                Objects.equals(emission, that.emission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(diffuse, specular, normal, emission);
    }
}

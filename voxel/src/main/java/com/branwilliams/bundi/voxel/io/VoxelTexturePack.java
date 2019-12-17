package com.branwilliams.bundi.voxel.io;

import com.branwilliams.bundi.engine.texture.TextureData;
import com.branwilliams.bundi.engine.texture.TextureLoader;
import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.shader.Material;
import com.branwilliams.bundi.engine.texture.Texture;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.bundi.voxel.builder.VoxelTextureBuilder;
import com.branwilliams.bundi.voxel.voxels.model.VoxelFaceTexture;
import com.branwilliams.bundi.voxel.voxels.model.VoxelProperties;
import com.branwilliams.bundi.voxel.voxels.Voxel;
import com.branwilliams.bundi.voxel.voxels.VoxelFace;
import com.branwilliams.bundi.voxel.voxels.VoxelIdentifier;
import com.branwilliams.bundi.voxel.voxels.VoxelRegistry;
import org.joml.Vector4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

import static com.branwilliams.bundi.voxel.VoxelConstants.MAX_TEXTURE_SIZE;

/**
 * @author Brandon
 * @since July 21, 2019
 */
public class VoxelTexturePack implements Destructible {

    public static final String PLACEHOLDER_FOR_UNMAPPED_TEXTURE = "placeholder_for_unmapped_";

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final VoxelRegistry voxelRegistry;

    private final VoxelFaceTexture defaultVoxelFaceTexture;

    /**
     * Texture location to texture mapping.
     * Diffuse texture paths are mapped to their place in the texture atlas.
     * */
    private Map<String, Vector4f> textureMappings;

    private VoxelTextureBuilder voxelTextureBuilder;

    private Texture diffuseTextureAtlas;

    private Texture specularTextureAtlas;

    private Texture normalTextureAtlas;

    private Texture emissionTextureAtlas;

    private Material material;

    private boolean initialized = false;

    public VoxelTexturePack(VoxelRegistry voxelRegistry, VoxelFaceTexture defaultVoxelFaceTexture) {
        this.voxelRegistry = voxelRegistry;
        this.defaultVoxelFaceTexture = defaultVoxelFaceTexture;
        this.textureMappings = new HashMap<>();
    }

    /**
     * Loads every texture found within the voxel properties loaded for each voxel.
     * Builds a single texture out of this data.
     * */
    public void initialize(TextureLoader textureLoader) throws VoxelTexturePackException {
        if (initialized) {
            log.error("Texture pack already initialized.");
            return;
        }

        voxelTextureBuilder = new VoxelTextureBuilder();

        // Build the (necessary) diffuse texture atlas along with building the texture mappings.
        // Each voxel requires at least a diffuse texture for each face. Therefore, this atlas and the mappings
        // generated from it are the baseline for all other atlases.
        TextureAtlasProperties textureAtlasProperties = buildDiffuseTextureAtlas(textureLoader);

//        System.out.println("specular:");
        specularTextureAtlas = buildOptionalTextureAtlas(textureLoader, textureAtlasProperties,
                VoxelFaceTexture::getSpecularPath,
                defaultVoxelFaceTexture.getSpecular());

//        System.out.println("normal:");
        normalTextureAtlas = buildOptionalTextureAtlas(textureLoader, textureAtlasProperties,
                VoxelFaceTexture::getNormalPath,
                defaultVoxelFaceTexture.getNormal());

//        System.out.println("emission:");
        emissionTextureAtlas = buildOptionalTextureAtlas(textureLoader, textureAtlasProperties,
                VoxelFaceTexture::getEmissionPath,
                defaultVoxelFaceTexture.getEmission());

        material = new Material();
        material.setProperty("materialShininess", 32F);
        material.setTexture(0, diffuseTextureAtlas);
        material.setTexture(1, specularTextureAtlas);
        material.setTexture(2, normalTextureAtlas);
        material.setTexture(3, emissionTextureAtlas);
        initialized = true;
    }

    /**
     * Builds a copy of the texture mappings (which are based on the diffuse texture path) such that the keys are
     * whatever corresponding texture path extracted by the extractor.
     * */
    public Map<String, Vector4f> buildAtlasTextureMapping(Function<VoxelFaceTexture, String> texturePathExtractor) {
        Map<String, Vector4f> optionalTextureMappings = new HashMap<>();

        int unmappedTextureCount = 0;

        for (VoxelProperties voxelProperties : voxelRegistry.getVoxelProperties().values()) {
            for (VoxelFaceTexture voxelFaceTexture : voxelProperties.getFaces().values()) {

                Vector4f mapping = this.textureMappings.get(voxelFaceTexture.getDiffusePath());
                String texturePath = texturePathExtractor.apply(voxelFaceTexture);

                if (texturePath != null && !texturePath.isEmpty()) {
                    optionalTextureMappings.put(texturePath, new Vector4f(mapping));
                } else {
                    optionalTextureMappings.put(PLACEHOLDER_FOR_UNMAPPED_TEXTURE + (++unmappedTextureCount), new Vector4f(mapping));
                }
            }
        }
        return optionalTextureMappings;
    }

    private Texture buildOptionalTextureAtlas(TextureLoader textureLoader,
                                                             TextureAtlasProperties dimensions,
                                                             Function<VoxelFaceTexture, String> texturePathExtractor,
                                                             TextureData defaultTexture) {
        // Loads all textures for this texture atlas into memory
        Map<String, TextureData> textures = loadTexturesForAtlas(textureLoader, texturePathExtractor);

        // Rebuilds a texture mapping for this atlas
        Map<String, Vector4f> textureMapping = buildAtlasTextureMapping(texturePathExtractor);

        // build the atlas texture itself
        Texture textureAtlas = buildTextureAtlas(dimensions, textures, textureMapping, defaultTexture);

        return textureAtlas;
    }

    private TextureAtlasProperties buildDiffuseTextureAtlas(TextureLoader textureLoader) throws VoxelTexturePackException {
        // Loads all textures for this texture atlas into memory
        Map<String, TextureData> textures = loadTexturesForAtlas(textureLoader, VoxelFaceTexture::getDiffusePath);

        // texture mappings must be calculated before the texture atlas can be built
        TextureAtlasProperties textureAtlasProperties = buildTextureMappings(textures);

        // This changes the texture mapping from pixel coordinates to UV coordinates
        normalizeTextureMappings(textureAtlasProperties.getWidth(), textureAtlasProperties.getHeight());

        // Build the diffuse texture atlas!
        diffuseTextureAtlas = buildTextureAtlas(textureAtlasProperties, textures, textureMappings, defaultVoxelFaceTexture.getDiffuse());

        // return those dimensions
        return textureAtlasProperties;
    }

    /**
     * Ensures all texture data has the same size and ensures that they are all squares.
     *
     * @return The size of the textures or -1 if no textures were found.
     * */
    private int findTextureSize(Map<String, TextureData> textures) throws VoxelTexturePackException {
        int size = -1;

        for (Map.Entry<String, TextureData> entry : textures.entrySet()) {
            TextureData textureData = entry.getValue();
            if (textureData.getWidth() != textureData.getHeight()) {
                throw new VoxelTexturePackException("Invalid texture size: " + entry.getKey());
            }

            if (size == -1) {
                size = textureData.getWidth();
            } else if (textureData.getWidth() != size) {
                throw new VoxelTexturePackException("Texture '" + entry.getKey() + "' does not fit dimensions: " + size);
            }
        }

        return size;
    }

    /**
     * Creates the texture mappings and texture width, height, and padding, given some diffuse texture data.
     * <br/>
     * Something weird to note about this function.
     * <br/>
     * The width, in pixels, of a texture atlas with the following properties:
     * <br/>
     * <pre>
     *     textureCount = maximum number of textures in horizontal axis, e.g. 16
     *     textureSize = size of texture, e.g. 64
     *     padding = number of pixels added between each texture and the edges of a texture atlas, e.g. 4
     * </pre>
     * is equal to the equation:
     * <pre>
     *     textureAtlasSize = textureCount * (textureSize + padding) + padding
     * </pre>
     * One problem with generating texture atlases that use mip maps is that the downsampled versions of the original
     * texture may have their edges bleed together. Also, the texture size should be in powers of two, so the width and
     * height are rounded up to the power-of-two they are closest to. The maximum number of textures stored horizontally
     * is limited by the constant {@link com.branwilliams.bundi.voxel.VoxelConstants#MAX_TEXTURE_SIZE MAX_TEXTURE_SIZE}.
     * This is not ideal, but it is there as precaution.
     *
     * <br/> <br/>
     * TODO some texture atlas creator, independent of the actual texture creation.
     * */
    private TextureAtlasProperties buildTextureMappings(Map<String, TextureData> textures) throws VoxelTexturePackException {
        int textureSize = findTextureSize(textures);

        if (textureSize == -1) {
            throw new VoxelTexturePackException("No diffuse textures are mapped!");
        }

        int padding = textureSize;

        // The maximum number of textures in the horizontal axis must be limited by the maximum texture size.
        int maxHorizontal = Mathf.floor((MAX_TEXTURE_SIZE - padding) / (float) (textureSize + padding));

        int positionX = 0;
        int positionY = 0;

        int maxWidth = 0;
        int maxHeight = 0;

        // height of the current row
        int tempRowHeight = 0;

        int index = 0;
        for (Map.Entry<String, TextureData> texture : textures.entrySet()) {
            TextureData textureData = texture.getValue();

            int width = textureData.getWidth();
            int height = textureData.getHeight();

            // for index = 0, the following steps will still apply, and therefore the initial positionX and positionY
            // will have padding. For every other index, a new row needs to be created, so the following logic occurs:
            // 1. reset x position,
            // 2. increase the y based on the height of the current row,
            // 3. and reset the current row height.
            if (index % maxHorizontal == 0) {
                positionX = padding / 2;
                positionY += tempRowHeight + (index == 0 ? padding / 2 : padding);
                tempRowHeight = 0;
            }

            if (height > tempRowHeight)
                tempRowHeight = height;

             textureMappings.put(texture.getKey(),
                     new Vector4f(positionX, positionY, positionX + width, positionY + height));

//             System.out.println((idx++) + ": x:" + positionX + ", y:" + positionY + ": texturePath:" + texture.getKey());

             // Ensure that we calculate the width and height of the output texture.
            if (positionX + width + padding > maxWidth)
                maxWidth = positionX + width + padding;

            if (positionY + height + padding > maxHeight)
                maxHeight = positionY + height + padding;

            positionX += width + padding;

            index++;
        }

        // Ensures that the width of the texture is a power of two.
        int appropriateWidth = (int) Math.pow(2, Mathf.ceil(Mathf.log(2, maxWidth)));
        int appropriateHeight = (int) Math.pow(2, Mathf.ceil(Mathf.log(2, maxHeight)));

        // Currently keeping the texture uniform in width/height...
        return new TextureAtlasProperties(appropriateWidth, appropriateHeight, padding, textureSize);
    }

    private void normalizeTextureMappings(int width, int height) {
        for (String texturePath : textureMappings.keySet()) {
            Vector4f mapping = textureMappings.get(texturePath);
            // Convert the values from pixel positions to 0 ~ 1
            mapping.x /= (float) width;
            mapping.y /= (float) height;
            mapping.z /= (float) width;
            mapping.w /= (float) height;
        }
    }

    /**
     * Builds a mapping of VoxelFaceTextures to TextureData using (potentially) between the following functions:
     * <br/>
     * * {@link VoxelFaceTexture#getDiffusePath()}
     * <br/>
     * * {@link VoxelFaceTexture#getSpecularPath()}
     * <br/>
     * * {@link VoxelFaceTexture#getNormalPath()}
     * <br/>
     * * {@link VoxelFaceTexture#getEmissionPath()}
     * @param textureLoader The {@link TextureLoader} to load textures with.
     * @param texturePathExtractor This function extracts a texture path from a given {@link VoxelFaceTexture}
     * */
    private Map<String, TextureData> loadTexturesForAtlas(TextureLoader textureLoader,
                                      Function<VoxelFaceTexture, String> texturePathExtractor) {
        // texture paths mapped to texture data
        Map<String, TextureData> textures = new HashMap<>();

        for (VoxelProperties voxelProperties : voxelRegistry.getVoxelProperties().values()) {
            for (VoxelFaceTexture voxelFaceTexture : voxelProperties.getFaces().values()) {

                String texturePath = texturePathExtractor.apply(voxelFaceTexture);
                if (texturePath != null && !textures.containsKey(texturePath)) {
                    try {
                        textures.put(texturePath, textureLoader.loadTexture(texturePath));
                    } catch (IOException e) {
                        log.error("Unable to load voxel texture: " + texturePath, e);
                    }
                }
            }
        }

        return textures;
    }

    private Texture buildTextureAtlas(TextureAtlasProperties textureAtlasProperties,
                                      Map<String, TextureData> textures,
                                      Map<String, Vector4f> textureMappings,
                                      TextureData defaultTexture) {
        Texture atlasTexture = voxelTextureBuilder.buildTextureAtlas(textureAtlasProperties, textures, textureMappings,
                defaultTexture);

        return atlasTexture;
    }

    public Vector4f getTextureCoordinates(VoxelIdentifier voxelId, VoxelFace voxelFace) {
        return getTextureCoordinates(getVoxelFaceTexture(voxelId, voxelFace));
    }

    public Vector4f getTextureCoordinates(Voxel voxel, VoxelFace voxelFace) {
        return getTextureCoordinates(getVoxelFaceTexture(voxel.id, voxelFace));
    }

    public Vector4f getTextureCoordinates(VoxelFaceTexture voxelFaceTexture) {
        if (voxelFaceTexture != null) {
            return textureMappings.get(voxelFaceTexture.getDiffusePath());
        } else {
            return null;
        }
    }

    /**
     * @return The {@link VoxelFaceTexture} object mapped to the face of the {@link VoxelIdentifier} provided.
     * */
    public VoxelFaceTexture getVoxelFaceTexture(VoxelIdentifier voxelId, VoxelFace voxelFace) {
        return getVoxelFaceTexture(voxelRegistry.getVoxelProperties(voxelId.normalized()), voxelFace);
    }

    /**
     * @return The {@link VoxelFaceTexture} object mapped to the face within the {@link VoxelProperties} provided.
     * */
    public VoxelFaceTexture getVoxelFaceTexture(VoxelProperties properties, VoxelFace voxelFace) {
        if (properties != null) {
            return properties.getTexturePath(voxelFace);
        } else {
            return null;
        }
    }


    /**
     * @return The Material used for rendering chunks.
     * */
    public Material getMaterial() {
        return material;
    }

    public Texture getDiffuseTextureAtlas() {
        return diffuseTextureAtlas;
    }

    public Texture getSpecularTextureAtlas() {
        return specularTextureAtlas;
    }

    public Texture getNormalTextureAtlas() {
        return normalTextureAtlas;
    }

    public Texture getEmissionTextureAtlas() {
        return emissionTextureAtlas;
    }

    @Override
    public void destroy() {
        diffuseTextureAtlas.destroy();
        specularTextureAtlas.destroy();
        normalTextureAtlas.destroy();
        emissionTextureAtlas.destroy();
    }

    public static class TextureAtlasProperties {

        private int width;

        private int height;

        private int padding;

        private int textureSize;

        public TextureAtlasProperties(int width, int height, int padding, int textureSize) {
            this.width = width;
            this.height = height;
            this.padding = padding;
            this.textureSize = textureSize;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public int getPadding() {
            return padding;
        }

        public int getTextureSize() {
            return textureSize;
        }
    }

}

package com.branwilliams.bundi.voxel.render.mesh.builder;

import com.branwilliams.bundi.engine.texture.DownsampledTextureData;
import com.branwilliams.bundi.engine.texture.TextureData;
import com.branwilliams.bundi.engine.texture.Texture;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.bundi.engine.util.TextureUtils;
import com.branwilliams.bundi.voxel.voxels.model.VoxelFaceTexture;
import com.branwilliams.bundi.voxel.io.VoxelTexturePack;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.*;

import static com.branwilliams.bundi.voxel.io.VoxelTexturePack.PLACEHOLDER_FOR_UNMAPPED_TEXTURE;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author Brandon
 * @since August 04, 2019
 */
public class VoxelTextureBuilder {

    // TODO fix this pos or scrap it
    private static boolean USE_CUSTOM_DOWNSAMPLING = false;

    private static final int TEXTURE_CHANNELS = 4;

    /**
     * Creates the texture atlas given some textures, their mappings, a default texture for those who have no mappings,
     * and some texture atlas properties.
     *
     * @param textureAtlasProperties The properties of the texture atlas.
     * @param textures A mapping of texture paths to texture data
     * @param textureMappings A mapping of {@link VoxelFaceTexture} to texture mapping
     * @param defaultTexture The default texture used when no extracted texture path is found within the textures map
     * @return A single texture atlas formed from every texture data provided.
     * */
    public Texture buildTextureAtlas(VoxelTexturePack.TextureAtlasProperties textureAtlasProperties,
                                     Map<String, TextureData> textures,
                                     Map<String, Vector4f> textureMappings,
                                     TextureData defaultTexture) {

        int maxMipMapLevels = Mathf.floor(Mathf.log(2, textureAtlasProperties.getTextureSize()));

        // Then build the original texture atlas
        TextureData originalTextureAtlas = buildTexture(textureAtlasProperties.getWidth(),
                textureAtlasProperties.getHeight(), textureAtlasProperties.getPadding(), textures, textureMappings, defaultTexture);

        Texture texture = new Texture(originalTextureAtlas, false);
//        System.out.println(texture);

        //
        // See the following regarding texture parameters:
        // https://www.khronos.org/registry/OpenGL-Refpages/gl4/html/glTexParameter.xhtml
        // https://www.khronos.org/opengl/wiki/Sampler_Object#Filtering
        //
        texture.bind();
        texture.magFilter(GL_NEAREST);
        texture.minFilter(GL_NEAREST_MIPMAP_NEAREST);
        texture.clampToEdges();
        texture.setMaxMipMapLevel(maxMipMapLevels);


        if (USE_CUSTOM_DOWNSAMPLING) {
            // First, downsample the texture data
            Map<String, DownsampledTextureData> downsampledTextures = downsampleTextures(textures, textureMappings,
                    defaultTexture);

            // Then create texture atlases using the downsampled texture data for each mipmap
            createMipmapsForTextureAtlas(textureAtlasProperties.getWidth(),
                    textureAtlasProperties.getHeight(), textureAtlasProperties.getPadding(), downsampledTextures,
                    textureMappings, texture, defaultTexture);
        } else {
            texture.generateMipmaps();
        }

        Texture.unbind(texture);

        return texture;
    }

    /**
     * Creates the mipmaps for a texture atlas using the downsampled texture data provided.
     * */
    private void createMipmapsForTextureAtlas(int width, int height, int padding,
                                              Map<String, DownsampledTextureData> downsampledTextures,
                                              Map<String, Vector4f> textureMappings,
                                              Texture texture,
                                              TextureData defaultTexture) {
        int maxSamples = getMaxSamples(downsampledTextures);

        // OpenGL mip map levels are indexed from 0 ~ GL_TEXTURE_MAX_LEVEL where 0 is the index of the original sized
        // image. The indices for the samples within the DownsampledTextureData object are between 0 ~ n - 1.
        for (int i = 0; i < maxSamples; i++) {
            // Half the width if possible
            if (width > 1)
                width = Mathf.floor(width * 0.5F);


            // Half the height if possible
            if (height > 1)
                height = Mathf.floor(height * 0.5F);

            // Decrease padding as well.
            padding *= 0.5F;

            int sample = i;
            Map<String, TextureData> textureData = extractTextureDataFromDownsampled(downsampledTextures, i);

            TextureData mipmapTextureAtlas = buildTexture(width, height, padding, textureData, textureMappings, defaultTexture);
            System.out.println("mip=" + (i + 1) + ", sampleIdx=" + sample + ",img=" + mipmapTextureAtlas);

            texture.uploadMipMap(i + 1, mipmapTextureAtlas);

            mipmapTextureAtlas.destroy();
        }

        // Destroy that texture data
        downsampledTextures.values().forEach(DownsampledTextureData::destroy);

    }

    /**
     * Extracts the texture data from the downsampled texture data, given some sample index.
     *
     * @param downsampledTextureData The downsampled texture data
     * @param sampleIndex Index of the downsampled texture data of interest.
     */
    private Map<String, TextureData> extractTextureDataFromDownsampled(
            Map<String, DownsampledTextureData> downsampledTextureData,
            int sampleIndex) {

        Map<String, TextureData> extractedTextureData = new HashMap<>();

        for (Map.Entry<String, DownsampledTextureData> entry : downsampledTextureData.entrySet()) {
            extractedTextureData.put(entry.getKey(), entry.getValue().getSample(sampleIndex));
        }

        return extractedTextureData;
    }

    /**
     * This will produce a mapping of texture paths to downsampled texture data. Any mappings which do not have
     * texture data associated with them will be assigned the downsampled default texture data.
     *
     * @param textures The mapping of voxel face textures to texture data
     * @param textureMappings The mapping of voxel face textures to pixel coordinates
     * @param defaultTexture The default texture used when a voxel face texture is mapped to no texture data.
     * */
    private Map<String, DownsampledTextureData> downsampleTextures(Map<String, TextureData> textures,
                                                                   Map<String, Vector4f> textureMappings,
                                                                   TextureData defaultTexture) {
        Map<String, DownsampledTextureData> downsampledTextures = new HashMap<>();

        // Downsample the default texture one time
        DownsampledTextureData downsampledDefaultTexture = TextureUtils.downsample(defaultTexture);

        // Keep track of the max number of samples
        int samples = downsampledDefaultTexture.getSamples();

        for (Map.Entry<String, Vector4f> entry : textureMappings.entrySet()) {
            // If no mappings can be found for a given VoxelFaceTexture, then the default texture is assumed.
            if (textures.containsKey(entry.getKey())) {
                DownsampledTextureData textureData = TextureUtils.downsample(textures.get(entry.getKey()));
                samples = Math.max(samples, textureData.getSamples());
                downsampledTextures.put(entry.getKey(), textureData);
            } else {
                downsampledTextures.put(entry.getKey(), downsampledDefaultTexture);
                textures.put(entry.getKey(), defaultTexture);
            }
        }

        return downsampledTextures;
    }

    /**
     * @return The max sample number within the map of downsampled texture data.
     * */
    private int getMaxSamples(Map<String, DownsampledTextureData> downsampledTextures) {
        OptionalInt optionalMax = downsampledTextures.values().stream().mapToInt(DownsampledTextureData::getSamples).max();
        return optionalMax.orElse(0);
    }

    /**
     * Creates a texture atlas given some width, height, padding, textures, and their mappings (in UV coordinates).
     * The padded areas are filled in with the texture edges, such that half of the pixels are from each texture.
     *
     * @param width The width of the texture to build
     * @param height The height of the texture to build
     * @param padding The amount of padding between individual textures within the texture atlas
     * @param textures A mapping of voxel face textures to texture data.
     * @param textureMappings The mapping of voxel face textures to pixel coordinates

     * @return A single texture atlas formed from every texture data provided.
     * */
    private TextureData buildTexture(int width, int height, int padding,
                                     Map<String, TextureData> textures,
                                     Map<String, Vector4f> textureMappings,
                                     TextureData defaultTexture) {

        // Half of the padded area contains pixels from the edges of each texture.
        int halfPadding = padding / 2;

        ByteBuffer buffer = MemoryUtil.memAlloc(TEXTURE_CHANNELS * width * height);

        for (Map.Entry<String, Vector4f> entry : textureMappings.entrySet()) {
            // Convert from UV coordinates to pixel coordinates
            Vector4f mapping = new Vector4f(textureMappings.get(entry.getKey())).mul(width, height, width, height);

            TextureData textureData;

            // Replace to use the thang
            if (entry.getKey().startsWith(PLACEHOLDER_FOR_UNMAPPED_TEXTURE)) {
                textureData = defaultTexture;
            } else {
                textureData = textures.get(entry.getKey());
            }

            ByteBuffer textureBuffer = textureData.getData();

            for (int x = -halfPadding; x < textureData.getWidth() + halfPadding; x++) {
                for (int y = -halfPadding; y < textureData.getHeight() + halfPadding; y++) {
                    int imageIndex = textureData.getIndex(x, y);
//                    System.out.println("x=" + x + ", y=" + y + ", imageIndex=" + imageIndex);

                    int mapX = Mathf.floor(mapping.x) + x;
                    int mapY = Mathf.floor(mapping.y) + y;
                    int stitchedIndex = (mapX + (mapY * width)) * TEXTURE_CHANNELS;
//                    System.out.println("mapX=" + mapX + ", mapY=" + mapY + ", stitchIndex=" + stitchedIndex);

                    byte r = textureBuffer.get(imageIndex);
                    byte g = textureBuffer.get(imageIndex + 1);
                    byte b = textureBuffer.get(imageIndex + 2);
                    byte a = (byte) 255;
                    if (textureData.getChannels() == 4)
                        a = textureBuffer.get(imageIndex + 3);

                    buffer.put(stitchedIndex, r);
                    buffer.put(stitchedIndex + 1, g);
                    buffer.put(stitchedIndex + 2, b);
                    buffer.put(stitchedIndex + 3, a);
                }
            }
        }

        TextureData textureData = new TextureData(width, height, TEXTURE_CHANNELS, GL_RGBA, buffer);

        return textureData;
    }

}

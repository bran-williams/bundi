package com.branwilliams.bundi.pbr.util;

import com.branwilliams.bundi.engine.shader.Material;
import com.branwilliams.bundi.engine.texture.Texture;
import com.branwilliams.bundi.engine.texture.TextureData;
import com.branwilliams.bundi.engine.texture.TextureLoader;
import com.branwilliams.bundi.engine.util.TextureUtils;
import com.branwilliams.bundi.pbr.pipeline.material.PbrCombiner;
import com.branwilliams.bundi.pbr.pipeline.material.PbrModifier;

import java.io.IOException;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Brandon
 * @since August 31, 2019
 */
public enum PbrUtils {
    INSTANCE;

    private PbrUtils() {}

    public static Material createPbrMaterial(TextureLoader textureLoader, String directory, String diffuse, String metallic, String normal, String roughness) throws IOException {
        return createPbrMaterial(textureLoader, directory + diffuse,
                metallic == null ? null : directory + metallic,
                directory + normal,
                roughness == null ? null : directory + roughness);
    }

    /**
     * Creates the {@link Material} object for the physically based rendering pipeline. This material will have two
     * textures,
     * <pre>
     *     texture 0 - diffuse in RGB and metallic  in A
     *     texture 1 - normal  in RGB and roughness in A
     * </pre>
     * The metallic and roughness values will be set to zero if those texture paths are not provided.
     *
     * @param textureLoader The texture loader to use given the paths.
     * @param diffuse The path to the diffuse texture.
     * @param metallic The path to the metallic texture. This can optionally be null.
     * @param normal The path to the normal texture.
     * @param roughness The path to the roughness texture. This can optionally be null.
     *
     * @return A material object for the physically based rendering pipeline.
     * */
    public static Material createPbrMaterial(TextureLoader textureLoader, String diffuse, String metallic, String normal, String roughness) throws IOException {
        BiFunction<Integer, Integer, Integer> combiner = new PbrCombiner();
        Function<Integer, Integer> modifier = new PbrModifier(0.0F);

        TextureData diffuseData = textureLoader.loadTexture(diffuse);
        TextureData normalData = textureLoader.loadTexture(normal);

        // Combine the metallic texture data into the alpha channel of the diffuse texture.
        // If the metallic texture is not provided, modify it so the alpha channel is zero.
        if (metallic != null) {
            TextureData metallicData = textureLoader.loadTexture(metallic);
            diffuseData = TextureUtils.combine(diffuseData, metallicData, 4, combiner);
            metallicData.destroy();
        } else {
            diffuseData = TextureUtils.modify(diffuseData, modifier);
        }

        // Combine the roughness texture data into the alpha channel of the normal texture.
        // If the roughness texture is not provided, modify it so the alpha channel is zero.
        if (roughness != null) {
            TextureData roughnessData = textureLoader.loadTexture(roughness);
            normalData = TextureUtils.combine(normalData, roughnessData, 4, combiner);
            roughnessData.destroy();
        } else {
            normalData = TextureUtils.modify(normalData, modifier);
        }

        Material material = new Material();
        material.setTexture(0, new Texture(diffuseData, true));
        material.setTexture(1, new Texture(normalData, true));
        return material;
    }
}

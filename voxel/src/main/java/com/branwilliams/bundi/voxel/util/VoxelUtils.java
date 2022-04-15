package com.branwilliams.bundi.voxel.util;

import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.texture.TextureLoader;
import com.branwilliams.bundi.voxel.io.JsonLoader;
import com.branwilliams.bundi.voxel.io.VoxelTexturePack;
import com.branwilliams.bundi.voxel.io.VoxelTexturePackException;
import com.branwilliams.bundi.voxel.voxels.VoxelRegistry;
import com.branwilliams.bundi.voxel.voxels.model.VoxelFaceTexture;
import com.branwilliams.bundi.voxel.voxels.model.VoxelProperties;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.Map;

public final class VoxelUtils {

    private static final Logger LOG = LoggerFactory.getLogger(VoxelUtils.class);

    private VoxelUtils() {}

    /**
     * @return The path to the asset directory for this voxel scene.
     * */
    public static Path getAssetDirectory(EngineContext engineContext) {
        return engineContext.getAssetDirectory().resolve("voxel");
    }

    public static VoxelRegistry loadVoxelRegistry(JsonLoader jsonLoader, Path voxels) {
        // Load the voxel definitions from the provided path.
        Type voxelPropertiesType = new TypeToken<Map<String, VoxelProperties>>() {}.getType();
        Map<String, VoxelProperties> properties = jsonLoader.loadObject(voxelPropertiesType, voxels);

        // Create the registry of voxels
        VoxelRegistry voxelRegistry = new VoxelRegistry(properties);
        voxelRegistry.initialize();
        return voxelRegistry;
    }

    /**
         * Reads the voxel properties and creates a registry of them. Then reads the default textures for voxel faces
         * that do not have textures. Finally, creates the texture pack using the previous two.
         * */
    public static VoxelTexturePack loadVoxelTextures(VoxelRegistry voxelRegistry, JsonLoader jsonLoader, Path assetDirectory,
                                  Path defaultVoxelFaces) {
        TextureLoader textureLoader = new TextureLoader(assetDirectory);

        // Load default faces for voxels with no mappings
        VoxelFaceTexture defaultVoxelFaceTexture = jsonLoader.loadObject(VoxelFaceTexture.class, defaultVoxelFaces);
        try {
            defaultVoxelFaceTexture.load(textureLoader);
        } catch (IOException e) {
            LOG.error("Unable create default voxel faces from " + defaultVoxelFaces, e);
            return null;
        }

        // Create the texture pack
        VoxelTexturePack texturePack = new VoxelTexturePack(voxelRegistry, defaultVoxelFaceTexture);

        try {
            texturePack.initialize(textureLoader);
            return texturePack;
        } catch (VoxelTexturePackException e) {
            LOG.error("Unable create texture pack from " + voxelRegistry, e);
            return null;
        }
    }
}

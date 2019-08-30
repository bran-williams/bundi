package com.branwilliams.bundi.voxel.io;

import com.branwilliams.bundi.engine.core.Keycode;
import com.branwilliams.bundi.engine.core.Keycodes;
import com.branwilliams.bundi.engine.util.IOUtils;
import com.branwilliams.bundi.voxel.voxels.model.VoxelProperties;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Brandon
 * @since August 15, 2019
 */
public class JsonLoader {

    private final Path assetDirectory;

    private Gson gson;

    public JsonLoader(Path assetDirectory) {
        this.assetDirectory = assetDirectory;
    }

    public void initialize(Keycodes keycodes) {
        GsonBuilder gsonBuilder = new GsonBuilder().enableComplexMapKeySerialization()
                .setPrettyPrinting();
        gsonBuilder.registerTypeAdapter(VoxelProperties.class, new VoxelProperties.VoxelPropertiesDeserializer());
        gsonBuilder.registerTypeAdapter(Keycode.class, new Keycode.KeycodeDeserializer(keycodes));
        gson = gsonBuilder.create();
    }

    public <T> T loadObject(Class<T> objectType, String filePath) {
        return  loadObject(objectType, Paths.get(filePath));
    }

    public <T> T loadObject(Class<T> objectType, Path filePath) {
        filePath = assetDirectory.resolve(filePath);
        String json = IOUtils.readFile(filePath, null);

        if (json == null) {
            throw new IllegalArgumentException("File must exist!");
        }

        return gson.fromJson(json, objectType);
    }

    public <T> T loadObject(Type objectType, String filePath) {
        return  loadObject(objectType, Paths.get(filePath));
    }

    public <T> T loadObject(Type objectType, Path filePath) {
        filePath = assetDirectory.resolve(filePath);
        String json = IOUtils.readFile(filePath, null);

        if (json == null) {
            throw new IllegalArgumentException("File must exist!");
        }

        return gson.fromJson(json, objectType);
    }

    public Path getAssetDirectory() {
        return assetDirectory;
    }
}

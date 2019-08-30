package com.branwilliams.bundi.voxel.voxels.model;

import com.branwilliams.bundi.voxel.voxels.VoxelFace;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Brandon
 * @since July 21, 2019
 */
public class VoxelProperties {

    private Map<VoxelFace, VoxelFaceTexture> faces;

    private boolean translucent;

    public VoxelProperties(Map<VoxelFace, VoxelFaceTexture> faces, boolean translucent) {
        this.faces = faces;
        this.translucent = translucent;
    }

    public Map<VoxelFace, VoxelFaceTexture> getFaces() {
        return faces;
    }

    public boolean isTranslucent() {
        return translucent;
    }

    public VoxelFaceTexture getTexturePath(VoxelFace voxelFace) {
        return faces.get(voxelFace);
    }

    @Override
    public String toString() {
        return "VoxelProperties{" +
                "faces=" + faces +
                '}';
    }

    public static class VoxelPropertiesDeserializer implements JsonDeserializer<VoxelProperties> {

        private static final String ALL_FACES_KEY = "all";

        private static final String TRANSLUCENT_KEY = "translucent";

        @Override
        public VoxelProperties deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            Map<VoxelFace, VoxelFaceTexture> faces = readFaces(jsonObject.getAsJsonObject("faces"), context);

            boolean translucent = readTranslucent(jsonObject);

            return new VoxelProperties(faces, translucent);
        }

        private Map<VoxelFace, VoxelFaceTexture> readFaces(JsonObject jsonObject, JsonDeserializationContext context) {
            if (jsonObject == null) {
                return new HashMap<>();
            }

            Map<VoxelFace, VoxelFaceTexture> faces = new HashMap<>();

            if (jsonObject.has(ALL_FACES_KEY)) {
                VoxelFaceTexture texture = context.deserialize(jsonObject.get(ALL_FACES_KEY), VoxelFaceTexture.class);

                for (VoxelFace face : VoxelFace.values()) {
                    faces.put(face, texture);
                }

            } else {
                for (String key : jsonObject.keySet()) {
                    VoxelFace face = VoxelFace.fromName(key);

                    if (face != null) {
                        VoxelFaceTexture texture = context.deserialize(jsonObject.get(key), VoxelFaceTexture.class);
                        faces.put(face, texture);
                    }
                }
            }

            return faces;
        }

        private boolean readTranslucent(JsonObject jsonObject) {
            if (jsonObject.has(TRANSLUCENT_KEY)) {
                return jsonObject.get(TRANSLUCENT_KEY).getAsBoolean();
            }
            return false;
        }
    }
}

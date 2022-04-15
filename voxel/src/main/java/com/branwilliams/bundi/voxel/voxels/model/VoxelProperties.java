package com.branwilliams.bundi.voxel.voxels.model;

import com.branwilliams.bundi.voxel.util.LightUtils;
import com.branwilliams.bundi.voxel.voxels.VoxelFace;
import com.google.gson.*;

import java.awt.Color;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static com.branwilliams.bundi.voxel.VoxelConstants.*;

/**
 * @author Brandon
 * @since July 21, 2019
 */
public class VoxelProperties {

    private Map<VoxelFace, VoxelFaceTexture> faces;

    private boolean opaque;

    private int light;

    public VoxelProperties(Map<VoxelFace, VoxelFaceTexture> faces, boolean opaque, int light) {
        this.faces = faces;
        this.opaque = opaque;
        this.light = light;
    }

    public Map<VoxelFace, VoxelFaceTexture> getFaces() {
        return faces;
    }

    public boolean isOpaque() {
        return opaque;
    }

    public int getLight() {
        return light;
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

        private static final String OPAQUE_KEY = "opaque";

        private static final String LIGHT_KEY = "light";

        @Override
        public VoxelProperties deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            Map<VoxelFace, VoxelFaceTexture> faces = readFaces(jsonObject.getAsJsonObject("faces"), context);

            boolean opaque = readOpaque(jsonObject);

            int light = readLight(jsonObject);

            return new VoxelProperties(faces, opaque, light);
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

        private boolean readOpaque(JsonObject jsonObject) {
            if (jsonObject.has(OPAQUE_KEY)) {
                return jsonObject.get(OPAQUE_KEY).getAsBoolean();
            }
            return true;
        }

        private int readLight(JsonObject jsonObject) {
            int light = 0;
            if (jsonObject.has(LIGHT_KEY)) {
                try {
                    Color color = Color.decode(jsonObject.get(LIGHT_KEY).getAsString());
                    int red = Math.max(0, Math.min(MAX_LIGHT_RED, color.getRed()));
                    int green = Math.max(0, Math.min(MAX_LIGHT_GREEN, color.getGreen()));
                    int blue = Math.max(0, Math.min(MAX_LIGHT_BLUE, color.getBlue()));
                    light = LightUtils.pack(red, green, blue);
                } catch (NumberFormatException ignored) {}
            }
            return light;
        }
    }
}

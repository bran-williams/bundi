package com.branwilliams.bundi.engine.deserializers;

import com.branwilliams.bundi.engine.util.ColorUtils;
import com.google.gson.*;
import org.joml.Vector4f;

import java.awt.Color;
import java.lang.reflect.Type;

import static com.branwilliams.bundi.engine.util.ColorUtils.toVector4;
import static com.branwilliams.bundi.engine.util.GsonUtils.getFirstValidKey;

/**
 * @author Brandon
 * @since December 13, 2020
 */
public class Vector4fDeserializer implements JsonDeserializer<Vector4f> {
    @Override
    public Vector4f deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        Vector4f vector4f = new Vector4f();

        if (jsonObject.has("argb") || jsonObject.has("rgb")) {
            String hexColor = getFirstValidKey(jsonObject, "argb", "rgb").getAsString();
            Color color = ColorUtils.fromHex(hexColor);
            vector4f = ColorUtils.toVector4(color);
            return vector4f;
        }

        vector4f.x = getFirstValidKey(jsonObject, "x", "r").getAsFloat();
        vector4f.y =  getFirstValidKey(jsonObject, "y", "g").getAsFloat();
        vector4f.z =  getFirstValidKey(jsonObject, "z", "b").getAsFloat();
        vector4f.w =  getFirstValidKey(jsonObject, "w", "a").getAsFloat();

        return vector4f;
    }

}

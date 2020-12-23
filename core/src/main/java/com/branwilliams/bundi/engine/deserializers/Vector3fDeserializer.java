package com.branwilliams.bundi.engine.deserializers;

import com.branwilliams.bundi.engine.util.ColorUtils;
import com.google.gson.*;
import org.joml.Vector3f;

import java.awt.*;
import java.lang.reflect.Type;

import static com.branwilliams.bundi.engine.util.GsonUtils.getFirstValidKey;

/**
 * @author Brandon
 * @since August 09, 2019
 */
public class Vector3fDeserializer implements JsonDeserializer<Vector3f> {
    @Override
    public Vector3f deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        Vector3f vector3f = new Vector3f();

        if (jsonObject.has("rgb")) {
            String hexColor = getFirstValidKey(jsonObject, "rgb").getAsString();
            Color color = ColorUtils.fromHex(hexColor);
            vector3f = ColorUtils.toVector3(color);
            return vector3f;
        }

        vector3f.x = getFirstValidKey(jsonObject, "x", "r").getAsFloat();
        vector3f.y =  getFirstValidKey(jsonObject, "y", "g").getAsFloat();
        vector3f.z =  getFirstValidKey(jsonObject, "z", "b").getAsFloat();

        return vector3f;
    }

}

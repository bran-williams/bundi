package com.branwilliams.bundi.engine.deserializers;

import com.google.gson.*;
import org.joml.Vector2f;

import java.lang.reflect.Type;

import static com.branwilliams.bundi.engine.util.GsonUtils.getFirstValidKey;

/**
 * @author Brandon
 * @since August 09, 2019
 */
public class Vector2fDeserializer implements JsonDeserializer<Vector2f> {
    @Override
    public Vector2f deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        Vector2f vector2f = new Vector2f();

        vector2f.x = getFirstValidKey(jsonObject, "x", "r").getAsFloat();
        vector2f.y = getFirstValidKey(jsonObject, "y", "g").getAsFloat();

        return vector2f;
    }
}

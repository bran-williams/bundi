package com.branwilliams.bundi.engine.deserializers;

import com.google.gson.*;
import org.joml.Vector3f;

import java.lang.reflect.Type;

/**
 * @author Brandon
 * @since August 09, 2019
 */
public class Vector3fDeserializer implements JsonDeserializer<Vector3f> {
    @Override
    public Vector3f deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        Vector3f vector3f = new Vector3f();

        vector3f.x = jsonObject.get("x").getAsFloat();
        vector3f.y = jsonObject.get("y").getAsFloat();
        vector3f.z = jsonObject.get("z").getAsFloat();

        return vector3f;
    }
}

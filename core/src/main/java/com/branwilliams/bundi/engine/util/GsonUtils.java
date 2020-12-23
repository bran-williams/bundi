package com.branwilliams.bundi.engine.util;

import com.branwilliams.bundi.engine.deserializers.Vector2fDeserializer;
import com.branwilliams.bundi.engine.deserializers.Vector3fDeserializer;
import com.branwilliams.bundi.engine.deserializers.Vector4fDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Brandon
 * @since January 09, 2020
 */
public class GsonUtils {

    public static Gson defaultGson() {
        return new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(Vector2f.class, new Vector2fDeserializer())
                .registerTypeAdapter(Vector3f.class, new Vector3fDeserializer())
                .registerTypeAdapter(Vector4f.class, new Vector4fDeserializer())
                .create();
    }

    public static <T> Type arrayListType(Class<T> clazz) {
        return new TypeToken<ArrayList<T>>(){}.getType();
    }

    public static JsonElement getFirstValidKey(JsonObject jsonObject, String... keys) {
        for (String key : keys) {
            if (jsonObject.has(key))
                return jsonObject.get(key);
        }
        throw new IllegalArgumentException("No value found for " + Arrays.toString(keys));
    }
}

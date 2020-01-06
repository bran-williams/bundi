package com.branwilliams.bundi.engine.deserializers;

import com.branwilliams.bundi.engine.core.Keycode;
import com.branwilliams.bundi.engine.core.Keycodes;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class KeycodeDeserializer implements JsonDeserializer<Keycode> {

    private final Keycodes keycodes;

    public KeycodeDeserializer(Keycodes keycodes) {
        this.keycodes = keycodes;
    }

    @Override
    public Keycode deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String keyCode = json.getAsString().toLowerCase();
        return new Keycode(keycodes.getKeycode(keyCode));
    }
}

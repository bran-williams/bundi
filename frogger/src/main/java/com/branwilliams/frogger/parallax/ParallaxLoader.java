package com.branwilliams.frogger.parallax;

import com.branwilliams.bundi.engine.texture.TextureLoader;
import com.branwilliams.bundi.engine.util.IOUtils;
import com.branwilliams.frogger.components.ScaledTexture;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;

public class ParallaxLoader {

    public static ParallaxBackground<ScaledTexture> loadParallaxBackground(String backgroundJsonFile) {
        String fileText = IOUtils.readFile(backgroundJsonFile, null);
        Gson gson = new GsonBuilder().create();
        Type parallaxBackgroundType = new TypeToken<ParallaxBackground<ScaledTexture>>() {}.getType();
        return gson.fromJson(fileText, parallaxBackgroundType);
    }

    public static void createParallaxBackgroundTextures(TextureLoader textureLoader,
                                                  ParallaxBackground<ScaledTexture> background) throws IOException {
        for (ParallaxLayer<ScaledTexture> layer : background.getLayers()) {
            for (ParallaxObject<ScaledTexture> object : layer.getObjects()) {
                object.getObject().load(textureLoader);
            }
        }
    }
}

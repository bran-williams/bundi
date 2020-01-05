package com.branwilliams.terrain.generator;

import com.branwilliams.bundi.engine.texture.TextureData;
import com.branwilliams.bundi.engine.util.Mathf;

import java.util.function.BiFunction;

import static org.lwjgl.opengl.GL11.GL_RGBA;

/**
 * @author Brandon
 * @since October 09, 2019
 */
public class HeightmapBlendmapGenerator implements BlendmapGenerator {

    public static final int GRAYSCALE_MAX_COLOR = 0x1000000;

    @Override
    public TextureData generateBlendmap(BiFunction<Integer, Integer, Float> heightGenerator, int width, int height) {
        int channels = 4;
        TextureData textureData = new TextureData(width, height, channels, GL_RGBA);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                byte r = 0;
                byte g = 0;
                byte b = 0;
                byte a = 0;

                float pixel = heightGenerator.apply(x, y);
                if (pixel >= 0.25F && pixel < 0.5F)
                    r = (byte) 255;
                else if (pixel >= 0.5F && pixel < 0.75F)
                    g = (byte) 255;
                else if (pixel >= 0.75F && pixel < 1F)
                    b = (byte) 255;

                textureData.setPixel(x, y, r, g, b, a);
            }
        }

        return textureData;
    }

    public TextureData generateBlendmap(TextureData heightmap, int maxPixel) {
        return generateBlendmap(getHeightGenerator(heightmap, maxPixel), heightmap.getWidth(), heightmap.getHeight());
    }

    private static BiFunction<Integer, Integer, Float> getHeightGenerator(final TextureData heightmap, int maxHeight) {
        return (x, y) -> {
            x = x < 0 ? 0 : x;
            x = x >= heightmap.getWidth() ? heightmap.getWidth() - 1 : x;
            y = y < 0 ? 0 : y;
            y = y >= heightmap.getHeight() ? heightmap.getHeight() - 1 : y;

            return (float) heightmap.getPixel(x, y) / (float) maxHeight;
        };
    }

    private float getPercentage(float value, float min, float max) {
        value = Mathf.clamp(value, min, max);
        return ( value - min ) / ( max - min );
    }
}

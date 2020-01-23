package com.branwilliams.bundi.engine.texture;

import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.util.ColorUtils;
import com.branwilliams.bundi.engine.util.IOUtils;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.bundi.engine.util.noise.PerlinNoise;

import static com.branwilliams.bundi.engine.util.TextureUtils.getFormatFromChannels;
import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * TODO add premultiplication function for premultiplying images with alpha value. See http://www.realtimerendering.com/blog/gpus-prefer-premultiplication/
 * also see https://github.com/LWJGL/lwjgl3/blob/master/modules/samples/src/test/java/org/lwjgl/demo/stb/Image.java
 * Created by Brandon Williams on 10/31/2018.
 */
public class TextureLoader {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Path directory;
    
    public TextureLoader(EngineContext context) {
        this(context.getAssetDirectory());
    }

    public TextureLoader(Path directory) {
        this.directory = directory;
    }

    public static TextureData loadTexture(ImageGen imageGen, int width, int height, int channels, Function<Double, Integer> createPixel) {
        return loadTexture(imageGen, 0, 0, width, height, channels, createPixel);
    }

    public static TextureData loadTexture(ImageGen imageGen, int x, int y, int width, int height, int channels, Function<Double, Integer> createPixel) {
        ByteBuffer buffer = MemoryUtil.memAlloc(channels * width * height);

        imageGen.generate(x, y, width, height, (noiseValue) -> {
            int argb = createPixel.apply(noiseValue);
            buffer.put(ColorUtils.red(argb));
            buffer.put(ColorUtils.green(argb));
            buffer.put(ColorUtils.blue(argb));
            if (channels == 4)
                buffer.put(ColorUtils.alpha(argb));
        });
        buffer.flip();
        return new TextureData(width, height, channels, GL_RGBA, buffer);
    }

    public TextureData loadTexture(Path imageLocation) throws IOException {
        File file = directory.resolve(imageLocation).toFile();
        return loadTexture(file);
    }

    public TextureData loadTexture(String imageLocation) throws IOException {
        File file = directory.resolve(imageLocation).toFile();
        return loadTexture(file);
    }

    public TextureData loadTexture(File file) throws IOException {
        int[] width = new int[1];
        int[] height = new int[1];
        int[] channels = new int[1];

        ByteBuffer buffer = STBImage.stbi_load(file.getPath(), width, height, channels, 0);

        if (buffer == null) {
            String msg = "Failed to load texture: " + STBImage.stbi_failure_reason();
            log.error(msg);
            throw new IOException(msg);
        } else {
            log.info(file + ": width=" + width[0] + ", height=" + height[0] + ", channels=" + channels[0]);
        }
        int format = getFormatFromChannels(channels[0]);

        return new TextureData(width[0], height[0], channels[0], format, buffer);
    }

    public CubeMapTexture loadCubeMapTexture(String csvFile) throws IOException {
        String lines = IOUtils.readFile(directory, csvFile, null);

        TextureData[] textureDatas = new TextureData[6];

        int index = 0;
        int width = -1, height = -1;
        // Read the csv file and load the images specified.
        for (String line : lines.split(",")) {
            TextureData textureData = loadTexture(line);

            if (width == -1) {
                width = textureData.getWidth();
            } else if (width != textureData.getWidth()){
                throw new IOException("Images must have the same width!");
            }

            if (height == -1) {
                height = textureData.getHeight();
            } else if (height != textureData.getHeight()) {
                throw new IOException("Images must have the same height!");
            }

            textureDatas[index] = textureData;
            index++;
        }
        return new CubeMapTexture(width, height, textureDatas);
    }

    public static class PerlinNoiseImageGen implements ImageGen {

        public static final float DEFAULT_NOISE_SCALE = 0.01F;

        private final PerlinNoise perlinNoise;

        private final float[] frequencies;
        private final float[] percentages;
        private final float noiseScale;

        public PerlinNoiseImageGen(PerlinNoise perlinNoise, float[] frequencies, float[] percentages) {
            this(perlinNoise, frequencies, percentages, DEFAULT_NOISE_SCALE);
        }

        public PerlinNoiseImageGen(PerlinNoise perlinNoise, float[] frequencies, float[] percentages, float noiseScale) {
            this.perlinNoise = perlinNoise;
            this.frequencies = frequencies;
            this.percentages = percentages;
            this.noiseScale = noiseScale;
        }

        public void generate(int x, int z, int lengthX, int lengthY, Consumer<Double> pixelConsumer) {
            if (frequencies.length != percentages.length) {
                throw new IllegalArgumentException("The number of frequencies must equal the number of percentages!");
            }
            for (int i = 0; i < lengthX; i++) {
                // Add one to ensure no zero values are given to the noise function.
                float nx = ((float) (x * lengthX + i + 1)) * noiseScale;
                for (int j = 0; j < lengthY; j++) {
                    // Add one to ensure no zero values are given to the noise function.
                    float ny = ((float) (z * lengthY + j + 1)) * noiseScale;

                    float e = 0F;
                    for (int k = 0; k < frequencies.length; k++) {
                        float frequency = frequencies[k];
                        e += (float) perlinNoise.noise(frequency * nx, frequency * ny, 0F) * percentages[k];
                    }

                    pixelConsumer.accept(((Mathf.clamp(e, 1D) + 1D) * 0.5D));
                }
            }
        }


    }

    public interface ImageGen {
        void generate(int x, int z, int width, int height, Consumer<Double> pixelConsumer);
    }
}


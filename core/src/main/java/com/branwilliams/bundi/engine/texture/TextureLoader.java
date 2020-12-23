package com.branwilliams.bundi.engine.texture;

import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicVAO;
import com.branwilliams.bundi.engine.sprite.SpriteSheet;
import com.branwilliams.bundi.engine.util.*;
import static com.branwilliams.bundi.engine.util.TextureUtils.getFormatFromChannels;

import org.lwjgl.stb.STBImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;

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

    public TextureData loadTexture(Path textureLocation) throws IOException {
        File file = directory.resolve(textureLocation).toFile();
        return loadTexture(file);
    }

    public TextureData loadTexture(String textureLocation) throws IOException {
        File file = directory.resolve(textureLocation).toFile();
        return loadTexture(file);
    }

    public TextureData loadTexture(File textureLocation) throws IOException {
        int[] width = new int[1];
        int[] height = new int[1];
        int[] channels = new int[1];
        ByteBuffer buffer = STBImage.stbi_load(textureLocation.getPath(), width, height, channels, 0);

        if (buffer == null) {
            String msg = "Failed to load texture: " + STBImage.stbi_failure_reason();
            log.error(msg);
            throw new IOException(msg);
        } else {
            log.info(textureLocation + ": width=" + width[0] + ", height=" + height[0] + ", channels=" + channels[0]);
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

    public SpriteSheet loadSpriteSheet(String spriteSheetFile, int spriteWidth, int spriteHeight) throws IOException {
        TextureData spriteImage = loadTexture(spriteSheetFile);

        Texture spriteTexture = new Texture(spriteImage, false)
                .bind().nearestFilter().clampToEdges();
         Texture.unbind();

        SpriteSheet spriteSheet = new SpriteSheet(spriteTexture, new DynamicVAO(), spriteWidth, spriteHeight);
        spriteSheet.setCenteredSprite(false);

        return spriteSheet;
    }
}


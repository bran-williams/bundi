package com.branwilliams.bundi.engine.texture;

import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.util.*;
import com.branwilliams.bundi.engine.util.noise.PerlinNoise;

import static com.branwilliams.bundi.engine.util.ColorUtils.*;
import static com.branwilliams.bundi.engine.util.TextureUtils.getFormatFromChannels;
import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    private final Path screenshots;

    public TextureLoader(EngineContext context) {
        this(context.getAssetDirectory());
    }

    public TextureLoader(Path directory) {
        this.directory = directory;
        this.screenshots = directory.resolve("screenshots");
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


    /**
     * TODO move this into the Window class and make the screenshots directory be configurable.
     * */
    public boolean screenshot() {
        String filename =  new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss'.png'").format(new Date());
        return screenshot(new File(screenshots.toFile(), filename));
    }

    public static boolean screenshot(File output) {
        int[] viewport = new int[4];
        glGetIntegerv(GL_VIEWPORT, viewport);

        ByteBuffer buffer = MemoryUtil.memAlloc(3 * viewport[2] * viewport[3]);

        glPixelStorei(GL_PACK_ALIGNMENT, 1);
        glReadPixels(viewport[0], viewport[1], viewport[2], viewport[3], GL_RGB, GL_UNSIGNED_BYTE, buffer);

        STBImageWrite.stbi_flip_vertically_on_write(true);
        boolean result = STBImageWrite.stbi_write_png(output.getPath(), viewport[2], viewport[3], 3, buffer, 0);
        MemoryUtil.memFree(buffer);
        return result;
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
}


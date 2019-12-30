package com.branwilliams.imageviewer;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.core.Nameable;
import com.branwilliams.bundi.engine.texture.Texture;
import com.branwilliams.bundi.engine.texture.TextureData;
import com.branwilliams.bundi.engine.texture.TextureLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @author Brandon
 * @since December 27, 2019
 */
public class Gallery implements Nameable, Destructible {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public final File directory;

    public final File[] files;

    private Texture[] textures;

    private int selectedTextureIndex;

    public Gallery(File directory, File[] files) {
        this.directory = directory;
        this.files = files;
    }

    public void loadTextures(TextureLoader textureLoader) {
        if (!isLoaded()) {
            this.textures = new Texture[files.length];

            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.exists() && file.isFile()) {
                    try {
                        TextureData textureData = textureLoader.loadTexture(file);
                        Texture texture = new Texture(textureData, false);
                        textures[i] = texture;
                    } catch (IOException e) {
                        log.error("Unable to load as image: " + file.getAbsolutePath());
                    }
                }
            }
        }
    }

    public boolean isLoaded() {
        return textures != null;
    }

    public Texture getSelectedTexture() {
        return textures[selectedTextureIndex];
    }

    public void nextTexture() {
        selectedTextureIndex++;
        if (selectedTextureIndex >= textures.length)
            selectedTextureIndex = 0;
    }

    public void previousTexture() {
        selectedTextureIndex--;
        if (selectedTextureIndex < 0)
            selectedTextureIndex = textures.length - 1;
    }

    public File getDirectory() {
        return directory;
    }

    public File[] getFiles() {
        return files;
    }

    public Texture[] getTextures() {
        return textures;
    }

    public int getSelectedTextureIndex() {
        return selectedTextureIndex;
    }

    @Override
    public String getName() {
        return directory.getName();
    }

    @Override
    public void destroy() {
        if (textures != null) {
            for (Texture texture : textures)
                texture.destroy();
        }
    }
}

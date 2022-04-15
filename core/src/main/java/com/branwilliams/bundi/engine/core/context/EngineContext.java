package com.branwilliams.bundi.engine.core.context;

import com.branwilliams.bundi.engine.core.Scene;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Holds the asset directory and a list of scenes which found from classpath during launch.
 *
 * */
public class EngineContext {

    private final List<Class<? extends Scene>> scenes;

    private final Path assetDirectory;

    private final Path tempDirectory;

    private final Path screenshotDirectory;

    public EngineContext(Path assetDirectory, Path tempDirectory, Path screenshotDirectory,
                         List<Class<? extends Scene>> scenes) {
        this.assetDirectory = toRealPath(assetDirectory);
        this.tempDirectory = toRealPath(tempDirectory);
        this.screenshotDirectory = toRealPath(screenshotDirectory);
        this.scenes = scenes;
    }

    private Path toRealPath(Path path) {
        try {
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
            return path.toRealPath();
        } catch (IOException e) {
            return null;
        }
    }

    public List<Class<? extends Scene>> getScenes() {
        return scenes;
    }

    public Path getAssetDirectory() {
        return assetDirectory;
    }

    public Path getTempDirectory() {
        return tempDirectory;
    }

    public Path getScreenshotDirectory() {
        return screenshotDirectory;
    }
}

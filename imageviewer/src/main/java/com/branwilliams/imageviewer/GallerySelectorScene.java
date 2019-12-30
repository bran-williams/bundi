package com.branwilliams.imageviewer;

import com.branwilliams.bundi.engine.core.*;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.shader.Projection;
import com.branwilliams.imageviewer.pipeline.GallerySelectorRenderPass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Brandon
 * @since December 27, 2019
 */
public class GallerySelectorScene extends AbstractScene implements Window.KeyListener {

    private static final String GALLERY_DIR = "temp";

    private final Logger log = LoggerFactory.getLogger(getClass());

    private ImageViewerScene viewerScene;

    private Gallery[] galleries;

    private int selectedGallery;

    private boolean hasSelectedGallery;

    public GallerySelectorScene() {
        super("galleryselector_scene");
        this.addKeyListener(this);
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        viewerScene = new ImageViewerScene(this);

        RenderContext renderContext = new RenderContext(new Projection(window));
        RenderPipeline<RenderContext> renderPipeline = new RenderPipeline<>(renderContext);
        renderPipeline.addLast(new GallerySelectorRenderPass(this));

        ImageViewerRenderer renderer = new ImageViewerRenderer(this, renderPipeline);
        setRenderer(renderer);
    }

    @Override
    public void play(Engine engine) {
        galleries = readGalleries(GALLERY_DIR);
//        selectedGallery = 0;
        hasSelectedGallery = false;
    }

    @Override
    public void pause(Engine engine) {

    }

    @Override
    public void update(Engine engine, double deltaTime) {
        super.update(engine, deltaTime);

        if (hasSelectedGallery)
            engine.setScene(viewerScene);
    }

    private Gallery[] readGalleries(String path) {
        List<Gallery> galleries = new ArrayList<>();

        File directory = new File(path);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files == null) {
                log.error("No galleries found within directory: " + directory.getAbsolutePath());
                return null;
            }

            // load galleries
            for (File file : files) {
                if (file.isDirectory()) {
                    Gallery gallery = readGallery(file);
                    if (gallery != null)
                        galleries.add(gallery);
                }
            }
        }
        return galleries.toArray(new Gallery[0]);
    }

    private Gallery readGallery(File directory) {
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files == null) {
                log.error("No files in directory: " + directory.getAbsolutePath());
                return null;
            }
            return new Gallery(directory, files);
        }
        return null;
    }

    private void setGallery(Gallery gallery) {
        viewerScene.setGallery(gallery);
        hasSelectedGallery = true;
    }

    public Gallery[] getGalleries() {
        return galleries;
    }
    @Override
    public void keyPress(Window window, int key, int scancode, int mods) {

    }

    @Override
    public void keyRelease(Window window, int key, int scancode, int mods) {
        switch (key) {
            case GLFW_KEY_UP:
                selectedGallery--;
                if (selectedGallery < 0)
                    selectedGallery = galleries.length - 1;
                break;
            case GLFW_KEY_DOWN:
                selectedGallery++;
                if (selectedGallery >= galleries.length)
                    selectedGallery = 0;
                break;
            case GLFW_KEY_ENTER:
                setGallery(galleries[selectedGallery]);
                break;

        }
    }

    public int getSelectedGallery() {
        return selectedGallery;
    }
}

package com.branwilliams.imageviewer;

import com.branwilliams.bundi.engine.core.*;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.core.window.KeyListener;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.shader.Projection;
import com.branwilliams.bundi.gui.api.ContainerManager;
import com.branwilliams.bundi.gui.api.components.Button;
import com.branwilliams.bundi.gui.pipeline.GuiRenderPass;
import com.branwilliams.bundi.gui.screen.GuiScreenManager;
import com.branwilliams.imageviewer.pipeline.GallerySelectorRenderPass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Brandon
 * @since December 27, 2019
 */
public class GallerySelectorScene extends AbstractScene {

    private static final String GALLERY_DIR = "temp";

    private final Logger log = LoggerFactory.getLogger(getClass());

    private ImageViewerScene viewerScene;

    private Gallery[] galleries;

    private int selectedGallery;

    private boolean hasSelectedGallery;

    private GuiScreenManager guiScreenManager;

    private static final String UI_GALLERY_LIST = "ui/imageviewer-galleries.xml";

    private static final Map<String, Object> UI_INGAME_HUD_ENVIRONMENT = createUIEnvironment();

    private static Map<String, Object> createUIEnvironment() {
        Map<String, Object> env = new HashMap<>();
        env.put("move_sun_controls", "Ctrl + Left click");
        return env;
    }

    public GallerySelectorScene() {
        super("galleryselector_scene");
        this.guiScreenManager = new GuiScreenManager(this);

        this.addKeyListener(this);
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        this.guiScreenManager.init(engine, window);

        viewerScene = new ImageViewerScene(this);

        RenderContext renderContext = new RenderContext(new Projection(window));
        RenderPipeline<RenderContext> renderPipeline = new RenderPipeline<>(renderContext);
        renderPipeline.addLast(new GallerySelectorRenderPass(this));
        renderPipeline.addLast(new GuiRenderPass<>(this, this::getGuiScreenManager));

        ImageViewerRenderer renderer = new ImageViewerRenderer(this, renderPipeline);
        setRenderer(renderer);
    }

    @Override
    public void play(Engine engine) {
        galleries = readGalleries(GALLERY_DIR);
//        selectedGallery = 0;
        hasSelectedGallery = false;

        Map<String, Object> env = new HashMap<>();

        List<String> galleriesEnv = new ArrayList<>();
        for (int i = 0; i < galleries.length; i++) {
            Gallery gallery = galleries[i];
            galleriesEnv.add(gallery.getName() + " (" + gallery.files.length + ")");
        }
        env.put("galleries", galleriesEnv);

        ContainerManager containerManager = this.guiScreenManager.loadAsGuiScreen(UI_GALLERY_LIST, env);

        for (int i = 0; i < galleries.length; i++) {
            Gallery gallery = galleries[i];
            Button button = containerManager.getByTag("gallery" + i);
            if (button != null) {
                button.onPressed((b, a) -> {
                    if (a.buttonId == GLFW_MOUSE_BUTTON_1) {
                        setGallery(gallery);
                        return true;
                    }
                    return false;
                });
            } else {
                System.out.println("button null. i=" + i);
            }
        }
    }

    @Override
    public void pause(Engine engine) {

    }

    @Override
    public void update(Engine engine, double deltaTime) {
        super.update(engine, deltaTime);
        guiScreenManager.update();

        if (hasSelectedGallery)
            engine.setScene(viewerScene);
    }

    @Override
    public void keyRelease(Window window, int key, int scancode, int mods) {
        super.keyRelease(window, key, scancode, mods);
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

    public int getSelectedGallery() {
        return selectedGallery;
    }

    public GuiScreenManager getGuiScreenManager() {
        return guiScreenManager;
    }
}

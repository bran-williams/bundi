package com.branwilliams.imageviewer;

import com.branwilliams.bundi.engine.core.*;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.window.WindowListener;
import com.branwilliams.bundi.engine.shader.Projection;
import com.branwilliams.bundi.engine.shader.Transformation;
import com.branwilliams.bundi.engine.texture.Texture;
import com.branwilliams.bundi.engine.texture.TextureLoader;
import com.branwilliams.imageviewer.components.ImageViewParameters;
import com.branwilliams.imageviewer.pipeline.ImageViewGuiRenderPass;
import com.branwilliams.imageviewer.pipeline.ImageViewRenderPass;
import com.branwilliams.imageviewer.system.ImageViewInputSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Brandon
 * @since December 27, 2019
 */
public class ImageViewerScene extends AbstractScene implements WindowListener {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final GallerySelectorScene parent;

    private TextureLoader textureLoader;

    private ImageViewParameters imageViewParameters;

    private Gallery gallery;

    private Lockable pauseLock;

    private int windowWidth;

    private int windowHeight;

    private boolean exitGallery;

    public ImageViewerScene(GallerySelectorScene parent) {
        super("imageviewer_scene");
        this.parent = parent;
        this.addWindowListener(this);
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        pauseLock = new Lock();

        es.addSystem(new ImageViewInputSystem(this, pauseLock));
        es.initSystems(engine, window);

        RenderContext renderContext = new RenderContext(new Projection(window));
        RenderPipeline<RenderContext> renderPipeline = new RenderPipeline<>(renderContext);
        renderPipeline.addLast(new ImageViewRenderPass(this));
        renderPipeline.addLast(new ImageViewGuiRenderPass(this));

        ImageViewerRenderer renderer = new ImageViewerRenderer(this, renderPipeline);
        setRenderer(renderer);

        textureLoader = new TextureLoader(engine.getContext());
        windowWidth = window.getWidth();
        windowHeight = window.getHeight();
    }

    @Override
    public void play(Engine engine) {
        exitGallery = false;

        imageViewParameters = new ImageViewParameters(new Transformation());
        if (hasGallery()) {
            gallery.loadTextures(textureLoader);
            onTextureChange(gallery.getSelectedTexture());
        }
    }

    @Override
    public void pause(Engine engine) {
        if (this.gallery != null && this.gallery.isLoaded())
            this.gallery.destroy();
    }

    @Override
    public void update(Engine engine, double deltaTime) {
        super.update(engine, deltaTime);
        if (exitGallery) {
            engine.setScene(parent);
        }
    }

    public void onTextureChange(Texture texture) {
        imageViewParameters.getTransform().position(windowWidth * 0.5F, windowHeight * 0.5F, 0F);
        imageViewParameters.getTransform().setScale(1F);
    }

    public ImageViewParameters getImageViewParameters() {
        return imageViewParameters;
    }

    @Override
    public void resize(Window window, int width, int height) {
        this.windowWidth = width;
        this.windowHeight = height;
    }

    public void exitGallery() {
        this.exitGallery = true;
    }

    public void setGallery(Gallery gallery) {
        this.gallery = gallery;
    }

    public Gallery getGallery() {
        return gallery;
    }

    public boolean hasGallery() {
        return gallery != null;
    }

}

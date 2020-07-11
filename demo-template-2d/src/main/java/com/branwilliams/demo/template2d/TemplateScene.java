package com.branwilliams.demo.template2d;

import com.branwilliams.bundi.engine.core.AbstractScene;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.core.pipeline.passes.DisableWireframeRenderPass;
import com.branwilliams.bundi.engine.core.pipeline.passes.EnableWireframeRenderPass;
import com.branwilliams.bundi.engine.shader.Camera;
import com.branwilliams.bundi.engine.shader.Projection;
import com.branwilliams.bundi.engine.shader.Transformation;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicVAO;
import com.branwilliams.bundi.engine.sprite.AnimatedSprite;
import com.branwilliams.bundi.engine.sprite.SpriteSheet;
import com.branwilliams.bundi.engine.texture.Texture;
import com.branwilliams.bundi.engine.texture.TextureData;
import com.branwilliams.bundi.engine.texture.TextureLoader;
import com.branwilliams.bundi.engine.util.RateLimiter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Brandon
 * @since November 30, 2019
 */
public class TemplateScene extends AbstractScene {

    private static final int[] FIREBALL_FRAMES = {  0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };

    private TextureLoader textureLoader;

    private float scale = 4F;

    private SpriteSheet spriteSheet;

    private Camera camera;

    private boolean wireframe;

    public TemplateScene() {
        super("template-2d");
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        textureLoader = new TextureLoader(engine.getContext());

//        es.addSystem(new DebugCameraMoveSystem(this, this::getCamera, 0.16F, 16F));
        es.initSystems(engine, window);

        Projection worldProjection = new Projection(window);
        RenderContext renderContext = new RenderContext(worldProjection);

        RenderPipeline<RenderContext> renderPipeline = new RenderPipeline<>(renderContext);
        renderPipeline.addLast(new EnableWireframeRenderPass(this::isWireframe));
        renderPipeline.addLast(new SpriteRenderPass(this));
        // Add render passes here.
        renderPipeline.addLast(new DisableWireframeRenderPass(this::isWireframe));
        TemplateRenderer<RenderContext> renderer = new TemplateRenderer<>(this, renderPipeline);
        setRenderer(renderer);
    }

    @Override
    public void play(Engine engine) {
//        camera = new Camera();
//        camera.setPosition(cameraStartingPosition);
//        camera.lookAt(cameraLookAt);

        SpriteSheet fireball;
        SpriteSheet adventurer;
        try {
            adventurer = loadSpriteSheet("textures/colored_tilemap_packed.png", 8, 8);
            fireball = loadSpriteSheet("textures/Fireball_68x9.png", 68, 9);

            es.entity("fireball").component(
                    new Transformation().position(50, 100, 0),
                    new AnimatedSprite(fireball, FIREBALL_FRAMES,
                            new RateLimiter(TimeUnit.MILLISECONDS, 100L), scale)
            ).build();

            es.entity("animated_sprite").component(
                    new Transformation().position(50, 50, 0),
                    new AnimatedSprite(adventurer, new int[] {
                            4, 5, 6, 7, 8, 9, 10, 11, 12
                    }, new RateLimiter(TimeUnit.MILLISECONDS, 100L), scale)
            ).build();

            es.entity("sprite2").component(
                    new Transformation().position(10, 10, 0),
                    adventurer.getSprite(4, scale)
            ).build();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void pause(Engine engine) {

    }

    @Override
    public void update(Engine engine, double deltaTime) {
        super.update(engine, deltaTime);
    }

    @Override
    public void fixedUpdate(Engine engine, double deltaTime) {
        super.fixedUpdate(engine, deltaTime);
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    private SpriteSheet loadSpriteSheet(String spriteSheetFile, int spriteWidth, int spriteHeight) throws IOException {
        TextureData spriteImage = textureLoader.loadTexture(spriteSheetFile);

        Texture spriteTexture = new Texture(spriteImage, false)
                .bind().nearestFilter().clampToEdges();
        // Texture.unbind();

        SpriteSheet spriteSheet = new SpriteSheet(spriteTexture, new DynamicVAO(), spriteWidth, spriteHeight);
        spriteSheet.setCenteredSprite(false);

        return spriteSheet;
    }

    public Camera getCamera() {
        return camera;
    }

    public boolean isWireframe() {
        return wireframe;
    }
}

package com.branwilliams.demo.template2d;

import com.branwilliams.bundi.engine.core.AbstractScene;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.core.pipeline.passes.DisableWireframeRenderPass;
import com.branwilliams.bundi.engine.core.pipeline.passes.EnableWireframeRenderPass;
import com.branwilliams.bundi.engine.shader.Projection;
import com.branwilliams.bundi.engine.shader.Transformation;
import com.branwilliams.bundi.engine.sprite.AnimatedSprite;
import com.branwilliams.bundi.engine.sprite.SpriteSheet;
import com.branwilliams.bundi.engine.systems.DebugCameraMoveSystem;
import com.branwilliams.bundi.engine.texture.TextureLoader;
import com.branwilliams.bundi.engine.util.RateLimiter;
import com.branwilliams.demo.template2d.pipeline.pass.SpriteRenderPass;

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

        SpriteSheet fireball;
        SpriteSheet adventurer;
        try {
            adventurer = textureLoader.loadSpriteSheet("textures/colored_tilemap_packed.png",
                    8, 8);
            fireball = textureLoader.loadSpriteSheet("textures/Fireball_68x9.png",
                    68, 9);

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

    public boolean isWireframe() {
        return wireframe;
    }
}

package com.branwilliams.demo.template2d;

import com.branwilliams.bundi.engine.core.AbstractScene;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.shader.Projection;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.engine.shader.Transformation;
import com.branwilliams.bundi.engine.sprite.AnimatedSprite;
import com.branwilliams.bundi.engine.sprite.Sprite;
import com.branwilliams.bundi.engine.sprite.SpriteSheet;
import com.branwilliams.bundi.engine.texture.Texture;
import com.branwilliams.bundi.engine.texture.TextureData;
import com.branwilliams.bundi.engine.texture.TextureLoader;
import com.branwilliams.bundi.engine.util.RateLimiter;
import com.branwilliams.demo.template2d.parallax.*;
import com.branwilliams.demo.template2d.pipeline.pass.ParallaxBackgroundRenderPass;
import com.branwilliams.demo.template2d.pipeline.pass.SpriteRenderPass;
import com.branwilliams.demo.template2d.system.ParallaxSystem;
import org.joml.Vector2f;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Brandon
 * @since November 30, 2019
 */
public class ParallaxScene extends AbstractScene {

    private static final int[] FIREBALL_FRAMES = {
             0,  1,  2,  3,  4,  5,  6,  7,  8,  9,
            10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
            20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
            30, 31, 32, 33, 34, 35, 36, 37, 38, 39,
            40, 41, 42, 43, 44, 45, 46, 47, 48, 49,
            50, 51, 52, 53, 54, 55, 56, 57, 58, 59
    };

    private ParallaxBackground parallaxBackground = new ParallaxBackground();

    private Vector2f focalPoint = new Vector2f();

    private TextureLoader textureLoader;

    private boolean wireframe;

    private Sprite fireballSprite;

    public ParallaxScene() {
        super("template-2d");
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        textureLoader = new TextureLoader(engine.getContext());

//        es.addSystem(new DebugCameraMoveSystem(this, this::getCamera, 0.16F, 16F));
        es.addSystem(new ParallaxSystem(this::getFocalPoint, this::getParallaxBackground));
        es.initSystems(engine, window);

        Projection worldProjection = new Projection(window);
        RenderContext renderContext = new RenderContext(worldProjection);

        RenderPipeline<RenderContext> renderPipeline = new RenderPipeline<>(renderContext);
//        renderPipeline.addLast(new EnableWireframeRenderPass(this::isWireframe));
        renderPipeline.addFirst(new ParallaxBackgroundRenderPass<>(this::getParallaxBackground));
        renderPipeline.addLast(new SpriteRenderPass(this));
        // Add render passes here.
//        renderPipeline.addLast(new DisableWireframeRenderPass(this::isWireframe));
        TemplateRenderer<RenderContext> renderer = new TemplateRenderer<>(this, renderPipeline);
        setRenderer(renderer);

    }

    @Override
    public void play(Engine engine) {

        float scale = 4F;
        float spriteScale = 8F;

        try {
            ParallaxLayer layer0 = new ParallaxLayer(new ParallaxProperties(0, new Vector2f(0.75F, 0)));
            parallaxBackground.addLayer(layer0);

            ParallaxLayer layer1 = new ParallaxLayer(new ParallaxProperties(1, new Vector2f(1.25F, 0)));
            parallaxBackground.addLayer(layer1);

            ParallaxLayer layer2 = new ParallaxLayer(new ParallaxProperties(2, new Vector2f(3F, 0)));
            parallaxBackground.addLayer(layer2);

//            ParallaxLayer layer3 = new ParallaxLayer(new ParallaxProperties(3, new Vector2f(4, 4)));
//            parallaxBackground.addLayer(layer3);

            layer0.addSprite(loadBackgroundTexture("textures/cyberpunk/far-buildings.png"),
                    ParallaxMovementType.STATIC, ParallaxDrawType.REPEAT, ParallaxSizeType.SCREEN_SIZE, scale);

            layer1.addSprite(loadBackgroundTexture("textures/cyberpunk/back-buildings.png"),
                    ParallaxMovementType.MOVING, ParallaxDrawType.REPEAT, ParallaxSizeType.SCREEN_SIZE, scale);

            layer2.addSprite(loadBackgroundTexture("textures/cyberpunk/foreground.png"),
                    ParallaxMovementType.MOVING, ParallaxDrawType.REPEAT, ParallaxSizeType.SCREEN_SIZE, scale);

            Texture.unbind();

            SpriteSheet fireball;
            SpriteSheet adventurer;

            adventurer = textureLoader.loadSpriteSheet("textures/colored_tilemap_packed.png",
                    8, 8);
            fireball = textureLoader.loadSpriteSheet("textures/Fireball_68x9.png",
                    68, 9);

            fireballSprite = new AnimatedSprite(fireball, FIREBALL_FRAMES,
                    new RateLimiter(TimeUnit.MILLISECONDS, 80L), scale);

            es.entity("fireball").component(
                    new Transformation().position(50, 100, 0),
                    fireballSprite
            ).build();

            es.entity("animated_sprite").component(
                    new Transformation().position(50, 50, 0),
                    new AnimatedSprite(adventurer, new int[] {
                            4, 5, 6, 7, 8, 9, 10, 11, 12
                    }, new RateLimiter(TimeUnit.MILLISECONDS, 100L), spriteScale)
            ).build();

            es.entity("sprite2").component(
                    new Transformation().position(10, 10, 0),
                    adventurer.getSprite(4, spriteScale)
            ).build();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Texture loadBackgroundTexture(String file) throws IOException {
        TextureData layerData = textureLoader.loadTexture(file);
        return new Texture(layerData, false).bind().nearestFilter().repeatEdges();
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
        focalPoint.x -= deltaTime * 100F;
        super.fixedUpdate(engine, deltaTime);
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void move(Window window, float newMouseX, float newMouseY, float oldMouseX, float oldMouseY) {
        super.move(window, newMouseX, newMouseY, oldMouseX, oldMouseY);
        if (window.isMouseInside()) {
            es.getEntity("fireball").getComponent(Transformable.class).position(newMouseX, newMouseY, 0F);
        }
    }

    public boolean isWireframe() {
        return wireframe;
    }

    public ParallaxBackground getParallaxBackground() {
        return parallaxBackground;
    }

    public Vector2f getFocalPoint() {
        return focalPoint;
    }
}

package com.branwilliams.frogger;

import com.branwilliams.bundi.engine.core.AbstractScene;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.shader.Projection;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.engine.shader.Transformation;
import com.branwilliams.bundi.engine.shape.*;
import com.branwilliams.bundi.engine.sprite.AnimatedSprite;
import com.branwilliams.bundi.engine.sprite.Sprite;
import com.branwilliams.bundi.engine.sprite.SpriteSheet;
import com.branwilliams.bundi.engine.texture.TextureLoader;
import com.branwilliams.bundi.engine.util.RateLimiter;
import com.branwilliams.frogger.components.ScaledTexture;
import com.branwilliams.frogger.parallax.ParallaxBackground;
import com.branwilliams.frogger.parallax.ParallaxLoader;
import com.branwilliams.frogger.pipeline.pass.ParallaxBackgroundRenderPass;
import com.branwilliams.frogger.pipeline.pass.HelpUIRenderPass;
import com.branwilliams.frogger.pipeline.pass.SpriteAABBRenderPass;
import com.branwilliams.frogger.pipeline.pass.SpriteRenderPass;
import com.branwilliams.frogger.system.FocalPointFollowSystem;
import com.branwilliams.frogger.system.ParallaxBackgroundMovementSystem;
import org.joml.Vector2f;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Brandon
 * @since November 30, 2019
 */
public class SATScene extends AbstractScene {

    private Camera2D camera;

    private Transformable frogmanTransform = new Transformation();

    private Transformable fireballTransform = new Transformation();

    private TextureLoader textureLoader;

    private ParallaxBackground<ScaledTexture> parallaxBackground;

    private Sprite fireballSprite;

    private Sprite frogmanTheSprite;

    private float spriteScale = 4F;

    private IEntity fireball;

    private IEntity frogman;

    public SATScene() {
        super("sat-test");
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        textureLoader = new TextureLoader(engine.getContext());
        camera = new Camera2D(window.getWidth(), window.getHeight());

        parallaxBackground = ParallaxLoader.loadParallaxBackground(FroggerConstants.PARALLAX_BACKGROUND_FILE);
        ParallaxLoader.createParallaxBackgroundTextures(textureLoader, parallaxBackground);

        es.addSystem(new FocalPointFollowSystem(camera::getFocalPoint, camera::setFocalPoint, camera::getTargetFocalPoint,
                FroggerConstants.CAMERA_MOVE_SPEED));
        es.addSystem(new ParallaxBackgroundMovementSystem(camera::getFocalPoint, this::getParallaxBackground));
        es.initSystems(engine, window);

        Projection worldProjection = new Projection(window);
        RenderContext renderContext = new RenderContext(worldProjection);

        RenderPipeline<RenderContext> renderPipeline = new RenderPipeline<>(renderContext);
        renderPipeline.addFirst(new ParallaxBackgroundRenderPass<>(this::getParallaxBackground));
//        renderPipeline.addLast(new SpriteAABBRenderPass(this));
        renderPipeline.addLast(new SpriteRenderPass(this));
        renderPipeline.addLast(new HelpUIRenderPass(new String[] {
                "Use the arrow keys to move frogman",
                "Left Shift + Left Click moves the background"
        }));
        // Add render passes here.
        FroggerRenderer<RenderContext> renderer = new FroggerRenderer<>(this, renderPipeline);
        setRenderer(renderer);
    }

    @Override
    public void play(Engine engine) {
        float backgroundScale = 4F;

        try {
            SpriteSheet fireballSpriteSheet = textureLoader.loadSpriteSheet("textures/Fireball_68x9.png",
                    68, 9);
            fireballSprite = new AnimatedSprite(fireballSpriteSheet, FroggerConstants.FIREBALL_FRAMES,
                    new RateLimiter(TimeUnit.MILLISECONDS, 80L), spriteScale);

            SpriteSheet froggerSpriteSheet = textureLoader.loadSpriteSheet("textures/frogger.png",
                    16, 16);
            frogmanTheSprite = new AnimatedSprite(froggerSpriteSheet, FroggerConstants.FROGMAN_FRAMES,
                    new RateLimiter(TimeUnit.MILLISECONDS, 180L), spriteScale);

            AABB2f frogmanAABB = (AABB2f) frogmanTheSprite.getAABB().copy();
            frogman = es.entity("frogman").component(
                    frogmanTransform,
                    frogmanTheSprite,
                    frogmanAABB
            ).build();

            AABB2f fireballAABB = (AABB2f) fireballSprite.getAABB().copy();
            fireball = es.entity("fireball").component(
                    fireballTransform,
                    fireballSprite,
                    fireballAABB
            ).build();

            fireballTransform.setPosition(280, 280, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pause(Engine engine) {

    }

    @Override
    public void resize(Window window, int width, int height) {
        super.resize(window, width, height);
    }

    @Override
    public void update(Engine engine, double deltaTime) {
        camera.updateScreen(engine.getWindow().getWidth(), engine.getWindow().getHeight());

        AABB2f fireballAABB = fireball.getComponent(AABB2f.class);
        fireballAABB.center(fireballTransform.x(), fireballTransform.y());

//        AABB2f frogmanAABB = frogman.getComponent(AABB2f.class);
//        frogmanTransform.setPosition(engine.getWindow().getMouseX(), engine.getWindow().getMouseY(), 0F);
//        frogmanAABB.center(frogmanTransform.x(), frogmanTransform.y());

        super.update(engine, deltaTime);
    }

    @Override
    public void fixedUpdate(Engine engine, double deltaTime) {
        super.fixedUpdate(engine, deltaTime);

        float keyMoveSpeed = 200F;

        if (engine.getWindow().isKeyPressed(GLFW_KEY_RIGHT)) {
            frogmanTransform.move(keyMoveSpeed * (float) deltaTime, 0, 0);
        }

        if (engine.getWindow().isKeyPressed(GLFW_KEY_LEFT)) {
            frogmanTransform.move(-keyMoveSpeed * (float) deltaTime, 0, 0);
        }

        boolean doGravity = true;
        if (engine.getWindow().isKeyPressed(GLFW_KEY_UP)) {
            frogmanTransform.move(0, -keyMoveSpeed * (float) deltaTime, 0);
            doGravity = false;
        }

        if (engine.getWindow().isKeyPressed(GLFW_KEY_DOWN)) {
            frogmanTransform.move(0, keyMoveSpeed * (float) deltaTime, 0);
            doGravity = false;
        }

        if (doGravity) {
            frogmanTransform.move(0, 60 * (float) deltaTime, 0);
        }

        AABB2f fireballAABB = fireball.getComponent(AABB2f.class);
        AABB2f frogmanAABB = frogman.getComponent(AABB2f.class);

        frogmanAABB.center(frogmanTransform.x(), frogmanTransform.y());
        SeparatingAxis.collide(fireballAABB, frogmanAABB, (push) -> frogmanTransform.move(push.x, push.y, 0));
        frogmanAABB.center(frogmanTransform.x(), frogmanTransform.y());
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void wheel(Window window, double xoffset, double yoffset) {
        super.wheel(window, xoffset, yoffset);
    }

    @Override
    public void press(Window window, float mouseX, float mouseY, int buttonId) {
        super.press(window, mouseX, mouseY, buttonId);

        if (window.isMouseInside() && window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            switch (buttonId) {
                case GLFW_MOUSE_BUTTON_1:
                    float movementX = mouseX - (window.getWidth() * 0.5F);
                    float movementY = mouseY - (window.getHeight() * 0.5F);
                    camera.getTargetFocalPoint().set(camera.getFocalPoint().x - movementX, camera.getTargetFocalPoint().y - movementY);
                    break;
            }
        }
    }

    @Override
    public void keyPress(Window window, int key, int scancode, int mods) {
        super.keyPress(window, key, scancode, mods);
        switch (key) {
            case GLFW_KEY_T:
                break;
        }
    }

    public ParallaxBackground getParallaxBackground() {
        return parallaxBackground;
    }

}

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
import com.branwilliams.bundi.engine.shape.AABB2f;
import com.branwilliams.bundi.engine.shape.ListSpatial2f;
import com.branwilliams.bundi.engine.shape.Shape2f;
import com.branwilliams.bundi.engine.shape.Spatial2f;
import com.branwilliams.bundi.engine.sprite.AnimatedSprite;
import com.branwilliams.bundi.engine.sprite.Sprite;
import com.branwilliams.bundi.engine.sprite.SpriteSheet;
import com.branwilliams.bundi.engine.texture.TextureLoader;
import com.branwilliams.bundi.engine.util.IOUtils;
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
import com.branwilliams.frogger.tilemap.SpriteAtlas;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Brandon
 * @since November 30, 2019
 */
public class SpatialScene extends AbstractScene {

    private Camera2D camera;

    private Transformable frogmanTransform = new Transformation();

    private Transformable fireballTransform = new Transformation();

    private TextureLoader textureLoader;

    private ParallaxBackground<ScaledTexture> parallaxBackground;

    private Sprite fireballSprite;

    private Sprite frogmanTheSprite;

    private float spriteScale = 4F;

    private Spatial2f<Shape2f, IEntity> spatial;

    private IEntity fireball;

    private IEntity frogman;

    public SpatialScene() {
        super("sat-test");
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        textureLoader = new TextureLoader(engine.getContext());
        camera = new Camera2D(window.getWidth(), window.getHeight());
        spatial = new ListSpatial2f<>();

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
        renderPipeline.addLast(new SpriteAABBRenderPass(this));
        renderPipeline.addLast(new SpriteRenderPass(this));
        renderPipeline.addLast(new HelpUIRenderPass(new String[] {
                "Use WASD to move the background",
                "Left Shift + Left Click moves the background",
                "Press T to test the collision"
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
            spatial.add(frogmanAABB, frogman);

            AABB2f fireballAABB = (AABB2f) fireballSprite.getAABB().copy();
            fireball = es.entity("fireball").component(
                    fireballTransform,
                    fireballSprite,
                    fireballAABB
            ).build();
            spatial.add(fireballAABB, fireball);

            fireballTransform.setPosition(80, 80, 0);
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
        frogmanTransform.setPosition(engine.getWindow().getMouseX(), engine.getWindow().getMouseY(), 0F);
        frogman.getComponent(AABB2f.class).center(frogmanTransform.x(), frogmanTransform.y());
        fireball.getComponent(AABB2f.class).center(fireballTransform.x(), fireballTransform.y());
//        frogman.getComponent(AABB2f.class).center(frogmanTransform.x() - focalPoint.x,
//                frogmanTransform.y() - focalPoint.y);
//        fireball.getComponent(AABB2f.class).center(fireballTransform.x() - focalPoint.x,
//                fireballTransform.y() - focalPoint.y);
        super.update(engine, deltaTime);
    }

    @Override
    public void fixedUpdate(Engine engine, double deltaTime) {
        super.fixedUpdate(engine, deltaTime);

        float keyMoveSpeed = 600F;

        if (engine.getWindow().isKeyPressed(GLFW_KEY_RIGHT)) {
            camera.getTargetFocalPoint().x -= keyMoveSpeed * deltaTime;
        }

        if (engine.getWindow().isKeyPressed(GLFW_KEY_LEFT)) {
            camera.getTargetFocalPoint().x += keyMoveSpeed * deltaTime;
        }

        if (engine.getWindow().isKeyPressed(GLFW_KEY_UP)) {
            camera.getTargetFocalPoint().y += keyMoveSpeed * deltaTime;
        }

        if (engine.getWindow().isKeyPressed(GLFW_KEY_DOWN)) {
            camera.getTargetFocalPoint().y -= keyMoveSpeed * deltaTime;
        }

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
                System.out.println(spatial.query(frogman.getComponent(AABB2f.class)));
                break;
        }
    }

    private SpriteAtlas loadSpriteAtlas(String fileLocation) throws IOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        String fileContents = IOUtils.readFile(fileLocation, null);
        if (fileContents != null) {
            return gson.fromJson(fileContents, SpriteAtlas.class);
        }
        return null;
    }

    public ParallaxBackground getParallaxBackground() {
        return parallaxBackground;
    }

}

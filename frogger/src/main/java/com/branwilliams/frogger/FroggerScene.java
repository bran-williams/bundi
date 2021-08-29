package com.branwilliams.frogger;

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
import com.branwilliams.bundi.engine.texture.TextureLoader;
import com.branwilliams.bundi.engine.util.IOUtils;
import com.branwilliams.bundi.engine.util.RateLimiter;
import com.branwilliams.frogger.components.ScaledTexture;
import com.branwilliams.frogger.parallax.ParallaxBackground;
import com.branwilliams.frogger.parallax.ParallaxLoader;
import com.branwilliams.frogger.pipeline.pass.ParallaxBackgroundRenderPass;
import com.branwilliams.frogger.pipeline.pass.HelpUIRenderPass;
import com.branwilliams.frogger.pipeline.pass.SpriteRenderPass;
import com.branwilliams.frogger.system.FocalPointFollowSystem;
import com.branwilliams.frogger.system.ParallaxBackgroundMovementSystem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joml.Vector2f;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Brandon
 * @since November 30, 2019
 */
public class FroggerScene extends AbstractScene {

    private Camera2D camera;

    private Transformable mouseTransform = new Transformation();

    private TextureLoader textureLoader;

    private ParallaxBackground<ScaledTexture> parallaxBackground;

    private Sprite fireballSprite;

    private int currentTileId = 0;

    private float spriteScale = 4F;

    public FroggerScene() {
        super("frogger");
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
        renderPipeline.addLast(new SpriteRenderPass(this));
        renderPipeline.addLast(new HelpUIRenderPass(new String[] {
                "Use the arrow keys to move the background",
                "Left Shift + Left click to move the background"
        }));
        // Add render passes here.
        FroggerRenderer<RenderContext> renderer = new FroggerRenderer<>(this, renderPipeline);
        setRenderer(renderer);
    }

    @Override
    public void play(Engine engine) {
        float backgroundScale = 4F;
        float fireballScale = 4F;

        try {
            SpriteSheet fireball;
            fireball = textureLoader.loadSpriteSheet("textures/Fireball_68x9.png",
                    68, 9);
            fireballSprite = new AnimatedSprite(fireball, FroggerConstants.FIREBALL_FRAMES,
                    new RateLimiter(TimeUnit.MILLISECONDS, 80L), fireballScale);

            SpriteSheet frogger = textureLoader.loadSpriteSheet("textures/frogger.png",
                    16, 16);

            Sprite frogmanTheSprite = new AnimatedSprite(frogger, FroggerConstants.FROGMAN_FRAMES,
                    new RateLimiter(TimeUnit.MILLISECONDS, 80L), fireballScale);

            es.entity("frogman").component(
                    mouseTransform,
                    frogmanTheSprite
            ).build();

            es.entity("fireball").component(
                    new Transformation().position(0, 40, 0),
                    fireballSprite
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
        camera.updateScreen(engine.getWindow().getWidth(), engine.getWindow().getHeight());
        mouseTransform.setPosition(engine.getWindow().getMouseX(), engine.getWindow().getMouseY(), 0F);
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

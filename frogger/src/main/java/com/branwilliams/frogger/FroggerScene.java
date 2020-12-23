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
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.bundi.engine.util.RateLimiter;
import com.branwilliams.frogger.components.ScaledTexture;
import com.branwilliams.frogger.gson.TilemapDeserializer;
import com.branwilliams.frogger.gson.TilemapSerializer;
import com.branwilliams.frogger.parallax.ParallaxBackground;
import com.branwilliams.frogger.parallax.ParallaxLayer;
import com.branwilliams.frogger.parallax.ParallaxObject;
import com.branwilliams.frogger.pipeline.pass.ParallaxBackgroundRenderPass;
import com.branwilliams.frogger.pipeline.pass.ParallaxUIRenderPass;
import com.branwilliams.frogger.pipeline.pass.SpriteRenderPass;
import com.branwilliams.frogger.pipeline.pass.TilemapRenderPass;
import com.branwilliams.frogger.system.FocalPointFollowSystem;
import com.branwilliams.frogger.system.ParallaxBackgroundMovementSystem;
import com.branwilliams.frogger.system.TilemapUpdateSystem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.joml.Vector2f;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Brandon
 * @since November 30, 2019
 */
public class FroggerScene extends AbstractScene {

    private Tilemap tilemap;

    private Vector2f focalPoint = new Vector2f();

    private Vector2f targetFocalPoint = new Vector2f();

    private Transformable mouseTransform = new Transformation();

    private TextureLoader textureLoader;

    private ParallaxBackground<ScaledTexture> parallaxBackground;

    private Sprite fireballSprite;

    private int currentTileId = 0;

    private float spriteScale = 4F;

    public FroggerScene() {
        super("template-2d");
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        textureLoader = new TextureLoader(engine.getContext());

        parallaxBackground = loadParallaxBackground(FroggerConstants.PARALLAX_BACKGROUND_FILE);
        createParallaxBackgroundTextures(parallaxBackground);

//        SpriteAtlas spriteAtlas = loadSpriteAtlas(FroggerConstants.TILEMAP_ATLAS_FILE);
//        spriteAtlas.load(textureLoader);

//        tilemap = createTilemap();
//        tilemap.setSpriteAtlas(spriteAtlas);

        es.addSystem(new FocalPointFollowSystem(this::getFocalPoint, this::setFocalPoint, this::getTargetFocalPoint,
                FroggerConstants.CAMERA_MOVE_SPEED));
//        es.addSystem(new TilemapUpdateSystem(this::getTilemap));
        es.addSystem(new ParallaxBackgroundMovementSystem(this::getFocalPoint, this::getParallaxBackground));
        es.initSystems(engine, window);

        Projection worldProjection = new Projection(window);
        RenderContext renderContext = new RenderContext(worldProjection);

        RenderPipeline<RenderContext> renderPipeline = new RenderPipeline<>(renderContext);
        renderPipeline.addFirst(new ParallaxBackgroundRenderPass<>(this::getParallaxBackground));
//        renderPipeline.addLast(new TilemapRenderPass(this::getFocalPoint, this::getTilemap));
        renderPipeline.addLast(new SpriteRenderPass(this));
        renderPipeline.addLast(new ParallaxUIRenderPass());
        // Add render passes here.
        FroggerRenderer<RenderContext> renderer = new FroggerRenderer<>(this, renderPipeline);
        setRenderer(renderer);
    }

    private Tilemap createTilemap() {
        Tilemap tilemap = new Tilemap(FroggerConstants.SCREEN_WIDTH_TILES * 10, FroggerConstants.SCREEN_HEIGHT_TILES * 10,
                FroggerConstants.TILE_WIDTH_SCALED, FroggerConstants.TILE_HEIGHT_SCALED);
        return tilemap;
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

            Sprite frogmanTheSprite = new AnimatedSprite(frogger, new int[] { 0, 1, 2, 3, 0 },
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
        mouseTransform.setPosition(engine.getWindow().getMouseX(), engine.getWindow().getMouseY(), 0F);
        super.update(engine, deltaTime);
    }

    @Override
    public void fixedUpdate(Engine engine, double deltaTime) {
        super.fixedUpdate(engine, deltaTime);

        float keyMoveSpeed = 600F;

        if (engine.getWindow().isKeyPressed(GLFW_KEY_RIGHT)) {
            targetFocalPoint.x -= keyMoveSpeed * deltaTime;
        }

        if (engine.getWindow().isKeyPressed(GLFW_KEY_LEFT)) {
            targetFocalPoint.x += keyMoveSpeed * deltaTime;
        }

        if (engine.getWindow().isKeyPressed(GLFW_KEY_UP)) {
            targetFocalPoint.y += keyMoveSpeed * deltaTime;
        }

        if (engine.getWindow().isKeyPressed(GLFW_KEY_DOWN)) {
            targetFocalPoint.y -= keyMoveSpeed * deltaTime;
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
                    targetFocalPoint.set(focalPoint.x - movementX, targetFocalPoint.y - movementY);
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

    private ParallaxBackground<ScaledTexture> loadParallaxBackground(String backgroundJsonFile) {
        String fileText = IOUtils.readFile(backgroundJsonFile, null);
        Gson gson = new GsonBuilder().create();
        Type parallaxBackgroundType = new TypeToken<ParallaxBackground<ScaledTexture>>() {}.getType();
        return gson.fromJson(fileText, parallaxBackgroundType);
    }

    private void createParallaxBackgroundTextures(ParallaxBackground<ScaledTexture> background) throws IOException {
        for (ParallaxLayer<ScaledTexture> layer : background.getLayers()) {
            for (ParallaxObject<ScaledTexture> object : layer.getObjects()) {
                object.getObject().load(textureLoader);
            }
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

    public Vector2f getMouseRelativeToFocalPoint(float mouseX, float mouseY) {
        return new Vector2f(mouseX - focalPoint.x, mouseY - focalPoint.y);
    }

    public ParallaxBackground getParallaxBackground() {
        return parallaxBackground;
    }

    public Vector2f getFocalPoint() {
        return focalPoint;
    }

    public void setFocalPoint(Vector2f focalPoint) {
        this.focalPoint = focalPoint;
    }

    public Vector2f getTargetFocalPoint() {
        return targetFocalPoint;
    }

    public Tilemap getTilemap() {
        return tilemap;
    }
}

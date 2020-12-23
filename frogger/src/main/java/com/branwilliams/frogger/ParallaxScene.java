package com.branwilliams.frogger;

import com.branwilliams.bundi.engine.core.AbstractScene;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.shader.Projection;
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
public class ParallaxScene extends AbstractScene {

    public static final int SCREEN_WIDTH_PIXELS = 1024;

    public static final int SCREEN_HEIGHT_PIXELS = 768;

    private static final int TILE_WIDTH = 16;

    private static final int TILE_HEIGHT = 16;

    private static final float TILE_SCALE = 2F;

    private static final int TILE_WIDTH_SCALED = (int) (TILE_WIDTH * TILE_SCALE);

    private static final int TILE_HEIGHT_SCALED = (int) (TILE_HEIGHT * TILE_SCALE);

    private static final int SCREEN_WIDTH_TILES = SCREEN_WIDTH_PIXELS / TILE_WIDTH_SCALED;

    private static final int SCREEN_HEIGHT_TILES = SCREEN_HEIGHT_PIXELS / TILE_HEIGHT_SCALED;

    private static final float CAMERA_MOVE_SPEED = 2F;

    private static final int[] FIREBALL_FRAMES = {
            0,  1,  2,  3,  4,  5,  6,  7,  8,  9,
            10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
            20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
            30, 31, 32, 33, 34, 35, 36, 37, 38, 39,
            40, 41, 42, 43, 44, 45, 46, 47, 48, 49,
            50, 51, 52, 53, 54, 55, 56, 57, 58, 59
    };

    private static final String TILEMAP_ATLAS_FILE = "assets/minecraft_atlas.json";

    private static final String TILEMAP_SAVE_FILE = "mymap.json";

    private static final String PARALLAX_BACKGROUND_FILE = "assets/glacial_background.json";

    private Tilemap tilemap;

    private Vector2f focalPoint = new Vector2f();

    private Vector2f targetFocalPoint = new Vector2f();

    private TextureLoader textureLoader;

    private ParallaxBackground<ScaledTexture> parallaxBackground;

    private Sprite fireballSprite;

    private int currentTileId = 0;

    private float spriteScale = 4F;

    public ParallaxScene() {
        super("template-2d");
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        textureLoader = new TextureLoader(engine.getContext());

        parallaxBackground = loadParallaxBackground(PARALLAX_BACKGROUND_FILE);
        createParallaxBackgroundTextures(parallaxBackground);

        SpriteAtlas spriteAtlas = loadSpriteAtlas(TILEMAP_ATLAS_FILE);
        spriteAtlas.load(textureLoader);

//        tilemap = createTilemap();
        tilemap = loadTilemap(TILEMAP_SAVE_FILE);
        tilemap.setSpriteAtlas(spriteAtlas);

        es.addSystem(new FocalPointFollowSystem(this::getFocalPoint, this::setFocalPoint, this::getTargetFocalPoint,
                CAMERA_MOVE_SPEED));
        es.addSystem(new TilemapUpdateSystem(this::getTilemap));
        es.addSystem(new ParallaxBackgroundMovementSystem(this::getFocalPoint, this::getParallaxBackground));
        es.initSystems(engine, window);

        Projection worldProjection = new Projection(window);
        RenderContext renderContext = new RenderContext(worldProjection);

        RenderPipeline<RenderContext> renderPipeline = new RenderPipeline<>(renderContext);
        renderPipeline.addFirst(new ParallaxBackgroundRenderPass<>(this::getParallaxBackground));
        renderPipeline.addLast(new TilemapRenderPass(this::getFocalPoint, this::getTilemap));
        renderPipeline.addLast(new SpriteRenderPass(this));
        renderPipeline.addLast(new ParallaxUIRenderPass());
        // Add render passes here.
        FroggerRenderer<RenderContext> renderer = new FroggerRenderer<>(this, renderPipeline);
        setRenderer(renderer);
    }

    private Tilemap createTilemap() {
        Tilemap tilemap = new Tilemap(SCREEN_WIDTH_TILES * 10, SCREEN_HEIGHT_TILES * 10,
                TILE_WIDTH_SCALED, TILE_HEIGHT_SCALED);
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
            fireballSprite = new AnimatedSprite(fireball, FIREBALL_FRAMES,
                    new RateLimiter(TimeUnit.MILLISECONDS, 80L), fireballScale);

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

        if (!engine.getWindow().isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            Vector2f clickPosition = getMouseRelativeToFocalPoint(engine.getWindow().getMouseX(),
                    engine.getWindow().getMouseY());
            if (engine.getWindow().isMouseButtonPressed(GLFW_MOUSE_BUTTON_1)) {
                tilemap.setTile(currentTileId, clickPosition.x, clickPosition.y);
            } else if (engine.getWindow().isMouseButtonPressed(GLFW_MOUSE_BUTTON_2)) {
                tilemap.setEmpty(clickPosition.x, clickPosition.y);
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void wheel(Window window, double xoffset, double yoffset) {
        super.wheel(window, xoffset, yoffset);
        if (yoffset > 0) {
            this.setCurrentTile(this.currentTileId + 1);
        } else if (yoffset < 0) {
            this.setCurrentTile(this.currentTileId - 1);
        }
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
                case GLFW_MOUSE_BUTTON_2:
                    Vector2f clickPosition = getMouseRelativeToFocalPoint(mouseX, mouseY);
                    Tile tile = tilemap.getTile(clickPosition.x, clickPosition.y);
                    if (tile != null) {
                        this.setCurrentTile(tile.getTileId());
                    }
                    break;
            }
        }
    }

    @Override
    public void keyPress(Window window, int key, int scancode, int mods) {
        super.keyPress(window, key, scancode, mods);
        switch (key) {
            case GLFW_KEY_T:
                try {
                    saveTilemap(tilemap, TILEMAP_SAVE_FILE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void setCurrentTile(int tileId) {
        this.currentTileId = Mathf.clamp(tileId, 0, tilemap.getSpriteAtlas().getMaxSpriteIndex());
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

    private Tilemap loadTilemap(String fileLocation) throws IOException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Tilemap.class, new TilemapDeserializer())
                .setPrettyPrinting()
                .create();
        String fileContents = IOUtils.readFile(fileLocation, null);
        if (fileContents != null) {
            return gson.fromJson(fileContents, Tilemap.class);
        }
        return null;
    }

    private void saveTilemap(Tilemap tilemap, String fileLocation) throws IOException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Tilemap.class, new TilemapSerializer())
                .setPrettyPrinting()
                .create();
        FileWriter writer = new FileWriter(fileLocation);
        gson.toJson(tilemap, writer);
        writer.close();
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

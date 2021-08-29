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
import com.branwilliams.bundi.engine.shape.SeparatingAxis;
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
import com.branwilliams.frogger.parallax.ParallaxLoader;
import com.branwilliams.frogger.pipeline.pass.*;
import com.branwilliams.frogger.system.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joml.Vector2f;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.branwilliams.frogger.FroggerConstants.*;
import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Brandon
 */
public class TilemapCollisionScene extends AbstractScene {

    private static final int TILE_WIDTH = 16;

    private static final int TILE_HEIGHT = 16;

    private static final float TILE_SCALE = 4F;

    private static final int TILE_WIDTH_SCALED = (int) (TILE_WIDTH * TILE_SCALE);

    private static final int TILE_HEIGHT_SCALED = (int) (TILE_HEIGHT * TILE_SCALE);

    private static final int SCREEN_WIDTH_TILES = SCREEN_WIDTH_PIXELS / TILE_WIDTH_SCALED;

    private static final int SCREEN_HEIGHT_TILES = SCREEN_HEIGHT_PIXELS / TILE_HEIGHT_SCALED;

    private static final float CAMERA_MOVE_SPEED = 2F;

    private static final String PARALLAX_BACKGROUND_FILE = "assets/swamp_background.json";

    private static final String TILEMAP_ATLAS_FILE = "assets/swamp_tiles.json";

    private static final String TILEMAP_SAVE_FILE = "tilemap/saves/swamptiles.json";

    private TextureLoader textureLoader;

    private Tilemap tilemap;

    private ParallaxBackground<ScaledTexture> parallaxBackground;

    private Camera2D camera;

    private Transformable frogmanTransform = new Transformation();

    private Sprite frogmanTheSprite;

    private float spriteScale = 4F;

    private IEntity frogman;

    private int currentTileId = 0;

    public TilemapCollisionScene() {
        super("tilemap-collision");
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        textureLoader = new TextureLoader(engine.getContext());
        camera = new Camera2D(window.getWidth(), window.getHeight());


        parallaxBackground = ParallaxLoader.loadParallaxBackground(PARALLAX_BACKGROUND_FILE);
        ParallaxLoader.createParallaxBackgroundTextures(textureLoader, parallaxBackground);

        SpriteAtlas spriteAtlas = loadSpriteAtlas(TILEMAP_ATLAS_FILE);
        spriteAtlas.load(textureLoader);

//        tilemap = createTilemap();
        tilemap = loadTilemap(TILEMAP_SAVE_FILE);
        tilemap.setSpriteAtlas(spriteAtlas);

        es.addSystem(new FocalPointFollowSystem(camera::getFocalPoint, camera::setFocalPoint,
                camera::getTargetFocalPoint, CAMERA_MOVE_SPEED));
        es.addSystem(new ParallaxBackgroundMovementSystem(camera::getFocalPoint, this::getParallaxBackground));
        es.addSystem(new TilemapUpdateSystem(this, this::getTilemap, this::getCamera));
        es.addSystem(new FrogmanMovementSystem());
        es.addSystem(new FrogmanCollisionSystem(this::getTilemap));
        es.initSystems(engine, window);

        Projection worldProjection = new Projection(window);
        RenderContext renderContext = new RenderContext(worldProjection);

        RenderPipeline<RenderContext> renderPipeline = new RenderPipeline<>(renderContext);
        renderPipeline.addFirst(new ParallaxBackgroundRenderPass<>(this::getParallaxBackground));
        renderPipeline.addLast(new TilemapRenderPass(camera::getFocalPoint, this::getTilemap));
        renderPipeline.addLast(new TilemapPlacementRenderPass(this::getCurrentTileId, this::getCamera,
                this::getTilemap));
        renderPipeline.addLast(new EntityAABBRenderPass(this));
        renderPipeline.addLast(new SpriteRenderPass(this));
        renderPipeline.addLast(new HelpUIRenderPass(new String[] {
                "Use the arrow keys to move around",
                "Left click to set a tile",
                "Right click to delete",
                "Mouse wheel to cycle tiles"
        }));
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
        try {

            SpriteSheet froggerSpriteSheet = textureLoader.loadSpriteSheet("textures/frogger.png",
                    16, 16);
            frogmanTheSprite = new AnimatedSprite(froggerSpriteSheet, FroggerConstants.FROGMAN_FRAMES,
                    new RateLimiter(TimeUnit.MILLISECONDS, 180L), spriteScale);

            AABB2f frogmanAABB = (AABB2f) frogmanTheSprite.getAABB().copy();
            frogman = es.entity(FROGMAN_NAME).component(
                    frogmanTransform,
                    frogmanTheSprite,
                    frogmanAABB
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
        super.update(engine, deltaTime);
    }

    @Override
    public void fixedUpdate(Engine engine, double deltaTime) {
        super.fixedUpdate(engine, deltaTime);

        if (!engine.getWindow().isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            Vector2f clickPosition = camera.getMouseRelativeToFocalPoint(engine.getWindow());
            if (engine.getWindow().isMouseButtonPressed(GLFW_MOUSE_BUTTON_1)) {
                tilemap.setTile(currentTileId, clickPosition.x, clickPosition.y);
            } else if (engine.getWindow().isMouseButtonPressed(GLFW_MOUSE_BUTTON_2)) {
                tilemap.setEmpty(clickPosition.x, clickPosition.y);
            }
        }
    }


    private Tilemap.TileConsumer doTileCollision(AABB2f frogmanAABB) {
        return (x, y, tile) -> {
            float pX = x * tilemap.getTileWidth();
            float pY = y * tilemap.getTileHeight();
            AABB2f tileAABB = new AABB2f(pX, pY, pX + tilemap.getTileWidth(), pY + tilemap.getTileHeight());
            if (SeparatingAxis.collide(tileAABB, frogmanAABB, (push) -> frogmanTransform.move(push.x, push.y, 0))) {
                frogmanAABB.center(frogmanTransform.x(), frogmanTransform.y());
            }
        };
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
                    camera.getTargetFocalPoint().set(camera.getFocalPoint().x + movementX,
                            camera.getTargetFocalPoint().y + movementY);
                    break;
                case GLFW_MOUSE_BUTTON_2:
                    Vector2f clickPosition = camera.getMouseRelativeToFocalPoint(mouseX, mouseY);
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
    }

    private void setCurrentTile(int tileId) {
        this.currentTileId = Mathf.clamp(tileId, 0, tilemap.getSpriteAtlas().getMaxSpriteIndex());
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
        return createTilemap();
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

    public int getCurrentTileId() {
        return currentTileId;
    }

    public ParallaxBackground getParallaxBackground() {
        return parallaxBackground;
    }

    public Camera2D getCamera() {
        return camera;
    }

    public Tilemap getTilemap() {
        return tilemap;
    }
}

package com.branwilliams.frogger;

import com.branwilliams.bundi.engine.core.AbstractScene;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.shader.Projection;
import com.branwilliams.bundi.engine.texture.TextureLoader;
import com.branwilliams.bundi.engine.util.IOUtils;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.frogger.components.ScaledTexture;
import com.branwilliams.frogger.gson.TilemapDeserializer;
import com.branwilliams.frogger.gson.TilemapSerializer;
import com.branwilliams.frogger.parallax.ParallaxBackground;
import com.branwilliams.frogger.parallax.ParallaxLoader;
import com.branwilliams.frogger.pipeline.pass.*;
import com.branwilliams.frogger.system.FocalPointFollowSystem;
import com.branwilliams.frogger.system.ParallaxBackgroundMovementSystem;
import com.branwilliams.frogger.system.TilemapUpdateSystem;
import com.branwilliams.frogger.tilemap.SpriteAtlas;
import com.branwilliams.frogger.tilemap.Tile;
import com.branwilliams.frogger.tilemap.Tilemap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joml.Vector2f;

import java.io.FileWriter;
import java.io.IOException;

import static com.branwilliams.frogger.FroggerConstants.SCREEN_HEIGHT_PIXELS;
import static com.branwilliams.frogger.FroggerConstants.SCREEN_WIDTH_PIXELS;
import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Brandon
 * @since November 30, 2019
 */
public class TilemapScene extends AbstractScene {

    private static final int TILE_WIDTH = 16;

    private static final int TILE_HEIGHT = 16;

    private static final float TILE_SCALE = 4F;

    private static final int TILE_WIDTH_SCALED = (int) (TILE_WIDTH * TILE_SCALE);

    private static final int TILE_HEIGHT_SCALED = (int) (TILE_HEIGHT * TILE_SCALE);

    private static final int SCREEN_WIDTH_TILES = SCREEN_WIDTH_PIXELS / TILE_WIDTH_SCALED;

    private static final int SCREEN_HEIGHT_TILES = SCREEN_HEIGHT_PIXELS / TILE_HEIGHT_SCALED;

    private static final float CAMERA_MOVE_SPEED = 2F;

    private static final String PARALLAX_BACKGROUND_FILE = "assets/parallax/swamp_background.json";

    private static final String TILEMAP_ATLAS_FILE = "assets/tilemap/swamp_tiles.json";

    private static final String TILEMAP_SAVE_FILE = "tilemap/saves/swamptiles.json";

    private Tilemap tilemap;

    private Camera2D camera;

    private TextureLoader textureLoader;

    private ParallaxBackground<ScaledTexture> parallaxBackground;

    private int currentTileId = 0;

    public TilemapScene() {
        super("tilemap");
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
        es.addSystem(new TilemapUpdateSystem(this, this::getTilemap, this::getCamera));
        es.addSystem(new ParallaxBackgroundMovementSystem(camera::getFocalPoint, this::getParallaxBackground));
        es.initSystems(engine, window);

        Projection worldProjection = new Projection(window);
        RenderContext renderContext = new RenderContext(worldProjection);

        RenderPipeline<RenderContext> renderPipeline = new RenderPipeline<>(renderContext);
        renderPipeline.addFirst(new ParallaxBackgroundRenderPass<>(this::getParallaxBackground));
        renderPipeline.addLast(new TilemapRenderPass(camera::getFocalPoint, this::getTilemap));
        renderPipeline.addLast(new TilemapPlacementRenderPass(this::getCurrentTileId, this::getCamera,
                this::getTilemap));

        renderPipeline.addLast(new SpriteRenderPass(this));
        renderPipeline.addLast(new HelpUIRenderPass(new String[] {
                "Use the arrow keys to move around",
                "Left click to set a tile",
                "Right click to delete",
                "Mouse wheel to cycle tiles",
                "Press T to save this tilemap"
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

        float keyMoveSpeed = 600F;

        if (engine.getWindow().isKeyPressed(GLFW_KEY_RIGHT)) {
            camera.getTargetFocalPoint().x += keyMoveSpeed * deltaTime;
        }

        if (engine.getWindow().isKeyPressed(GLFW_KEY_LEFT)) {
            camera.getTargetFocalPoint().x -= keyMoveSpeed * deltaTime;
        }

        if (engine.getWindow().isKeyPressed(GLFW_KEY_UP)) {
            camera.getTargetFocalPoint().y -= keyMoveSpeed * deltaTime;
        }

        if (engine.getWindow().isKeyPressed(GLFW_KEY_DOWN)) {
            camera.getTargetFocalPoint().y += keyMoveSpeed * deltaTime;
        }

        if (!engine.getWindow().isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            Vector2f clickPosition = camera.getMouseRelativeToFocalPoint(engine.getWindow().getMouseX(),
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
                    camera.getTargetFocalPoint().set(camera.getFocalPoint().x + movementX, camera.getTargetFocalPoint().y + movementY);
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

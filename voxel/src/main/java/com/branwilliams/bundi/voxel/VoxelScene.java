package com.branwilliams.bundi.voxel;

import com.branwilliams.bundi.engine.core.*;
import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.material.Material;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.skybox.Skybox;
import com.branwilliams.bundi.engine.systems.LockableSystem;
import com.branwilliams.bundi.engine.texture.CubeMapTexture;
import com.branwilliams.bundi.engine.texture.TextureLoader;
import com.branwilliams.bundi.engine.util.RateLimiter;
import com.branwilliams.bundi.engine.util.noise.LayeredNoise;
import com.branwilliams.bundi.engine.util.noise.OpenSimplexNoise;
import com.branwilliams.bundi.gui.impl.ColorPack;
import com.branwilliams.bundi.gui.screen.GuiScreen;
import com.branwilliams.bundi.gui.screen.GuiScreenManager;
import com.branwilliams.bundi.voxel.builder.VoxelChunkMeshBuilder;
import com.branwilliams.bundi.voxel.builder.VoxelChunkMeshBuilderImpl;
import com.branwilliams.bundi.voxel.builder.VoxelMeshBuilder;
import com.branwilliams.bundi.voxel.components.*;
import com.branwilliams.bundi.voxel.inventory.ItemRegistry;
import com.branwilliams.bundi.voxel.io.*;
import com.branwilliams.bundi.voxel.io.SettingsLoader;
import com.branwilliams.bundi.voxel.system.world.AtmosphereSystem;
import com.branwilliams.bundi.voxel.system.world.ChunkLoadSystem;
import com.branwilliams.bundi.voxel.system.world.PhysicsSystem;
import com.branwilliams.bundi.voxel.voxels.model.VoxelFaceTexture;
import com.branwilliams.bundi.voxel.voxels.model.VoxelProperties;
import com.branwilliams.bundi.voxel.render.VoxelRenderer;
import com.branwilliams.bundi.voxel.system.player.*;
import com.branwilliams.bundi.voxel.voxels.VoxelRegistry;
import com.branwilliams.bundi.voxel.world.generator.NoiseChunkGenerator;
import com.branwilliams.bundi.voxel.world.generator.VoxelChunkGenerator;
import com.branwilliams.bundi.voxel.builder.VoxelMeshBuilderImpl;
import com.branwilliams.bundi.voxel.render.pipeline.VoxelRenderPipeline;
import com.branwilliams.bundi.voxel.world.VoxelWorld;
import com.branwilliams.bundi.voxel.world.storage.ChunkMeshStorage;
import com.branwilliams.bundi.voxel.world.storage.ChunkStorage;
import com.branwilliams.bundi.voxel.world.storage.HashChunkStorage;
import com.google.gson.reflect.TypeToken;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Brandon Williams on 3/12/2018.
 */
public class VoxelScene extends AbstractScene implements Lockable {

    private final Logger log = LoggerFactory.getLogger(getClass());

    /** Used to determine whether this scene is paused. */
    private final Lock delegateLock = new Lock();

    private Engine engine;

    private Window window;

    private VoxelGameState gameState = VoxelGameState.INGAME;

    private GuiScreenManager<VoxelScene> guiScreenManager;

    private VoxelSoundManager voxelSoundManager;

    private Projection projection;

    private Camera camera;

    private Skybox skybox;

    private VoxelRegistry voxelRegistry;

    private ItemRegistry itemRegistry;

    private VoxelWorld voxelWorld;

    private VoxelTexturePack texturePack;

    private VoxelMeshBuilder voxelMeshBuilder;

    private VoxelChunkMeshBuilder voxelChunkMeshBuilder;

    private PlayerState playerState;

    private PlayerControls playerControls;

    private GameSettings gameSettings;

    private IEntity player;

    private Atmosphere atmosphere;

    private boolean stop = false;

    public VoxelScene() {
        super("voxel_scene");
    }

    /**
     * @return The path to the asset directory for this voxel scene.
     * */
    public static Path getAssetDirectory(EngineContext engineContext) {
        return engineContext.getAssetDirectory().resolve("voxel");
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        // TODO Don't do this maybe?
        this.engine = engine;
        this.window = window;

//        engine.setUpdateInterval(1 / 20D);
        guiScreenManager = new GuiScreenManager(this);
        guiScreenManager.init(engine, window);
        ColorPack.LIGHT_BLUE.apply(guiScreenManager.getToolbox());

        projection = new Projection(window, 70, 0.01F, 1000F);
        VoxelRenderPipeline voxelRenderPipeline = new VoxelRenderPipeline(this, projection);
        VoxelRenderer voxelRenderer = new VoxelRenderer(this, voxelRenderPipeline);
        setRenderer(voxelRenderer);

        camera = new Camera();

        // pause input
        es.addSystem(new PlayerPauseSystem(this));

        // world loading/updating
        es.addSystem(new LockableSystem(this, new ChunkLoadSystem(this)));
        es.addSystem(new LockableSystem(this, new AtmosphereSystem()));

        // input
        es.addSystem(new LockableSystem(this, new PlayerInputSystem(this)));
        es.addSystem(new PlayerCameraInputSystem(this)); // This system extends a system with lockable logic already.

        // physics update and collision resolution
        es.addSystem(new LockableSystem(this, new PhysicsSystem(this, new Vector3f(0F, -9.8F, 0F))));
        es.addSystem(new LockableSystem(this, new PlayerCollisionSystem(this)));

        // camera update (to follow user position)
        es.addSystem(new LockableSystem(this, new PlayerCameraUpdateSystem(this)));

        // raycast for world interaction
        es.addSystem(new LockableSystem(this, new PlayerRaycastSystem(this)));
        es.addSystem(new PlayerInteractSystem(this, this)); // This system implements its own lockable logic.

        es.initSystems(engine, window);

        // disable cursor so user can have 3d movement
        window.disableCursor();

        Path assetDirectory = getAssetDirectory(engine.getContext());
        JsonLoader jsonLoader = new JsonLoader(assetDirectory);
        jsonLoader.initialize(engine.getWindow().getKeycodes());

        loadVoxelData(jsonLoader, assetDirectory, Paths.get("voxel_properties_hd.json"),
                Paths.get("default_textures.json"));

        itemRegistry = new ItemRegistry(voxelRegistry);

        loadSettings(jsonLoader);

        voxelMeshBuilder = new VoxelMeshBuilderImpl(voxelRegistry, texturePack);
        voxelChunkMeshBuilder = new VoxelChunkMeshBuilderImpl(voxelRegistry, texturePack);

        voxelSoundManager = new VoxelSoundManager(this);
        voxelSoundManager.initialize(engine);
    }

    @Override
    public void play(Engine engine) {
        es.clearEntities();

        try {
            TextureLoader textureLoader = new TextureLoader(engine.getContext());
            CubeMapTexture skyboxTexture = textureLoader.loadCubeMapTexture("assets/one.csv");
            skybox = new Skybox(500, new Material(skyboxTexture));
        } catch (IOException e) {
            e.printStackTrace();
        }

        playerState = new PlayerState();
        playerState.getInventory().addItems(itemRegistry);

        player = es.entity("player")
                .component(new CameraComponent(camera, 0.16F),
                        new Transformation().position(128, 128, 128),
                        new MovementComponent(1F),
                        new WalkComponent(30F),
                        playerControls,
                        playerState
                ).build();
        playerState.updateBoundingBox(player.getComponent(Transformable.class));

        DirectionalLight sun = new DirectionalLight(
                new Vector3f(-0.2F, -1.0F, -0.3F), // direction
                new Vector3f(0.5F),                      // ambient
                new Vector3f(0.4F),                      // diffuse
                new Vector3f(0.5F));                     // specular

        // blueish
        Vector4f skyColor = new Vector4f(0.5F, 0.6F, 0.7F, 1.0F);

        // yellowish
        Vector4f sunColor = new Vector4f(1.0F, 0.9F, 0.7F, 1.0F);

        // TODO flesh out the fog component.. make this part of the atmosphere module.
        Fog fog = new Fog(0.025F);
        atmosphere = new Atmosphere(sun, skyColor, sunColor, fog);

        es.entity("atmosphere").component(
                atmosphere
        ).build();

        loadWorld();
    }

    @Override
    public void pause(Engine engine) {

    }

    @Override
    public void destroy() {
        super.destroy();
        this.voxelWorld.destroy();
        this.voxelSoundManager.destroy();
    }

    /**
     * Reads the voxel properties and creates a registry of them. Then reads the default textures for voxel faces that
     * do not have textures. Finally, creates the texture pack using the previous two.
     * */
    public void loadVoxelData(JsonLoader jsonLoader, Path assetDirectory, Path voxels, Path defaultVoxelFaces) {

        // Load the voxel definitions from the provided path.
        Type voxelPropertiesType = new TypeToken<Map<String, VoxelProperties>>() {}.getType();
        Map<String, VoxelProperties> properties = jsonLoader.loadObject(voxelPropertiesType, voxels);

        // Create the registry of voxels
        voxelRegistry = new VoxelRegistry(properties);
        voxelRegistry.initialize();

        TextureLoader textureLoader = new TextureLoader(assetDirectory);

        // Load default faces for voxels with no mappings
        VoxelFaceTexture defaultVoxelFaceTexture = jsonLoader.loadObject(VoxelFaceTexture.class, defaultVoxelFaces);
        try {
            defaultVoxelFaceTexture.load(textureLoader);
        } catch (IOException e) {
            log.error("Unable create default voxel faces from " + defaultVoxelFaces, e);
            return;
        }

        // Create the texture pack
        texturePack = new VoxelTexturePack(voxelRegistry, defaultVoxelFaceTexture);

        try {
            texturePack.initialize(textureLoader);
        } catch (VoxelTexturePackException e) {
            log.error("Unable create texture pack from " + voxels, e);
        }
    }

    /**
     * Loads the user settings such as keybinds.
     *
     * */
    public void loadSettings(JsonLoader jsonLoader) {
        SettingsLoader settingsLoader = new SettingsLoader(jsonLoader);
        playerControls = settingsLoader.loadPlayerControls();
        gameSettings = settingsLoader.loadGameSettings();
    }

    public void loadWorld() {
        if (voxelWorld != null)
            voxelWorld.destroy();

        RateLimiter meshCreationLimiter = new RateLimiter(TimeUnit.MILLISECONDS, 50L);

        // Create chunk & chunk mesh storage
        ChunkMeshStorage chunkMeshStorage = new ChunkMeshStorage(voxelChunkMeshBuilder, meshCreationLimiter);
        ChunkStorage chunkStorage = new HashChunkStorage();

        // create generator and world
//        VoxelChunkGenerator voxelChunkGenerator = new NoiseChunkGenerator(new PerlinNoise());
        VoxelChunkGenerator voxelChunkGenerator = new NoiseChunkGenerator(new LayeredNoise(new OpenSimplexNoise(), 5));
        voxelWorld = new VoxelWorld(voxelRegistry, voxelChunkGenerator, chunkStorage, chunkMeshStorage, es);

        // load the chunks at a given position in a given radius
        Transformable transformable = player.getComponent(Transformable.class);
        voxelWorld.loadChunks(transformable.getPosition().x, transformable.getPosition().z, gameSettings.getChunkRenderDistance());
        voxelWorld.forceGenerateChunkMeshes();
    }

    @Override
    public void update(Engine engine, double updateInterval) {
        super.update(engine, updateInterval);

        if (this.getGuiScreen() != null)
            this.getGuiScreen().update();

        if (stop)
            engine.stop();
    }

    @Override
    public boolean isLocked() {
        return delegateLock.isLocked();
    }

    @Override
    public void setLocked(boolean locked) {
        delegateLock.setLocked(locked);
    }

    public void onGamePaused() {
        setGameState(VoxelGameState.PAUSED);
        this.setLocked(true);
    }

    public void onGameUnpaused() {
        setGameState(VoxelGameState.INGAME);
        this.setLocked(false);
    }

    public VoxelGameState getGameState() {
        return gameState;
    }

    public void setGameState(VoxelGameState gameState) {
        this.gameState = gameState;
    }

    public Atmosphere getAtmosphere() {
        return atmosphere;
    }

    public GuiScreenManager getGuiScreenManager() {
        return guiScreenManager;
    }

    public Projection getProjection() {
        return projection;
    }

    public Camera getCamera() {
        return camera;
    }

    public Skybox getSkybox() {
        return skybox;
    }

    public VoxelRegistry getVoxelRegistry() {
        return voxelRegistry;
    }

    public VoxelWorld getVoxelWorld() {
        return voxelWorld;
    }

    public VoxelTexturePack getTexturePack() {
        return texturePack;
    }

    public VoxelMeshBuilder getVoxelMeshBuilder() {
        return voxelMeshBuilder;
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    public PlayerControls getPlayerControls() {
        return playerControls;
    }

    public DirectionalLight getSun() {
        return atmosphere.getSun();
    }

    public IEntity getPlayer() {
        return player;
    }

    public GuiScreen<VoxelScene> getGuiScreen() {
        return guiScreenManager.getGuiScreen();
    }

    public void setGuiScreen(GuiScreen<VoxelScene> guiScreen) {
        guiScreenManager.setGuiScreen(guiScreen);
    }

    public GameSettings getGameSettings() {
        return gameSettings;
    }

    public VoxelSoundManager getVoxelSoundManager() {
        return voxelSoundManager;
    }

    /**
     * Sets the boolean 'stop' to true, signaling to this scene to stop the engine, ultimately stopping this
     * application.
     * */
    public void stop() {
        stop = true;
    }

    public Engine getEngine() {
        return engine;
    }

    public Window getWindow() {
        return window;
    }
}
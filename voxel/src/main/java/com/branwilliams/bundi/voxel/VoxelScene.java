package com.branwilliams.bundi.voxel;

import com.branwilliams.bundi.engine.core.*;
import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.core.event.LockableStateUpdateEvent;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.skybox.Skybox;
import com.branwilliams.bundi.engine.systems.LockableSystem;
import com.branwilliams.bundi.engine.texture.CubeMapTexture;
import com.branwilliams.bundi.engine.texture.TextureLoader;
import com.branwilliams.bundi.engine.util.RateLimiter;
import com.branwilliams.bundi.gui.screen.GuiScreen;
import com.branwilliams.bundi.gui.screen.GuiScreenManager;
import com.branwilliams.bundi.voxel.components.*;
import com.branwilliams.bundi.voxel.io.*;
import com.branwilliams.bundi.voxel.io.SettingsLoader;
import com.branwilliams.bundi.voxel.system.world.ChunkLoadSystem;
import com.branwilliams.bundi.voxel.system.world.PhysicsSystem;
import com.branwilliams.bundi.voxel.voxels.model.VoxelFaceTexture;
import com.branwilliams.bundi.voxel.voxels.model.VoxelProperties;
import com.branwilliams.bundi.voxel.render.VoxelRenderer;
import com.branwilliams.bundi.voxel.system.player.*;
import com.branwilliams.bundi.voxel.voxels.VoxelRegistry;
import com.branwilliams.bundi.voxel.world.generator.PerlinChunkGenerator;
import com.branwilliams.bundi.voxel.world.generator.VoxelChunkGenerator;
import com.branwilliams.bundi.voxel.builder.VoxelMeshBuilder;
import com.branwilliams.bundi.voxel.render.pipeline.VoxelRenderPipeline;
import com.branwilliams.bundi.voxel.world.VoxelWorld;
import com.branwilliams.bundi.voxel.world.storage.ChunkMeshStorage;
import com.branwilliams.bundi.voxel.world.storage.ChunkStorage;
import com.branwilliams.bundi.voxel.world.storage.HashChunkStorage;
import com.google.gson.reflect.TypeToken;
import org.joml.Vector3f;
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

    private GuiScreenManager guiScreenManager;

    private Projection projection;

    private Camera camera;

    private Skybox skybox;

    private VoxelRegistry voxelRegistry;

    private VoxelWorld voxelWorld;

    private VoxelTexturePack texturePack;

    private VoxelMeshBuilder voxelMeshBuilder;

    private PlayerState playerState;

    private PlayerControls playerControls;

    private GameSettings gameSettings;

    private IEntity player;

    private DirectionalLight sun;


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
        guiScreenManager = new GuiScreenManager(this);
        projection = new Projection(window, 70, 0.01F, 1000F);
        VoxelRenderPipeline voxelRenderPipeline = new VoxelRenderPipeline(this, projection);
        VoxelRenderer voxelRenderer = new VoxelRenderer(this, voxelRenderPipeline);
        setRenderer(voxelRenderer);

        camera = new Camera();
        sun = new DirectionalLight(
                new Vector3f(-0.2F, -1.0F, -0.3F), // direction
                new Vector3f(0.5F),                      // ambient
                new Vector3f(0.4F),                      // diffuse
                new Vector3f(0.5F));                     // specular

        es.addSystem(new PlayerPauseSystem(this, this));
        es.addSystem(new LockableSystem(this, new ChunkLoadSystem(this)));
        es.addSystem(new LockableSystem(this, new PlayerInputSystem(this)));
        es.addSystem(new LockableSystem(this, new PhysicsSystem(this)));
        es.addSystem(new LockableSystem(this, new PlayerCollisionSystem(this)));
        es.addSystem(new PlayerCameraUpdateSystem(this)); // This system extends a system with lockable logic already.
        es.addSystem(new LockableSystem(this, new PlayerRaycastSystem(this)));
        es.addSystem(new PlayerInteractSystem(this, this)); // This system implements its own lockable logic.
        es.initSystems(engine, window);

        window.disableCursor();

        Path assetDirectory = getAssetDirectory(engine.getContext());
        JsonLoader jsonLoader = new JsonLoader(assetDirectory);
        jsonLoader.initialize(engine.getWindow().getKeycodes());


        loadVoxelData(jsonLoader, assetDirectory, Paths.get("voxel_properties_hd.json"),
                Paths.get("default_textures.json"));

        loadSettings(jsonLoader);

        voxelMeshBuilder = new VoxelMeshBuilder(voxelRegistry, texturePack);
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

        player = es.entity("player")
                .component(new CameraComponent(camera, 0.16F),
                        new Transformation().position(128, 128, 128),
                        new MovementComponent(1F),
                        new WalkComponent(30F),
                        playerControls,
                        playerState
                ).build();
        playerState.updateBoundingBox(player.getComponent(Transformable.class));

        loadWorld();
    }

    @Override
    public void pause(Engine engine) {

    }

    @Override
    public void destroy() {
        super.destroy();
        this.voxelWorld.destroy();
    }

    /**
     * Reads the voxel properties and creates a registry of them. Then reads the default textures for voxel faces that
     * do not have textures. Finally, creates the texture pack using the previous two.
     * */
    public void loadVoxelData(JsonLoader jsonLoader, Path assetDirectory, Path voxels, Path defaultVoxelFaces) {
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

    private void loadWorld() {
        if (voxelWorld != null)
            voxelWorld.destroy();

        RateLimiter meshCreationLimiter = new RateLimiter(TimeUnit.MILLISECONDS, 50L);
        // Create chunk & chunk mesh storage
        ChunkMeshStorage chunkMeshStorage = new ChunkMeshStorage(voxelMeshBuilder, meshCreationLimiter);
        ChunkStorage chunkStorage = new HashChunkStorage();

        // create generator and world
        VoxelChunkGenerator voxelChunkGenerator = new PerlinChunkGenerator();
        voxelWorld = new VoxelWorld(voxelRegistry, voxelChunkGenerator, chunkStorage, chunkMeshStorage);

        // load the chunks at a given position in a given radius
        Transformable transformable = player.getComponent(Transformable.class);
        voxelWorld.loadChunks(transformable.getPosition().x, transformable.getPosition().z, gameSettings.getChunkRenderDistance());
        voxelWorld.forceGenerateChunkMeshes();
    }

    @Override
    public void update(Engine engine, double updateInterval) {
        super.update(engine, updateInterval);
    }

    @Override
    public boolean isLocked() {
        return delegateLock.isLocked();
    }

    @Override
    public void setLocked(boolean locked) {
        delegateLock.setLocked(locked);
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
        return sun;
    }

    public IEntity getPlayer() {
        return player;
    }

    public GuiScreen getGuiScreen() {
        return guiScreenManager.getGuiScreen();
    }

    public void setGuiScreen(GuiScreen guiScreen) {
        guiScreenManager.setGuiScreen(guiScreen);
    }

    public GameSettings getGameSettings() {
        return gameSettings;
    }
}
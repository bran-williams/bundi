package com.branwilliams.cubes;

import com.branwilliams.bundi.engine.core.*;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.core.pipeline.passes.DisableWireframeRenderPass;
import com.branwilliams.bundi.engine.core.pipeline.passes.EnableWireframeRenderPass;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.material.Material;
import com.branwilliams.bundi.engine.material.MaterialElement;
import com.branwilliams.bundi.engine.material.MaterialFormat;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.systems.DebugCameraMoveSystem;
import com.branwilliams.bundi.engine.texture.Texture;
import com.branwilliams.bundi.engine.texture.TextureData;
import com.branwilliams.bundi.engine.texture.TextureLoader;
import com.branwilliams.bundi.engine.util.Grid3i;
import com.branwilliams.bundi.engine.util.IOUtils;
import com.branwilliams.bundi.engine.util.noise.OpenSimplexNoise;
import com.branwilliams.cubes.builder.*;
import com.branwilliams.cubes.builder.evaluators.*;
import com.branwilliams.cubes.math.RaycastResult;
import com.branwilliams.cubes.pipeline.DebugRenderPass;
import com.branwilliams.cubes.pipeline.MarchingCubeChunkRenderPass;
import com.branwilliams.cubes.pipeline.RaycastResultRenderPass;
import com.branwilliams.cubes.system.PlayerInteractSystem;
import com.branwilliams.cubes.world.MarchingCubeChunk;
import com.branwilliams.cubes.world.MarchingCubeData;
import com.branwilliams.cubes.world.MarchingCubeWorld;
import com.branwilliams.cubes.world.WorldProperties;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import static com.branwilliams.bundi.engine.util.ColorUtils.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;

/**
 * @author Brandon
 * @since November 30, 2019
 */
public class CubesScene extends AbstractScene {

    public static final Color WORLD_COLOR = fromHex("#573B0C");

    public static final Color FOG_COLOR = fromHex("#573B0C");

    // play vars

    private static final float RAYCAST_DISTANCE  = 256F;

    private static final int NUM_CHUNKS_XZ = 4;

    private static final int NUM_CHUNKS_Y = 4;


    // world vars

    private static final int MAX_NUM_CHUNKS_X = NUM_CHUNKS_XZ;

    private static final int MAX_NUM_CHUNKS_Y = NUM_CHUNKS_Y;

    private static final int MAX_NUM_CHUNKS_Z = NUM_CHUNKS_XZ;

    private static final int CUBE_SIZE = 1;

    private static final int CHUNK_SIZE_X = 32;

    private static final int CHUNK_SIZE_Y = 32;

    private static final int CHUNK_SIZE_Z = 32;

    private static final float ISO_LEVEL = 0.25F;

    private final DirectionalLight sun = new DirectionalLight(
            new Vector3f(-0.2F, -1F, -0.3F), // direction
            new Vector3f(0.5F),  // ambient
            new Vector3f(0.8F),  // diffuse
            toVector3(WORLD_COLOR.brighter())); // specular

    private final PointLight playerLight = new PointLight(new Vector3f(0, 0, 0),
            new Vector3f(0.05F),
            new Vector3f(0.5F),
            new Vector3f(0.5F));

    private Environment environment = new Environment(
            new Fog(0.005F, toVector4(FOG_COLOR)),
            new PointLight[] {
                    playerLight
            },
            new DirectionalLight[] {
                    sun
            }, null);

    private TextureLoader textureLoader;

    private Camera camera;

    private RaycastResult raycast;

    private boolean wireframe;

    private MarchingCubeWorld<MarchingCubeData> world;

    private Lockable pauseState = new Lock(true);

    public CubesScene() {
        super("cubes");
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        super.init(engine, window);

        es.addSystem(new DebugCameraMoveSystem(this, pauseState, this::getCamera, 0.16F,
                16F, true));
        es.addSystem(new PlayerInteractSystem(this, this::getRaycastDistance));
        es.initSystems(engine, window);

        Projection worldProjection = new Projection(window, 70, 0.01F, 1000F);
        RenderContext renderContext = new RenderContext(worldProjection);

        RenderPipeline<RenderContext> renderPipeline = new RenderPipeline<>(renderContext);
        renderPipeline.addLast(new EnableWireframeRenderPass(this::isWireframe));
        renderPipeline.addLast(new MarchingCubeChunkRenderPass(this, this::getEnvironment, this::getCamera));
        renderPipeline.addLast(new RaycastResultRenderPass(this, this::getCamera));
        renderPipeline.addLast(new DisableWireframeRenderPass(this::isWireframe));
        renderPipeline.addLast(new DebugRenderPass(this, this::getCamera));
        CubesRenderer renderer = new CubesRenderer(this, renderPipeline);
        setRenderer(renderer);

        textureLoader = new TextureLoader(engine.getContext());
    }

    @Override
    public void play(Engine engine) {

        camera = new Camera();
        camera.setPosition(-10, 2, -10);
        camera.lookAt(0, 0, 0);

        IsoEvaluator evaluator = new NoiseIsoEvaluator(new OpenSimplexNoise(), 0.07F)
                .andThen(new SphereIsoEvaluator2(new Vector3f(80, 45, 80), 30));

        GridBuilder<MarchingCubeData> gridBuilder = new GridBuilderImpl<>(evaluator,
                (x, y, z) -> new MarchingCubeData(), MarchingCubeData[]::new);

        GridMeshBuilder<MarchingCubeData> gridMeshBuilder = new GridMeshBuilderSmoothNormals<>();

        WorldProperties worldProperties = new WorldProperties(
                new Vector3i(MAX_NUM_CHUNKS_X, MAX_NUM_CHUNKS_Y, MAX_NUM_CHUNKS_Z),
                new Vector3i(CHUNK_SIZE_X, CHUNK_SIZE_Y, CHUNK_SIZE_Z), CUBE_SIZE, ISO_LEVEL);

        world = new MarchingCubeWorld<>(worldProperties, gridBuilder, gridMeshBuilder);
        world.loadAllChunks();

        try {
            buildWorld();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void buildWorld() throws IOException {
        Material material = new Material();

        material.setMaterialFormat(MaterialFormat.DIFFUSE_VEC4);
        material.setProperty(MaterialElement.DIFFUSE, toVector4(WORLD_COLOR));

        material.setProperty("triplanarBlendOffset", 0.1F);
        material.setProperty("triplanarTile", 0.5F);

        // diffuse
//        material.setMaterialFormat(MaterialFormat.DIFFUSE_SAMPLER2D);
//        TextureData diffuseData = textureLoader.loadTexture("textures/grass/grass01.png");
//        Texture diffuse = new Texture(diffuseData, true);
//        material.setTexture(diffuse);

//        // diffuse + normal
//        material.setMaterialFormat(MaterialFormat.DIFFUSE_NORMAL);
//        TextureData diffuseData = textureLoader.loadTexture("textures/rock/rock_color.jpg");
//        Texture diffuse = new Texture(diffuseData, true);
//
//        TextureData normalData = textureLoader.loadTexture("textures/rock/rock_norm.jpg");
//        Texture normal = new Texture(normalData, true);
//        material.setTextures(diffuse, normal);

        // diffuse + normal + specular
        material.setMaterialFormat(MaterialFormat.DIFFUSE_NORMAL);
        TextureData diffuseData = textureLoader.loadTexture("textures/grass/grass01.png");
        Texture diffuse = new Texture(diffuseData, true);

        TextureData normalData = textureLoader.loadTexture("textures/grass/grass01_n.png");
        Texture normal = new Texture(normalData, true);

//        TextureData specularData = textureLoader.loadTexture("textures/grass/grass01_s.png");
//        Texture specular = new Texture(specularData, true);
//        material.setTextures(diffuse, normal, specular);

        for (MarchingCubeChunk<MarchingCubeData> chunk : world.getChunks()) {
            IEntity entity = es.entity("chunk-(" + chunk.getOffset() + ")")
                    .component(
                            new Transformation().position(chunk.getOffset()),
                            material,
                            chunk)
                    .build();
        }
    }

    private void loadFromFile(String worldProperties, String worldDir) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        String propertiesContent = IOUtils.readFile(worldProperties, null);
        WorldProperties worldProperties_ = gson.fromJson(propertiesContent, WorldProperties.class);

        Type chunkDataType = new TypeToken<Grid3i<Float>>(){}.getType();
        Type listOfChunks = new TypeToken<List<Grid3i<Float>>>(){}.getType();

        String worldDataContent = IOUtils.readFile(worldDir, null);
        Grid3i<Float> chunkData = gson.fromJson(worldDataContent, chunkDataType);
    }

    private void toGrid3f() {
        
    }

    @Override
    public void pause(Engine engine) {

    }

    @Override
    public void update(Engine engine, double deltaTime) {
        super.update(engine, deltaTime);
        world.update(engine, deltaTime);
        if (this.getPlayerLight() != null) {
            this.getPlayerLight().setPosition(camera.getPosition());
        }
    }

    @Override
    public void keyPress(Window window, int key, int scancode, int mods) {
        super.keyPress(window, key, scancode, mods);
        if (key == GLFW_KEY_R) {
            wireframe = !wireframe;
        }

        if (key == GLFW_KEY_ESCAPE) {
            pauseState.toggle();

            if (pauseState.isLocked()) {
                window.showCursor();
                window.centerCursor();
            } else {
                window.disableCursor();
            }
        }
    }

    public float getRaycastDistance() {
        return RAYCAST_DISTANCE;
    }

    public DirectionalLight getSun() {
        return sun;
    }

    public Camera getCamera() {
        return camera;
    }

    public boolean isWireframe() {
        return wireframe;
    }

    public MarchingCubeWorld<MarchingCubeData> getWorld() {
        return world;
    }

    public RaycastResult getRaycast() {
        return raycast;
    }

    public void setRaycast(RaycastResult raycast) {
        this.raycast = raycast;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public PointLight getPlayerLight() {
        return playerLight;
    }
}

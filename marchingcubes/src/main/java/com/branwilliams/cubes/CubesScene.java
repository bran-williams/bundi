package com.branwilliams.cubes;

import com.branwilliams.bundi.engine.core.*;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.core.pipeline.passes.DisableWireframeRenderPass;
import com.branwilliams.bundi.engine.core.pipeline.passes.EnableWireframeRenderPass;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.shader.Camera;
import com.branwilliams.bundi.engine.shader.DirectionalLight;
import com.branwilliams.bundi.engine.shader.Projection;
import com.branwilliams.bundi.engine.shader.Transformation;
import com.branwilliams.bundi.engine.systems.DebugCameraMoveSystem;
import com.branwilliams.bundi.engine.texture.TextureLoader;
import com.branwilliams.bundi.engine.util.Grid3i;
import com.branwilliams.bundi.engine.util.IOUtils;
import com.branwilliams.bundi.engine.util.noise.OpenSimplexNoise;
import com.branwilliams.cubes.builder.*;
import com.branwilliams.cubes.builder.evaluators.NoiseIsoEvaluator;
import com.branwilliams.cubes.builder.evaluators.SphereIsoEvaluator;
import com.branwilliams.cubes.math.RaycastResult;
import com.branwilliams.cubes.pipeline.DebugRenderPass;
import com.branwilliams.cubes.pipeline.GridCellRenderPass;
import com.branwilliams.cubes.pipeline.RaycastResultRenderPass;
import com.branwilliams.cubes.system.PlayerInteractSystem;
import com.branwilliams.cubes.world.MarchingCubeChunk;
import com.branwilliams.cubes.world.MarchingCubeWorld;
import com.branwilliams.cubes.world.WorldProperties;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.awt.Color;
import java.lang.reflect.Type;
import java.util.List;

import static com.branwilliams.bundi.engine.util.ColorUtils.fromHex;
import static com.branwilliams.bundi.engine.util.ColorUtils.toVector3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F5;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;

/**
 * @author Brandon
 * @since November 30, 2019
 */
public class CubesScene extends AbstractScene implements Window.KeyListener {

    public static final Color WORLD_COLOR = fromHex("#573B0C");

    public static final Color FOG_COLOR = fromHex("#573B0C");

    // play vars

    private static final float RAYCAST_DISTANCE  = 8F;

    private static final int NUM_CHUNKS_XZ = 4;

    private static final int NUM_CHUNKS_Y = 2;


    // world vars

    private static final int MAX_NUM_CHUNKS_X = NUM_CHUNKS_XZ;

    private static final int MAX_NUM_CHUNKS_Y = NUM_CHUNKS_Y;

    private static final int MAX_NUM_CHUNKS_Z = NUM_CHUNKS_XZ;

    private static final int CUBE_SIZE = 1;

    private static final int GRID_CELL_SIZE_X = 32;

    private static final int GRID_CELL_SIZE_Y = 32;

    private static final int GRID_CELL_SIZE_Z = 32;

    private static final float ISO_LEVEL = 0.25F;

    private final DirectionalLight sun = new DirectionalLight(
            new Vector3f(-0.2F, -1F, -0.3F), // direction
            new Vector3f(0.5F),  // ambient
            new Vector3f(0.4F),  // diffuse
            toVector3(WORLD_COLOR.brighter())); // specular

    private TextureLoader textureLoader;

    private Camera camera;

    private RaycastResult raycast;

    private boolean wireframe;

    private MarchingCubeWorld world;

    public CubesScene() {
        super("cubes");
        this.addKeyListener(this);
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        es.addSystem(new DebugCameraMoveSystem(this, this::getCamera, 0.16F, 16F));
        es.addSystem(new PlayerInteractSystem(this, this::getRaycastDistance));
        es.initSystems(engine, window);

        Projection worldProjection = new Projection(window, 70, 0.01F, 1000F);
        RenderContext renderContext = new RenderContext(worldProjection);

        RenderPipeline<RenderContext> renderPipeline = new RenderPipeline<>(renderContext);
        renderPipeline.addLast(new EnableWireframeRenderPass(this::isWireframe));
        renderPipeline.addLast(new GridCellRenderPass(this, this::getSun, this::getCamera));
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


//        GridCellGridBuilder gridCellGridBuilder = new GridCellGridBuilderImpl(
//                new NoiseIsoEvaluator(new OpenSimplexNoise(), 0.1F)
//                        .andThen(new GradientIsoEvaluator(GRID_CELL_SIZE_Y)));

        GridCellGridBuilder gridCellGridBuilder = new GridCellGridBuilderImpl(
                new NoiseIsoEvaluator(new OpenSimplexNoise(), 0.1F)
                        .andThen(new SphereIsoEvaluator(new Vector3f(GRID_CELL_SIZE_X, GRID_CELL_SIZE_Y * 0.5F, GRID_CELL_SIZE_Z), 16)));

//        GridCellGridBuilder gridCellGridBuilder = new GridCellGridBuilderImpl(
//                new NoiseIsoEvaluator(new OpenSimplexNoise(), 0.1F)
//                        .andThen(new TorusIsoEvaluator(new Vector3f(GRID_CELL_SIZE_X, GRID_CELL_SIZE_Y * 0.5F, GRID_CELL_SIZE_Z),
//                                new Torus(8, 4))));

        GridCellMeshBuilder gridCellMeshBuilder = new GridCellMeshBuilderImpl();

        WorldProperties worldProperties = new WorldProperties(
                new Vector3i(MAX_NUM_CHUNKS_X, MAX_NUM_CHUNKS_Y, MAX_NUM_CHUNKS_Z),
                new Vector3i(GRID_CELL_SIZE_X, GRID_CELL_SIZE_Y, GRID_CELL_SIZE_Z), CUBE_SIZE, ISO_LEVEL);

        world = new MarchingCubeWorld(worldProperties,
                gridCellGridBuilder, gridCellMeshBuilder);
        world.loadAllChunks();

//        loadFromFile("cubes/world_properties.json", "cubes/world00.json");
        buildWorld();
    }


    private void buildWorld() {
        for (MarchingCubeChunk chunk : world.getChunks()) {
            IEntity entity = es.entity("chunk-(" + chunk.getOffset() + ")")
                    .component(
                            new Transformation().position(chunk.getOffset()),
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
    }

    @Override
    public void keyPress(Window window, int key, int scancode, int mods) {
        if (key == GLFW_KEY_R) {
            wireframe = !wireframe;
        }
        if (key == GLFW_KEY_F5) {
            textureLoader.screenshot();
        }
    }

    @Override
    public void keyRelease(Window window, int key, int scancode, int mods) {

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

    public MarchingCubeWorld getWorld() {
        return world;
    }

    public RaycastResult getRaycast() {
        return raycast;
    }

    public void setRaycast(RaycastResult raycast) {
        this.raycast = raycast;
    }
}

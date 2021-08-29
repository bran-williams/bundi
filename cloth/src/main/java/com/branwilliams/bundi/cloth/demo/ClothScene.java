package com.branwilliams.bundi.cloth.demo;

import com.branwilliams.bundi.cloth.Cloth;
import com.branwilliams.bundi.cloth.ClothPhysicsParameters;
import com.branwilliams.bundi.cloth.system.ClothUpdateSystem;
import com.branwilliams.bundi.engine.core.AbstractScene;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.material.MaterialFormat;
import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.material.Material;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.skybox.Skybox;
import com.branwilliams.bundi.engine.systems.DebugCameraMoveSystem;
import com.branwilliams.bundi.engine.texture.CubeMapTexture;
import com.branwilliams.bundi.engine.texture.Texture;
import com.branwilliams.bundi.engine.texture.TextureData;
import com.branwilliams.bundi.engine.texture.TextureLoader;
import com.branwilliams.bundi.engine.util.GsonUtils;
import com.branwilliams.bundi.engine.util.IOUtils;
import com.branwilliams.bundi.engine.util.Mathf;
import com.google.gson.Gson;
import org.joml.Spheref;
import org.joml.Vector3f;

import java.io.IOException;
import static org.lwjgl.glfw.GLFW.*;

/**
 * From https://viscomp.alexandra.dk/?p=147
 *
 * @author Brandon
 * @since November 20, 2019
 */
public class ClothScene extends AbstractScene {

    private static final String USA_ALBEDO = "textures/usa.png";

    private static final String CLOTH_ALBEDO = "textures/fabric/fabric_albedo.png";
    private static final String CLOTH_NORMAL = "textures/fabric/fabric_normal.png";

    public static final VertexFormat<?> NORMAL_MAPPED_VERTEX_FORMAT = VertexFormat.POSITION_UV_NORMAL_TANGENT;
    public static final MaterialFormat DIFFUSE_NORMAL_MATERIAL_FORMAT = MaterialFormat.DIFFUSE_NORMAL;

    public static final VertexFormat<?> REGULAR_VERTEX_FORMAT = VertexFormat.POSITION_UV_NORMAL;
    public static final MaterialFormat DIFFUSE_MATERIAL_FORMAT = MaterialFormat.DIFFUSE_SAMPLER2D;

    private TextureLoader textureLoader;

    private Camera camera;

    private Skybox skybox;

    private Environment environment;

    private IEntity cloth;

    private IEntity cloth2;

    private Spheref sphere;

    private boolean wireframe = false;

    private float windSpeed = 0F;

    public ClothScene() {
        super("cloth");
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        super.init(engine, window);

        textureLoader = new TextureLoader(engine.getContext());
        loadEnvironmentFile();

        es.addSystem(new DebugCameraMoveSystem(this, this::getCamera, 0.16F, 16F));
        es.addSystem(new ClothUpdateSystem(this));
        es.initSystems(engine, window);

        Projection worldProjection = new Projection(window, 70, 0.01F, 1000F);
        RenderContext renderContext = new RenderContext(worldProjection);

        RenderPipeline<RenderContext> renderPipeline = new ClothRenderPipeline(this, renderContext);
        ClothRenderer renderer = new ClothRenderer(this, renderPipeline);
        setRenderer(renderer);
    }

    @Override
    public void play(Engine engine) {
        sphere = new Spheref(0F, -20F, 40F, 10.0F);

        try {
            Cloth cloth = new Cloth(50, 50, new ClothPhysicsParameters(0.01F,
                    0.5F * 0.5F, 15), 32, 32, 2);
            for (int i = 0; i < cloth.getParticleSizeX(); i++) {
                cloth.getParticle(i, 0).setMovable(false);
                cloth.getParticle(i, 1).setMovable(false);
            }

//            // all four corners
//            cloth.getParticle(0, 0).setMovable(false);
//            cloth.getParticle(cloth.getParticleSizeX() - 1, 0).setMovable(false);
//
//            cloth.getParticle(0, cloth.getParticleSizeY() - 1).setMovable(false);
//            cloth.getParticle(cloth.getParticleSizeX() - 1, cloth.getParticleSizeY() - 1).setMovable(false);

            Mesh mesh = new Mesh();
            mesh.setVertexFormat(NORMAL_MAPPED_VERTEX_FORMAT);

            this.cloth = es.entity("cloth").component(
                    new Transformation(),
                    mesh,
                    loadDiffuseNormalMaterial(CLOTH_ALBEDO, CLOTH_NORMAL),
                    cloth
            ).build();

            cloth = new Cloth(35, 25, new ClothPhysicsParameters(0.01F,
                    0.5F * 0.5F, 15), 32, 32, 5);
            for (int i = 0; i < cloth.getParticleSizeY(); i++) {
                cloth.getParticle(0, i).setMovable(false);
                cloth.getParticle(1, i).setMovable(false);
                cloth.getParticle(2, i).setMovable(false);
            }

            mesh = new Mesh();
            mesh.setVertexFormat(REGULAR_VERTEX_FORMAT);

            this.cloth2 = es.entity("cloth2").component(
                    new Transformation().position(0, 0, 50),
                    mesh,
                    loadDiffuseMaterial(USA_ALBEDO),
                    cloth
            ).build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        camera = new Camera();
        camera.setPosition(40, 20, 80);
        try {
            CubeMapTexture skyboxTexture = textureLoader.loadCubeMapTexture("assets/ame_siege.csv");
            skybox = new Skybox(500, new Material(skyboxTexture));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pause(Engine engine) {

    }

    @Override
    public void fixedUpdate(Engine engine, double deltaTime) {
        super.fixedUpdate(engine, deltaTime);

        wireframe = engine.getWindow().isKeyPressed(GLFW_KEY_R);

        float sphereSpeed = 16F * (float) deltaTime;
        if (engine.getWindow().isKeyPressed(GLFW_KEY_UP))
            sphere.z -= sphereSpeed;

        if (engine.getWindow().isKeyPressed(GLFW_KEY_DOWN))
            sphere.z += sphereSpeed;

        if (engine.getWindow().isKeyPressed(GLFW_KEY_LEFT))
            sphere.x -= sphereSpeed;

        if (engine.getWindow().isKeyPressed(GLFW_KEY_RIGHT))
            sphere.x += sphereSpeed;

        if (engine.getWindow().isKeyPressed(GLFW_KEY_E))
            sphere.y += sphereSpeed;

        if (engine.getWindow().isKeyPressed(GLFW_KEY_Q))
            sphere.y -= sphereSpeed;

        if (engine.getWindow().isKeyPressed(GLFW_KEY_T))
            windSpeed += 0.01F;
        else
            windSpeed = Math.max(0F, windSpeed - 0.01F);

        Vector3f facing = camera.getFacingDirection();
        float windX = facing.x;
        float windY = Mathf.sin(engine.getTime()) * 0.25F;
        float windZ = facing.z;

        Spheref cameraSphere = new Spheref(camera.getPosition(), 5);

        Vector3f clothPos = this.cloth.getComponent(Transformable.class).getPosition();
        Cloth cloth = this.cloth.getComponent(Cloth.class);
        cloth.addForce(new Vector3f(0F, -0.2F, 0F)); // add gravity each frame, pointing down
        cloth.addWindForce(new Vector3f(windX * windSpeed, windY * windSpeed, windZ * windSpeed)); // generate some wind each frame
        cloth.collideWithSphere(clothPos, new Vector3f(sphere.x, sphere.y, sphere.z), sphere.r);
        cloth.collideWithSphere(clothPos, new Vector3f(cameraSphere.x, cameraSphere.y, cameraSphere.z), cameraSphere.r);

        clothPos = this.cloth2.getComponent(Transformable.class).getPosition();
        cloth = this.cloth2.getComponent(Cloth.class);
        cloth.addForce(new Vector3f(0F, -0.2F, 0F)); // add gravity each frame, pointing down
        cloth.addWindForce(new Vector3f(windX * windSpeed, windY * windSpeed, windZ * windSpeed)); // generate some wind each frame
        cloth.collideWithSphere(clothPos, new Vector3f(sphere.x, sphere.y, sphere.z), sphere.r);
        cloth.collideWithSphere(clothPos, new Vector3f(cameraSphere.x, cameraSphere.y, cameraSphere.z), cameraSphere.r);

        engine.getWindow().setTitle("Cloth FPS=" + engine.getFrames() + " windSpeed=" + windSpeed);
    }

    private Material loadDiffuseMaterial(String diffuse) throws IOException {
        TextureData textureData = textureLoader.loadTexture(diffuse);
        Texture diffuseTexture = new Texture(textureData, true);
        diffuseTexture.bind().linearFilter(true).clampToEdges();
        Texture.unbind(diffuseTexture);

        Material material = new Material(diffuseTexture);
        material.setMaterialFormat(DIFFUSE_MATERIAL_FORMAT);
        return material;
    }

    private Material loadDiffuseNormalMaterial(String diffuse, String normal) throws IOException {
        TextureData textureData = textureLoader.loadTexture(normal);
        Texture normalTexture = new Texture(textureData, true);
        normalTexture.bind().linearFilter(true).clampToEdges();
        Texture.unbind(normalTexture);

        Material material = loadDiffuseMaterial(diffuse);
        material.setTexture(1, normalTexture);
        material.setMaterialFormat(DIFFUSE_NORMAL_MATERIAL_FORMAT);

        return material;
    }

    private void loadEnvironmentFile() {
        Gson gson = GsonUtils.defaultGson();
        this.environment = gson.fromJson(IOUtils.readResource("environment.json", null),
                Environment.class);
    }

    public boolean isWireframe() {
        return wireframe;
    }

    public Spheref getSphere() {
        return sphere;
    }

    public Camera getCamera() {
        return camera;
    }

    public Skybox getSkybox() {
        return skybox;
    }

    public Environment getEnvironment() {
        return environment;
    }
}

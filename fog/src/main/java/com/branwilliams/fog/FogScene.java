package com.branwilliams.fog;

import com.branwilliams.bundi.engine.core.AbstractScene;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.deserializers.Vector2fDeserializer;
import com.branwilliams.bundi.engine.deserializers.Vector3fDeserializer;
import com.branwilliams.bundi.engine.deserializers.Vector4fDeserializer;
import com.branwilliams.bundi.engine.material.Material;
import com.branwilliams.bundi.engine.material.MaterialFormat;
import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.mesh.primitive.CubeMesh;
import com.branwilliams.bundi.engine.mesh.primitive.SphereMesh;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.systems.DebugCameraMoveSystem;
import com.branwilliams.bundi.engine.texture.*;
import com.branwilliams.bundi.engine.util.GsonUtils;
import com.branwilliams.bundi.engine.util.IOUtils;
import com.branwilliams.bundi.gui.screen.GuiScreenManager;
import com.branwilliams.fog.pipeline.FogRenderPipeline;
import com.branwilliams.fog.systems.RotationAnimationSystem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.branwilliams.bundi.engine.util.ColorUtils.toVector4;
import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Brandon
 * @since November 30, 2019
 */
public class FogScene extends AbstractScene {

    // blueish
    private static final Vector4f SKY_COLOR = toVector4(new Color(0xFF87CEEB));

    // yellowish
    private static final  Vector4f SUN_COLOR = toVector4(new Color(0xFFFFE5B2));

    private static final String ENVIRONMENT_FILE = "environment.json";

    private static final String UI_INGAME_HUD = "ui/fog-ingame-hud.xml";

    private static final String MODEL_LOCATION = "models/cartoonland2/cartoonland2.obj";
    private static final String MODEL_TEXTURES = "models/cartoonland2/";

    private final GuiScreenManager<FogScene> guiScreenManager = new GuiScreenManager<>(this);;

    private Vector3f cameraStartingPosition = new Vector3f(-2, 0, 0);

    private Vector3f cameraLookAt = new Vector3f();

    private Camera camera;

    private boolean wireframe;

    private Atmosphere atmosphere;

    private Mesh skydome;

    private boolean movingSun;

    private float floorSize = 50F;

//    private PointLight playerLight = null;
    private PointLight playerLight = new PointLight(new Vector3f(cameraStartingPosition),
            new Vector3f(0),
            new Vector3f(0.3f),
            new Vector3f(0.3F));

    private Environment environment;

    public FogScene() {
        super("fog");
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {

        loadEnvironmentFile();
        loadIngameUI(engine, window);

        es.addSystem(new RotationAnimationSystem());
        es.addSystem(new DebugCameraMoveSystem(this, this::getCamera, 0.16F, 6F));
        es.initSystems(engine, window);

        Projection worldProjection = new Projection(window, 70, 0.01F, 1000F);
        RenderContext renderContext = new RenderContext(worldProjection);

        RenderPipeline<RenderContext> renderPipeline = new FogRenderPipeline(renderContext, this);
        FogRenderer<RenderContext> renderer = new FogRenderer<>(this, renderPipeline);
        setRenderer(renderer);
    }

    @Override
    public void play(Engine engine) {
        TextureLoader textureLoader = new TextureLoader(engine.getContext());

        camera = new Camera();
        camera.setPosition(cameraStartingPosition);
        camera.lookAt(cameraLookAt);

        DirectionalLight sun = getSun();
        Fog fog = getFog();

        atmosphere = new Atmosphere(sun, SKY_COLOR, SUN_COLOR, fog);
        skydome = new SphereMesh(300, 90, 90, VertexFormat.POSITION, true);

//        try {
//            CubeMapTexture skyboxTexture = textureLoader.loadCubeMapTexture("assets/one.csv");
//            skybox = new Skybox(500, new Material(skyboxTexture));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        CubeMesh cubeMesh = new CubeMesh(1, 1, 1, VertexFormat.POSITION_UV_NORMAL);
        CubeMesh cubeMeshWithTangents = new CubeMesh(1, 1, 1, 2F, VertexFormat.POSITION_UV_NORMAL_TANGENT);
        CubeMesh cubeFloor0 = new CubeMesh(50, 0.05F, 50, 24F,
                VertexFormat.POSITION_UV_NORMAL_TANGENT);

        Material material0 = new Material(MaterialFormat.DIFFUSE_VEC4_SPECULAR_VEC4);
        material0.setProperty("specular", toVector4(new Color(0xFFFFFFFF)));
        material0.setProperty("diffuse", toVector4(new Color(0xFF021eDD)));

        Material material1 = new Material(MaterialFormat.DIFFUSE_VEC4_SPECULAR_VEC4);
        material1.setProperty("specular", toVector4(new Color(0xFFFFFFFF)));
        material1.setProperty("diffuse", toVector4(new Color(0xFFffb800)));

        Material material2 = new Material(MaterialFormat.DIFFUSE_VEC4_SPECULAR_VEC4);
        material2.setProperty("specular", toVector4(new Color(0xFFFFFFFF)));
        material2.setProperty("diffuse", toVector4(new Color(0xFFAE1500)));

        Material material4 = new Material(MaterialFormat.DIFFUSE_VEC4_SPECULAR_VEC4);
        material4.setProperty("specular", toVector4(new Color(0xFFFFFFFF)));
        material4.setProperty("diffuse", toVector4(new Color(0xFF007700)));

        es.entity("cube0").component(
                cubeMesh,
                material0,
                new Transformation().position(-8, 2, -15).scale(4)
        ).build();

        es.entity("cube2").component(
                cubeMesh,
                material1,
                new Transformation().position(4, 1, -12F).scale(2)
        ).build();

        es.entity("cube3").component(
                cubeMesh,
                material2,
                new Transformation().position(10, 3, -4F).scale(3)
        ).build();

        Material floorMaterial = createDiffuseNormalSpecularMaterial(textureLoader,
                "textures/logl/floor_diffuse.jpg",
                "textures/logl/floor_normal.jpg",
                "textures/logl/floor_specular.jpg");

        Material boxMaterial = createDiffuseNormalSpecularMaterial(textureLoader,
                "textures/wooden_deck/texture_diffuse.jpg",
                "textures/wooden_deck/texture_normal.jpg",
                "textures/wooden_deck/texture_specular.jpg");

        es.entity("floor0").component(
                cubeFloor0,
                floorMaterial,
                new Transformation().position(0, -2, 0)
        ).build();

        es.entity("cube5").component(
                cubeMeshWithTangents,
                new Transformation().position(-8, 2, 15F).scale(4),
                boxMaterial
        ).build();

        es.entity("cube6").component(
                cubeMeshWithTangents,
                new Transformation().position(4, 1, 12F).scale(2),
                boxMaterial
        ).build();

        es.entity("cube7").component(
                cubeMeshWithTangents,
                new Transformation().position(10, 3, 4F).scale(3),
                boxMaterial
        ).build();

        es.entity("cube8").component(
                cubeMeshWithTangents,
                new Transformation().position(-10, 2, 20 * 0.5F).scale(5),
                boxMaterial,
                new RotationAnimation(new Vector3f(1F, 0F, 0F), 0.3F)
        ).build();
    }

    @Override
    public void pause(Engine engine) {

    }

    @Override
    public void update(Engine engine, double deltaTime) {
        super.update(engine, deltaTime);
        this.guiScreenManager.update();

        if (movingSun && this.getSun() != null) {
            this.getSun().setDirection(this.camera.getFacingDirection().negate().normalize());
        }

        if (this.getPlayerPointLight() != null) {
            this.getPlayerPointLight().setPosition(camera.getPosition());
        }
    }

    @Override
    public void fixedUpdate(Engine engine, double deltaTime) {
        super.fixedUpdate(engine, deltaTime);
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void keyPress(Window window, int key, int scancode, int mods) {
        super.keyPress(window, key, scancode, mods);
        if (key == GLFW_KEY_G) {
            wireframe = !wireframe;
        }

        if (key == GLFW_KEY_H) {
            loadEnvironmentFile();
        }
    }

    @Override
    public void press(Window window, float mouseX, float mouseY, int buttonId) {
        super.press(window, mouseX, mouseY, buttonId);
        if ((window.isKeyPressed(GLFW_KEY_LEFT_CONTROL) || window.isKeyPressed(GLFW_KEY_RIGHT_CONTROL))
                && buttonId == GLFW_MOUSE_BUTTON_1) {
            movingSun = true;
        }

    }

    @Override
    public void release(Window window, float mouseX, float mouseY, int buttonId) {
        super.release(window, mouseX, mouseY, buttonId);
        movingSun = false;
    }

    private void loadIngameUI(Engine engine, Window window) {
        Map<String, Object> env = new HashMap<>();
        env.put("move_sun_controls", "Ctrl + Left click");
        env.put("wireframe_controls", "G");
        env.put("reloadenv_controls", "H");

        this.guiScreenManager.init(engine, window);
        this.guiScreenManager.loadAsGuiScreen(UI_INGAME_HUD, env);
    }

    private void loadEnvironmentFile() {
        Gson gson = GsonUtils.defaultGson();
        this.environment = gson.fromJson(IOUtils.readFile(ENVIRONMENT_FILE, null),
                Environment.class);
        this.environment.getPointLights()[0] = playerLight;
    }

    public Material createDiffuseSpecularMaterial(TextureLoader textureLoader, String diffusePath, String specularPath) {
        Material material = null;
        try {
            TextureData textureData = textureLoader.loadTexture(diffusePath);
            Texture diffuse = new Texture(textureData, true);

            textureData = textureLoader.loadTexture(specularPath);
            Texture specular = new Texture(textureData, true);

            material = new Material(MaterialFormat.DIFFUSE_SPECULAR);
            material.setTexture(0, diffuse);
            material.setTexture(1, specular);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return material;
    }

    public Material createDiffuseNormalSpecularMaterial(TextureLoader textureLoader, String diffusePath,
                                                        String normalPath, String specularPath) {
        Material material = null;
        try {
            TextureData textureData = textureLoader.loadTexture(diffusePath);
            Texture diffuse = new Texture(textureData, true);

            textureData = textureLoader.loadTexture(normalPath);
            Texture normal = new Texture(textureData, true);

            textureData = textureLoader.loadTexture(specularPath);
            Texture specular = new Texture(textureData, true);

            material = new Material(MaterialFormat.DIFFUSE_NORMAL_SPECULAR);
            material.setTexture(0, diffuse);
            material.setTexture(1, normal);
            material.setTexture(2, specular);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return material;
    }
    public Environment getEnvironment() {
        return environment;
    }

    public Camera getCamera() {
        return camera;
    }

    public boolean isWireframe() {
        return wireframe;
    }

    public Atmosphere getAtmosphere() {
        return atmosphere;
    }

    public Fog getFog() {
        return environment.getFog();
    }

    public Mesh getSkydome() {
        return skydome;
    }

    public GuiScreenManager getGuiScreenManager() {
        return guiScreenManager;
    }

    public boolean hasSun() {
        return environment.hasDirectionalLights();
    }

    public DirectionalLight getSun() {
        return hasSun() ? environment.getDirectionalLights()[0] : null;
    }

    public PointLight getPlayerPointLight() {
        return playerLight;
    }

}

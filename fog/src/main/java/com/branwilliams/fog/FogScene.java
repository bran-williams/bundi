package com.branwilliams.fog;

import com.branwilliams.bundi.engine.core.AbstractScene;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.material.Material;
import com.branwilliams.bundi.engine.material.MaterialElement;
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
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.bundi.gui.pipeline.GuiRenderPass;
import com.branwilliams.bundi.gui.screen.GuiScreenManager;
import com.branwilliams.fog.pipeline.FogRenderPipeline;
import com.branwilliams.fog.systems.ParticleUpdateSystem;
import com.branwilliams.fog.systems.RotationAnimationSystem;
import com.google.gson.Gson;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

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

    private final GuiScreenManager<FogScene> guiScreenManager = new GuiScreenManager<>(this);

    private Vector3f cameraStartingPosition = new Vector3f(-2, 0, 0);

    private Vector3f cameraLookAt = new Vector3f();

    private Camera camera;

    private boolean wireframe;

    private Atmosphere atmosphere;

    private Mesh skydome;

    private boolean movingSun;

    private final float floorSize = 50F;

//    private PointLight playerLight = null;
    private PointLight playerLight = new PointLight(new Vector3f(cameraStartingPosition),
            new Vector3f(0),
            new Vector3f(1f),
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
        es.addSystem(new ParticleUpdateSystem());
        es.initSystems(engine, window);

        Projection worldProjection = new Projection(window, 70, 0.01F, 1000F);
        RenderContext renderContext = new RenderContext(worldProjection);

        RenderPipeline<RenderContext> renderPipeline = new FogRenderPipeline(renderContext, this,
                this::isWireframe, this::getCamera, this::getEnvironment, this::getAtmosphere, this::getSkydome);
        renderPipeline.addLast(new GuiRenderPass<>(this, this::getGuiScreenManager));
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

        atmosphere = new Atmosphere(sun, FogConstants.SKY_COLOR, FogConstants.SUN_COLOR, fog);
        skydome = new SphereMesh(300, 90, 90, VertexFormat.POSITION, true);
//        try {
//            CubeMapTexture skyboxTexture = textureLoader.loadCubeMapTexture("assets/one.csv");
//            skybox = new Skybox(500, new Material(skyboxTexture));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        ParticleEmitter smokeEmitter = new ParticleEmitter(100, 260,
                loadTextures(textureLoader, FogConstants.SMOKE_PARTICLES));
        smokeEmitter.setVelocity(new Vector3f(0.25F));
        smokeEmitter.setDispurseAmount(new Vector3f(4F, 1F, 4F));
        smokeEmitter.setGravity(7F);
        smokeEmitter.setScale(4F);
        smokeEmitter.respawnParticles();

        es.entity("smokeParticles").component(
                new Transformation().position(-10, -4, 0),
                smokeEmitter
        ).build();

        CubeMesh cubeMesh = new CubeMesh(1, 1, 1, VertexFormat.POSITION_UV_NORMAL);
        CubeMesh cubeMeshWithTangents = new CubeMesh(1, 1, 1, 2F,
                VertexFormat.POSITION_UV_NORMAL_TANGENT);


        CubeMesh cubeFloor0 = new CubeMesh(50, 0.05F, 50, 16F,
                VertexFormat.POSITION_UV_NORMAL);
        CubeMesh cubeRoof0 = new CubeMesh(50, 0.05F, 50, 16F,
                VertexFormat.POSITION_UV_NORMAL);
        CubeMesh cubeWall0 = new CubeMesh(50, 10F, 0.05F, 16F,
                VertexFormat.POSITION_UV_NORMAL);
        CubeMesh cubeWall1 = new CubeMesh(0.05F, 10F, 50F, 16F,
                VertexFormat.POSITION_UV_NORMAL);

        Material material0 = new Material(MaterialFormat.DIFFUSE_VEC4_SPECULAR_VEC4);
        material0.setProperty(MaterialElement.SPECULAR, toVector4(new Color(0xFF999999)));
        material0.setProperty(MaterialElement.DIFFUSE, toVector4(new Color(0xFF021eDD)));

        Material material1 = new Material(MaterialFormat.DIFFUSE_VEC4_SPECULAR_VEC4);
        material1.setProperty(MaterialElement.SPECULAR, toVector4(new Color(0xFF999999)));
        material1.setProperty(MaterialElement.DIFFUSE, toVector4(new Color(0xFFffb800)));

        Material material2 = new Material(MaterialFormat.DIFFUSE_VEC4_SPECULAR_VEC4);
        material2.setProperty(MaterialElement.SPECULAR, toVector4(new Color(0xFF999999)));
        material2.setProperty(MaterialElement.DIFFUSE, toVector4(new Color(0xFFAE1500)));

        Material material4 = new Material(MaterialFormat.DIFFUSE_VEC4_SPECULAR_VEC4);
        material4.setProperty(MaterialElement.SPECULAR, toVector4(new Color(0xFF999999)));
        material4.setProperty(MaterialElement.DIFFUSE, toVector4(new Color(0xFF007700)));

        Material material5 = new Material(MaterialFormat.DIFFUSE_VEC4_SPECULAR_VEC4);
        material5.setProperty(MaterialElement.SPECULAR, toVector4(new Color(0xFF999999)));
        material5.setProperty(MaterialElement.DIFFUSE, toVector4(new Color(0xFFFFFFFF)));

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
                cubeMeshWithTangents,
                createDiffuseNormalSpecularMaterial(textureLoader,
                        "textures/wooden_deck/texture_diffuse.jpg",
                        "textures/wooden_deck/texture_normal.jpg",
                        "textures/wooden_deck/texture_specular.jpg"),
                new Transformation().position(10, 3, -4F).scale(3)
        ).build();

        Material boxMaterial = createDiffuseNormalMaterial(textureLoader,
                "textures/wooden_deck/texture_diffuse.jpg",
                "textures/wooden_deck/texture_normal.jpg");

        Material floorMaterial = createDiffuseMaterial(textureLoader,
                "textures/dark/texture_01.png");

        Material roofMaterial = createDiffuseMaterial(textureLoader,
                "textures/dark/texture_08.png");

        Material wallMaterial = createDiffuseMaterial(textureLoader,
                "textures/dark/texture_13.png");

        es.entity("floor0").component(
                cubeFloor0,
                floorMaterial,
                new Transformation().position(0, -2, 0)
        ).build();

        es.entity("roof0").component(
                cubeRoof0,
                roofMaterial,
                new Transformation().position(0, 18, 0)
        ).build();

        es.entity("wall0").component(
                cubeWall0,
                material5,
                new Transformation().position(0, 8, 50)
        ).build();
        es.entity("wall1").component(
                cubeWall0,
                material5,
                new Transformation().position(0, 8, -50)
        ).build();

        es.entity("wall2").component(
                cubeWall1,
                material5,
                new Transformation().position(50, 8, 0)
        ).build();

        es.entity("wall3").component(
                cubeWall1,
                material5,
                new Transformation().position(-50, 8, 0)
        ).build();

        es.entity("cube5").component(
                cubeMeshWithTangents,
                new Transformation().position(-8, 2, 15).scale(4),
                boxMaterial
        ).build();

        es.entity("cube6").component(
                cubeMeshWithTangents,
                new Transformation().position(4, 1, 12).scale(2),
                boxMaterial
        ).build();

        es.entity("cube7").component(
                cubeMeshWithTangents,
                new Transformation().position(10, 3, 4).scale(3),
                boxMaterial
        ).build();

        Material rotatingCubeMaterial = createDiffuseSpecularEmissiveMaterial(textureLoader,
                "textures/logl/box_diffuse.png",
                "textures/logl/box_specular.png",
                "textures/logl/matrix.png");


        es.entity("rotatingCube").component(
                new CubeMesh(1, 1, 1, 1F, VertexFormat.POSITION_UV_NORMAL_TANGENT),
                new Transformation().position(0, 2, 0).scale(3),
                rotatingCubeMaterial,
                new RotationAnimation(Mathf.randomAxisAngle(), 0.3F)
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

        if (key == GLFW_KEY_J) {
            es.getEntity("rotatingCube").getComponent(RotationAnimation.class)
                    .setAxisAngle(Mathf.randomAxisAngle());
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

    private Texture[] loadTextures(TextureLoader textureLoader, String[] images) {
        Texture[] textures = new Texture[images.length];
        try {
            for (int i = 0; i < images.length; i++) {
                TextureData textureData = textureLoader.loadTexture(images[i]);
                textures[i] = new Texture(textureData, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return textures;
    }

    private void loadIngameUI(Engine engine, Window window) {
        Map<String, Object> env = new HashMap<>();
        env.put("move_sun_controls", "Ctrl + Left click");
        env.put("wireframe_controls", "G");
        env.put("reload_env_controls", "H");
        env.put("rotate_cube_controls", "J");

        this.guiScreenManager.init(engine, window);
        this.guiScreenManager.loadAsGuiScreen(guiScreenManager.loadFromResources(FogConstants.UI_INGAME_HUD, env));
    }

    private void loadEnvironmentFile() {
        Gson gson = GsonUtils.defaultGson();
        this.environment = gson.fromJson(IOUtils.readResource(FogConstants.ENVIRONMENT_FILE, null),
                Environment.class);
        this.environment.getPointLights()[0] = playerLight;
    }


    public Material createDiffuseMaterial(TextureLoader textureLoader, String diffusePath) {
        Material material = null;
        try {
            TextureData textureData = textureLoader.loadTexture(diffusePath);
            Texture diffuse = new Texture(textureData, true);

            material = new Material(MaterialFormat.DIFFUSE_SAMPLER2D);
            material.setTexture(0, diffuse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return material;
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

    public Material createDiffuseNormalMaterial(TextureLoader textureLoader, String diffusePath,
                                                        String normalPath) {
        Material material = null;
        try {
            TextureData textureData = textureLoader.loadTexture(diffusePath);
            Texture diffuse = new Texture(textureData, true);

            textureData = textureLoader.loadTexture(normalPath);
            Texture normal = new Texture(textureData, true);

            material = new Material(MaterialFormat.DIFFUSE_NORMAL);
            material.setTexture(0, diffuse);
            material.setTexture(1, normal);
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


    public Material createDiffuseSpecularEmissiveMaterial(TextureLoader textureLoader, String diffusePath,
                                                          String specularPath, String emissivePath) {
        Material material = null;
        try {
            TextureData textureData = textureLoader.loadTexture(diffusePath);
            Texture diffuse = new Texture(textureData, true);

            textureData = textureLoader.loadTexture(specularPath);
            Texture specular = new Texture(textureData, true);

            textureData = textureLoader.loadTexture(emissivePath);
            Texture emissive = new Texture(textureData, true);

            material = new Material(MaterialFormat.DIFFUSE_SPECULAR_EMISSIVE);
            material.setTexture(0, diffuse);
            material.setTexture(1, specular);
            material.setTexture(2, emissive);

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

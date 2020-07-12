package com.branwilliams.fog;

import com.branwilliams.bundi.engine.core.AbstractScene;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.screenshot.ScreenshotCapturer;
import com.branwilliams.bundi.engine.core.window.MouseListener;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.core.pipeline.passes.DisableWireframeRenderPass;
import com.branwilliams.bundi.engine.core.pipeline.passes.EnableWireframeRenderPass;
import com.branwilliams.bundi.engine.material.Material;
import com.branwilliams.bundi.engine.material.MaterialFormat;
import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.mesh.primitive.CubeMesh;
import com.branwilliams.bundi.engine.mesh.primitive.PlaneMesh;
import com.branwilliams.bundi.engine.mesh.primitive.SphereMesh;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.systems.DebugCameraMoveSystem;
import com.branwilliams.bundi.engine.texture.*;
import com.branwilliams.bundi.gui.pipeline.GuiRenderPass;
import com.branwilliams.bundi.gui.screen.GuiScreenManager;
import com.branwilliams.fog.pipeline.passes.AtmosphereRenderPass;
import com.branwilliams.fog.pipeline.passes.TemplateRenderPass;
import com.branwilliams.fog.systems.RotationAnimationSystem;
import org.joml.Planef;
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

    private static final String UI_INGAME_HUD = "ui/fog-ingame-hud.xml";

    private static final Map<String, Object> UI_INGAME_HUD_ENVIRONMENT = createUIEnvironment();

    private static Map<String, Object> createUIEnvironment() {
        Map<String, Object> env = new HashMap<>();
        env.put("move_sun_controls", "Ctrl + Left click");
        return env;
    }

    private final String modelLocation = "models/cartoonland2/cartoonland2.obj";
    private final String modelTextures = "models/cartoonland2/";

    private final GuiScreenManager guiScreenManager;

    private Vector3f cameraStartingPosition = new Vector3f();

    private Vector3f cameraLookAt = new Vector3f();

    private Camera camera;

    private boolean wireframe;

    private Atmosphere atmosphere;

    private Mesh skydome;

//    private Skybox skybox;

    private DirectionalLight sun;

    private boolean movingSun;

    public FogScene() {
        super("fog");
        this.guiScreenManager = new GuiScreenManager(this);
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {

        this.guiScreenManager.init(engine, window);
        this.guiScreenManager.loadAsGuiScreen(UI_INGAME_HUD, UI_INGAME_HUD_ENVIRONMENT);

        es.addSystem(new RotationAnimationSystem());
        es.addSystem(new DebugCameraMoveSystem(this, this::getCamera, 0.16F, 16F));
        es.initSystems(engine, window);

        Projection worldProjection = new Projection(window, 90, 0.01F, 1000F);
        RenderContext renderContext = new RenderContext(worldProjection);

        RenderPipeline<RenderContext> renderPipeline = new RenderPipeline<>(renderContext);
        renderPipeline.addLast(new EnableWireframeRenderPass(this::isWireframe));
        renderPipeline.addLast(new AtmosphereRenderPass<>(this::getCamera, this::getAtmosphere, this::getSkydome));
//        renderPipeline.addLast(new CubesRenderPass(this, this::getCamera, this::getFog));
        renderPipeline.addLast(new TemplateRenderPass(this, this::getCamera, this::getFog, this::getSun,
                VertexFormat.POSITION_UV_NORMAL, MaterialFormat.DIFFUSE_SPECULAR));
        renderPipeline.addLast(new TemplateRenderPass(this, this::getCamera, this::getFog, this::getSun,
                VertexFormat.POSITION_UV_NORMAL, MaterialFormat.DIFFUSE_VEC4));
//        renderPipeline.addLast(new TemplateModelRenderPass(this, this::getCamera, this::getFog, this::getSun));

        renderPipeline.addLast(new DisableWireframeRenderPass(this::isWireframe));
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

        Vector3f planeFacingDirection = new Vector3f(1F, 0F, 0f);


        // blueish
        Vector4f skyColor = toVector4(new Color(0xFF87CEEB));
//        skyColor = toVector4(new Color(0xFFBFBFBF));

        // yellowish
        Vector4f sunColor = toVector4(new Color(0xFFFFE5B2)); // new Vector4f(1.0F, 0.9F, 0.7F, 1.0F);

        sun = new DirectionalLight(
                new Vector3f(0F, -1.0F, 0F), // direction
                new Vector3f(0.3F),                    // ambient
                new Vector3f(sunColor.x, sunColor.y, sunColor.z),                    // diffuse
                new Vector3f(sunColor.x, sunColor.y, sunColor.z));                   // specular


        // TODO flesh out the fog component.. make this part of the atmosphere module.
        Fog fog = new Fog(0.005F, skyColor);
        atmosphere = new Atmosphere(sun, skyColor, sunColor, fog);

        skydome = new SphereMesh(250, 90, 90, VertexFormat.POSITION, true);

//        try {
//            CubeMapTexture skyboxTexture = textureLoader.loadCubeMapTexture("assets/one.csv");
//            skybox = new Skybox(500, new Material(skyboxTexture));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        CubeMesh cubeMesh0 = new CubeMesh(1, 1, 1, VertexFormat.POSITION_UV_NORMAL);

        PlaneMesh planeMesh0 = new PlaneMesh(VertexFormat.POSITION_UV_NORMAL,
                new Planef(planeFacingDirection.x, planeFacingDirection.y, planeFacingDirection.z, 1F),
                10, 5);

        Material material0 = new Material(MaterialFormat.DIFFUSE_VEC4);
        material0.setProperty("diffuse", toVector4(new Color(0x021e44)));

        Material material1 = new Material(MaterialFormat.DIFFUSE_VEC4);
        material1.setProperty("diffuse", toVector4(new Color(0xffb800)));

        Material material2 = new Material(MaterialFormat.DIFFUSE_VEC4);
        material2.setProperty("diffuse", toVector4(new Color(0x04e1500)));

        Material material4 = new Material(MaterialFormat.DIFFUSE_VEC4);
        material4.setProperty("diffuse", toVector4(new Color(0x0007700)));

        es.entity("plane0").component(
                planeMesh0,
                material4,
                new Transformation().position(0, 0, 0)
        ).build();

        es.entity("cube0").component(
                cubeMesh0,
                material0,
                new Transformation().position(-20, 2, -40 * 0.5F).scale(5)
        ).build();

        es.entity("cube2").component(
                cubeMesh0,
                material1,
                new Transformation().position(0, 1, -40 * 0.35F).scale(2)
        ).build();

        es.entity("cube3").component(
                cubeMesh0,
                material2,
                new Transformation().position(10, 3, -40 * 0.7F).scale(3)
        ).build();

        Material boxMaterial = createDiffuseSpecularMaterial(textureLoader,
                "textures/logl/box_diffuse.png",
                "textures/logl/box_specular.png");

        Material floorMaterial = createDiffuseSpecularMaterial(textureLoader,
                "textures/logl/floor_diffuse.jpg",
                "textures/logl/floor_specular.jpg");

        PlaneMesh floorMesh = new PlaneMesh(VertexFormat.POSITION_UV_NORMAL,
                new Planef(0F, 1F, 0F, 1F), 6, 6);

        planeFacingDirection.reflect(PlaneMesh.UP);

        PlaneMesh planeMesh1 = new PlaneMesh(VertexFormat.POSITION_UV_NORMAL,
                new Planef(-planeFacingDirection.x,
                        -planeFacingDirection.y,
                        -planeFacingDirection.z, 1F), 10, 5);

        es.entity("plane1").component(
                planeMesh1,
                new Transformation().position(0, 0, 0),
                boxMaterial
        ).build();

        es.entity("floor0").component(
                floorMesh,
                new Transformation().position(0, 0, 0),
                floorMaterial
        ).build();

        es.entity("cube5").component(
                cubeMesh0,
                new Transformation().position(-20, 2, 40 * 0.5F).scale(5),
                boxMaterial
        ).build();

        es.entity("cube6").component(
                cubeMesh0,
                new Transformation().position(0, 1, 40 * 0.35F).scale(2),
                boxMaterial
        ).build();

        es.entity("cube7").component(
                cubeMesh0,
                new Transformation().position(10, 3, 40 * 0.7F).scale(3),
                boxMaterial
        ).build();

        es.entity("cube8").component(
                cubeMesh0,
                new Transformation().position(-10, 2, 20 * 0.5F).scale(5),
                boxMaterial,
                new RotationAnimation(new Vector3f(1F, 0F, 0F), 0.3F)
        ).build();


//        ModelLoader modelLoader = new ModelLoader(engine.getContext(), textureLoader);
//        try {
//            Model model0 = modelLoader.load(modelLocation, modelTextures, VertexFormat.POSITION_UV_NORMAL);
//
//            es.entity("model0").component(
//                    model0,
//                    new Transformation().position(0F, 0F, 0F).scale(7),
//                    70F
//            ).build();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void pause(Engine engine) {

    }

    @Override
    public void update(Engine engine, double deltaTime) {
        super.update(engine, deltaTime);
        this.guiScreenManager.update();
        if (movingSun) {
            this.sun.setDirection(this.camera.getFacingDirection().negate().normalize());
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
        return atmosphere.getFog();
    }

    public Mesh getSkydome() {
        return skydome;
    }

    public GuiScreenManager getGuiScreenManager() {
        return guiScreenManager;
    }

    public DirectionalLight getSun() {
        return sun;
    }
}

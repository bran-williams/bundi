package com.branwilliams.bundi.atmosphere;

import com.branwilliams.bundi.atmosphere.pipeline.passes.AtmosphereRenderPass;
import com.branwilliams.bundi.atmosphere.pipeline.passes.AtmosphereRenderPass2;
import com.branwilliams.bundi.atmosphere.pipeline.passes.ModelRenderPass;
import com.branwilliams.bundi.atmosphere.pipeline.passes.SkydomeRenderPass;
import com.branwilliams.bundi.engine.core.*;
import com.branwilliams.bundi.engine.core.pipeline.passes.DisableWireframeRenderPass;
import com.branwilliams.bundi.engine.core.pipeline.passes.EnableWireframeRenderPass;
import com.branwilliams.bundi.engine.mesh.primitive.SphereMesh;
import com.branwilliams.bundi.engine.model.ModelLoader;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.shader.Camera;
import com.branwilliams.bundi.engine.model.Model;
import com.branwilliams.bundi.engine.shader.Material;
import com.branwilliams.bundi.engine.shader.Projection;
import com.branwilliams.bundi.engine.shader.Transformation;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.systems.DebugCameraMoveSystem;
import com.branwilliams.bundi.engine.systems.LockableSystem;
import com.branwilliams.bundi.engine.texture.Texture;
import com.branwilliams.bundi.engine.texture.TextureLoader;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.bundi.gui.api.ContainerManager;
import com.branwilliams.bundi.gui.api.components.Label;
import com.branwilliams.bundi.gui.api.components.Slider;
import com.branwilliams.bundi.gui.pipeline.GuiRenderPass;
import com.branwilliams.bundi.gui.screen.GuiScreenManager;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

/**
 * Created by Brandon Williams on 9/15/2019.
 */
public class AtmosphereScene extends AbstractScene implements Window.KeyListener {

    private Camera camera;

    private Skydome skydome;

    private Material skydomeMaterial;

    private Vector3f sunPosition;

    private float weather;

    private GuiScreenManager guiScreenManager;

    private final Lock guiLock = new Lock();

    public AtmosphereScene() {
        super("atmosphere");
        this.addKeyListener(this);
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        guiScreenManager = new GuiScreenManager(this);
        guiScreenManager.init(engine, window);

        sunPosition = new Vector3f(-0.0625F, 0.15F, 0F);
        weather = 0.5F;

        es.addSystem(new DebugCameraMoveSystem(this, guiLock, this::getCamera, 0.16F, 1F, true));
        es.initSystems(engine, window);

        Projection worldProjection = new Projection(window, 70, 0.01F, 1000F);
        RenderContext renderContext = new RenderContext(worldProjection);

        RenderPipeline<RenderContext> renderPipeline = new RenderPipeline<>(renderContext);

//        renderPipeline.addLast(new EnableWireframeRenderPass(() -> true));
//        renderPipeline.addLast(new SkydomeRenderPass(this::getCamera, this::getSkydome));
        renderPipeline.addLast(new DebugRenderPass(this::getCamera));
        renderPipeline.addLast(new AtmosphereRenderPass2(this::getSkydomeMaterial, this::getCamera, this::getSkydome,
                this::getSunPosition, this::getWeather));
        renderPipeline.addLast(new GuiRenderPass<>(this, this::getGuiScreenManager));
//        renderPipeline.addLast(new DisableWireframeRenderPass(() -> true));
        AtmosphereRenderer renderer = new AtmosphereRenderer(this, renderPipeline);
        setRenderer(renderer);
        window.disableCursor();
    }

    @Override
    public void play(Engine engine) {
        camera = new Camera();
        camera.lookAt(0, 0, -2);

        SphereMesh skydomeSphere = new SphereMesh(250, 50, 50, VertexFormat.POSITION_NORMAL, true);
        Vector4f apexColor = new Vector4f(0F, 0F, 0.2F, 1F);
        Vector4f centerColor = new Vector4f(0.39F, 0.52F, 0.93F, 1F).mul(0.9F);
        skydome = new Skydome(skydomeSphere, apexColor, centerColor);

        TextureLoader textureLoader = new TextureLoader(engine.getContext());
        try {
            skydomeMaterial = new Material();
            skydomeMaterial.setTexture(0, new Texture(textureLoader.loadTexture("textures/atmosphere/tint.png"), false));
            skydomeMaterial.setTexture(1, new Texture(textureLoader.loadTexture("textures/atmosphere/tint2.png"), false));
            skydomeMaterial.setTexture(2, new Texture(textureLoader.loadTexture("textures/atmosphere/sun.png"), false));
//            skydomeMaterial.setTexture(3, new Texture(textureLoader.loadTexture("textures/atmosphere/moon.png"), false));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadUI() {
        ContainerManager containerManager = guiScreenManager.load("./ui/atmosphere.xml");

        final Label xLabel = containerManager.getByTag("x_slider_value");
        final Label yLabel = containerManager.getByTag("y_slider_value");
        final Label zLabel = containerManager.getByTag("z_slider_value");

        Slider xSlider = containerManager.getByTag("x_slider");
        xLabel.setText(String.format("%.3f", sunPosition.x));
        xSlider.setSliderPercentage((sunPosition.x + 1F) * 0.5F);
        xSlider.onValueChange((slider) -> {
            sunPosition.x = -1F + 2F * (slider.getSliderPercentage());
            sunPosition.normalize();

            xLabel.setText(String.format("%.3f", sunPosition.x));
            yLabel.setText(String.format("%.3f", sunPosition.y));
            zLabel.setText(String.format("%.3f", sunPosition.z));
        });

        Slider ySlider = containerManager.getByTag("y_slider");
        yLabel.setText(String.format("%.3f", sunPosition.y));
        ySlider.setSliderPercentage((sunPosition.y + 1F) * 0.5F);
        ySlider.onValueChange((slider) -> {
//            sunPosition.y = 0.01F + (0.99F * slider.getSliderPercentage());
            sunPosition.y = -1F + 2F * (slider.getSliderPercentage());
            sunPosition.normalize();

            xLabel.setText(String.format("%.3f", sunPosition.x));
            yLabel.setText(String.format("%.3f", sunPosition.y));
            zLabel.setText(String.format("%.3f", sunPosition.z));
        });

        Slider zSlider = containerManager.getByTag("z_slider");
        zLabel.setText(String.format("%.3f", sunPosition.z));
        zSlider.setSliderPercentage((sunPosition.z + 1F) * 0.5F);
        zSlider.onValueChange((slider) -> {
            sunPosition.z = -1F + 2F * (slider.getSliderPercentage());
            sunPosition.normalize();

            xLabel.setText(String.format("%.3f", sunPosition.x));
            yLabel.setText(String.format("%.3f", sunPosition.y));
            zLabel.setText(String.format("%.3f", sunPosition.z));
        });

        final Label weatherLabel = containerManager.getByTag("weather_slider_value");
        Slider weatherSlider = containerManager.getByTag("weather_slider");
        weatherLabel.setText(String.format("%.3f", weather));
        weatherSlider.setSliderPercentage((weather - 0.5F) * 2);
        weatherSlider.onValueChange((slider) -> {
            weather = (0.5F + 0.5F * slider.getSliderPercentage());

            weatherLabel.setText(String.format("%.3f", weather));
        });
    }

    @Override
    public void pause(Engine engine) {

    }

    @Override
    public void update(Engine engine, double deltaTime) {
        super.update(engine, deltaTime);
        if (guiScreenManager.getGuiScreen() != null)
            guiScreenManager.getGuiScreen().update();
    }

    public float getWeather() {
        return weather;
    }

    public Vector3f getSunPosition() {
        return sunPosition;
    }

    public Camera getCamera() {
        return camera;
    }

    public Skydome getSkydome() {
        return skydome;
    }

    public Material getSkydomeMaterial() {
        return skydomeMaterial;
    }

    public GuiScreenManager getGuiScreenManager() {
        return guiScreenManager;
    }

    @Override
    public void keyPress(Window window, int key, int scancode, int mods) {

    }

    @Override
    public void keyRelease(Window window, int key, int scancode, int mods) {
        if (key == GLFW_KEY_ESCAPE) {
            guiLock.toggle();
            if (guiLock.isLocked()) {
                loadUI();
                window.showCursor();
            } else {
                window.disableCursor();
                guiScreenManager.setGuiScreen(null);
            }
        }
    }
}

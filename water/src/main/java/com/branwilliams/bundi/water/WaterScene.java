package com.branwilliams.bundi.water;

import com.branwilliams.bundi.engine.core.AbstractScene;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.core.pipeline.passes.DisableWireframeRenderPass;
import com.branwilliams.bundi.engine.core.pipeline.passes.EnableWireframeRenderPass;
import com.branwilliams.bundi.engine.shader.Camera;
import com.branwilliams.bundi.engine.shader.Material;
import com.branwilliams.bundi.engine.shader.Projection;
import com.branwilliams.bundi.engine.skybox.Skybox;
import com.branwilliams.bundi.engine.skybox.SkyboxRenderPass;
import com.branwilliams.bundi.engine.texture.CubeMapTexture;
import com.branwilliams.bundi.engine.texture.TextureLoader;
import com.branwilliams.bundi.water.pipeline.passes.WaterNormalRenderPass;
import com.branwilliams.bundi.water.pipeline.passes.WaterRenderPass;
import com.branwilliams.bundi.engine.systems.DebugCameraMoveSystem;
import com.branwilliams.bundi.water.system.WaterUpdateSystem;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;


/**
 * @author Brandon
 * @since September 03, 2019
 */
public class WaterScene extends AbstractScene implements Window.KeyListener {

    public static final int WATER_PLANE_LENGTH = 128;

    public static final int NUMBERWAVES = 4;

    private final Water water = createWater(0.3F, WATER_PLANE_LENGTH);

    private Camera camera;

    private Skybox skybox;

    private boolean wireframe;

    private Water createWater(float overallSteepness, int planeLength) {
        Wave[] normalWaves = {
                new Wave(0.05F, 0.02F, 0.3F, overallSteepness / (0.3F * 0.02F * NUMBERWAVES),
                        new Vector2f(1F, 1.5F)),

                new Wave(0.1F, 0.01F, 0.4F, overallSteepness / (0.4F * 0.01F * NUMBERWAVES),
                        new Vector2f(0.8F, 0.2F)),

                new Wave(0.04F, 0.035F, 0.1F, overallSteepness / (0.4F * 0.01F * NUMBERWAVES),
                        new Vector2f(-0.2F, -0.1F)),

                new Wave(0.05F, 0.007F, 0.2F, overallSteepness / (0.4F * 0.01F * NUMBERWAVES),
                        new Vector2f(-0.4F, -0.3F)),
        };

        Wave[] surfaceWaves = {
                new Wave(1F, 0.01F, 4F, overallSteepness / (4F * 0.01F * NUMBERWAVES),
                        new Vector2f(1F, 1F)),

                new Wave(0.5F, 0.02F, 3F, overallSteepness / (3F * 0.02F * NUMBERWAVES),
                        new Vector2f(1F, 0F)),

                new Wave(0.1F, 0.015F, 2F, overallSteepness / (3F * 0.02F * NUMBERWAVES),
                        new Vector2f(-0.1F, -0.2F)),

                new Wave(1.1F, 0.008F, 1F, overallSteepness / (3F * 0.02F * NUMBERWAVES),
                        new Vector2f(-0.2F, -0.1F)),
        };
        return new Water(normalWaves, surfaceWaves, planeLength);
    }

    public WaterScene() {
        super("water_scene");
        addKeyListener(this);
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {

        es.addSystem(new WaterUpdateSystem());
        es.addSystem(new DebugCameraMoveSystem(this, this::getCamera, 0.16F, 6F));
        es.initSystems(engine, window);

        Projection worldProjection = new Projection(window, 70, 0.01F, 1000F);
        RenderContext renderContext = new RenderContext(worldProjection);

        RenderPipeline<RenderContext> renderPipeline = new RenderPipeline<>(renderContext);
        renderPipeline.addLast(new WaterNormalRenderPass(this));
        renderPipeline.addLast(new EnableWireframeRenderPass(this::isWireframe));
        renderPipeline.addLast(new WaterRenderPass(this, this::getCamera));
        renderPipeline.addLast(new DisableWireframeRenderPass(this::isWireframe));
        renderPipeline.addLast(new SkyboxRenderPass<>(this::getCamera, this::getSkybox));

        WaterRenderer renderer = new WaterRenderer(this, renderPipeline);
        setRenderer(renderer);
    }

    @Override
    public void play(Engine engine) {
        camera = new Camera();
        camera.setPosition(0, 5F, 0);
        camera.lookAt(-WATER_PLANE_LENGTH * 0.5F, 0F, -2F);

        TextureLoader textureLoader = new TextureLoader(engine.getContext());

        try {
            CubeMapTexture environment = textureLoader.loadCubeMapTexture("assets/stormydays.csv");

            skybox = new Skybox(500, new Material(environment));

            water.initialize(environment, 1024, 1024);
            water.setColor(new Vector4f(0F, 0.01F, 0.075F, 0F));
//            water.setColor(new Vector4f(0F, 0.10F, 0.01F, 0F));
            water.getTransformable().position(-WATER_PLANE_LENGTH * 0.5F, 0F, -WATER_PLANE_LENGTH);
            es.entity("waterTile").component(
                    water
            ).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pause(Engine engine) {

    }

    @Override
    public void keyPress(Window window, int key, int scancode, int mods) {
        if (key == GLFW_KEY_G) {
            wireframe = !wireframe;
        }
    }

    @Override
    public void keyRelease(Window window, int key, int scancode, int mods) {

    }

    public Camera getCamera() {
        return camera;
    }

    public Skybox getSkybox() {
        return skybox;
    }

    public boolean isWireframe() {
        return wireframe;
    }
}

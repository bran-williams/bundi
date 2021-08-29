package com.branwilliams.bundi.water;

import com.branwilliams.bundi.engine.core.AbstractScene;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.KeyListener;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.core.pipeline.passes.DisableWireframeRenderPass;
import com.branwilliams.bundi.engine.core.pipeline.passes.EnableWireframeRenderPass;
import com.branwilliams.bundi.engine.mesh.primitive.CubeMesh;
import com.branwilliams.bundi.engine.shader.Camera;
import com.branwilliams.bundi.engine.material.Material;
import com.branwilliams.bundi.engine.shader.Projection;
import com.branwilliams.bundi.engine.shader.Transformation;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.skybox.Skybox;
import com.branwilliams.bundi.engine.skybox.SkyboxRenderPass;
import com.branwilliams.bundi.engine.texture.CubeMapTexture;
import com.branwilliams.bundi.engine.texture.TextureLoader;
import com.branwilliams.bundi.water.pipeline.passes.CubesRenderPass;
import com.branwilliams.bundi.water.pipeline.passes.WaterNormalRenderPass;
import com.branwilliams.bundi.water.pipeline.passes.WaterRenderPass;
import com.branwilliams.bundi.engine.systems.DebugCameraMoveSystem;
import com.branwilliams.bundi.water.system.WaterUpdateSystem;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;


/**
 * @author Brandon
 * @since September 03, 2019
 */
public class WaterScene extends AbstractScene {

    public static final int WATER_PLANE_LENGTH = 128;

//    private final Water water = createFlatWater(WATER_PLANE_LENGTH);

    private final Water water = createWater(0.24F, WATER_PLANE_LENGTH);

    private TextureLoader textureLoader;

    private Camera camera;

    private Skybox skybox;

    private boolean wireframe;

    public WaterScene() {
        super("water_scene");
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
        renderPipeline.addLast(new CubesRenderPass(this, this::getCamera));
        renderPipeline.addLast(new DisableWireframeRenderPass(this::isWireframe));
        renderPipeline.addLast(new SkyboxRenderPass<>(this::getCamera, this::getSkybox));

        WaterRenderer renderer = new WaterRenderer(this, renderPipeline);
        setRenderer(renderer);
    }

    @Override
    public void play(Engine engine) {
        camera = new Camera();
        camera.setPosition(0, 2F, 0);
        camera.lookAt(-WATER_PLANE_LENGTH * 0.5F, 0F, -2F);

        textureLoader = new TextureLoader(engine.getContext());

        try {
            CubeMapTexture environment = textureLoader.loadCubeMapTexture("assets/stormydays.csv");

            skybox = new Skybox(500, new Material(environment));

            water.initialize(environment, 1024, 1024);
//            water.setColor(new Vector4f(0F, 0.01F, 0.075F, 0F));
            water.setColor(new Vector4f(0F, 0F, 0F, 0F));
//            water.setColor(new Vector4f(0F, 0.10F, 0.01F, 0F));
            water.getTransformable().position(-WATER_PLANE_LENGTH * 0.5F, 0F, -WATER_PLANE_LENGTH);
            es.entity("waterTile").component(
                    water
            ).build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        CubeMesh cubeMesh0 = new CubeMesh(1, 1, 1, VertexFormat.POSITION);

        Color color0 = new Color(0x021e44);
        Color color1 = new Color(0xffb800);
        Color color2 = new Color(0x04e1500);

        es.entity("cube0").component(
                cubeMesh0,
                color0,
                new Transformation().position(-20, 2, -WATER_PLANE_LENGTH * 0.5F).scale(5)
        ).build();

        es.entity("cube2").component(
                cubeMesh0,
                color1,
                new Transformation().position(0, 1, -WATER_PLANE_LENGTH * 0.35F).scale(2)
        ).build();

        es.entity("cube3").component(
                cubeMesh0,
                color2,
                new Transformation().position(10, 3, -WATER_PLANE_LENGTH * 0.7F).scale(7)
        ).build();
    }

    public static Water createFlatWater(int planeLength) {
        Wave[] normalWaves = {
                new Wave(0F, 0F, 1F, 0f, new Vector2f(1F)),
                new Wave(0F, 0F, 1F, 0f, new Vector2f(1F)),
                new Wave(0F, 0F, 1F, 0f, new Vector2f(1F)),
                new Wave(0F, 0F, 1F, 0f, new Vector2f(1F)),
        };

        Wave[] surfaceWaves = {
                new Wave(0F, 0F, 1F, 0f, new Vector2f(1F)),
                new Wave(0F, 0F, 1F, 0f, new Vector2f(1F)),
                new Wave(0F, 0F, 1F, 0f, new Vector2f(1F)),
                new Wave(0F, 0F, 1F, 0f, new Vector2f(1F)),
        };
        return new Water(normalWaves, surfaceWaves, planeLength);
    }

    public static Water createWater(float overallSteepness, int planeLength) {
        Wave[] normalWaves = {
                new Wave(0.05F, 0.02F, 0.3F, overallSteepness / (0.3F * 0.02F * Water.NUMBERWAVES),
                        new Vector2f(1F, 1.5F)),

                new Wave(0.1F, 0.01F, 0.4F, overallSteepness / (0.4F * 0.01F * Water.NUMBERWAVES),
                        new Vector2f(0.8F, 0.2F)),

                new Wave(0.04F, 0.035F, 0.1F, overallSteepness / (0.4F * 0.01F * Water.NUMBERWAVES),
                        new Vector2f(-0.2F, -0.1F)),

                new Wave(0.05F, 0.007F, 0.2F, overallSteepness / (0.4F * 0.01F * Water.NUMBERWAVES),
                        new Vector2f(-0.4F, -0.3F)),
        };
        Wave[] surfaceWaves = {
                new Wave(1F, 0.01F, 4F, overallSteepness / (4F * 0.01F * Water.NUMBERWAVES),
                        new Vector2f(1F, 1F)),

                new Wave(0.5F, 0.02F, 3F, overallSteepness / (3F * 0.02F * Water.NUMBERWAVES),
                        new Vector2f(1F, 0F)),

                new Wave(0.1F, 0.015F, 2F, overallSteepness / (3F * 0.02F * Water.NUMBERWAVES),
                        new Vector2f(-0.1F, -0.2F)),

                new Wave(1.1F, 0.008F, 1F, overallSteepness / (3F * 0.02F * Water.NUMBERWAVES),
                        new Vector2f(-0.2F, -0.1F)),
        };
        return new Water(normalWaves, surfaceWaves, planeLength);
    }

    @Override
    public void pause(Engine engine) {

    }

    @Override
    public void keyPress(Window window, int key, int scancode, int mods) {
        super.keyPress(window, key, scancode, mods);
        if (key == GLFW_KEY_G) {
            wireframe = !wireframe;
        }
        if (key == GLFW_KEY_E) {
            water.getTransformable().getRotationAsEuler().x += 0.5F;
        }

        if (key == GLFW_KEY_Q) {
            water.getTransformable().getRotationAsEuler().x -= 0.5F;
        }
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

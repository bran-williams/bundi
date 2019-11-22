package com.branwilliams.bundi.cloth;

import com.branwilliams.bundi.cloth.pipeline.ClothRenderPass;
import com.branwilliams.bundi.cloth.pipeline.SphereRenderPass;
import com.branwilliams.bundi.engine.core.AbstractScene;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.shader.Camera;
import com.branwilliams.bundi.engine.shader.Material;
import com.branwilliams.bundi.engine.shader.Projection;
import com.branwilliams.bundi.engine.skybox.Skybox;
import com.branwilliams.bundi.engine.skybox.SkyboxRenderPass;
import com.branwilliams.bundi.engine.systems.DebugCameraMoveSystem;
import com.branwilliams.bundi.engine.texture.CubeMapTexture;
import com.branwilliams.bundi.engine.texture.TextureLoader;
import org.joml.Planef;
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

    private Camera camera;

    private Skybox skybox;

    private Cloth cloth;

    private ClothMeshBuilder clothMeshBuilder;

    private Spheref sphere;

    public ClothScene() {
        super("cloth");
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        es.addSystem(new DebugCameraMoveSystem(this, this::getCamera, 0.16F, 16F));
        es.initSystems(engine, window);

        Projection worldProjection = new Projection(window, 70, 0.01F, 1000F);
        RenderContext renderContext = new RenderContext(worldProjection);

        RenderPipeline<RenderContext> renderPipeline = new RenderPipeline<>(renderContext);
        renderPipeline.addLast(new SkyboxRenderPass<>(this::getCamera, this::getSkybox));
        renderPipeline.addLast(new SphereRenderPass(this::getCamera, this::getSphere));
        renderPipeline.addLast(new ClothRenderPass(this::getCamera, this::getCloth));
        ClothRenderer renderer = new ClothRenderer(this, renderPipeline);
        setRenderer(renderer);
    }

    @Override
    public void play(Engine engine) {
        ClothPhysicsParameters clothPhysicsParameters = new ClothPhysicsParameters(0.01F, 0.5F * 0.5F, 15);
        clothMeshBuilder = new ClothMeshBuilder();
        sphere = new Spheref(0F, -20F, 40F, 10.0F);

        cloth = new Cloth(clothPhysicsParameters, 70, 50, 32, 32);

        // Pin the corners down
        for (int i = 0; i < 3; i++) {
            cloth.getParticle(i, 0).offsetPosition(new Vector3f(0.5F, 0F, 0F));
            cloth.getParticle(i, 0).setMovable(false);

            cloth.getParticle(cloth.getParticleSizeX() - 1 - i, 0).offsetPosition(new Vector3f(-0.5F, 0F, 0F));
            cloth.getParticle(cloth.getParticleSizeX() - 1 - i, 0).setMovable(false);
        }

        clothMeshBuilder.buildMesh(cloth);

        camera = new Camera();
        camera.setPosition(40, 20, 100);
        try {
            TextureLoader textureLoader = new TextureLoader(engine.getContext());
            CubeMapTexture skyboxTexture = textureLoader.loadCubeMapTexture("assets/one.csv");
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

        cloth.addForce(new Vector3f(0F, -0.2F, 0F)); // add gravity each frame, pointing down
        cloth.addWindForce(new Vector3f(0.1F,0F,0.1F)); // generate some wind each frame
        cloth.update();
        cloth.collideWithSphere(new Vector3f(sphere.x, sphere.y, sphere.z), sphere.r);
        clothMeshBuilder.rebuildMesh(cloth);

        engine.getWindow().setTitle("Cloth FPS=" + engine.getFrames());
    }

    public Spheref getSphere() {
        return sphere;
    }

    public Cloth getCloth() {
        return cloth;
    }

    public Camera getCamera() {
        return camera;
    }

    public Skybox getSkybox() {
        return skybox;
    }


}

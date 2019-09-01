package com.branwilliams.bundi.pbr;

import com.branwilliams.bundi.engine.core.AbstractScene;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.mesh.primitive.SphereMesh;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.texture.TextureLoader;
import com.branwilliams.bundi.engine.util.IOUtils;
import com.branwilliams.bundi.pbr.pipeline.PbrRenderPipeline;
import com.branwilliams.bundi.pbr.pipeline.material.PbrMaterial;
import com.branwilliams.bundi.pbr.pipeline.passes.PbrDebugRenderPass;
import com.branwilliams.bundi.pbr.system.TransformationRotationSystem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joml.Vector3f;

import java.io.IOException;
import java.nio.file.Path;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;

/**
 * @author Brandon
 * @since August 31, 2019
 */
public class PbrScene extends AbstractScene implements Window.KeyListener {

    private final Vector3f cameraPosition = new Vector3f(0F, 0F, 0F);

    private final Vector3f objectPosition = new Vector3f(0F, 0F, -2F);

    /**
     * Change me in order to view another material!
     * */
    private final String material = "pbr/rustediron.json";

    private final float exposure = 1F;

    private TextureLoader textureLoader;

    private Camera camera;

    private IEntity sphereEntity;

    private Path assetDirectory;

    public PbrScene() {
        super("pbr_scene");
        addKeyListener(this);
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        assetDirectory = engine.getContext().getAssetDirectory();

        es.addSystem(new TransformationRotationSystem(this, this::getObjectTransformable, 0.16F));
        es.initSystems(engine, window);

        Projection worldProjection = new Projection(window, 70, 0.01F, 1000F);
        PbrRenderPipeline renderPipeline = new PbrRenderPipeline(this, this::getCamera, this::getExposure, worldProjection);
        renderPipeline.addLast(new PbrDebugRenderPass());
        PbrRenderer renderer = new PbrRenderer(this, renderPipeline);
        setRenderer(renderer);

        textureLoader = new TextureLoader(engine.getContext());
    }

    @Override
    public void play(Engine engine) {
        camera = new Camera();
        camera.setPosition(cameraPosition);
        camera.lookAt(objectPosition);

        try {
            PbrMaterial material = readMaterial(assetDirectory, this.material);
            SphereMesh sphereMesh = new SphereMesh(1F, 50, 50, true, false);
            Model sphereModel = new Model(sphereMesh, material.createMaterial(textureLoader));
            sphereEntity = es.entity("sphere").component(
                    new Transformation().position(objectPosition).rotate(90F, 0F, 0F),
                    sphereModel
            ).build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        es.entity("light1").component(
                new PointLight(new Vector3f(cameraPosition))
        ).build();

        es.entity("light2").component(
                new PointLight(new Vector3f(cameraPosition).add(-2F, 0F, -2F), new Vector3f(1F, 0F, 0F))
        ).build();

        es.entity("light3").component(
                new PointLight(new Vector3f(cameraPosition).add(2F, 0F, -2F), new Vector3f(0F, 1F, 0F))
        ).build();

        es.entity("light4").component(
                new PointLight(new Vector3f(cameraPosition).add(0F, 2F, -2F), new Vector3f(0F, 0F, 1F))
        ).build();

    }

    @Override
    public void pause(Engine engine) {

    }

    @Override
    public void update(Engine engine, double deltaTime) {
        super.update(engine, deltaTime);
    }

    private PbrMaterial readMaterial(Path directory, String filePath) {
        return readMaterial(directory.resolve(filePath));
    }

    private PbrMaterial readMaterial(Path filePath) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        String json = IOUtils.readFile(filePath, null);

        return gson.fromJson(json, PbrMaterial.class);
    }

    public Transformable getObjectTransformable() {
        return sphereEntity.getComponent(Transformable.class);
    }

    public Camera getCamera() {
        return camera;
    }

    public float getExposure() {
        return exposure;
    }

    @Override
    public void keyPress(Window window, int key, int scancode, int mods) {
        if (key == GLFW_KEY_R) {
            Model model = sphereEntity.getComponent(Model.class);
            model.getMaterial()[0].destroy();

            PbrMaterial material = readMaterial(assetDirectory, this.material);

            try {
                model.setMaterial(material.createMaterial(textureLoader));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void keyRelease(Window window, int key, int scancode, int mods) {

    }
}

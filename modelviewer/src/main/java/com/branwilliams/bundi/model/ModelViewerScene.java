package com.branwilliams.bundi.model;

import com.branwilliams.bundi.engine.core.AbstractScene;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.KeyListener;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.material.Material;
import com.branwilliams.bundi.engine.model.Model;
import com.branwilliams.bundi.engine.model.ModelLoader;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.skybox.Skybox;
import com.branwilliams.bundi.engine.skybox.SkyboxRenderPass;
import com.branwilliams.bundi.engine.systems.DebugCameraMoveSystem;
import com.branwilliams.bundi.engine.texture.CubeMapTexture;
import com.branwilliams.bundi.engine.texture.TextureLoader;
import com.branwilliams.bundi.model.pipeline.ModelRenderPass;
import org.joml.Vector3f;


import java.io.IOException;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;

/**
 * @author Brandon
 * @since August 31, 2019
 */
public class ModelViewerScene extends AbstractScene implements KeyListener {

    private final Vector3f cameraPosition = new Vector3f();

    /**
     * Change me in order to view another model
     * */
    private final String modelLocation = "models/cartoonland2/cartoonland2.obj";
    private final String modelTextures = "models/cartoonland2/";

//    private final String modelLocation = "models/white_oak/white_oak.obj";
//    private final String modelTextures = "models/white_oak/";

//    private final String modelLocation = "models/logo2/logo2.obj";
//    private final String modelTextures = "models/logo2/";

    private final VertexFormat vertexFormat = VertexFormat.POSITION_UV;

    private final float exposure = 1F;

    private TextureLoader textureLoader;

    private ModelLoader modelLoader;

    private Camera camera;

    private Skybox skybox;

    private Model model;

    private Transformable modelTransform = new Transformation().position(0F, 0F, -3F).scale(0.125F);;

    public ModelViewerScene() {
        super("modelviewer_scene");
        addKeyListener(this);
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {

        es.addSystem(new DebugCameraMoveSystem(this, this::getCamera, 0.16F, 6F));
        es.initSystems(engine, window);

        Projection worldProjection = new Projection(window, 70, 0.01F, 1000F);
        RenderContext renderContext = new RenderContext(worldProjection);
        RenderPipeline renderPipeline = new RenderPipeline<>(renderContext);
        renderPipeline.addLast(new SkyboxRenderPass(this::getCamera, this::getSkybox));
        renderPipeline.addLast(new ModelRenderPass(this::getCamera, vertexFormat, this::getModelTransform, this::getModel));
        ModelViewerRenderer renderer = new ModelViewerRenderer(this, renderPipeline);
        setRenderer(renderer);

        textureLoader = new TextureLoader(engine.getContext());
    }

    @Override
    public void play(Engine engine) {
        camera = new Camera();
        camera.setPosition(cameraPosition);
        camera.lookAt(modelTransform.getPosition());

        textureLoader = new TextureLoader(engine.getContext());
        modelLoader = new ModelLoader(engine.getContext(), textureLoader);
        try {
            model = modelLoader.load(modelLocation, modelTextures, vertexFormat);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
    public void update(Engine engine, double deltaTime) {
        super.update(engine, deltaTime);
    }

    public Model getModel() {
        return model;
    }

    public Transformable getModelTransform() {
        return modelTransform;
    }

    public Camera getCamera() {
        return camera;
    }

    public float getExposure() {
        return exposure;
    }

    public Skybox getSkybox() {
        return skybox;
    }

    @Override
    public void keyPress(Window window, int key, int scancode, int mods) {
        if (key == GLFW_KEY_R) {
        }
    }

    @Override
    public void keyRelease(Window window, int key, int scancode, int mods) {

    }
}

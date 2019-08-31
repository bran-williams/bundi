package com.branwilliams.terrain;

import com.branwilliams.bundi.engine.core.AbstractScene;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.texture.ArrayTexture;
import com.branwilliams.bundi.engine.texture.Texture;
import com.branwilliams.bundi.engine.texture.TextureData;
import com.branwilliams.bundi.engine.texture.TextureLoader;
import com.branwilliams.terrain.generator.HeightGenerator;
import com.branwilliams.terrain.generator.PerlinNoiseGenerator;
import com.branwilliams.terrain.generator.TerrainGenerator;
import com.branwilliams.terrain.render.TerrainRenderPass;
import com.branwilliams.terrain.render.TerrainRenderer;
import com.branwilliams.terrain.system.RotatingCameraSystem;
import org.joml.Vector3f;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

/**
 * @author Brandon
 * @since August 30, 2019
 */
public class TerrainScene extends AbstractScene implements Window.KeyListener {

    private static final int TERRAIN_TILE_SIZE = 128;

    private static final int TERRAIN_TILE_VERTICES_X = 256;

    private static final int TERRAIN_TILE_VERTICES_Z = 256;

    private static final float TERRAIN_TILE_AMPLITUDE = 16;

    private static final Transformable focalPoint = new Transformation()
            .position(TERRAIN_TILE_SIZE * 0.5F, 0F, TERRAIN_TILE_SIZE * 0.5F);

    private boolean shouldExit = false;

    private Camera camera;

    private Projection worldProjection;

    public TerrainScene() {
        super("terrain_scene");
        this.addKeyListener(this);
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        window.disableCursor();

        es.addSystem(new RotatingCameraSystem(this, focalPoint, new Vector3f(TERRAIN_TILE_SIZE, TERRAIN_TILE_AMPLITUDE, TERRAIN_TILE_SIZE)));
        es.initSystems(engine, window);

        worldProjection = new Projection(window, 70, 0.001F, 1000F);

        RenderContext renderContext = new RenderContext(worldProjection);
        RenderPipeline<RenderContext> renderPipeline = new RenderPipeline<>(renderContext);
        renderPipeline.addLast(new TerrainRenderPass(this, this::getCamera));
        setRenderer(new TerrainRenderer(this, renderPipeline));
    }

    @Override
    public void play(Engine engine) {
        this.camera = new Camera();
        this.camera.move(0F, TERRAIN_TILE_AMPLITUDE, -4F);
        this.camera.lookAt(TERRAIN_TILE_SIZE * 0.5F, 0F, TERRAIN_TILE_SIZE * 0.5F);

        TextureLoader textureLoader = new TextureLoader(engine.getContext());
        Material material = createTerrainMaterial(textureLoader);

        TerrainGenerator terrainGenerator = new TerrainGenerator();

        float[] frequencies = { 1F, 4F, 16  };
        float[] percentages = { 1F, 0.25F, 0.125F };

        HeightGenerator generator = new PerlinNoiseGenerator(1024, frequencies, percentages,
                1F / TERRAIN_TILE_SIZE);
        TerrainTile terrainTile = terrainGenerator.generateTerrainTile(generator, material, TERRAIN_TILE_AMPLITUDE,
                0, 0, TERRAIN_TILE_SIZE, TERRAIN_TILE_VERTICES_X, TERRAIN_TILE_VERTICES_Z);

        es.entity("terrainTile").component(
                terrainTile
        ).build();

        es.entity("cameraEntity").component(
                camera
        ).build();
    }

    private Material createTerrainMaterial(TextureLoader textureLoader) {
        Material terrainMaterial = new Material();
        try {
            TextureData diffuse0 = textureLoader.loadTexture("textures/grass/grass01.png");
            TextureData diffuse1 = textureLoader.loadTexture("textures/sand/sand_color.jpg");
            TextureData diffuse2 = textureLoader.loadTexture("textures/rock/rock_color.jpg");
            TextureData diffuse3 = textureLoader.loadTexture("textures/snow/snow_color.jpg");

            ArrayTexture diffuse = new ArrayTexture(Texture.TextureType.COLOR8, diffuse0, diffuse1, diffuse2, diffuse3);
            terrainMaterial.setTexture(0, diffuse);

            TextureData normal0 = textureLoader.loadTexture("textures/grass/grass01_n.png");
            TextureData normal1 = textureLoader.loadTexture("textures/sand/sand_norm.jpg");
            TextureData normal2 = textureLoader.loadTexture("textures/rock/rock_norm.jpg");
            TextureData normal3 = textureLoader.loadTexture("textures/snow/snow_norm.jpg");

            ArrayTexture normal = new ArrayTexture(Texture.TextureType.COLOR8, normal0, normal1, normal2, normal3);
            terrainMaterial.setTexture(1, normal);
            terrainMaterial.setProperty("hasNormalTexture", true);

            terrainMaterial.setProperty("tiling", 16);
            terrainMaterial.setProperty("materialShininess", 100F);
            TextureData blendmap = textureLoader.loadTexture("textures/blendmap0.png");
            terrainMaterial.setTexture(2, new Texture(blendmap, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return terrainMaterial;
    }

    @Override
    public void pause(Engine engine) {

    }

    @Override
    public void update(Engine engine, double deltaTime) {
        super.update(engine, deltaTime);
        if (shouldExit)
            engine.stop();
    }

    @Override
    public void keyPress(Window window, int key, int scancode, int mods) {
        switch (key) {
            case GLFW_KEY_ESCAPE:
                shouldExit = true;
                break;
        }
    }

    @Override
    public void keyRelease(Window window, int key, int scancode, int mods) {

    }

    public Camera getCamera() {
        return this.camera;
    }
}

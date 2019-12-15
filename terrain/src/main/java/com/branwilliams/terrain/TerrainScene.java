package com.branwilliams.terrain;

import com.branwilliams.bundi.engine.core.AbstractScene;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.systems.DebugCameraMoveSystem;
import com.branwilliams.bundi.engine.texture.ArrayTexture;
import com.branwilliams.bundi.engine.texture.Texture;
import com.branwilliams.bundi.engine.texture.TextureData;
import com.branwilliams.bundi.engine.texture.TextureLoader;
import com.branwilliams.terrain.builder.TerrainMeshBuilder;
import com.branwilliams.terrain.builder.TerrainTileBuilder;
import com.branwilliams.terrain.generator.*;
import com.branwilliams.terrain.render.LineGraphRenderPass;
import com.branwilliams.terrain.render.TerrainRenderPass;
import com.branwilliams.terrain.render.TerrainRenderer;

import java.io.IOException;

import static com.branwilliams.terrain.generator.HeightmapBlendmapGenerator.GRAYSCALE_MAX_COLOR;

/**
 * @author Brandon
 * @since August 30, 2019
 */
public class TerrainScene extends AbstractScene {

    private static final int TERRAIN_TILE_SIZE = 256;

    private static final int TERRAIN_TILE_VERTICES_X = 1024;

    private static final int TERRAIN_TILE_VERTICES_Z = 1024;

    private static final float TERRAIN_TILE_AMPLITUDE = 16;

    private Camera camera;

    private Projection worldProjection;

    private TextureData heightmap;

    public TerrainScene() {
        super("terrain_scene");
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        es.addSystem(new DebugCameraMoveSystem(this, this::getCamera, 0.16F, 6F));
        es.initSystems(engine, window);

        worldProjection = new Projection(window, 70, 0.001F, 1000F);

        RenderContext renderContext = new RenderContext(worldProjection);
        RenderPipeline<RenderContext> renderPipeline = new RenderPipeline<>(renderContext);
        renderPipeline.addLast(new TerrainRenderPass(this, this::getCamera));
        renderPipeline.addLast(new LineGraphRenderPass(this));
        setRenderer(new TerrainRenderer(this, renderPipeline));
    }

    @Override
    public void play(Engine engine) {
        this.camera = new Camera();
        this.camera.move(0F, TERRAIN_TILE_AMPLITUDE, -4F);
        this.camera.lookAt(TERRAIN_TILE_SIZE * 0.5F, 0F, TERRAIN_TILE_SIZE * 0.5F);

        // Create the material for the terrain
        TextureLoader textureLoader = new TextureLoader(engine.getContext());
        Material material = createTerrainMaterial(textureLoader);


        // Create or load the height generator
//        float[] frequencies = { 1F, 2F, 4F, 8F };
//        float[] percentages = { 1F, 1F, 0.5F, 0.25F };
//        HeightGenerator generator = new PerlinNoiseGenerator(1024, frequencies, percentages,
//                1F / TERRAIN_TILE_SIZE);
        HeightGenerator generator = new HeightmapGenerator(heightmap);

        // Create the tile builder
        TerrainTileBuilder terrainTileBuilder = new TerrainTileBuilder();
        TerrainTile terrainTile = terrainTileBuilder.buildTerrainTile(generator, material, TERRAIN_TILE_AMPLITUDE,
                0, 0, TERRAIN_TILE_SIZE, TERRAIN_TILE_VERTICES_X, TERRAIN_TILE_VERTICES_Z);

        // Create the mesh builder for that tile
        TerrainMeshBuilder terrainMeshBuilder = new TerrainMeshBuilder();
        terrainMeshBuilder.buildTerrainMesh(terrainTile);

        es.entity("terrainTile").component(
                terrainTile
        ).build();

        es.entity("cameraEntity").component(
                camera
        ).build();
    }

    private Material createTerrainMaterial(TextureLoader textureLoader) {
        Material terrainMaterial = new Material();
        terrainMaterial.setProperty("tiling", 16);
        terrainMaterial.setProperty("materialShininess", 100F);

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

            

            heightmap = textureLoader.loadTexture("textures/heightmap2.png");

//            TextureData blendmap = textureLoader.loadTexture("textures/blendmap0.png");

            HeightmapBlendmapGenerator blendmapGenerator = new HeightmapBlendmapGenerator();
            TextureData blendmap = blendmapGenerator.generateBlendmap(heightmap, GRAYSCALE_MAX_COLOR);

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
    }

    public Camera getCamera() {
        return this.camera;
    }
}

package com.branwilliams.terrain;

import com.branwilliams.bundi.engine.core.AbstractScene;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.material.Material;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.systems.DebugCameraMoveSystem;
import com.branwilliams.bundi.engine.texture.ArrayTexture;
import com.branwilliams.bundi.engine.texture.Texture;
import com.branwilliams.bundi.engine.texture.TextureData;
import com.branwilliams.bundi.engine.texture.TextureLoader;
import com.branwilliams.bundi.engine.util.IOUtils;
import com.branwilliams.bundi.engine.util.noise.LayeredNoise;
import com.branwilliams.bundi.engine.util.noise.Noise;
import com.branwilliams.bundi.engine.util.noise.OpenSimplexNoise;
import com.branwilliams.terrain.builder.TerrainMeshBuilder;
import com.branwilliams.terrain.builder.TerrainTileBuilder;
import com.branwilliams.terrain.component.TerrainMaterial;
import com.branwilliams.terrain.component.TerrainTexture;
import com.branwilliams.terrain.generator.*;
import com.branwilliams.terrain.render.LineGraphRenderPass;
import com.branwilliams.terrain.render.TerrainRenderPass2;
import com.branwilliams.terrain.render.TerrainRenderer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Brandon
 * @since August 30, 2019
 */
public class TerrainScene extends AbstractScene {

    private static final int TERRAIN_TILE_SIZE = 256;

    private static final int TERRAIN_TILE_VERTICES_X = 256;

    private static final int TERRAIN_TILE_VERTICES_Z = 256;

    private static final float TERRAIN_TILE_AMPLITUDE = 128;

    private Camera camera;

    private Projection worldProjection;

    private TextureData heightmap;

    public TerrainScene() {
        super("terrain_scene");
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        es.addSystem(new DebugCameraMoveSystem(this, this::getCamera, 0.16F, 16F));
        es.initSystems(engine, window);

        worldProjection = new Projection(window, 70, 0.001F, 1000F);

        RenderContext renderContext = new RenderContext(worldProjection);
        RenderPipeline<RenderContext> renderPipeline = new RenderPipeline<>(renderContext);
        renderPipeline.addLast(new TerrainRenderPass2(this, this::getCamera));
        setRenderer(new TerrainRenderer(this, renderPipeline));
    }

    @Override
    public void play(Engine engine) {
        this.camera = new Camera();
        this.camera.move(0F, TERRAIN_TILE_AMPLITUDE, -4F);
        this.camera.lookAt(TERRAIN_TILE_SIZE * 0.5F, 0F, TERRAIN_TILE_SIZE * 0.5F);

        // Create the material for the terrain
        TextureLoader textureLoader = new TextureLoader(engine.getContext());
        TerrainMaterial terrainMaterial = loadTerrainMaterial("terrain/terrain_material.json");
        Material material = createTerrainMaterial(textureLoader, 16, 100F, terrainMaterial);

        float defaultScale = 1F / TERRAIN_TILE_SIZE;
        // Create or load the height generator

//        float[] frequencies = { 1F, 2F, 4F, 8F };
//        float[] percentages = { 1F, 1F, 0.5F, 0.25F };
//        float[] noiseScales = { defaultScale, defaultScale, defaultScale, defaultScale };

//        float[] frequencies = { 1.0F,          1.0F       };
//        float[] percentages = { 1.0F,          0.1F       };
//        float[] noiseScales = { 1.0F / 128.0F, 1.0F / 16.0F };

//        float[] frequencies = { 1.0F };
//        float[] percentages = { 1.0F };
//        float[] noiseScales = { defaultScale };
//        HeightGenerator generator = new NoiseGenerator(1024, frequencies, percentages,
//                noiseScales);

//        HeightGenerator generator = new HeightmapGenerator(terrainMaterial.getHeightmapTextureData());

        Noise heightNoise = new LayeredNoise(new OpenSimplexNoise(1024), 5);
//        Noise heightNoise = new OpenSimplexNoise(1024);

        HeightGenerator generator = new NoiseHeightGenerator(heightNoise, 1F / TERRAIN_TILE_SIZE);

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

    public static Material createTerrainMaterial(TextureLoader textureLoader, int tiling, float materialShininess, TerrainMaterial terrainMaterial_) {
        Material terrainMaterial = new Material();
        terrainMaterial.setProperty("tiling", tiling);
        terrainMaterial.setProperty("materialShininess", materialShininess);

        List<TextureData> diffuseTextureData = new ArrayList<>();
        List<TextureData> normalTextureData = new ArrayList<>();

        try {
            terrainMaterial_.load(textureLoader);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        for (TerrainTexture terrainTexture : terrainMaterial_.getTextures()) {
            diffuseTextureData.add(terrainTexture.getTextureData().getDiffuseTextureData());
            normalTextureData.add(terrainTexture.getTextureData().getNormalTextureData());
        }
        ArrayTexture diffuse = new ArrayTexture(Texture.TextureType.COLOR8, diffuseTextureData.toArray(new TextureData[0]));
        diffuse.bind();
        diffuse.generateMipmaps();
        diffuse.linearFilter(true);
        Texture.unbind(diffuse);

        terrainMaterial.setTexture(0, diffuse);

        ArrayTexture normal = new ArrayTexture(Texture.TextureType.COLOR8, normalTextureData.toArray(new TextureData[0]));
        normal.bind();
        normal.generateMipmaps();
        normal.linearFilter(true);
        Texture.unbind(normal);

        terrainMaterial.setTexture(1, normal);
        terrainMaterial.setProperty("hasNormalTexture", true);

        if (terrainMaterial_.getBlendmap() != null) {
            terrainMaterial.setTexture(2, new Texture(terrainMaterial_.getBlendmapTextureData(), true));
        }
//        TextureData blendmap = textureLoader.loadTexture("textures/blendmap0.png");
//        HeightmapBlendmapGenerator blendmapGenerator = new HeightmapBlendmapGenerator();
//        TextureData blendmap = blendmapGenerator.generateBlendmap(heightmap, GRAYSCALE_MAX_COLOR);
        return terrainMaterial;
    }

//    private List<TerrainTexture> loadTerrainTextures(TextureLoader textureLoader, String path) {
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        String fileContents = IOUtils.readFile(path, "");
//        List<TerrainTexture> terrainTextures = gson.fromJson(fileContents, GsonUtils.arrayListType(TerrainTexture.class));
//        return terrainTextures;
//    }

    public static TerrainMaterial loadTerrainMaterial(String path) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String fileContents = IOUtils.readFile(path, "");
        TerrainMaterial terrainMaterial = gson.fromJson(fileContents, TerrainMaterial.class);
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

package com.branwilliams.demo.mountain;

import com.branwilliams.bundi.engine.core.AbstractScene;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.core.pipeline.passes.DisableWireframeRenderPass;
import com.branwilliams.bundi.engine.core.pipeline.passes.EnableWireframeRenderPass;
import com.branwilliams.bundi.engine.shader.Camera;
import com.branwilliams.bundi.engine.material.Material;
import com.branwilliams.bundi.engine.shader.Projection;
import com.branwilliams.bundi.engine.skybox.Skybox;
import com.branwilliams.bundi.engine.skybox.SkyboxRenderPass;
import com.branwilliams.bundi.engine.systems.DebugCameraMoveSystem;
import com.branwilliams.bundi.engine.texture.CubeMapTexture;
import com.branwilliams.bundi.engine.texture.TextureLoader;
import com.branwilliams.bundi.water.Water;
import com.branwilliams.bundi.water.pipeline.passes.WaterNormalRenderPass;
import com.branwilliams.bundi.water.pipeline.passes.WaterRenderPass;
import com.branwilliams.bundi.water.system.WaterUpdateSystem;
import com.branwilliams.terrain.TerrainTile;
import com.branwilliams.terrain.builder.TerrainMeshBuilder;
import com.branwilliams.terrain.builder.TerrainTileBuilder;
import com.branwilliams.terrain.component.TerrainMaterial;
import com.branwilliams.terrain.generator.HeightGenerator;
import com.branwilliams.terrain.generator.HeightmapHeightGenerator;
import com.branwilliams.terrain.generator.NoiseGenerator;
import com.branwilliams.terrain.render.TerrainRenderPass2;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.IOException;

import static com.branwilliams.bundi.water.WaterScene.createWater;
import static com.branwilliams.terrain.TerrainScene.createTerrainMaterial;
import static com.branwilliams.terrain.TerrainScene.loadTerrainMaterial;

/**
 * @author Brandon
 * @since November 30, 2019
 */
public class MountainScene extends AbstractScene {

    private static final int TERRAIN_TILE_SIZE = 512;

    private static final int TERRAIN_TILE_VERTICES_X = 256;

    private static final int TERRAIN_TILE_VERTICES_Z = 256;

    private static final float TERRAIN_TILE_AMPLITUDE = 128;

    private static final int WATER_PLANE_LENGTH = 128;

    private static final float WATER_PLANE_SCALE = 4F;

//    private static final int WATER_HEIGHT = 8;

    private static final int WATER_HEIGHT = 32;

    private static Vector4f waterColor = new Vector4f(0F, 0.01F, 0.075F, 0F);

    private final Water water = createWater(0.3F, WATER_PLANE_LENGTH);

    private Vector3f cameraStartingPosition = new Vector3f(TERRAIN_TILE_VERTICES_X * 0.5F, TERRAIN_TILE_AMPLITUDE * 0.5F, 0F);

    private Vector3f cameraLookAt = new Vector3f(TERRAIN_TILE_VERTICES_X * 0.5F, TERRAIN_TILE_AMPLITUDE * 0.5F, TERRAIN_TILE_VERTICES_Z * 0.5F);

    private Camera camera;

    private Skybox skybox;

    private boolean wireframe;

    public MountainScene() {
        super("mountain");
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        es.addSystem(new DebugCameraMoveSystem(this, this::getCamera, 0.16F, 16F));
        es.addSystem(new WaterUpdateSystem());
        es.initSystems(engine, window);

        Projection worldProjection = new Projection(window, 70, 0.01F, 1000F);
        RenderContext renderContext = new RenderContext(worldProjection);

        RenderPipeline<RenderContext> renderPipeline = new RenderPipeline<>(renderContext);
        // pre-passes
        renderPipeline.addLast(new WaterNormalRenderPass(this));

        renderPipeline.addLast(new EnableWireframeRenderPass(this::isWireframe));
        renderPipeline.addLast(new TerrainRenderPass2(this, this::getCamera));
        renderPipeline.addLast(new WaterRenderPass(this, this::getCamera));
        renderPipeline.addLast(new DisableWireframeRenderPass(this::isWireframe));

        // skybox
        renderPipeline.addLast(new SkyboxRenderPass<>(this::getCamera, this::getSkybox));

        MountainRenderer<RenderContext> renderer = new MountainRenderer<>(this, renderPipeline);
        setRenderer(renderer);
    }

    @Override
    public void play(Engine engine) {
        camera = new Camera();
        camera.setPosition(cameraStartingPosition);
        camera.lookAt(cameraLookAt);

        TextureLoader textureLoader = new TextureLoader(engine.getContext());
        createTerrainEntity(textureLoader);
        createWaterEntity(textureLoader);
    }

    private void createTerrainEntity(TextureLoader textureLoader) {
        TerrainMaterial terrainMaterial = loadTerrainMaterial("terrain/terrain_material.json");
        Material material = createTerrainMaterial(textureLoader, 16, 100F, terrainMaterial);

        HeightGenerator heightGenerator;
        if (terrainMaterial.getHeightmap() != null) {
            heightGenerator = new HeightmapHeightGenerator(terrainMaterial.getHeightmapTextureData());
        } else {
            float[] frequencies = { 1F, 2F, 4F, 8F, 16F };
            float[] percentages = { 1F, 1F, 0.5F, 0.25F, 0.5F };
            heightGenerator = new NoiseGenerator(1024, frequencies, percentages,
                    1F / TERRAIN_TILE_SIZE);
        }

        TerrainTileBuilder terrainTileBuilder = new TerrainTileBuilder();
        TerrainTile terrainTile = terrainTileBuilder.buildTerrainTile(heightGenerator, material, TERRAIN_TILE_AMPLITUDE,
                0, 0, TERRAIN_TILE_SIZE, TERRAIN_TILE_VERTICES_X, TERRAIN_TILE_VERTICES_Z);

        // Create the mesh builder for that tile
        TerrainMeshBuilder terrainMeshBuilder = new TerrainMeshBuilder();
        terrainMeshBuilder.buildTerrainMesh(terrainTile);

        es.entity("terrainTile").component(
                terrainTile
        ).build();
    }

    private void createWaterEntity(TextureLoader textureLoader) {
        try {
            CubeMapTexture environment = textureLoader.loadCubeMapTexture("assets/stormydays.csv");

            skybox = new Skybox(500, new Material(environment));

            water.initialize(environment, 1024, 1024);
            water.setColor(waterColor);
            water.getTransformable().position(0, WATER_HEIGHT, 0).scale(WATER_PLANE_SCALE);
            es.entity("waterTile").component(
                    water
            ).build();

//            int numWaterTiles = 2;
//            int halfNumWaterTiles = numWaterTiles / 2;
//            for (int i = -halfNumWaterTiles; i < halfNumWaterTiles; i++) {
//                for (int j = -halfNumWaterTiles; j < halfNumWaterTiles; j++) {
//                    if (i == 0 && j == 0)
//                        continue;
//
//                    Water copy = water.copy();
//                    copy.getTransformable().position(i * WATER_PLANE_LENGTH, WATER_HEIGHT, j * WATER_PLANE_LENGTH);
//                    es.entity("waterTile x=" + i + " z=" + j).component(
//                            copy
//                    ).build();
//                }
//            }
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

    @Override
    public void fixedUpdate(Engine engine, double deltaTime) {
        super.fixedUpdate(engine, deltaTime);
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    public Camera getCamera() {
        return camera;
    }

    public boolean isWireframe() {
        return wireframe;
    }

    public Skybox getSkybox() {
        return skybox;
    }
}

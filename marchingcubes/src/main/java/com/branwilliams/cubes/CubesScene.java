package com.branwilliams.cubes;

import com.branwilliams.bundi.engine.core.AbstractScene;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.core.pipeline.passes.DisableWireframeRenderPass;
import com.branwilliams.bundi.engine.core.pipeline.passes.EnableWireframeRenderPass;
import com.branwilliams.bundi.engine.shader.Camera;
import com.branwilliams.bundi.engine.shader.Projection;
import com.branwilliams.bundi.engine.skybox.SkyboxRenderPass;
import com.branwilliams.bundi.engine.systems.DebugCameraMoveSystem;
import com.branwilliams.cubes.pipeline.GridCellRenderPass;

/**
 * @author Brandon
 * @since November 30, 2019
 */
public class CubesScene extends AbstractScene {

    private Camera camera;

    private boolean wireframe;

    private GridCell gridCell;

    private GridCellMesh gridCellMesh;

    public CubesScene() {
        super("cubes");
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        es.addSystem(new DebugCameraMoveSystem(this, this::getCamera, 0.16F, 16F));
        es.initSystems(engine, window);

        Projection worldProjection = new Projection(window, 70, 0.01F, 1000F);
        RenderContext renderContext = new RenderContext(worldProjection);

        RenderPipeline<RenderContext> renderPipeline = new RenderPipeline<>(renderContext);
        renderPipeline.addLast(new EnableWireframeRenderPass(this::isWireframe));
        renderPipeline.addLast(new GridCellRenderPass(this, this::getCamera));
        renderPipeline.addLast(new DisableWireframeRenderPass(this::isWireframe));
        CubesRenderer renderer = new CubesRenderer(this, renderPipeline);
        setRenderer(renderer);
    }

    @Override
    public void play(Engine engine) {
        camera = new Camera();
        camera.setPosition(20, 20, 60);
        camera.lookAt(16, 16, 16);

        GridCellKernelBuilder gridCellKernelBuilder = new GridCellKernelBuilder();
        GridCellBuilder gridCellBuilder = new GridCellBuilder(gridCellKernelBuilder);
        gridCell = gridCellBuilder.buildGridCell(32, 32, 32, 0.25F);

        GridCellMeshBuilder gridCellMeshBuilder = new GridCellMeshBuilder();
        gridCellMesh = gridCellMeshBuilder.buildMesh(gridCell);
    }

    @Override
    public void pause(Engine engine) {

    }

    public GridCell getGridCell() {
        return gridCell;
    }

    public GridCellMesh getGridCellMesh() {
        return gridCellMesh;
    }

    public Camera getCamera() {
        return camera;
    }

    public boolean isWireframe() {
        return wireframe;
    }
}

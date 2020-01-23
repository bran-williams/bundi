package com.branwilliams.cubes;

import com.branwilliams.bundi.engine.core.AbstractScene;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.core.pipeline.passes.DisableWireframeRenderPass;
import com.branwilliams.bundi.engine.core.pipeline.passes.EnableWireframeRenderPass;
import com.branwilliams.bundi.engine.shader.Camera;
import com.branwilliams.bundi.engine.shader.DirectionalLight;
import com.branwilliams.bundi.engine.shader.Projection;
import com.branwilliams.bundi.engine.shader.Transformation;
import com.branwilliams.bundi.engine.systems.DebugCameraMoveSystem;
import com.branwilliams.cubes.pipeline.GridCellRenderPass;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;

/**
 * @author Brandon
 * @since November 30, 2019
 */
public class CubesScene extends AbstractScene implements Window.KeyListener {

    private static final int CELL_SIZE = 1;

    private static final int GRID_CELL_SIZE_X = 32;

    private static final int GRID_CELL_SIZE_Y = 64;

    private static final int GRID_CELL_SIZE_Z = 32;

    private Camera camera;

    private DirectionalLight sun = new DirectionalLight(
            new Vector3f(-0.2F, -1F, -0.3F), // direction
            new Vector3f(0.5F),  // ambient
            new Vector3f(0.4F),  // diffuse
            new Vector3f(0.5F)); // specular

    private boolean wireframe;

    public CubesScene() {
        super("cubes");
        this.addKeyListener(this);
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        es.addSystem(new DebugCameraMoveSystem(this, this::getCamera, 0.16F, 16F));
        es.initSystems(engine, window);

        Projection worldProjection = new Projection(window, 70, 0.01F, 1000F);
        RenderContext renderContext = new RenderContext(worldProjection);

        RenderPipeline<RenderContext> renderPipeline = new RenderPipeline<>(renderContext);
        renderPipeline.addLast(new EnableWireframeRenderPass(this::isWireframe));
        renderPipeline.addLast(new GridCellRenderPass(this, this::getSun, this::getCamera));
        renderPipeline.addLast(new DisableWireframeRenderPass(this::isWireframe));
        CubesRenderer renderer = new CubesRenderer(this, renderPipeline);
        setRenderer(renderer);
    }

    @Override
    public void play(Engine engine) {
        camera = new Camera();
        camera.setPosition(20, 20, 60);
        camera.lookAt(16, 16, 16);

        GridCellGridBuilder gridCellGridBuilder = new GridCellGridBuilder(CELL_SIZE);
        GridCellMeshBuilder gridCellMeshBuilder = new GridCellMeshBuilder();

//        Vector3f offset = new Vector3f(0, 0, 0);
//        Grid3f<GridCell> gridCellGrid = gridCellGridBuilder.buildGridCellGrid(offset,
//                GRID_CELL_SIZE_X, GRID_CELL_SIZE_Y, GRID_CELL_SIZE_Z);
//        GridCellMesh gridCellMesh = gridCellMeshBuilder.buildMesh(gridCellGrid);
//
//        es.entity("grid")
//                .component(
//                        new Transformation().position(offset),
//                        gridCellMesh)
//                .build();


        buildGridOfGrids(gridCellGridBuilder, gridCellMeshBuilder, 4);
    }

    private void buildGridOfGrids(GridCellGridBuilder gridCellGridBuilder, GridCellMeshBuilder gridCellMeshBuilder,
                                  int numGrids) {
        int halfNumGrids = numGrids / 2;

        for (int i = -halfNumGrids; i < halfNumGrids; i++) {
            for (int j = -halfNumGrids; j < halfNumGrids; j++) {
                Vector3f offset = new Vector3f(i * GRID_CELL_SIZE_X, 0, j * GRID_CELL_SIZE_Z);

                Grid3f<GridCell> gridCellGrid = gridCellGridBuilder.buildGridCellGrid(offset,
                        GRID_CELL_SIZE_X, GRID_CELL_SIZE_Y, GRID_CELL_SIZE_Z);

                GridCellMesh gridCellMesh = gridCellMeshBuilder.buildMesh(gridCellGrid);

                es.entity("grid-" + i + ":" + j)
                        .component(
                                new Transformation().position(offset),
                                gridCellMesh)
                        .build();
            }
        }
    }

    @Override
    public void pause(Engine engine) {

    }

    public DirectionalLight getSun() {
        return sun;
    }

    public Camera getCamera() {
        return camera;
    }

    public boolean isWireframe() {
        return wireframe;
    }

    @Override
    public void keyPress(Window window, int key, int scancode, int mods) {
        if (key == GLFW_KEY_R) {
            wireframe = !wireframe;
        }
    }

    @Override
    public void keyRelease(Window window, int key, int scancode, int mods) {

    }
}

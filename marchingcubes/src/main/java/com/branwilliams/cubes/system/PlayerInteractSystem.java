package com.branwilliams.cubes.system;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.KeyListener;
import com.branwilliams.bundi.engine.core.window.MouseListener;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.ecs.AbstractSystem;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.engine.ecs.matchers.ClassComponentMatcher;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.cubes.CubesScene;
import com.branwilliams.cubes.GridCell;
import com.branwilliams.cubes.GridCellMesh;
import com.branwilliams.cubes.world.MarchingCubeChunk;
import org.joml.Vector3f;

import java.util.function.Supplier;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Brandon
 * @since January 25, 2020
 */
public class PlayerInteractSystem extends AbstractSystem implements KeyListener, MouseListener {

    private final CubesScene scene;

    private final Supplier<Float> raycastDistance;

    private int pressed;

    private boolean shouldEdit;

    public PlayerInteractSystem(CubesScene scene, Supplier<Float> raycastDistance) {
        super(new ClassComponentMatcher(Transformable.class, GridCellMesh.class));
        this.scene = scene;
        this.scene.addMouseListener(this);
        this.scene.addKeyListener(this);
        this.raycastDistance = raycastDistance;
    }

    @Override
    public void init(Engine engine, EntitySystemManager entitySystemManager, Window window) {

    }

    @Override
    public void update(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {
//        RaycastResult result = scene.getWorld().raycast(scene.getCamera().getPosition(),
//                scene.getCamera().getDirection(),
//                raycastDistance.get());
//        scene.setRaycast(result);

        Vector3f ray = scene.getCamera().getFacingDirection().mul(raycastDistance.get());
        if (scene.getRaycast() != null && pressed != -1 && shouldEdit) {
            MarchingCubeChunk chunk = scene.getWorld().getChunk(scene.getRaycast().position);
            if (chunk != null) {
                GridCell gridCell = scene.getWorld().getGridCell(scene.getRaycast().position);
                if (gridCell != null) {
                    for (int i = 0; i < gridCell.getIsoValues().length; i++) {
                        gridCell.getIsoValues()[i] += pressed == 0 ? 0.01F : -0.01F;
                    }

                    for (int x = -1; x <= 1; x++) {
                        for (int y = -1; y <= 1; y++) {
                            for (int z = -1; z <= 1; z++) {

                                // skip origin
                                if (x == 0 && y == 0 && z == 0)
                                    continue;
                                updateNeighborCells(gridCell, scene.getRaycast().position, x, y, z);
                            }
                        }
                    }
                    chunk.markDirty();
                }
            }

        }
    }

    private void updateNeighborCells(GridCell origin, Vector3f pos, float x, float y, float z) {
        boolean markdirty = false;
        GridCell neighbor = scene.getWorld().getGridCell(pos.x + x, pos.y + y, pos.z + z);

        if (neighbor == null)
            return;

        for (int i = 0; i < origin.getPoints().length; i++) {
            Vector3f originPosition = origin.getPoints()[i];
            for (int j = 0; j < neighbor.getPoints().length; j++) {
                Vector3f neighborPosition = neighbor.getPoints()[j];
                if (originPosition.equals(neighborPosition)) {
                    neighbor.getIsoValues()[j] = origin.getIsoValues()[i];
                    markdirty = true;
                }
            }
        }
        if (markdirty)
            scene.getWorld().getChunk(pos.x + x, pos.y + y, pos.z + z).markDirty();
    }

    @Override
    public void fixedUpdate(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {
    }

    @Override
    public void move(Window window, float newMouseX, float newMouseY, float oldMouseX, float oldMouseY) {

    }

    @Override
    public void press(Window window, float mouseX, float mouseY, int buttonId) {
        pressed = buttonId;
    }

    @Override
    public void release(Window window, float mouseX, float mouseY, int buttonId) {
        pressed = -1;
    }

    @Override
    public void wheel(Window window, double xoffset, double yoffset) {

    }

    @Override
    public void keyPress(Window window, int key, int scancode, int mods) {
        float increment = 0.01F;
        if (key == GLFW_KEY_E) {
            scene.getWorld().setIsoLevel(scene.getWorld().getIsoLevel() + increment);
            scene.getWorld().reloadChunks();
        }
        if (key == GLFW_KEY_Q) {
            scene.getWorld().setIsoLevel(scene.getWorld().getIsoLevel() - increment);
            scene.getWorld().reloadChunks();
        }
        if (key == GLFW_KEY_LEFT_CONTROL || key == GLFW_KEY_RIGHT_CONTROL) {
            shouldEdit = !shouldEdit;
        }
    }

    @Override
    public void keyRelease(Window window, int key, int scancode, int mods) {

    }
}

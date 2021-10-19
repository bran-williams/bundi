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
import com.branwilliams.cubes.math.RaycastResult;
import com.branwilliams.cubes.world.MarchingCubeChunk;
import com.branwilliams.cubes.world.MarchingCubeData;

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

    private int editRadius = 1;

    public PlayerInteractSystem(CubesScene scene, Supplier<Float> raycastDistance) {
        super(new ClassComponentMatcher(Transformable.class));
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
//        Vector3f ray = scene.getCamera().getFacingDirection().mul(raycastDistance.get());
        if (shouldEdit) {
            RaycastResult raycast = scene.getWorld().raycast(scene.getCamera().getPosition(),
                    scene.getCamera().getFacingDirection(), scene.getRaycastDistance());
            scene.setRaycast(raycast);

            if (raycast != null && pressed != -1) {
                MarchingCubeChunk chunk = scene.getWorld().getChunk(raycast.position);

                if (chunk != null) {
                    for (int x = -editRadius; x <= editRadius; x++) {
                        for (int y = -editRadius; y <= editRadius; y++) {
                            for (int z = -editRadius; z <= editRadius; z++) {
                                MarchingCubeData gridData = scene.getWorld().getGridData(raycast.position.x + x,
                                        raycast.position.y + y, raycast.position.z + z);
                                if (gridData != null) {
                                    chunk = scene.getWorld().getChunk(raycast.position.x + x,
                                            raycast.position.y + y, raycast.position.z + z);
                                    chunk.markDirty();
                                    gridData.setIsoValue(gridData.getIsoValue() +
                                            (pressed == 0 ? 0.2F : -0.2F) * (float) deltaTime);
                                    scene.getWorld().updateNeighborChunks(gridData, chunk,
                                            raycast.position.x + x, raycast.position.y + y,
                                            raycast.position.z + z);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            scene.setRaycast(null);
        }

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
        if (shouldEdit) {
            if (yoffset > 0) {
                editRadius++;
            } else if (yoffset < 0) {
                editRadius--;
            }
            editRadius = Math.max(1, editRadius);
        }
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

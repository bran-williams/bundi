package com.branwilliams.bundi.voxel.system.player;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.KeyListener;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.ecs.AbstractSystem;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.ecs.matchers.ClassComponentMatcher;
import com.branwilliams.bundi.voxel.VoxelGameState;
import com.branwilliams.bundi.voxel.VoxelScene;
import com.branwilliams.bundi.voxel.components.PlayerControls;
import com.branwilliams.bundi.voxel.render.gui.screens.PauseGuiScreen;

/**
 *
 * */
public class PlayerPauseSystem extends AbstractSystem implements KeyListener {

    private final VoxelScene scene;

    public PlayerPauseSystem(VoxelScene scene) {
        super(new ClassComponentMatcher(PlayerControls.class));
        this.scene = scene;
        scene.addKeyListener(this);
    }

    @Override
    public void init(Engine engine, EntitySystemManager entitySystemManager, Window window) {
    }

    @Override
    public void update(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {
        if (scene.getGameState() != VoxelGameState.INGAME && scene.getGuiScreen() == null) {
            engine.getWindow().disableCursor();
            scene.onGameUnpaused();
        }
    }

    @Override
    public void fixedUpdate(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {

    }

    @Override
    public void keyPress(Window window, int key, int scancode, int mods) {
        if (scene.getGameState() == VoxelGameState.INGAME) {
            for (IEntity entity : getEs().getEntities(this)) {
                PlayerControls playerControls = entity.getComponent(PlayerControls.class);

                if (playerControls.getPause().getKeyCode() == key) {
                    scene.onGamePaused();
                    window.showCursor();
                    window.centerCursor();
                    scene.setGuiScreen(new PauseGuiScreen(scene));
                }
            }
        }
    }

    @Override
    public void keyRelease(Window window, int key, int scancode, int mods) {

    }

}

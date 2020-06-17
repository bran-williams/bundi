package com.branwilliams.bundi.voxel.system;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.ecs.AbstractSystem;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.engine.ecs.matchers.ClassComponentMatcher;
import com.branwilliams.bundi.voxel.VoxelScene;

public class VoxelGameStateUpdateSystem extends AbstractSystem {

    private final VoxelScene scene;

    public VoxelGameStateUpdateSystem(VoxelScene scene) {
        super(new ClassComponentMatcher());
        this.scene = scene;
    }

    @Override
    public void init(Engine engine, EntitySystemManager entitySystemManager, Window window) {

    }

    @Override
    public void update(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {
//        VoxelGameState gameState = scene.getGameState();
//
//        if (scene.getGuiScreen() instanceof MainMenuGuiScreen && gameState != VoxelGameState.MAIN_MENU) {
//            scene.setGameState(VoxelGameState.MAIN_MENU);
//        } else if (scene.getGuiScreen() != null && gameState != VoxelGameState.MAIN_MENU
//                && gameState != VoxelGameState.PAUSED) {
//            scene.setGameState(VoxelGameState.PAUSED);
//        } else if (scene.getGuiScreen() == null && gameState != VoxelGameState.INGAME) {
//            scene.setGameState(VoxelGameState.INGAME);
//        }
    }

    @Override
    public void fixedUpdate(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {

    }

}

package com.branwilliams.bundi.voxel.gui.screens;

import com.branwilliams.bundi.gui.api.ContainerManager;
import com.branwilliams.bundi.gui.api.components.Button;
import com.branwilliams.bundi.voxel.gui.VoxelGuiScreen;
import com.branwilliams.bundi.voxel.scene.VoxelMainMenuScene;


public class MainMenuGuiScreen extends VoxelGuiScreen<VoxelMainMenuScene> {

    private final VoxelMainMenuScene scene;

    public MainMenuGuiScreen(VoxelMainMenuScene scene) {
        super(scene::getGuiScreenManager, "voxel/ui/voxel-main-menu.xml");
        this.scene = scene;
    }

    @Override
    protected void onLoadUI(ContainerManager containerManager) {
        Button playButton = containerManager.getByTag("play_button");
        playButton.onPressed(((button, clickAction) -> {
            scene.playGame();
            return true;
        }));

        Button quitButton = containerManager.getByTag("quit_button");
        quitButton.onPressed(((button, clickAction) -> {
            scene.stop();
            return true;
        }));
    }

    @Override
    public boolean shouldEscape() {
        return false;
    }
}

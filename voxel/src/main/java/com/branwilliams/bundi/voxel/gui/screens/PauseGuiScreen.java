package com.branwilliams.bundi.voxel.gui.screens;

import com.branwilliams.bundi.gui.api.ContainerManager;
import com.branwilliams.bundi.gui.api.components.Button;
import com.branwilliams.bundi.voxel.scene.VoxelScene;
import com.branwilliams.bundi.voxel.gui.VoxelGuiScreen;

public class PauseGuiScreen extends VoxelGuiScreen<VoxelScene> {

    private final VoxelScene scene;

    public PauseGuiScreen(VoxelScene scene) {
        super(scene::getGuiScreenManager, "voxel/ui/voxel-pause.xml");
        this.scene = scene;
    }

    @Override
    protected void onLoadUI(ContainerManager containerManager) {
        Button resumeButton = containerManager.getByTag("resume_button");
        resumeButton.onPressed(((button, clickAction) -> {
            screenManager.get().setGuiScreen(null);
            return true;
        }));

        Button optionsButton = containerManager.getByTag("options_button");
        optionsButton.onPressed(((button, clickAction) -> {
            screenManager.get().setGuiScreen(new OptionsGuiScreen(this, scene));
            return true;
        }));

        Button quitButton = containerManager.getByTag("quit_button");
        quitButton.onPressed(((button, clickAction) -> {
            scene.quitToMainMenu();
            return true;
        }));
    }

}

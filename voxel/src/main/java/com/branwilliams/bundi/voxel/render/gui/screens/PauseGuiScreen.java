package com.branwilliams.bundi.voxel.render.gui.screens;

import com.branwilliams.bundi.gui.api.components.Button;
import com.branwilliams.bundi.voxel.VoxelScene;
import com.branwilliams.bundi.voxel.render.gui.VoxelGuiScreen;

public class PauseGuiScreen extends VoxelGuiScreen {

    public PauseGuiScreen(VoxelScene scene) {
        super(scene, "ui/voxel-pause.xml");

        Button resumeButton = containerManager.getByTag("resume_button");

        resumeButton.onPressed(((button, clickAction) -> {
            scene.getVoxelSoundManager().playButtonSoundEffect();
            scene.setGuiScreen(null);
            return true;
        }));

        Button optionsButton = containerManager.getByTag("options_button");

        optionsButton.onPressed(((button, clickAction) -> {
            scene.setGuiScreen(new OptionsGuiScreen(this, scene));
            scene.getVoxelSoundManager().playButtonSoundEffect();
            return true;
        }));

        Button quitButton = containerManager.getByTag("quit_button");
        quitButton.onPressed(((button, clickAction) -> {
            scene.stop();
            return true;
        }));
    }

}

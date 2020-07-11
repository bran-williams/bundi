package com.branwilliams.bundi.voxel.render.gui.screens;

import com.branwilliams.bundi.gui.api.components.Button;
import com.branwilliams.bundi.gui.api.components.Checkbox;
import com.branwilliams.bundi.gui.api.components.Label;
import com.branwilliams.bundi.gui.api.components.Slider;
import com.branwilliams.bundi.gui.screen.GuiScreen;
import com.branwilliams.bundi.voxel.VoxelScene;
import com.branwilliams.bundi.voxel.render.gui.VoxelGuiScreen;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class OptionsGuiScreen extends VoxelGuiScreen {

    public OptionsGuiScreen(GuiScreen<VoxelScene> previous, VoxelScene scene) {
        super(scene, previous, "ui/voxel-options.xml");

        Button previousButton = containerManager.getByTag("previous_button");
        previousButton.onPressed(((button, clickAction) -> {
            scene.getGuiScreenManager().setGuiScreen(previous);
            return true;
        }));

        Checkbox playMusicCheckbox = containerManager.getByTag("play_music_checkbox");
        playMusicCheckbox.setEnabled(scene.getGameSettings().isPlayMusic());
        playMusicCheckbox.onPressed(((checkbox, clickAction) -> {
            boolean state = !scene.getGameSettings().isPlayMusic();

            scene.getGameSettings().setPlayMusic(state);
            checkbox.setEnabled(state);

            if (state) {
                scene.getVoxelSoundManager().getMusicSource().play();
            } else {
                scene.getVoxelSoundManager().getMusicSource().stop();
            }
            return true;
        }));

        Checkbox toggleVsync = containerManager.getByTag("toggle_vsync");
        toggleVsync.setEnabled(scene.getGameSettings().isVsync());
        toggleVsync.onPressed(((checkbox, clickAction) -> {
            boolean state = !scene.getWindow().isVsync();

            scene.getGameSettings().setVsync(state);
            checkbox.setEnabled(state);

            scene.getWindow().setVsync(state);
            return true;
        }));

        Checkbox toggleFullscreen = containerManager.getByTag("toggle_fullscreen");
        toggleFullscreen.setEnabled(scene.getWindow().isFullscreen());
        toggleFullscreen.onPressed(((checkbox, clickAction) -> {
            boolean state = !scene.getWindow().isFullscreen();

            scene.getGameSettings().setFullscreen(state);
            checkbox.setEnabled(scene.getWindow().isFullscreen());

            scene.getWindow().setFullscreen(state);

            return true;
        }));

        Slider slider = containerManager.getByTag("music_volume_slider");

        Label sliderValueLabel = containerManager.getByTag("music_volume_value");

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);

        slider.onValueChange((s) -> {
            float roundedValue = Float.parseFloat(df.format(s.getSliderPercentage()));
            sliderValueLabel.setText((int) (roundedValue * 100) + "%");
            scene.getVoxelSoundManager().getMusicSource().setGain(roundedValue);
        });
        slider.setSliderPercentage(scene.getVoxelSoundManager().getMusicSource().getGain());

    }
}

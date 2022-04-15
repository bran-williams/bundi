package com.branwilliams.bundi.voxel.gui.screens;

import com.branwilliams.bundi.gui.api.ContainerManager;
import com.branwilliams.bundi.gui.api.components.*;
import com.branwilliams.bundi.gui.screen.GuiScreen;
import com.branwilliams.bundi.voxel.scene.VoxelScene;
import com.branwilliams.bundi.voxel.gui.VoxelGuiScreen;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import static com.branwilliams.bundi.voxel.VoxelConstants.MAX_LIGHT;

public class OptionsGuiScreen extends VoxelGuiScreen<VoxelScene> {

    private final VoxelScene scene;

    public OptionsGuiScreen(GuiScreen<VoxelScene> previous, VoxelScene scene) {
        super(scene::getGuiScreenManager, previous, "voxel/ui/voxel-options.xml");
        this.scene = scene;
    }

    @Override
    protected void onLoadUI(ContainerManager containerManager) {
        Button previousButton = containerManager.getByTag("previous_button");
        previousButton.onPressed(((button, clickAction) -> {
            screenManager.get().setGuiScreen(previous);
            return true;
        }));

        Checkbox playMusicCheckbox = containerManager.getByTag("play_music_checkbox");
        playMusicCheckbox.setEnabled(scene.getGameSettings().isPlayMusic());
        playMusicCheckbox.onPressed(((checkbox, clickAction) -> {
            boolean state = !scene.getGameSettings().isPlayMusic();

            scene.getGameSettings().setPlayMusic(state);
            checkbox.setEnabled(state);

            if (state) {
                scene.getVoxelSoundManager().getMusicSoundSource().play();
            } else {
                scene.getVoxelSoundManager().getMusicSoundSource().stop();
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

        Slider musicVolumeSlider = containerManager.getByTag("music_volume_slider");
        Label musicVolumeLabel = containerManager.getByTag("music_volume_value");

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);

        musicVolumeSlider.onValueChange((s) -> {
            float roundedValue = Float.parseFloat(df.format(s.getSliderPercentage()));
            musicVolumeLabel.setText((int) (roundedValue * 100) + "%");
            scene.getGameSettings().setMusicVolume(roundedValue);
            scene.getVoxelSoundManager().getMusicSoundSource().setGain(roundedValue);
        });
        musicVolumeLabel.setText((int) (scene.getGameSettings().getMusicVolume() * 100) + "%");
        musicVolumeSlider.setSliderPercentage(scene.getGameSettings().getMusicVolume());


        Slider renderDistanceSlider = containerManager.getByTag("render_distance_slider");
        Label renderDistanceLabel = containerManager.getByTag("render_distance_value");
        int maxChunkRenderDistance = 16;

        renderDistanceSlider.onValueChange((s) -> {
            float roundedValue = Float.parseFloat(df.format(s.getSliderPercentage()));
            int renderDistance = (int) (roundedValue * maxChunkRenderDistance);
            renderDistanceLabel.setText(String.valueOf(renderDistance));
            scene.getGameSettings().setChunkRenderDistance(renderDistance);
        });
        renderDistanceLabel.setText(String.valueOf(scene.getGameSettings().getChunkRenderDistance()));
        renderDistanceSlider.setSliderPercentage((float) scene.getGameSettings().getChunkRenderDistance() / (float) maxChunkRenderDistance);


        Slider minLightSlider = containerManager.getByTag("min_light_slider");
        Label minLightLabel = containerManager.getByTag("min_light_value");

        minLightSlider.onValueChange((s) -> {
            float roundedValue = Float.parseFloat(df.format(s.getSliderPercentage()));
            int minLight = (int) (roundedValue * MAX_LIGHT);
            minLightLabel.setText(String.valueOf(minLight));
            scene.getGameSettings().setMinBlockLight(minLight);
        });
        minLightLabel.setText(String.valueOf(scene.getGameSettings().getMinBlockLight()));
        minLightSlider.setSliderPercentage((float) scene.getGameSettings().getMinBlockLight() / MAX_LIGHT);
    }
}

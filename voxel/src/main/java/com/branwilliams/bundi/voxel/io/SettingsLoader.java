package com.branwilliams.bundi.voxel.io;

import com.branwilliams.bundi.voxel.components.GameSettings;
import com.branwilliams.bundi.voxel.components.PlayerControls;

/**
 * @author Brandon
 * @since August 15, 2019
 */
public class SettingsLoader {

    private final JsonLoader jsonLoader;

    public SettingsLoader(JsonLoader jsonLoader) {
        this.jsonLoader = jsonLoader;
    }

    public PlayerControls loadPlayerControls() {
        return jsonLoader.loadObject(PlayerControls.class, "controls.json");
    }

    public GameSettings loadGameSettings() {
        return jsonLoader.loadObject(GameSettings.class, "game_settings.json");
    }

}

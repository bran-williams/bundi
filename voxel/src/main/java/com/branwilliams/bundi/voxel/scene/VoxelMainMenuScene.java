package com.branwilliams.bundi.voxel.scene;

import com.branwilliams.bundi.engine.core.AbstractScene;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.gui.impl.ColorPack;
import com.branwilliams.bundi.gui.screen.GuiScreen;
import com.branwilliams.bundi.gui.screen.GuiScreenManager;
import com.branwilliams.bundi.voxel.io.JsonLoader;
import com.branwilliams.bundi.voxel.io.VoxelTexturePack;
import com.branwilliams.bundi.voxel.render.VoxelMainMenuRenderer;
import com.branwilliams.bundi.voxel.gui.screens.MainMenuGuiScreen;
import com.branwilliams.bundi.voxel.util.VoxelUtils;
import com.branwilliams.bundi.voxel.voxels.VoxelRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

public class VoxelMainMenuScene extends AbstractScene {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private GuiScreenManager<VoxelMainMenuScene> guiScreenManager;

    private VoxelRegistry voxelRegistry;

    private VoxelTexturePack texturePack;

    private boolean stop = false;

    private boolean playGame = false;

    public VoxelMainMenuScene() {
        super("voxel_main_menu");
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        super.init(engine, window);

        guiScreenManager = new GuiScreenManager<>(this);
        guiScreenManager.init(engine, window);
        ColorPack.GREY.apply(guiScreenManager.getToolbox());

        window.showCursor();

        VoxelMainMenuRenderer voxelRenderer = new VoxelMainMenuRenderer(this);
        setRenderer(voxelRenderer);


        Path assetDirectory = VoxelUtils.getAssetDirectory(engine.getContext());
        JsonLoader jsonLoader = new JsonLoader(assetDirectory);
        jsonLoader.initialize(window.getKeycodes());

        voxelRegistry = VoxelUtils.loadVoxelRegistry(jsonLoader,
                Paths.get("voxel_properties_hd.json"));
        texturePack = VoxelUtils.loadVoxelTextures(voxelRegistry, jsonLoader, assetDirectory,
                Paths.get("default_textures.json"));
    }

    @Override
    public void update(Engine engine, double updateInterval) {
        super.update(engine, updateInterval);

        if (this.getGuiScreen() != null) {
            this.getGuiScreen().update();
        }

        if (stop) {
            engine.stop();
        } else if (playGame) {
            engine.setScene(new VoxelScene(voxelRegistry, texturePack));
        }
    }

    @Override
    public void play(Engine engine) {
        this.guiScreenManager.setGuiScreen(new MainMenuGuiScreen(this));
    }

    @Override
    public void pause(Engine engine) {

    }

    @Override
    public boolean destroyUponReplacement() {
        return true;
    }

    /**
     * Sets the boolean 'stop' to true, signaling to this scene to stop the engine, ultimately stopping this
     * application.
     * */
    public void stop() {
        stop = true;
    }

    public void playGame() {
        playGame = true;
    }

    public GuiScreen<VoxelMainMenuScene> getGuiScreen() {
        return guiScreenManager.getGuiScreen();
    }

    public GuiScreenManager<VoxelMainMenuScene> getGuiScreenManager() {
        return guiScreenManager;
    }

    public VoxelRegistry getVoxelRegistry() {
        return voxelRegistry;
    }

    public VoxelTexturePack getTexturePack() {
        return texturePack;
    }
}


package com.branwilliams.bundi.voxel.render.gui;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.gui.api.ContainerManager;
import com.branwilliams.bundi.gui.screen.ContainerScreen;
import com.branwilliams.bundi.gui.screen.GuiScreen;
import com.branwilliams.bundi.voxel.VoxelScene;

import java.util.HashMap;
import java.util.Map;

public class VoxelGuiScreen extends ContainerScreen<VoxelScene> {

    protected final VoxelScene scene;

    protected GuiScreen<VoxelScene> previous;

    public VoxelGuiScreen(VoxelScene scene) {
        this.scene = scene;
    }

    public VoxelGuiScreen(VoxelScene scene, String fileName) {
        this(scene, null, fileName, new HashMap<>());
    }

    public VoxelGuiScreen(VoxelScene scene, GuiScreen<VoxelScene> previous, String fileName) {
        this(scene, previous, fileName, new HashMap<>());
    }

    public VoxelGuiScreen(VoxelScene scene, GuiScreen<VoxelScene> previous, String fileName, Map<String, Object> env) {
        this(scene);
        this.previous = previous;
        ContainerManager containerManager = scene.getGuiScreenManager().load(fileName, env);
        this.setContainerManager(containerManager);
    }

    @Override
    public void initialize(VoxelScene scene, Engine engine, Window window) {
        super.initialize(scene, engine, window);
    }

    @Override
    public void close(VoxelScene scene, Engine engine, Window window) {
        super.close(scene, engine, window);
    }

    @Override
    public void keyPress(Window window, int key, int scancode, int mods) {
        super.keyPress(window, key, scancode, mods);

        // TODO fix me!
        // The pause system will listen for the pause key press and create the pause gui screen. this gui screen is
        // added to the list of listeners and therefore is immediately invoked after the pause system. this causes the
        // pause menu to open and close instantly.
//        if (shouldEscape() && key == scene.getPlayerControls().getPause().getKeyCode()) {
//            scene.setGuiScreen(previous);
//        }
    }

    public boolean shouldEscape() {
        return true;
    }

}

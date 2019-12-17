package com.branwilliams.bundi.voxel.system.player;

import com.branwilliams.bundi.engine.audio.AudioData;
import com.branwilliams.bundi.engine.audio.AudioLoader;
import com.branwilliams.bundi.engine.audio.AudioSource;
import com.branwilliams.bundi.engine.audio.Sound;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Lockable;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.ecs.AbstractSystem;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.ecs.matchers.ClassComponentMatcher;
import com.branwilliams.bundi.gui.api.ContainerManager;
import com.branwilliams.bundi.gui.api.components.Button;
import com.branwilliams.bundi.voxel.VoxelScene;
import com.branwilliams.bundi.voxel.components.PlayerControls;

/**
 * Updates the lockable state of some Lockable whenever the pause key of some entity is pressed.
 *
 * */
public class PlayerPauseSystem extends AbstractSystem implements Window.KeyListener {

    private final VoxelScene scene;

    private final Lockable lockable;

    private AudioSource source;

    public PlayerPauseSystem(VoxelScene scene, Lockable lockable) {
        super(new ClassComponentMatcher(PlayerControls.class));
        this.scene = scene;
        scene.addKeyListener(this);

        this.lockable = lockable;
    }

    @Override
    public void init(Engine engine, EntitySystemManager entitySystemManager, Window window) {
        AudioLoader audioLoader = new AudioLoader(engine.getContext().getAssetDirectory());
        AudioData audioData = audioLoader.loadAudio("sounds/hit1.ogg");
        Sound sound = new Sound(audioData);
        source = new AudioSource();
        source.setPlayback(sound);
    }

    @Override
    public void update(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {
        if (scene.getGuiScreen() != null)
            scene.getGuiScreen().update();
    }

    @Override
    public void fixedUpdate(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {

    }

    @Override
    public void keyPress(Window window, int key, int scancode, int mods) {
        for (IEntity entity : getEs().getEntities(this)) {
            PlayerControls playerControls = entity.getComponent(PlayerControls.class);

            if (playerControls.getPause().getKeyCode() == key) {
                togglePause(window);
            }
        }
    }

    @Override
    public void keyRelease(Window window, int key, int scancode, int mods) {

    }

    private void togglePause(final Window window) {
        lockable.toggle();

        if (lockable.isLocked()) {
            loadPauseMenu(window);
            window.showCursor();
            window.centerCursor();
        } else {
            scene.setGuiScreen(null);
            window.disableCursor();
        }
    }

    private void loadPauseMenu(final Window window) {
        ContainerManager containerManager = scene.getGuiScreenManager().loadAsGuiScreen("./ui/voxel-pause.xml");
        Button resumeButton = containerManager.getByTag("resume_button");

        resumeButton.onPressed(((button, clickAction) -> {
            PlayerPauseSystem.this.togglePause(window);
            source.play();
            return true;
        }));

        Button quitButton = containerManager.getByTag("quit_button");
        quitButton.onPressed(((button, clickAction) -> {
            source.play();
            scene.stop();
            return true;
        }));
    }

    private void loadMainMenu(final Window window) {
        ContainerManager containerManager = scene.getGuiScreenManager().loadAsGuiScreen("./ui/voxel-main-menu.xml");
//        Button resumeButton = containerManager.getByTag("resume_button");
//
//        resumeButton.onPressed(((button, clickAction) -> {
//            PlayerPauseSystem.this.togglePause(window);
//            source.play();
//            return true;
//        }));
//
//        Button quitButton = containerManager.getByTag("quit_button");
//        quitButton.onPressed(((button, clickAction) -> {
//            source.play();
//            scene.stop();
//            return true;
//        }));
    }
}

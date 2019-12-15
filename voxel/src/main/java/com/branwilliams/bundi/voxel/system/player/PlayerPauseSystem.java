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
import com.branwilliams.bundi.engine.font.FontCache;
import com.branwilliams.bundi.engine.font.FontData;
import com.branwilliams.bundi.gui.api.Container;
import com.branwilliams.bundi.gui.api.ContainerManager;
import com.branwilliams.bundi.gui.api.Toolbox;
import com.branwilliams.bundi.gui.api.components.Button;
import com.branwilliams.bundi.gui.api.loader.UILoader;
import com.branwilliams.bundi.gui.impl.BasicRenderer;
import com.branwilliams.bundi.gui.impl.BasicToolbox;
import com.branwilliams.bundi.gui.impl.ColorPack;
import com.branwilliams.bundi.gui.screen.AbstractContainerScreen;
import com.branwilliams.bundi.voxel.VoxelScene;
import com.branwilliams.bundi.voxel.components.PlayerControls;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.branwilliams.bundi.gui.impl.Pointers.FONT_TOOLTIP;

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
            ContainerManager containerManager = scene.getGuiScreenManager().load("./ui/voxel-pause.xml");
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
            window.showCursor();
            window.centerCursor();
        } else {
            scene.setGuiScreen(null);
            window.disableCursor();
        }
    }
}

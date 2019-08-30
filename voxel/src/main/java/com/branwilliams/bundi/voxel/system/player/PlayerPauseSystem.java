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
import com.branwilliams.bundi.engine.font.FontData;
import com.branwilliams.bundi.gui.ContainerManager;
import com.branwilliams.bundi.gui.Toolbox;
import com.branwilliams.bundi.gui.components.Button;
import com.branwilliams.bundi.gui.containers.Frame;
import com.branwilliams.bundi.gui.impl.BasicRenderer;
import com.branwilliams.bundi.gui.impl.BasicToolbox;
import com.branwilliams.bundi.gui.impl.ColorPack;
import com.branwilliams.bundi.gui.layouts.ListLayout;
import com.branwilliams.bundi.voxel.VoxelScene;
import com.branwilliams.bundi.voxel.components.PlayerControls;
import com.branwilliams.bundi.voxel.render.gui.AbstractContainerScreen;
import com.branwilliams.bundi.voxel.render.gui.ValueContainer;

import java.awt.*;

import static com.branwilliams.bundi.gui.impl.Pointers.FONT_TOOLTIP;

/**
 * Updates the lockable state of some Lockable whenever the pause key of some entity is pressed.
 *
 * */
public class PlayerPauseSystem extends AbstractSystem implements Window.KeyListener {

    private static final String[] ITEMS = {
            "bruh",
            "ok",
            "epic"
    };

    private static final int UI_PADDING = 20;

    private final VoxelScene scene;

    private AudioSource source;

    private final ValueContainer.Value pitch_value = new ValueContainer.Value("Pitch", 0F, 1F, 3F, "adjust oof pitch");

    private Toolbox toolbox;

    private FontData smallFont;

    private FontData largeFont;

    private BasicRenderer renderManager;

    private final Lockable lockable;

    public PlayerPauseSystem(VoxelScene scene, Lockable lockable) {
        super(new ClassComponentMatcher(PlayerControls.class));
        this.scene = scene;
        scene.addKeyListener(this);
        this.lockable = lockable;
    }

    @Override
    public void init(Engine engine, EntitySystemManager entitySystemManager, Window window) {
        toolbox = new BasicToolbox(window);
        ColorPack.random().apply(toolbox);

        smallFont = new FontData().setFont(new Font("Trebuchet MS", Font.BOLD, 18), true);

        largeFont = new FontData().setFont(new Font("Trebuchet MS", Font.BOLD, 24), true);

        renderManager = new BasicRenderer(toolbox);
        renderManager.getFontRenderer().setFontData(smallFont);
        toolbox.put(FONT_TOOLTIP, smallFont);

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
                lockable.toggle();

                if (lockable.isLocked()) {
                    scene.setGuiScreen(new AbstractContainerScreen(() -> {
                        ContainerManager containerManager = new ContainerManager(renderManager, toolbox);

                        Frame frame = new Frame("frame_tag", "frame title");
                        frame.setLayering(false);
                        frame.setUseLayoutSize(true);
                        frame.setFont(smallFont);
                        frame.setTooltip("frame tooltip");
                        frame.setPosition(UI_PADDING, UI_PADDING);
                        frame.setSize(300, 200);
                        frame.setLayout(new ListLayout(8, 4));

                        for (String item : ITEMS) {
                            Button button = new Button(item + "_tag", item);
                            button.setTooltip("Run the scene " + item + ".");
                            button.setFont(smallFont);
                            button.onPressed((b, action) -> {
                                source.setPitch(pitch_value.getValue());
                                source.play();
                                return true;
                            });
                            button.setSize(150, 32);
                            frame.add(button);
                        }

                        ValueContainer valueContainer = new ValueContainer(pitch_value, largeFont, smallFont);
                        frame.add(valueContainer);
                        frame.layout();
                        containerManager.add(frame);
                        return containerManager;
                    }));
                    window.showCursor();
                    window.centerCursor();
                } else {
                    scene.setGuiScreen(null);
                    window.disableCursor();
                }
            }
        }
    }

    @Override
    public void keyRelease(Window window, int key, int scancode, int mods) {

    }
}

package com.branwilliams.bundi.voxel.render.gui;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.core.LockableStateListener;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.event.LockableStateUpdateEvent;

/**
 * @author Brandon
 * @since August 17, 2019
 */
public interface GuiScreen extends LockableStateListener, Destructible, Window.KeyListener, Window.MouseListener, Window.WindowListener, Window.CharacterListener {

    default void initialize(Scene scene) {
        scene.addKeyListener(this);
        scene.addMouseListener(this);
        scene.addWindowListener(this);
        scene.addCharacterListener(this);
    }

    void render();

    void update();

    default void close(Scene scene) {
        scene.removeKeyListener(this);
        scene.removeMouseListener(this);
        scene.removeWindowListener(this);
        scene.removeCharacterListener(this);
    }

    @Override
    default void consume(LockableStateUpdateEvent event) {
        Scene scene = (Scene) event.getLockable();
        if (!event.getLockable().isLocked())
            close(scene);
    }

}

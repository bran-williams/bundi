package com.branwilliams.bundi.engine.core;

import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.tukio.EventManager;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by Brandon Williams on 12/27/2017.
 */
public interface Scene extends Nameable, Destructible, Updateable {

    /**
     * Invoked to initialize the scene. This is only invoked once, whenever the scene is first set. This always occurs
     * before the play function. The renderer is initialized immediately after the scene. If the scene is destroyed
     * upon replacement, the init function may be invoked in the event that a new instance of it is set.
     *
     * @param engine The engine playing this scene.
     * @param window The window this scene is rendered onto.
     * */
    void init(Engine engine, Window window) throws Exception;

    /**
     * Invoked when the scene begins. This occurs every time this scene is set by the engine. This always occurs after
     * the {@link Scene#init(Engine, Window)} function.
     *
     * @param engine The engine playing this scene.
     * */
    void play(Engine engine);

    /**
     * Invoked when this scene is replaced with another. Indicates that this scene is no longer being used, but could be
     * used again. This is always invoked before the destroy method and the destroy method is only invoked if
     * {@link Scene#destroyUponReplacement()} returns true.
     *
     * @param engine The engine playing this scene.
     * */
    void pause(Engine engine);

    /**
     * @return True if this scene is ready to be set.
     * */
    boolean isReady();

    /**
     * @return True if this scene should be destroyed if it is replaced with another of the same type.
     * */
    default boolean destroyUponReplacement() {
        return false;
    }

    /**
     * When this scene is played, it's renderer takes over and handles the rendering of this scene. If this returns
     * null, the engine's renderer does not change.
     * @return The renderer this scene uses.
     * */
    @Nullable
    Renderer getRenderer();

    void setRenderer(Renderer renderer);

    /**
     * This {@link EventManager} is used by the engine for dispatching events to subscribers from this
     * scene.
     * */
    EventManager getEventManager();

    List<Window.WindowListener> getWindowListeners();

    void addWindowListener(Window.WindowListener windowListener);

    boolean removeWindowListener(Window.WindowListener windowListener);

    List<Window.MouseListener> getMouseListeners();

    void addMouseListener(Window.MouseListener mouseListener);

    boolean removeMouseListener(Window.MouseListener mouseListener);

    List<Window.KeyListener> getKeyListeners();

    void addKeyListener(Window.KeyListener keyListener);

    boolean removeKeyListener(Window.KeyListener keyListener);

    List<Window.CharacterListener> getCharacterListeners();

    void addCharacterListener(Window.CharacterListener characterListener);

    boolean removeCharacterListener(Window.CharacterListener characterListener);

    List<Window.JoystickListener> getJoystickListeners();

    void addJoystickListener(Window.JoystickListener joystickListener);

    boolean removeJoystickListener(Window.JoystickListener joystickListener);

    EntitySystemManager getEs();

    void setEs(EntitySystemManager es);

}

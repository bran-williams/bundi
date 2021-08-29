package com.branwilliams.bundi.engine.core;

import com.branwilliams.bundi.engine.Profiler;
import com.branwilliams.bundi.engine.core.scenes.ErrorScene;
import com.branwilliams.bundi.engine.core.window.Window;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SceneManager implements Destructible {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Profiler profiler = new Profiler(getClass());

    // Each scene is mapped in order to ensure it is destroyed at the ending of this application and in order to ensure
    // a scene is only initialized once.
    private Map<String, Scene> sceneCache = new HashMap<>();

    /** The current scene of this engine. */
    private Scene current;

    /** When a new scene is set, it is stored in this variable.
     * The engine will attempt to replace {@link SceneManager#current} with this scene every frame.
     * This will only occur once next.isReady() returns true.
     */
    private Scene next;

    /**
     * Updates the current scene to the next scene when possible. If the next scene is not cached it will be initialized
     * and its renderer will also be initialized. Any exceptions thrown will be caught and an error scene will become
     * the next scene. <br/> <br/>
     * The current scene will only be replaced once the next scene returns true from {@link Scene#isReady()}. When the
     * current scene is being replaced, it will be paused. If it returns true from
     * {@link Scene#destroyUponReplacement()}, it will also be destroyed and removed from the cache. The new current
     * scene will be played and any exceptions thrown here will also be caught and an error scene will replace it.
     * @return True if the current scene is not null.
     * */
    public boolean updateScene(Engine engine, Window window) {
        if (hasNext()) {
            initSceneAndCache(engine, window, next);

            // When the scene is ready, pause the old one, set the new one, and play it!
            if (next.isReady()) {
                // Pause and destroy the old scene.
                if (hasCurrentScene()) {
                    pauseScene(engine, window, current);
                }

                // Replace the current scene.
                current = next;
                next = null;

                playScene(engine, window, current);
            }
        }
        return hasCurrentScene();
    }

    /**
     * Initializes a scene if it is not within the cache.
     * */
    private void initSceneAndCache(Engine engine, Window window, Scene scene) {
        // When a scene isn't within this map, it means that it is a new scene or it was destroyed beforehand.
        // Initialize it and put it there!
        if (!sceneCache.containsKey(scene.getName())) {
            sceneCache.put(scene.getName(), scene);
            try {
                log.info("Initializing scene " + scene.getName() + ".");
                profiler.begin("init_scene:" + scene.getName());
                scene.init(engine, window);

                if (scene.getRenderer() != null) {
                    profiler.endBegin("renderer_init");
                    log.info("Initializing renderer " + scene.getRenderer().getName() + ".");
                    scene.getRenderer().init(engine, window);
                }
            } catch (Exception e) {
                log.error("Unable to initialize scene " + scene.getName() + ":");
                handleException(e);
            } finally {
                profiler.end();
            }
        }
    }

    /**
     * Pauses the current scene and removes it from the window as a window event listener.
     * */
    private void pauseScene(Engine engine, Window window, Scene scene) {
        window.removeWindowEventListener(scene);

        profiler.begin("pause_scene:" + scene.getName());
        try {
            scene.pause(engine);
        } catch (Exception e) {
            log.error("Unable to pause scene " + scene.getName() + ":");
            handleException(e);
        } finally {
            profiler.end();
        }

        if (scene.destroyUponReplacement()) {
            scene.destroy();
            sceneCache.remove(scene.getName());
        }
    }

    /**
     * Play the scene which involves adding it as a window listener and setting the engine renderer if the scenes is not
     * null.
     * */
    private void playScene(Engine engine, Window window, Scene scene) {
        window.addWindowEventListener(current);

        // Replace the renderer if necessary.
        if (current.getRenderer() != null) {
            engine.setRenderer(current.getRenderer());
        }

        // Play the scene.
        log.info("Playing scene " + scene.getName() + ".");
        profiler.begin("play_scene:" + scene.getName());
        try {
            scene.play(engine);
        } catch (Exception e) {
            log.error("Unable to play scene " + scene.getName() + ":");
            handleException(e);
        } finally {
            profiler.end();
        }
    }

    /**
     * Sets the scene to an error scene whenever an exception is thrown from a provided scene.
     * */
    private void handleException(Exception exception) {
        exception.printStackTrace();
        setScene(new ErrorScene(exception));
    }

    /**
     * @return True if the current scene is not null.
     * */
    public boolean hasCurrentScene() {
        return current != null;
    }

    /**
     * @return True if the next scene is not null.
     * */
    public boolean hasNext() {
        return next != null;
    }

    /**
     * Sets the next scene to the one provided.
     * @param scene The next scene to play.
     * */
    public void setScene(@NotNull Scene scene) {
        if (scene == null)
            throw new NullPointerException("Scene cannot be null!");
        this.next = scene;
    }

    public Scene getCurrent() {
        return current;
    }

    /**
     * @return The cached scene with the provided name or null if not found.
     * */
    public Scene recallScene(String sceneName) {
        return this.sceneCache.get(sceneName);
    }

    @Override
    public void destroy() {
        try {
            if (!sceneCache.isEmpty())
                sceneCache.values().forEach(Scene::destroy);
        } catch (Exception e) {
            // TODO create an engine exception?
            log.error("Unable to destroy remaining scenes: ", e);
        }
    }
}

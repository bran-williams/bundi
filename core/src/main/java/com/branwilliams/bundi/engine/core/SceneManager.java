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
    private boolean updateScene(Engine engine, Window window) {
        if (next != null) {
            // When a scene isn't within this map, it means that it is a new scene. Initialize it and put it there!
            if (!sceneCache.containsKey(next.getName())) {
                sceneCache.put(next.getName(), next);
                try {
                    log.info("Initializing scene " + next.getName() + ".");
                    profiler.begin("init_scene:" + next.getName());
                    next.init(engine, window);

                    if (next.getRenderer() != null) {
                        profiler.endBegin("renderer_init");
                        log.info("Initializing renderer " + next.getRenderer().getName() + ".");
                        next.getRenderer().init(engine, window);
                    }
                } catch (Exception e) {
                    log.error("Unable to initialize scene " + next.getName() + ":");
                    handleException(e);
                    return hasCurrentScene();
                } finally {
                    profiler.end();
                }
            }

            // When the scene is ready, pause the old one, set the new one, and play it!
            if (next.isReady()) {
                // Pause and destroy the old scene.
                if (current != null) {
                    current.pause(engine);
                    if (current.destroyUponReplacement()) {
                        current.destroy();
                        sceneCache.remove(current.getName());
                    }
                }
                // Replace the current scene.
                current = next;
                window.addWindowEventListener(current);
                next = null;

//                // Replace the renderer and event manager if necessary. Play the scene.
//                if (current.getRenderer() != null)
//                    renderer = current.getRenderer();

                log.info("Playing scene " + current.getName() + ".");
                profiler.begin("play_scene:" + current.getName());
                try {
                    current.play(engine);
                } catch (Exception e) {
                    log.error("Unable to play scene " + current.getName() + ":");
                    handleException(e);
                } finally {
                    profiler.end();
                }
            }
        }
        return hasCurrentScene();
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
    private boolean hasCurrentScene() {
        return current != null;
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

    /**
     * @return The cached scene with the provided name or null if not found.
     * */
    public Scene recallScene(String sceneName) {
        return this.sceneCache.get(sceneName);
    }

    @Override
    public void destroy() {
        try {
            sceneCache.values().forEach(Scene::destroy);
        } catch (Exception e) {
            // TODO create an engine exception?
            log.error("Unable to destroy remaining scenes.");
        }
    }
}

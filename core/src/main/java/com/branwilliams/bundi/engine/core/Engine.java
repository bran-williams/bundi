package com.branwilliams.bundi.engine.core;

import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.core.scenes.ErrorScene;
import org.jetbrains.annotations.NotNull;
import com.branwilliams.bundi.engine.Profiler;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.opengl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.opengl.GLDebugMessageCallback.getMessage;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Engine used to manage updating, rendering, and asset loading/handling. <br/>
 * Created by Brandon on 9/4/2016.
 */
public class Engine implements Runnable {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Profiler profiler = new Profiler("Engine");

    private final EngineContext context;

    private final Window window;

    // ---------------------------------------
    // These variables can be set via the scene.
    // They can also not be set by the scene and one renderer can rule them all!
    //

    private Renderer renderer;

    // ---------------------------------------

    // Set true when the engine is ran.
    private boolean running = false;

    // Rate at which the fixedUpdate function is invoked. In invocations/second.
    private double updateInterval = 1D / 60D;

    // Calculated frame rate.
    private int frames = 0;

    // Each scene is mapped in order to ensure it is destroyed at the ending of this application and in order to ensure
    // a scene is only initialized once.
    private Map<String, Scene> sceneCache = new HashMap<>();

    /** The current scene of this engine. */
    private Scene current;

    /** When a new scene is set, it is stored in this variable.
     * The engine will attempt to replace {@link Engine#current} with this scene every frame.
     * This will only occur once next.isReady() returns true.
     */
    private Scene next;

    private GLCapabilities glCapabilities;

    private ALCapabilities alCapabilities;

    private long alDevice;

    private long alContext;

    public Engine(EngineContext context, Window window, Scene scene) {
        this.context = context;
        this.window = window;
        this.setScene(scene);
        if (window == null)
            throw new IllegalStateException("Window cannot be null!");
    }

    @Override
    public void run() {
        try {
            window.initialize();
            running = true;
            loop();
            closeScenes();
            alcDestroyContext(alContext);
            alcCloseDevice(alDevice);
            window.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (window.hasInitialized())
                glfwTerminate();
            // glfwSetErrorCallback(null).free();
            running = false;
        }
    }

    /**
     * Main game loop.
     * */
    private void loop() throws Exception {
        glCapabilities = GL.createCapabilities();

        ErrorUtils.setupDebugMessageCallback(log);

        log.info("OpenGL version: " + glGetString(GL_VERSION));
        log.info("Graphics card: " + glGetString(GL_RENDERER));
        log.info("Graphics card vendor: " + glGetString(GL_VENDOR));
        log.info("Max texture (1D/2D) size: " + glGetInteger(GL_MAX_TEXTURE_SIZE));

        createALContext();

        // Set the clear color and initial viewport dimensions.
        glClearColor(0F, 0F, 0F, 0F);
        glViewport(0, 0, window.getWidth(), window.getHeight());

        // Time between updates. Used to ensure the update function does not rely on frame rate.
        double time = window.getTime();

        // This is the time (in seconds) between each individual render pass.
        double delta = 0F;

        // This float is an accumulation of deltas, used for the fixed update function within an engine controller.
        double elapsed = 0F;

        // Used to calculate the frame rate.
        double frameTime = time;
        int frames = 0;

        // Loops until the window was closed or running was set to false.
        while (running && !window.shouldClose()) {
            // Update the current scene.
            if (updateScene()) {
                this.profiler.begin("scene_update");

                // Invokes the update function as often as necessary.
                current.update(this, delta);

                this.profiler.endBegin("scene_fixed_update");

                // Invokes the update function as many times as necessary. This is done at a fixed interval.
                while (elapsed >= updateInterval) {
                    current.fixedUpdate(this, updateInterval);
                    elapsed -= updateInterval;
                }

                this.profiler.endBegin("render");

                //checkGLErrors();

                // Invoke the renderer as much as possible.
                if (renderer != null) {
                    renderer.render(this, window, delta);
                }

                // Update the window and viewport if necessary.
                window.update();
                if (window.isResized()) {
                    glViewport(0, 0, window.getWidth(), window.getHeight());
                }
                this.profiler.end();
            }

            double currentTime = window.getTime();

            // Calculate the time it took to render this frame.
            delta = currentTime - time;
            elapsed += delta;
            time = currentTime;

            // Update the frame count and update the frame rate once a second has passed.
            frames++;
            if (currentTime - frameTime >= 1D) {
                this.frames = frames;
                frames = 0;
                frameTime = currentTime;
            }
        }
    }

    private void closeScenes() {
        try {
            sceneCache.values().forEach(Scene::destroy);
        } catch (Exception e) {
            // TODO create an engine exception?
            log.error("Unable to destroy remaining scenes.");
        }

    }

    /**
     * Creates the OpenAL context using the default audio device.
     *
     * */
    private void createALContext() {
        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);

        alDevice = alcOpenDevice(defaultDeviceName);

        int[] attributes = {0};
        alContext = alcCreateContext(alDevice, attributes);
        if (!alcMakeContextCurrent(alContext)) {
            throw new RuntimeException("Unable to create OpenAL context");
        }

        ALCCapabilities alcCapabilities = ALC.createCapabilities(alDevice);
        alCapabilities = AL.createCapabilities(alcCapabilities);

        log.info("OpenAL context created using default device");
    }

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
    private boolean updateScene() {
        if (next != null) {
            // When a scene isn't within this map, it means that it is a new scene. Initialize it and put it there!
            if (!sceneCache.containsKey(next.getName())) {
                sceneCache.put(next.getName(), next);
                try {
                    log.info("Initializing scene " + next.getName() + ".");
                    profiler.begin("init_scene:" + next.getName());
                    next.init(this, window);

                    if (next.getRenderer() != null) {
                        profiler.endBegin("renderer_init");
                        log.info("Initializing renderer " + next.getRenderer().getName() + ".");
                        next.getRenderer().init(this, window);
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
                    current.pause(this);
                    if (current.destroyUponReplacement()) {
                        current.destroy();
                        sceneCache.remove(current.getName());
                    }
                }
                // Replace the current scene.
                current = next;
                window.setScene(current);
                next = null;

                // Replace the renderer and event manager if necessary. Play the scene.
                if (current.getRenderer() != null)
                    renderer = current.getRenderer();

                log.info("Playing scene " + current.getName() + ".");
                profiler.begin("play_scene:" + current.getName());
                try {
                    current.play(this);
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
     * @return True if the current scene is not null.
     * */
    private boolean hasCurrentScene() {
        return current != null;
    }

    /**
     * Sets the scene to an error scene whenever an exception is thrown from a provided scene.
     * */
    private void handleException(Exception exception) {
        exception.printStackTrace();
        setScene(new ErrorScene(exception));
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

    public Window getWindow() {
        return window;
    }

    public double getTime() {
        return window.getTime();
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    /**
     * @return True if this engine is running the game loop.
     * */
    public boolean isRunning() {
        return running;
    }

    /**
     * Stops the game loop the following frame. Simply setting 'running = false'.
     * */
    public void stop() {
        this.running = false;
    }

    public double getUpdateInterval() {
        return updateInterval;
    }

    /**
     * Sets the interval for fixed updates within the game loop. This interval value should be a fraction, such as the
     * following:
     * <pre>
     *    engine.setUpdateInterval(1D / 60D); // 60 updates per second
     *    engine.setUpdateInterval(1D / 20D); // 20 updates per second
     *    engine.setUpdateInterval(1D); // 1 update per second
     * </pre>
     *
     * */
    public void setUpdateInterval(double updateInterval) {
        this.updateInterval = updateInterval;
    }

    public int getFrames() {
        return frames;
    }

    public Profiler getProfiler() {
        return profiler;
    }

    public EngineContext getContext() {
        return context;
    }

    public GLCapabilities getGLCapabilities() {
        return glCapabilities;
    }

    public ALCapabilities getALCapabilities() {
        return alCapabilities;
    }

    /**
     * This utility class is used to translate error codes from OpenGL to their plain English counterparts.
     * */
    private static class ErrorUtils {

        private static void setupDebugMessageCallback(Logger log) {
            GLUtil.setupDebugMessageCallback();

            glDebugMessageCallback((source, type, id, severity, length, message, userParam) -> {
                String src = getDebugSource(source);
                String tpe = getDebugType(type);
                String msg = getMessage(length, message);
                String svrty = getDebugSeverity(severity);
                if (severity != GL_DEBUG_SEVERITY_NOTIFICATION) {
                    log.debug( "OpenGL Debug Message" +
                            "\n\tSource: " + src +
                            "\n\tSeverity: " + svrty +
                            "\n\tType: " + tpe +
                            "\n\tMessage: " + msg
                    );
                }
            }, NULL);
        }

        private static String getDebugSeverity(int severity) {
            switch (severity) {
                case GL_DEBUG_SEVERITY_NOTIFICATION:
                    return "Notification";
                case GL_DEBUG_SEVERITY_LOW:
                    return "Low";
                case GL_DEBUG_SEVERITY_MEDIUM:
                    return "Medium";
                case GL_DEBUG_SEVERITY_HIGH:
                    return "High";
                default:
                    return "Unknown";
            }
        }

        private static String getDebugSource(int source) {
            switch (source) {
                case GL_DEBUG_SOURCE_API:
                    return "OpenGL API";
                case GL_DEBUG_SOURCE_APPLICATION:
                    return "Application";
                case GL_DEBUG_SOURCE_WINDOW_SYSTEM:
                    return "Window System";
                case GL_DEBUG_SOURCE_THIRD_PARTY:
                    return "Third Party";
                case GL_DEBUG_SOURCE_SHADER_COMPILER:
                    return "Shader compiler";
                case GL_DEBUG_SOURCE_OTHER:
                    return "Other";
                default:
                    return "UNKNOWN SOURCE";
            }
        }

        private static String getDebugType(int type) {
            switch (type) {
                case GL_DEBUG_TYPE_ERROR:
                    return "Error";
                case GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR:
                    return "Deprecated Behavior";
                case GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR:
                    return "Undefined behavior";
                case GL_DEBUG_TYPE_PORTABILITY:
                    return "Portability";
                case GL_DEBUG_TYPE_PERFORMANCE:
                    return "Performance";
                case GL_DEBUG_TYPE_MARKER:
                    return "Marker";
                case GL_DEBUG_TYPE_OTHER:
                    return "Other";
                default:
                    return "UNKNOWN TYPE";
            }
        }
    }
}

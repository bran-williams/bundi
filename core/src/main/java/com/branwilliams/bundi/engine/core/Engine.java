package com.branwilliams.bundi.engine.core;

import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.core.window.Window;
import org.jetbrains.annotations.NotNull;
import com.branwilliams.bundi.engine.Profiler;

import org.lwjgl.opengl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.lwjgl.glfw.GLFW.glfwTerminate;
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

    private final Profiler profiler = new Profiler(getClass());

    private final EngineContext context;

    private final Window window;

    // ---------------------------------------
    // These variables can be set via the scene.
    // They can also not be set by the scene and one renderer can rule them all!
    //

    private Renderer renderer;

    // ---------------------------------------

    // Set true when the game loop begins.
    private boolean running = false;

    // Rate at which the fixedUpdate function is invoked. In invocations/second.
    private double updateInterval = 1D / 60D;

    // Calculated frame rate.
    private int frames = 0;

    private GLCapabilities glCapabilities;

    private SceneManager sceneManager;

    private AudioManager audioManager;

    public Engine(EngineContext context, Window window, Scene scene) {
        this.context = context;
        this.window = window;

        if (window == null)
            throw new IllegalStateException("Window cannot be null!");

        this.sceneManager = new SceneManager();
        this.sceneManager.setScene(scene);

        this.audioManager = new AudioManager();
    }

    @Override
    public void run() {
        try {
            init();
            running = true;
            loop();
            destroy();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (window.hasInitialized())
                glfwTerminate();
            running = false;
        }
    }

    /**
     * Initialize the window, Setup debug message callback, create OpenAL context, log some info.
     * */
    private void init() {
        window.initialize();

        glCapabilities = GL.createCapabilities();

        OpenGLDebugMessageLogger.setupDebugMessageCallback(log);

        audioManager.createALContext();

        log.info("OpenGL version: " + glGetString(GL_VERSION));
        log.info("Graphics card: " + glGetString(GL_RENDERER));
        log.info("Graphics card vendor: " + glGetString(GL_VENDOR));
        log.info("Max texture (1D/2D) size: " + glGetInteger(GL_MAX_TEXTURE_SIZE));
    }

    /**
     * Main game loop.
     * */
    private void loop() throws Exception {
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
            if (sceneManager.updateScene(this, window)) {
                this.profiler.begin("scene_update");

                // Invokes the update function as often as necessary.
                sceneManager.getCurrent().update(this, delta);

                this.profiler.endBegin("scene_fixed_update");

                // Invokes the update function as many times as necessary. This is done at a fixed interval.
                while (elapsed >= updateInterval) {
                    sceneManager.getCurrent().fixedUpdate(this, Math.min(updateInterval, elapsed));
                    elapsed -= updateInterval;
                }

                this.profiler.endBegin("render");

                // Invoke the renderer as much as possible.
                if (renderer != null) {
                    renderer.render(this, window, delta);
                }

                // Update the window and viewport if necessary.
                window.update();
                if (window.wasResized()) {
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

    private void destroy() {
        sceneManager.destroy();
        audioManager.destroy();
        window.destroy();
    }

    /**
     * Stops the game loop the following frame. Simply setting 'running = false'.
     * */
    public void stop() {
        this.running = false;
    }

    /**
     * Sets the next scene to the one provided.
     * @param scene The next scene to play.
     * */
    public void setScene(@NotNull Scene scene) {
        this.sceneManager.setScene(scene);
    }

    /**
     * @return The cached scene with the provided name or null if not found.
     * */
    public Scene recallScene(String sceneName) {
        return sceneManager.recallScene(sceneName);
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

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public AudioManager getAudioManager() {
        return audioManager;
    }

    /**
     * This utility class is used to translate error codes from OpenGL to their plain English counterparts.
     * */
    private static class OpenGLDebugMessageLogger {

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

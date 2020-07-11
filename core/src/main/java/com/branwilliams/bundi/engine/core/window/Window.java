package com.branwilliams.bundi.engine.core.window;

import com.branwilliams.bundi.engine.core.Joystick;
import com.branwilliams.bundi.engine.core.Keycode;
import com.branwilliams.bundi.engine.core.Keycodes;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.engine.shader.Transformation;
import org.lwjgl.glfw.*;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.branwilliams.bundi.engine.util.IOUtils.ioResourceToByteBuffer;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * GLFW Window handler. <br/>
 *
 * Created by Brandon on 9/4/2016.
 */
public class Window {

    public static final String DEFAULT_ICON_PATH = "icons/favicon-32x32.png";

    private final Transformable mouseTransform = new Transformation();

    // The listeners for window events. This is updated from within the engine.
    private final List<WindowEventListener> windowEventListeners;

    // All connected joysticks live here.
    private final List<Joystick> connectedJoysticks;

    private long windowId = -1;

    // True only if the init function was able to successfully run.
    private boolean hasInitialized = false;

    private String title;

    private int width, height;

    private float mouseX, mouseY;

    private boolean vsync;

    private boolean fullscreen;

    // True whenever this window is resized. Set to false once it's getter is used.
    private boolean resized = true;

    // True if the mouse is within this window.
    private boolean mouseInside = false;

    private Keycodes keycodes;

    public Window(String title, int width, int height, boolean vsync, boolean fullscreen, Keycodes keycodes) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.vsync = vsync;
        this.fullscreen = fullscreen;
        this.keycodes = keycodes;

        this.windowEventListeners = new ArrayList<>();
        this.connectedJoysticks = new ArrayList<>();
    }

    /**
     * Initializes the window information.
     * */
    public void initialize() throws WindowInitializationException {
        // Sets the error callback to point to the System.err print stream.
        GLFWErrorCallback.createPrint(System.err).set();

        // Uh oh
        if (!glfwInit())
            throw new WindowInitializationException("Unable to initialize GLFW");

        hasInitialized = true;

        // Set default window hints.
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        // glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Prevent legacy versions of OpenGL from being used.
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        glfwWindowHint(GLFW_SAMPLES, 4);

        // Actually creates the window.
        windowId = glfwCreateWindow(width, height, title, fullscreen ? glfwGetPrimaryMonitor() : NULL, NULL);

        setIcon(DEFAULT_ICON_PATH);

        // This will help make sure we don't miss a key press or key release.
        // if we choose to use glfwPollEvents()
        //glfwSetInputMode(windowId, GLFW_STICKY_KEYS, GLFW_TRUE);
        //glfwSetInputMode(windowId, GLFW_STICKY_MOUSE_BUTTONS, GLFW_TRUE);

        // Just in case lol
        glfwSetInputMode(windowId, GLFW_CURSOR, GLFW_CURSOR_NORMAL);

//        glfwSetErrorCallback((error, description) -> {
//            throw new IllegalStateException(GLFWErrorCallback.getDescription(description));
//        });

        // Allows for listeners to recieved key events.
        glfwSetKeyCallback(windowId, (windowId, key, scancode, action, mods) -> {
            switch (action) {
                case GLFW_RELEASE:
                    for (int i = 0; i < windowEventListeners.size(); i++) {
                        windowEventListeners.get(i).keyRelease(this, key, scancode, mods);
                    }
                    break;
                case GLFW_PRESS:
                    for (int i = 0; i < windowEventListeners.size(); i++) {
                        windowEventListeners.get(i).keyPress(this, key, scancode, mods);
                    }
                    break;
                case GLFW_REPEAT:
                    for (int i = 0; i < windowEventListeners.size(); i++) {
                        windowEventListeners.get(i).keyHeld(this, key, scancode, mods);
                    }
                    break;
            }
        });

        // Updates information once the window is resized.
        glfwSetWindowSizeCallback(windowId, (window, width, height) -> {
            this.width = width;
            this.height = height;
            this.resized = true;
            for (int i = 0; i < windowEventListeners.size(); i++) {
                windowEventListeners.get(i).resize(this, width, height);
            }
        });

        // Updates listeners when the mouse wheel has changed.
        glfwSetScrollCallback(windowId, (window, xoffset, yoffset) -> {
            for (int i = 0; i < windowEventListeners.size(); i++) {
                windowEventListeners.get(i).wheel(this, xoffset, yoffset);
            }
        });

        // Update our mouse position information once the mouse moves.
        glfwSetCursorPosCallback(windowId, (window, mouseX, mouseY) -> {
            float oldMouseX = this.mouseX, oldMouseY = this.mouseY;
            this.mouseX = (float) mouseX;
            this.mouseY = (float) mouseY;
            for (int i = 0; i < windowEventListeners.size(); i++) {
                windowEventListeners.get(i).move(this, this.mouseX, this.mouseY, oldMouseX, oldMouseY);
            }
        });

        glfwSetCursorEnterCallback(windowId, (window, inside) -> this.mouseInside = inside);

        // Updates the listeners with mouse clicks.
        glfwSetMouseButtonCallback(windowId, (window, button, action, mods) -> {
            switch (action) {
                case GLFW_RELEASE:
                    for (int i = 0; i < windowEventListeners.size(); i++) {
                        windowEventListeners.get(i).release(this, mouseX, mouseY, button);
                    }
                    break;
                case GLFW_PRESS:
                    for (int i = 0; i < windowEventListeners.size(); i++) {
                        windowEventListeners.get(i).press(this, mouseX, mouseY, button);
                    }
                    break;
            }
        });

        glfwSetCharCallback(windowId, (window, codepoint) -> {
            String characters = String.valueOf(Character.toChars(codepoint));
            for (int i = 0; i < windowEventListeners.size(); i++) {
                windowEventListeners.get(i).charTyped(this, characters);
            }
        });

        glfwSetJoystickCallback((joystickId, action) -> {
            Joystick joystick = Joystick.get(joystickId);

            if (action == GLFW_CONNECTED) {
                connectedJoysticks.add(joystick);

                for (int i = 0; i < windowEventListeners.size(); i++) {
                    windowEventListeners.get(i).onJoystickConnected(joystick);
                }

            } else if (action == GLFW_DISCONNECTED) {
                connectedJoysticks.remove(joystick);

                for (int i = 0; i < windowEventListeners.size(); i++) {
                    windowEventListeners.get(i).onJoystickDisconnected(joystick);
                }
                joystick.destroy();
            }

        });

        // Positions the window using information provided by the vidmode.
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (vidmode != null)
            glfwSetWindowPos(windowId, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);

        // Assigns the window we've initialized as the current glfw context.
        glfwMakeContextCurrent(windowId);

        // Set vsync true if necessary
        if (vsync)
            glfwSwapInterval(1);
        else
            glfwSwapInterval(0);

        // Shows the window.
        glfwShowWindow(windowId);

        // Initially connected joysticks...
        for (Joystick.JoystickId joystickId : Joystick.JoystickId.values()) {
            Joystick joystick = new Joystick(joystickId);

            if (joystick.isConnected()) {
                connectedJoysticks.add(joystick);
            }
        }
    }

    /**
     * Frees the window from glfw and deletes the information regarding it.
     * */
    public void destroy() {
        glfwFreeCallbacks(windowId);
        glfwDestroyWindow(windowId);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(windowId);
    }

    /**
     * Updates the window. This will swap the front and back buffers as well as poll for events.
     * */
    public void update() {
        glfwSwapBuffers(windowId);
        glfwPollEvents();
    }

    /**
     * Sets the icon of this window to the image specified at the path.
     * */
    public void setIcon(String path) {
        IntBuffer w = memAllocInt(1);
        IntBuffer h = memAllocInt(1);
        IntBuffer comp = memAllocInt(1);

        // Icons
        ByteBuffer icon16;
        ByteBuffer icon32;

        try {
            icon16 = ioResourceToByteBuffer(path, 2048);
            icon32 = ioResourceToByteBuffer(path, 4096);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try (GLFWImage.Buffer icons = GLFWImage.malloc(2)) {

            ByteBuffer pixels16 = STBImage.stbi_load_from_memory(icon16, w, h, comp, 4);
            icons.position(0)
                    .width(w.get(0))
                    .height(h.get(0))
                    .pixels(pixels16);

            ByteBuffer pixels32 = STBImage.stbi_load_from_memory(icon32, w, h, comp, 4);
            icons.position(1)
                    .width(w.get(0))
                    .height(h.get(0))
                    .pixels(pixels32);

            icons.position(0);

            glfwSetWindowIcon(windowId, icons);

            STBImage.stbi_image_free(pixels32);
            STBImage.stbi_image_free(pixels16);
        }

        memFree(comp);
        memFree(h);
        memFree(w);
    }

    /**
     * Sets the fullscreen state of this window. Will also have to reset the vsync status.
     * */
    public void setFullscreen(boolean fullscreen) {
        if (fullscreen != this.fullscreen) {
            this.fullscreen = fullscreen;
            glfwSetWindowMonitor(windowId, fullscreen ? glfwGetPrimaryMonitor() : NULL, 0, 0, width, height,
                    GLFW_DONT_CARE);
            this.setVsync(this.vsync);
        }
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public List<Joystick> getConnectedJoysticks() {
        return connectedJoysticks;
    }

    /**
     * @return True if this window was able to successfully initialize.
     * */
    public boolean hasInitialized() {
        return hasInitialized;
    }

    /**
     * @return The current mode the cursor is set to.
     * <br/>
     * See {@link org.lwjgl.glfw.GLFW#GLFW_CURSOR_DISABLED}, {@link org.lwjgl.glfw.GLFW#GLFW_CURSOR_HIDDEN}, and {@link org.lwjgl.glfw.GLFW#GLFW_CURSOR_NORMAL}.
     * */
    public int getCursorMode() {
        return glfwGetInputMode(windowId, GLFW_CURSOR);
    }

    /**
     * Moves the cursor to the center of this window.
     * */
    public void centerCursor() {
        setCursorPosition(width * 0.5F, height * 0.5F);
    }

    /**
     * Moves the cursor to this position within the window.
     * */
    public void setCursorPosition(float x, float y) {
        glfwSetCursorPos(windowId, x, y);
    }

    public boolean isCursorVisible() {
        return getCursorMode() == GLFW_CURSOR_NORMAL;
    }

    /**
     * Sets the cursor mode to {@link org.lwjgl.glfw.GLFW#GLFW_CURSOR_HIDDEN}.
     * */
    public void hideCursor() {
        glfwSetInputMode(windowId, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
    }

    /**
     * Sets the cursor mode to {@link org.lwjgl.glfw.GLFW#GLFW_CURSOR_NORMAL}.
     * */
    public void showCursor() {
        glfwSetInputMode(windowId, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
    }

    /**
     * Sets the cursor mode to {@link org.lwjgl.glfw.GLFW#GLFW_CURSOR_DISABLED}.
     * */
    public void disableCursor() {
        glfwSetInputMode(windowId, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }

    /**
     * Closes this window.
     * */
    public void close() {
        glfwSetWindowShouldClose(windowId, true);
    }

    /**
     * @return True if the key is pressed.
     * */
    public boolean isKeyPressed(int keyCode) {
        return glfwGetKey(windowId, keyCode) == GLFW_PRESS;
    }

    public boolean isKeyPressed(Keycode keyCode) {
        return isKeyPressed(keyCode.getKeyCode());
    }

        /**
         * @return A String representation of the provided key.
         * */
    public String getKeyName(int key) {
        return glfwGetKeyName(key, glfwGetKeyScancode(key));
    }

    /**
     * @return True if the given mouse button is pressed.
     * */
    public boolean isMouseButtonPressed(int button) {
        return glfwGetMouseButton(windowId, button) == GLFW_PRESS;
    }

    /**
     * @return The title of this window.
     * */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        glfwSetWindowTitle(windowId, title);
        this.title = title;
    }

    /**
     * Resizes the dimensions of this window.
     * */
    public void setSize(int width, int height) {
        glfwSetWindowSize(windowId, width, height);
        this.width = width;
        this.height = height;
    }

    /**
     * Puts this window at the center of the screen.
     * */
    public void center() {
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(windowId, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);
    }

    /**
     * @return The width of this window in pixels.
     * */
    public int getWidth() {
        return width;
    }

    /**
     * @return The height of this window in pixels.
     * */
    public int getHeight() {
        return height;
    }

    /**
     * @return True if vsync is enabled for this window.
     * */
    public boolean isVsync() {
        return vsync;
    }

    /**
     * Enables vsync for this window.
     * */
    public void setVsync(boolean vsync) {
        glfwSwapInterval(vsync ? 1 : 0);
        this.vsync = vsync;
    }

    /**
     * @return The last x position of the mouse.
     * */
    public float getMouseX() {
        return mouseX;
    }

    /**
     * @return The last y position of the mouse.
     * */
    public float getMouseY() {
        return mouseY;
    }

    /**
     * @return True if the mouse is within this window.
     * */
    public boolean isMouseInside() {
        return mouseInside;
    }

    /**
     * Note: This value is going to be reset once this getter is used.
     * @return True if this window has been resized.
     * */
    protected boolean isResized() {
        boolean resized = this.resized;
        this.resized = false;
        return resized;
    }

    /**
     * @return The time since glfw was initialized. (in seconds)
     * */
    public double getTime() {
        return glfwGetTime();
    }

    /**
     * Adds the provided listener to this window.
     * */
    public boolean addWindowEventListener(WindowEventListener windowEventListener) {
        return windowEventListeners.add(windowEventListener);
    }

    /**
     * Removes the provided listener from this window.
     * */
    public boolean removeWindowEventListener(WindowEventListener windowEventListener) {
        return windowEventListeners.remove(windowEventListener);
    }

    /**
     * @return A {@link Transformable} with the mouse positions.
     * */
    public Transformable getMousePosition() {
        mouseTransform.setPosition(getMouseX(), getMouseY(), 0F);
        return mouseTransform;
    }

    public Keycodes getKeycodes() {
        return keycodes;
    }

}

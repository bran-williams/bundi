package com.branwilliams.imageviewer.system;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Lockable;
import com.branwilliams.bundi.engine.core.window.KeyListener;
import com.branwilliams.bundi.engine.core.window.MouseListener;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.ecs.AbstractSystem;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.engine.ecs.matchers.ClassComponentMatcher;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.imageviewer.ImageViewerScene;
import com.branwilliams.imageviewer.components.ImageViewParameters;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Brandon
 * @since December 27, 2019
 */
public class ImageViewInputSystem extends AbstractSystem implements KeyListener, MouseListener {

    private static final float MINIMUM_SCALE = 0.1F;

    private static float SCALE_FACTOR = 0.1F;

    private ImageViewerScene scene;

    private Lockable lock;

    private boolean grabbed = false;

    public ImageViewInputSystem(ImageViewerScene scene, Lockable lock) {
        super(new ClassComponentMatcher(ImageViewParameters.class));
        this.scene = scene;
        this.scene.addKeyListener(this);
        this.scene.addMouseListener(this);
        this.lock = lock;
    }

    @Override
    public void init(Engine engine, EntitySystemManager entitySystemManager, Window window) {

    }

    @Override
    public void update(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {

    }

    @Override
    public void fixedUpdate(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {

    }

    @Override
    public void move(Window window, float newMouseX, float newMouseY, float oldMouseX, float oldMouseY) {
        if (!lock.isLocked() && grabbed) {
            Transformable transformable = scene.getImageViewParameters().getTransform();
            transformable.getPosition().x += newMouseX - oldMouseX;
            transformable.getPosition().y += newMouseY - oldMouseY;
        }
    }

    @Override
    public void press(Window window, float mouseX, float mouseY, int buttonId) {
        if (!lock.isLocked()) {
            grabbed = true;
        }
    }

    @Override
    public void release(Window window, float mouseX, float mouseY, int buttonId) {
        grabbed = false;
    }

    @Override
    public void wheel(Window window, double xoffset, double yoffset) {
        if (!lock.isLocked()) {
            Transformable transformable = scene.getImageViewParameters().getTransform();
            transformable.setScale(transformable.getScale() + (float) yoffset * SCALE_FACTOR);

            if (transformable.getScale() <= 0F) {
                transformable.setScale(MINIMUM_SCALE);
            }
        }
    }

    @Override
    public void keyPress(Window window, int key, int scancode, int mods) {

    }

    @Override
    public void keyRelease(Window window, int key, int scancode, int mods) {
//        if (key == GLFW_KEY_ESCAPE) {
//            lock.toggle();
//        }

        if (key == GLFW_KEY_RIGHT_CONTROL) {
            scene.exitGallery();
        }

        if (!lock.isLocked() && scene.hasGallery()) {
            switch (key) {
                case GLFW_KEY_RIGHT:
                    scene.getGallery().nextTexture();
                    scene.onTextureChange(scene.getGallery().getSelectedTexture());
                    break;
                case GLFW_KEY_LEFT:
                    scene.getGallery().previousTexture();
                    scene.onTextureChange(scene.getGallery().getSelectedTexture());
                    break;
            }
        }
    }
}

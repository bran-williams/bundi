package com.branwilliams.demo.template2d.system;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.ecs.AbstractSystem;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.engine.ecs.matchers.ClassComponentMatcher;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.demo.template2d.ParallaxScene;
import com.branwilliams.demo.template2d.parallax.ParallaxBackground;
import com.branwilliams.demo.template2d.parallax.ParallaxLayer;
import com.branwilliams.demo.template2d.parallax.ParallaxObject;
import com.branwilliams.demo.template2d.parallax.ParallaxProperties;
import org.joml.Vector2f;

import java.util.function.Supplier;

/**
 * @author Brandon
 * @since March 05, 2019
 */
public class ParallaxSystem extends AbstractSystem {

    private final Supplier<Vector2f> focalPoint;

    private final Supplier<ParallaxBackground> background;

    private Vector2f previousFocalPoint = new Vector2f();

    public ParallaxSystem(Supplier<Vector2f> focalPoint, Supplier<ParallaxBackground> background) {
        super(new ClassComponentMatcher(Transformable.class, ParallaxProperties.class));
        this.focalPoint = focalPoint;
        this.background = background;
    }

    @Override
    public void init(Engine engine, EntitySystemManager entitySystemManager, Window window) {
        previousFocalPoint.set(focalPoint.get());
    }

    @Override
    public void update(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {
        Vector2f focalPoint = this.focalPoint.get();

        for (ParallaxLayer layer : background.get().getLayers()) {
            for (ParallaxObject object : layer.getObjects()) {
                switch (object.getMovementType()) {
                    case MOVING:
                        float offsetX = (previousFocalPoint.x - focalPoint.x) * layer.getProperties().getSpeed().x;
                        float offsetY = (previousFocalPoint.y - focalPoint.y) * layer.getProperties().getSpeed().y;

                        float newX = object.getOffsetX() + (offsetX / (float) object.texture.getWidth());
                        float newY = object.getOffsetY() + (offsetY / (float) object.texture.getHeight());

                        object.setOffsetX(Mathf.lerp(object.getOffsetX(), newX, (float) (1F * deltaTime)));
                        object.setOffsetY(Mathf.lerp(object.getOffsetY(), newY, (float) (1F * deltaTime)));

//                        float offsetX = (previousFocalPoint.x - scene.getFocalPoint().x) * component.getSpeed().x;
//                        float offsetY = (previousFocalPoint.y - scene.getFocalPoint().y) * component.getSpeed().y;
//                        Vector2f targetPosition = new Vector2f(object.transformable.x(), object.transformable.y());
//                        targetPosition.x += offsetX;
//                        targetPosition.y += offsetY;
//
//                        Vector2f newPosition = new Vector2f(object.transformable.x(), object.transformable.y());
//                        newPosition.lerp(targetPosition, (float) (10F * deltaTime));

//                        object.transformable.position(newPosition.x, newPosition.y, 0F);
                        break;
                }
            }
        }
        previousFocalPoint.set(focalPoint);
    }

    @Override
    public void fixedUpdate(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {

    }
}
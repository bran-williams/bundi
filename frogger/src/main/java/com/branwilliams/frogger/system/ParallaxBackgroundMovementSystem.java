package com.branwilliams.frogger.system;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.ecs.AbstractSystem;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.engine.ecs.matchers.ClassComponentMatcher;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.frogger.parallax.ParallaxBackground;
import com.branwilliams.frogger.parallax.ParallaxLayer;
import com.branwilliams.frogger.parallax.ParallaxObject;
import com.branwilliams.frogger.parallax.ParallaxProperties;
import org.joml.Vector2f;

import java.util.function.Supplier;

/**
 * @author Brandon
 * @since March 05, 2019
 */
public class ParallaxBackgroundMovementSystem extends AbstractSystem {

    private final Supplier<Vector2f> focalPoint;

    private final Supplier<ParallaxBackground<?>> background;

    private Vector2f previousFocalPoint = new Vector2f();

    // temp for each update.
    private Vector2f newOffset = new Vector2f();

    public ParallaxBackgroundMovementSystem(Supplier<Vector2f> focalPoint,
                                            Supplier<ParallaxBackground<?>> background) {
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

        for (ParallaxLayer<?> layer : background.get().getLayers()) {
            for (ParallaxObject<?> object : layer.getObjects()) {
                switch (object.getMovementType()) {
                    case MOVING:
                        float offsetX = (previousFocalPoint.x - focalPoint.x) * layer.getProperties().getSpeed().x;
                        float offsetY = (previousFocalPoint.y - focalPoint.y) * layer.getProperties().getSpeed().y;

                        float newX = object.getOffset().x + (offsetX / object.getSize().x);
                        float newY = object.getOffset().y + (offsetY / object.getSize().y);
                        newOffset.set(newX, newY);
                        newOffset.add(object.getVelocity());
                        object.setOffset(Mathf.lerp(object.getOffset(), newOffset, (float) (1F * deltaTime)));
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
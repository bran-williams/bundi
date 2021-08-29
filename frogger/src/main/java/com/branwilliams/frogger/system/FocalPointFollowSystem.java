package com.branwilliams.frogger.system;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.ecs.AbstractSystem;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.engine.ecs.matchers.ClassComponentMatcher;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.frogger.Camera2D;
import org.joml.Vector2f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Brandon
 * @since September 08, 2019
 */
public class FocalPointFollowSystem extends AbstractSystem {

    private final Supplier<Vector2f> focalPoint;

    private final Consumer<Vector2f> focalPointSetter;

    private final Supplier<Vector2f> targetFocalPoint;

    private final float moveSpeed;

    public FocalPointFollowSystem(Supplier<Vector2f> focalPoint,
                                  Consumer<Vector2f> focalPointSetter, Supplier<Vector2f> targetFocalPoint,
                                  float moveSpeed) {
        super(new ClassComponentMatcher(Transformable.class));
        this.focalPoint = focalPoint;
        this.focalPointSetter = focalPointSetter;
        this.targetFocalPoint = targetFocalPoint;
        this.moveSpeed = moveSpeed;
    }


    @Override
    public void init(Engine engine, EntitySystemManager entitySystemManager, Window window) {

    }

    @Override
    public void update(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {
        followTargetFocalPoint(engine, entitySystemManager, deltaTime);
    }

    @Override
    public void fixedUpdate(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {

    }

    private void followTargetFocalPoint(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {
        Vector2f focalPoint = this.focalPoint.get();
        Vector2f targetFocalPoint = this.targetFocalPoint.get();

        if (focalPoint.equals(targetFocalPoint)) {
            return;
        }

        if (Mathf.equalsWithEpsilon(focalPoint, targetFocalPoint, Camera2D.FOCALPOINT_MOVEMENT_EPSILON)) {
            this.focalPointSetter.accept(new Vector2f(targetFocalPoint));
        } else {
            focalPointSetter.accept(Mathf.lerp(focalPoint, targetFocalPoint, (float) deltaTime * moveSpeed));
        }
    }

}

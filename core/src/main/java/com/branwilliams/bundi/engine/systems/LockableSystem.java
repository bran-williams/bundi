package com.branwilliams.bundi.engine.systems;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.ecs.AbstractSystem;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.engine.ecs.ISystem;
import com.branwilliams.bundi.engine.core.Lockable;

/**
 * @author Brandon
 * @since August 16, 2019
 */
public class LockableSystem extends AbstractSystem {

    private final Lockable lockable;

    private ISystem delegate;

    public LockableSystem(Lockable lockable, ISystem delegate) {
        super(delegate.getMatcher());
        this.delegate = delegate;
        this.lockable = lockable;
    }

    @Override
    public void init(Engine engine, EntitySystemManager entitySystemManager, Window window) {
        delegate.init(engine, entitySystemManager, window);
    }

    @Override
    public void update(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {
        if (!lockable.isLocked()) {
            delegate.update(engine, entitySystemManager, deltaTime);
        }
    }

    @Override
    public void fixedUpdate(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {
        if (!lockable.isLocked()) {
            delegate.fixedUpdate(engine, entitySystemManager, deltaTime);
        }
    }
    @Override
    public void setEs(EntitySystemManager es) {
        super.setEs(es);
        delegate.setEs(es);
    }
}

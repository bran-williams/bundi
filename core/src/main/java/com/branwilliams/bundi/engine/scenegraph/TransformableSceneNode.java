package com.branwilliams.bundi.engine.scenegraph;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.shader.Transformable;

/**
 * @author Brandon
 * @since December 27, 2019
 */
public class TransformableSceneNode extends SceneNode {

    private Transformable worldTransform;

    private Transformable localTransform;

    @Override
    public void update(Engine engine, double deltaTime) {

    }

    /**
     * TODO scene graph
     * */
    protected void updateWorldTransform() {
//        if (hasParent()) {
//            worldTransform = getParent().getWorldTransform().add(localTransform);
//        } else {
//            worldTransform = localTransform;
//        }
    }

    public Transformable getWorldTransform() {
        return worldTransform;
    }

    public Transformable getLocalTransform() {
        return localTransform;
    }

    @Override
    public void destroy() {

    }
}

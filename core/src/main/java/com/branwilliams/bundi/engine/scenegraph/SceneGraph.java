package com.branwilliams.bundi.engine.scenegraph;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Updateable;

/**
 * @author Brandon
 * @since December 26, 2019
 */
public class SceneGraph implements Destructible, Updateable {

    private SceneNode root;

    public SceneGraph() {
        root = new RootSceneNode();
    }

    public void update(Engine engine, double deltaTime) {
        root.update(engine, deltaTime);
    }

    @Override
    public void fixedUpdate(Engine engine, double deltaTime) {
        root.fixedUpdate(engine, deltaTime);
    }

    public boolean addSceneNode(SceneNode node) {
        return root.addChild(node);
    }

    public boolean removeSceneNode(SceneNode node) {
        return root.removeChild(node);
    }

    @Override
    public void destroy() {
        root.destroy();
    }

    public static class RootSceneNode extends SceneNode {

        @Override
        public void update(Engine engine, double deltaTime) {
            for (SceneNode child : getChildren())
                child.update(engine, deltaTime);
        }

        @Override
        public void fixedUpdate(Engine engine, double deltaTime) {
            for (SceneNode child : getChildren())
                child.fixedUpdate(engine, deltaTime);
        }

        @Override
        public void destroy() {
            for (SceneNode child : getChildren())
                child.destroy();
        }
    }
}

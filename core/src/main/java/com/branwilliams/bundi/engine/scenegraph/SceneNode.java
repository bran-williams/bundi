package com.branwilliams.bundi.engine.scenegraph;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.core.Engine;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brandon
 * @since December 27, 2019
 */
public abstract class SceneNode implements Destructible {

    private SceneNode parent;

    private List<SceneNode> children;

    public SceneNode() {
        this.children = new ArrayList<>();
    }

    public abstract void update(Engine engine, double deltaTime);

    /**
     * This function should only be invoked when this node is added to or removed from a parent.
     * */
    protected void setParent(SceneNode node) {
        this.parent = node;
    }

    /**
     * @return True if this node has a parent node.
     * */
    public boolean hasParent() {
        return parent != null;
    }

    /**
     * Adds the node to this node. Will set the provided nodes parent to this.
     * @return True if this node was added.
     * */
    public boolean addChild(SceneNode node) {
        node.setParent(this);
        return children.add(node);
    }

    /**
     * Removes the node from this if it exists. This will set the removed nodes parent to null.
     * @return True if the node was removed from this.
     * */
    public boolean removeChild(SceneNode node) {
        boolean removed = children.remove(node);

        if (removed)
            node.setParent(null);

        return removed;
    }

    public SceneNode getParent() {
        return parent;
    }

    public List<SceneNode> getChildren() {
        return children;
    }

}

package com.branwilliams.demo.template2d.parallax;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;

/**
 * @author Brandon
 * @since March 05, 2019
 */
public class ParallaxProperties implements Comparable<ParallaxProperties> {

    public final int layer;

    private Vector2f speed;

    public ParallaxProperties(int layer, Vector2f speed) {
        this.layer = layer;
        this.speed = speed;
    }

    public Vector2f getSpeed() {
        return speed;
    }

    public void setSpeed(Vector2f speed) {
        this.speed = speed;
    }

    @Override
    public int compareTo(@NotNull ParallaxProperties o) {
        return Integer.compare(this.layer, o.layer);
    }

    @Override
    public String toString() {
        return "ParallaxProperties{" +
                "layer=" + layer +
                '}';
    }
}
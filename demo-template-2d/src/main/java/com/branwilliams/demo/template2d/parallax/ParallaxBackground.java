package com.branwilliams.demo.template2d.parallax;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Brandon
 * @since March 05, 2019
 */
public class ParallaxBackground {

    private final SortedSet<ParallaxLayer> layers;

    public ParallaxBackground() {
        this.layers = new TreeSet<>();
    }

    /**
     *
     * */
    public void addLayer(ParallaxLayer layer) {
        layers.add(layer);
    }

    public SortedSet<ParallaxLayer> getLayers() {
        return layers;
    }

    @Override
    public String toString() {
        return "ParallaxBackground{" +
                "layers=" + layers +
                '}';
    }
}
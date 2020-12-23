package com.branwilliams.frogger.parallax;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Brandon
 * @since March 05, 2019
 */
public class ParallaxBackground <ObjectType> {

    private final SortedSet<ParallaxLayer<ObjectType>> layers;

    public ParallaxBackground() {
        this.layers = new TreeSet<>();
    }

    /**
     *
     * */
    public void addLayer(ParallaxLayer<ObjectType> layer) {
        layers.add(layer);
    }

    public SortedSet<ParallaxLayer<ObjectType>> getLayers() {
        return layers;
    }

    @Override
    public String toString() {
        return "ParallaxBackground{" +
                "layers=" + layers +
                '}';
    }
}
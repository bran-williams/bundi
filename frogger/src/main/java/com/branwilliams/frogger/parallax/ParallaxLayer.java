package com.branwilliams.frogger.parallax;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ParallaxLayer <ObjectType> implements Comparable<ParallaxLayer<?>> {

    private final ParallaxProperties properties;

    private final List<ParallaxObject<ObjectType>> objects;

    public ParallaxLayer(ParallaxProperties properties) {
        this.properties = properties;
        this.objects = new ArrayList<>();
    }

    public void addSprite(ParallaxObject<ObjectType> layerObject) {
        objects.add(layerObject);
    }

    public List<ParallaxObject<ObjectType>> getObjects() {
        return objects;
    }

    public ParallaxProperties getProperties() {
        return properties;
    }

    @Override
    public int compareTo(@NotNull ParallaxLayer o) {
        return properties == null ? -1 : o.properties == null ? 1 : properties.compareTo(o.properties);
    }

    @Override
    public String toString() {
        return "ParallaxLayer{" +
                "objects.size=" + objects.size() +
                ", properties=" + properties +
                '}';
    }
}

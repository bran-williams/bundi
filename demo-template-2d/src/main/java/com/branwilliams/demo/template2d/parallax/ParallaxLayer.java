package com.branwilliams.demo.template2d.parallax;

import com.branwilliams.bundi.engine.texture.Texture;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ParallaxLayer implements Comparable<ParallaxLayer> {

    private final ParallaxProperties properties;

    private final List<ParallaxObject> objects;

    public ParallaxLayer(ParallaxProperties properties) {
        this.properties = properties;
        this.objects = new ArrayList<>();
    }

//    public void addMovingRepeatingSprite(Texture texture, float scale) {
//        addSprite(texture, ParallaxMovementType.MOVING, ParallaxDrawType.REPEAT, scale);
//    }
//
//    public void addRepeatingSprite(Texture texture, ParallaxMovementType movementType, float scale) {
//        addSprite(texture, movementType, ParallaxDrawType.REPEAT, scale);
//    }
//
//    public void addMovingSprite(Texture texture, ParallaxDrawType drawType,  float scale) {
//        addSprite(texture, ParallaxMovementType.MOVING, drawType, scale);
//    }

    public void addSprite(Texture texture, ParallaxMovementType movementType, ParallaxDrawType drawType,
                          ParallaxSizeType sizeType, float scale) {
        objects.add(new ParallaxObject(texture, movementType, drawType, sizeType, scale));
    }

    public List<ParallaxObject> getObjects() {
        return objects;
    }

    public ParallaxProperties getProperties() {
        return properties;
    }

    @Override
    public int compareTo(@NotNull ParallaxLayer o) {
        return properties.compareTo(o.getProperties());
    }

    @Override
    public String toString() {
        return "ParallaxLayer{" +
                "objects.size=" + objects.size() +
                ", properties=" + properties +
                '}';
    }
}

package com.branwilliams.cubes.builder.evaluators;

import org.joml.Vector3f;

/**
 * @author Brandon
 * @since January 26, 2020
 */
public class SphereIsoEvaluator2 implements IsoEvaluator {

    private final Vector3f center;

    private final float radius;

    public SphereIsoEvaluator2(Vector3f center, float radius) {
        this.center = center;
        this.radius = radius;
    }

    @Override
    public float evaluate(float x, float y, float z, float isoValue) {
        int x1 = (int) Math.pow((x - center.x), 2);
        int y1 = (int) Math.pow((y - center.y), 2);
        int z1 = (int) Math.pow((z - center.z), 2);
        if ((x1 + y1 + z1) > radius * radius) {
            float distFromSurface = (x1 + y1 + z1) - (radius * radius);
            if (distFromSurface < 16) {
                return distFromSurface / radius;
            }
            return 0;
        } else {
            return isoValue;
        }
    }
}

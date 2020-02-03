package com.branwilliams.cubes.builder.evaluators;

import com.branwilliams.cubes.math.Torus;
import org.joml.Vector3f;

/**
 * Equation for torus from
 * https://marcin-chwedczuk.github.io/ray-tracing-torus
 *
 * @author Brandon
 * @since January 26, 2020
 */
public class TorusIsoEvaluator implements IsoEvaluator {

    private final Vector3f center;

    private final Torus torus;

    public TorusIsoEvaluator(Vector3f center, Torus torus) {
        this.center = center;
        this.torus = torus;
    }

    @Override
    public float evaluate(float x, float y, float z, float isoValue) {
        Vector3f nearest = new Vector3f(
                torus.getMajorRadius() * (x / (x * x + y * y + z * z)),
                torus.getMajorRadius() * (y / (x * x + y * y + z * z)),
                torus.getMajorRadius() * (z / (x * x + y * y + z * z)));
        if (nearest.distance(x, y, z) < torus.getMajorRadius()) {
            System.out.println("one?");
            return 0F;
        } else {
            return isoValue;
        }

//        float i = x * x + y * y + z * z;
//        float major2 = (torus.getMajorRadius() * torus.getMajorRadius());
//        float minor2 = (torus.getMinorRadius() * torus.getMinorRadius());
//
//        // 2 * (major^2 + minor^2)
//        float j = 2 * (major2 + minor2);
//
//        // 4 * major^2 * minor^2
//        float k = 4 * major2 * minor2;
//
//        // (major^2 - minor^2)^2
//        float l = (major2 - minor2) * (major2 - minor2);
//
//        // all put together...
////        float f = (i * i) - (j * i) + k + l;
////
//        float f = (i + major2 - minor2) * (i + major2 - minor2) - (4 * major2 * (x * x + y * y));
//
//        if (Math.abs(f)  <= 0.001F) {
//            System.out.println("one?");
//            return 0F;
//        } else {
//            return isoValue;
//        }
    }
}

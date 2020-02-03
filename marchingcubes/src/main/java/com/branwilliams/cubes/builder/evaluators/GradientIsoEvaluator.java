package com.branwilliams.cubes.builder.evaluators;

import com.branwilliams.bundi.engine.util.Mathf;

/**
 * @author Brandon
 * @since January 26, 2020
 */
public class GradientIsoEvaluator implements IsoEvaluator {

    private final float maxY;

    public GradientIsoEvaluator(float maxY) {
        this.maxY = maxY;
    }

    @Override
    public float evaluate(float x, float y, float z, float isoValue) {
        float gradientIso = 0.3F - Mathf.smoothStep(0F, 0.3F, y / maxY);
        return  Math.max(gradientIso, isoValue);
    }
}

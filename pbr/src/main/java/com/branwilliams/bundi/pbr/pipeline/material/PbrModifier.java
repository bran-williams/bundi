package com.branwilliams.bundi.pbr.pipeline.material;

import com.branwilliams.bundi.engine.util.ColorUtils;

import java.util.function.Function;

/**
 * Takes two pixel inputs and produces one pixel output in the following manner: <br/>
 * output.a = defaultAlpha <br/>
 * output.r = first.r <br/>
 * output.g = first.g <br/>
 * output.b = first.b <br/> <br/>
 *
 * This is useful for the case that one image may have the same value for each channel (e.g. a heightmap for terrain)
 * and another may have information in the rgb channel and it's a channel is not used (e.g. the albedo of a terrain).
 * */
public class PbrModifier implements Function<Integer, Integer> {

    private final float defaultAlpha;

    public PbrModifier(float defaultAlpha) {
        this.defaultAlpha = defaultAlpha;
    }

    @Override
    public Integer apply(Integer argb) {
        return ((int) (defaultAlpha * 255F) << 24) | argb;
    }

    public float getDefaultAlpha() {
        return defaultAlpha;
    }
}

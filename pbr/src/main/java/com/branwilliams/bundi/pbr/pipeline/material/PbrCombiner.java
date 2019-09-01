package com.branwilliams.bundi.pbr.pipeline.material;

import com.branwilliams.bundi.engine.util.ColorUtils;

import java.util.function.BiFunction;

/**
 * Takes two pixel inputs and produces one pixel output in the following manner: <br/>
 * output.a = second.r <br/>
 * output.r = first.r <br/>
 * output.g = first.g <br/>
 * output.b = first.b <br/> <br/>
 *
 * This is useful for the case that one image may have the same value for each channel (e.g. a heightmap for terrain)
 * and another may have information in the rgb channel and it's a channel is not used (e.g. the albedo of a terrain).
 * */
public class PbrCombiner implements BiFunction<Integer, Integer, Integer> {

    @Override
    public Integer apply(Integer firstARGB, Integer secondARGB) {
        return ColorUtils.toARGB(ColorUtils.red(secondARGB), ColorUtils.red(firstARGB), ColorUtils.green(firstARGB), ColorUtils.blue(firstARGB));
    }
}

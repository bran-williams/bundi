package com.branwilliams.bundi.engine.shader.patching;

/**
 * Created by Brandon Williams on 11/17/2018.
 */
public interface ShaderPatch {

    /**
     * Modifies shader code to varying degrees.
     *
     * @return The modified code.
     * */
    String patch(String code);
}

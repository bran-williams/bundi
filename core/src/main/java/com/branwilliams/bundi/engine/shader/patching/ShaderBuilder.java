package com.branwilliams.bundi.engine.shader.patching;

import com.branwilliams.bundi.engine.shader.ShaderInitializationException;
import com.branwilliams.bundi.engine.shader.ShaderProgram;

/**
 * Created by Brandon Williams on 11/17/2018.
 */
public interface ShaderBuilder {

    ShaderBuilder vertexShader(String code) throws ShaderInitializationException;

    ShaderBuilder fragmentShader(String code) throws ShaderInitializationException;

    ShaderProgram build() throws ShaderInitializationException;
}

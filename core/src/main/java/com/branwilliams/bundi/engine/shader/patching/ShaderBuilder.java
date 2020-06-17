package com.branwilliams.bundi.engine.shader.patching;

import com.branwilliams.bundi.engine.shader.ShaderInitializationException;
import com.branwilliams.bundi.engine.shader.ShaderProgram;

/**
 * Created by Brandon Williams on 11/17/2018.
 */
public interface ShaderBuilder <T extends ShaderProgram> {

    ShaderBuilder<T> vertexShader(String code) throws ShaderInitializationException;

    ShaderBuilder<T> fragmentShader(String code) throws ShaderInitializationException;

    ShaderBuilder<T> geometryShader(String code) throws ShaderInitializationException;

    ShaderBuilder<T> tessellationControlShader(String code) throws ShaderInitializationException;

    ShaderBuilder<T> tessellationEvaluationShader(String code) throws ShaderInitializationException;

    T build() throws ShaderInitializationException;
}

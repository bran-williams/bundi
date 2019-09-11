package com.branwilliams.bundi.water.pipeline.shaders;

import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.util.IOUtils;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.bundi.water.Water;
import com.branwilliams.bundi.water.Wave;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.nio.file.Path;

/**
 * @author Brandon
 * @since September 03, 2019
 */
public class WaterNormalShaderProgram extends ShaderProgram {

    private final Matrix4f worldMatrix = new Matrix4f();

    public WaterNormalShaderProgram(EngineContext engineContext) throws ShaderInitializationException, ShaderUniformException {
        Path directory = engineContext.getAssetDirectory();
        this.setVertexShader(IOUtils.readFile(directory, "shaders/water/normal/vertexShader.vert", null));
        this.setFragmentShader(IOUtils.readFile(directory, "shaders/water/normal/fragmentShader.frag", null));
        this.link();

        this.createUniform("passedTime");

        this.createUniform("waveParameters");
        this.createUniform("waveDirections");
        this.validate();
    }

    public void setWater(Water water) {
        this.setUniform("passedTime", water.getPassedTime());

        Wave[] waves = water.getNormalWaves();
        Vector4f[] waveParameters = new Vector4f[waves.length];
        Vector2f[] waveDirections = new Vector2f[waves.length];
        for (int i = 0; i < waves.length; i++) {
            Wave wave = waves[i];
            waveParameters[i] = new Vector4f(wave.getSpeed(), wave.getAmplitude(), wave.getWavelength(), wave.getSteepness());
            waveDirections[i] = wave.getDirection();
        }
        this.setUniform("waveParameters", waveParameters);
        this.setUniform("waveDirections", waveDirections);
    }
}

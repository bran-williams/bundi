package com.branwilliams.bundi.water.pipeline.shaders;

import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.util.IOUtils;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.bundi.water.Water;
import com.branwilliams.bundi.water.Wave;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.nio.file.Path;

/**
 * @author Brandon
 * @since September 03, 2019
 */
public class WaterShaderProgram extends ShaderProgram {

    private final Matrix4f modelMatrix = new Matrix4f();

    public WaterShaderProgram(EngineContext engineContext) throws ShaderInitializationException, ShaderUniformException {
        Path directory = engineContext.getAssetDirectory();
        this.setVertexShader(IOUtils.readFile(directory, "shaders/water/surface/vertexShader.vert", null));
        this.setFragmentShader(IOUtils.readFile(directory, "shaders/water/surface/fragmentShader.frag", null));
        this.link();

        this.createUniform("projectionMatrix");
        this.createUniform("viewMatrix");
        this.createUniform("modelMatrix");
        this.createUniform("inverseViewNormalMatrix");

        this.createUniform("waterPlaneLength");
        this.createUniform("passedTime");
        this.createUniform("waterColor");

        this.createUniform("waveParameters");
        this.createUniform("waveDirections");

        this.createUniform("enviroment");
        this.createUniform("normalMap");

        this.validate();

        this.bind();
        this.setUniform("enviroment", 0);
        this.setUniform("normalMap", 1);
        ShaderProgram.unbind();
    }

    public void setWater(Water water) {
        this.setUniform("waterPlaneLength", (float) water.getPlaneLength());
        this.setUniform("passedTime", water.getPassedTime());

        Wave[] waves = water.getSurfaceWaves();
        Vector4f[] waveParameters = new Vector4f[waves.length];
        Vector2f[] waveDirections = new Vector2f[waves.length];
        for (int i = 0; i < waves.length; i++) {
            Wave wave = waves[i];
            waveParameters[i] = new Vector4f(wave.getSpeed(), wave.getAmplitude(), wave.getWavelength(), wave.getSteepness());
            waveDirections[i] = wave.getDirection();
        }
        this.setUniform("waveParameters", waveParameters);
        this.setUniform("waveDirections", waveDirections);
        this.setUniform("waterColor", water.getColor());
    }

    public void setModelMatrix(Transformable transformable) {
        this.setUniform("modelMatrix", Mathf.toModelMatrix(modelMatrix, transformable));
    }

    public void setProjectionMatrix(Projection projection) {
        this.setUniform("projectionMatrix", projection.toProjectionMatrix());
    }

    public void setViewMatrix(Camera camera) {
        this.setUniform("viewMatrix", camera.toViewMatrix());
        this.setUniform("inverseViewNormalMatrix", new Matrix3f(camera.toViewMatrix()).invert());
    }
}

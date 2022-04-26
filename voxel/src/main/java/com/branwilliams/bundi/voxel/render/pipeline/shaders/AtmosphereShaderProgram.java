package com.branwilliams.bundi.voxel.render.pipeline.shaders;

import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.util.IOUtils;
import com.branwilliams.bundi.voxel.components.Atmosphere;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.nio.file.Path;

/**
 * Created by Brandon Williams on 1/5/2018.
 */
public class AtmosphereShaderProgram extends ShaderProgram {

    // Used to create a copy of the view matrix
    private final Matrix4f copy = new Matrix4f();

    public AtmosphereShaderProgram(EngineContext engineContext) throws ShaderInitializationException, ShaderUniformException {
        super();
        this.setVertexShader(IOUtils.readResource( "voxel/shaders/atmosphere/vertexShader.vert", null));
        this.setFragmentShader(IOUtils.readResource("voxel/shaders/atmosphere/fragmentShader.frag", null));
        this.link();

        this.createUniform("projectionMatrix");
        this.createUniform("viewMatrix");

        this.createUniform("directionalLight.direction");
        this.createUniform("fogDensity");
        this.createUniform("skyColor");
        this.createUniform("sunColor");

        this.validate();
    }

    public void setProjectionMatrix(Projection projection) {
        this.setUniform("projectionMatrix", projection.toProjectionMatrix());
    }

    public void setViewMatrix(Camera camera) {
        // Put contents of 'view matrix' into 'copy'.
        copy.set(camera.toViewMatrix());
        // Remove the translations, making it only the rotations.
        copy.m30(0);
        copy.m31(0);
        copy.m32(0);
        this.setUniform("viewMatrix", copy);
    }

    public void setAtmosphere(Atmosphere atmosphere) {
        this.setUniform("directionalLight.direction", atmosphere.getSun().getDirection());
        this.setUniform("fogDensity", atmosphere.getFog().getDensity());
        this.setUniform("skyColor", atmosphere.getSkyColor());
        this.setUniform("sunColor", atmosphere.getSunColor());
    }
}

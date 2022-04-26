package com.branwilliams.bundi.voxel.render.pipeline.shaders;

import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.util.IOUtils;

import java.nio.file.Path;

/**
 * Created by Brandon Williams on 6/29/2018.
 */
public class VoxelPostProcessingShaderProgram extends ShaderProgram {

    public VoxelPostProcessingShaderProgram(EngineContext engineContext) throws ShaderInitializationException, ShaderUniformException {
        this.setVertexShader(IOUtils.readResource("voxel/shaders/postprocessing/vertexShader.vert", null));
        this.setFragmentShader(IOUtils.readResource("voxel/shaders/postprocessing/fragmentShader.frag", null));
        this.link();

        this.createUniform("albedoSampler");
        this.createUniform("ssaoSampler");
        this.createUniform("bloomSampler");

//        this.createUniform("near");
//        this.createUniform("far");
//        this.createUniform("viewMatrix");
//        this.createUniform("projectionMatrix");

        this.bind();
        this.setUniform("albedoSampler", 0);
        this.setUniform("ssaoSampler", 1);
        this.setUniform("bloomSampler", 2);
        ShaderProgram.unbind();

        this.validate();
    }

    public void setProjection(Projection projection) {
//        this.setUniform("near", projection.getNear());
//        this.setUniform("far", projection.getFar());
//        this.setUniform("projectionMatrix", projection.toProjectionMatrix());
    }

    public void setViewMatrix(Camera camera) {
//        this.setUniform("viewMatrix", camera.toViewMatrix());
    }
}
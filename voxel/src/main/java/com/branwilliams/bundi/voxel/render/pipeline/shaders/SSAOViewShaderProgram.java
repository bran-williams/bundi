package com.branwilliams.bundi.voxel.render.pipeline.shaders;

import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.util.IOUtils;

/**
 * Created by Brandon Williams on 6/29/2018.
 */
public class SSAOViewShaderProgram extends ShaderProgram {

    public SSAOViewShaderProgram(EngineContext engineContext) throws ShaderInitializationException, ShaderUniformException {
        this.setVertexShader(IOUtils.readResource("voxel/shaders/postprocessing/ssaoView/vertexShader.vert", null));
        this.setFragmentShader(IOUtils.readResource("voxel/shaders/postprocessing/ssaoView/fragmentShader.frag", null));
        this.link();

        this.createUniform("albedoSampler");
//        this.createUniform("normalSampler");
        this.createUniform("ssaoSampler");

//        this.createUniform("near");
//        this.createUniform("far");
//        this.createUniform("viewMatrix");
        this.createUniform("projectionMatrix");

        this.bind();
        this.setUniform("albedoSampler", 0);
        this.setUniform("ssaoSampler", 1);
//        this.setUniform("normalSampler", 1);
//        this.setUniform("depthSampler", 2);
        ShaderProgram.unbind();

        this.validate();
    }

    public void setProjection(Projection projection) {
//        this.setUniform("near", projection.getNear());
//        this.setUniform("far", projection.getFar());
        this.setUniform("projectionMatrix", projection.toProjectionMatrix());
    }

    public void setViewMatrix(Camera camera) {
//        this.setUniform("viewMatrix", camera.toViewMatrix());
    }
}

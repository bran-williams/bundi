package com.branwilliams.bundi.pbr.pipeline.shaders;

import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.shader.ShaderInitializationException;
import com.branwilliams.bundi.engine.shader.ShaderProgram;
import com.branwilliams.bundi.engine.shader.ShaderUniformException;
import com.branwilliams.bundi.engine.util.IOUtils;

import java.nio.file.Path;

/**
 * Created by Brandon Williams on 6/29/2018.
 */
public class PbrPostProcessingShaderProgram extends ShaderProgram {

    public PbrPostProcessingShaderProgram(EngineContext engineContext) throws ShaderInitializationException, ShaderUniformException {
        Path directory = engineContext.getAssetDirectory();
        this.setVertexShader(IOUtils.readFile(directory, "shaders/deferred/pbr/postprocessing/vertexShader.vert", null));
        this.setFragmentShader(IOUtils.readFile(directory, "shaders/deferred/pbr/postprocessing/fragmentShader.frag", null));
        this.link();

        this.createUniform("textureSampler");
        this.createUniform("exposure");
//        this.createUniform("near");
//        this.createUniform("far");
        this.bind();
        this.setUniform("textureSampler", 0);
        ShaderProgram.unbind();
        this.validate();
    }

    public void setExposure(float exposure) {
        this.setUniform("exposure", exposure);
    }

//    public void setProjectionMatrix(Projection projection) {
//        this.setUniform("near", projection.getNear());
//        this.setUniform("far", projection.getFar());
//    }
}

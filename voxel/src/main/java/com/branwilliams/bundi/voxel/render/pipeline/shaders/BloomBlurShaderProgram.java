package com.branwilliams.bundi.voxel.render.pipeline.shaders;

import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.shader.ShaderInitializationException;
import com.branwilliams.bundi.engine.shader.ShaderProgram;
import com.branwilliams.bundi.engine.shader.ShaderUniformException;
import com.branwilliams.bundi.engine.util.IOUtils;

/**
 * Created by Brandon Williams on 6/29/2018.
 */
public class BloomBlurShaderProgram extends ShaderProgram {

    public BloomBlurShaderProgram(EngineContext engineContext) throws ShaderInitializationException, ShaderUniformException {
        this.setVertexShader(IOUtils.readResource("voxel/shaders/postprocessing/vertexShader.vert", null));
        this.setFragmentShader(IOUtils.readResource("voxel/shaders/postprocessing/bloom/fragmentShader.frag", null));
        this.link();

        this.createUniform("horizontal");
        this.createUniform("emissionSampler");

        this.bind();
        this.setUniform("emissionSampler", 0);

        ShaderProgram.unbind();

        this.validate();
    }

    public void setHorizontal(boolean horizontal) {
        this.setUniform("horizontal", horizontal);
    }

}

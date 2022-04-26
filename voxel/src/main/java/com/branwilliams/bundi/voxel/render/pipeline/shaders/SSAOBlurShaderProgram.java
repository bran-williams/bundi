package com.branwilliams.bundi.voxel.render.pipeline.shaders;

import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.util.IOUtils;

/**
 * Created by Brandon Williams on 6/29/2018.
 */
public class SSAOBlurShaderProgram extends ShaderProgram {

    public SSAOBlurShaderProgram(EngineContext engineContext) throws ShaderInitializationException, ShaderUniformException {
        this.setVertexShader(IOUtils.readResource("voxel/shaders/postprocessing/vertexShader.vert", null));
        this.setFragmentShader(IOUtils.readResource("voxel/shaders/postprocessing/ssaoBlur/fragmentShader.frag", null));
        this.link();

        this.createUniform("ssaoSampler");

        this.bind();
        this.setUniform("ssaoSampler", 0);
        ShaderProgram.unbind();

        this.validate();
    }
}

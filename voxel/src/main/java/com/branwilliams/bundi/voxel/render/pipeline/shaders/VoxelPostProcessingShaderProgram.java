package com.branwilliams.bundi.voxel.render.pipeline.shaders;

import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.shader.Projection;
import com.branwilliams.bundi.engine.shader.ShaderInitializationException;
import com.branwilliams.bundi.engine.shader.ShaderProgram;
import com.branwilliams.bundi.engine.shader.ShaderUniformException;
import com.branwilliams.bundi.engine.util.IOUtils;

import java.nio.file.Path;

/**
 * Created by Brandon Williams on 6/29/2018.
 */
public class VoxelPostProcessingShaderProgram extends ShaderProgram {

    public VoxelPostProcessingShaderProgram(EngineContext engineContext) throws ShaderInitializationException, ShaderUniformException {
        Path directory = engineContext.getAssetDirectory();
        this.setVertexShader(IOUtils.readFile(directory, "voxel/shaders/postprocessing/vertexShader.vert", null));
        this.setFragmentShader(IOUtils.readFile(directory, "voxel/shaders/postprocessing/fragmentShader.frag", null));
        this.link();

        this.createUniform("albedoSampler");
//        this.createUniform("depthSampler");

//        this.createUniform("near");
//        this.createUniform("far");

        this.bind();
        this.setUniform("albedoSampler", 0);
//        this.setUniform("depthSampler", 1);
        ShaderProgram.unbind();

        this.validate();
    }

    public void setProjection(Projection projection) {
//        this.setUniform("near", projection.getNear());
//        this.setUniform("far", projection.getFar());
    }
}

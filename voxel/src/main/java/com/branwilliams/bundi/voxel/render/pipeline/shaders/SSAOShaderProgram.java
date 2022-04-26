package com.branwilliams.bundi.voxel.render.pipeline.shaders;

import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.util.IOUtils;
import org.joml.Vector3f;

/**
 * Created by Brandon Williams on 6/29/2018.
 */
public class SSAOShaderProgram extends ShaderProgram {

    public static final int MAX_SAMPLES = 64;

    public SSAOShaderProgram(EngineContext engineContext) throws ShaderInitializationException, ShaderUniformException {
        this.setVertexShader(IOUtils.readResource("voxel/shaders/postprocessing/vertexShader.vert", null));
        this.setFragmentShader(IOUtils.readResource("voxel/shaders/postprocessing/ssao/fragmentShader.frag", null));
        this.link();

        this.createUniform("albedoSampler");
        this.createUniform("normalSampler");
        this.createUniform("depthSampler");
        this.createUniform("noiseSampler");

        this.createUniform("power");
        this.createUniform("radius");
        this.createUniform("bias");

        for (int i = 0; i < MAX_SAMPLES; i++) {
            this.createUniform("samples[" + i + "]");
        }

        this.createUniform("screenWidth");
        this.createUniform("screenHeight");

        this.createUniform("projectionMatrix");

        this.bind();
        this.setUniform("albedoSampler", 0);
        this.setUniform("normalSampler", 1);
        this.setUniform("depthSampler", 2);
        this.setUniform("noiseSampler", 3);

        ShaderProgram.unbind();

        this.validate();
    }

    public void setPower(float power) {
        this.setUniform("power", power);
    }

    public void setRadius(float radius) {
        this.setUniform("radius", radius);
    }

    public void setBias(float bias) {
        this.setUniform("bias", bias);
    }

    public void setScreenSize(int width, int height) {
        this.setUniform("screenWidth", width);
        this.setUniform("screenHeight", height);
    }

    public void setSamples(Vector3f[] samples) {
        for (int i = 0; i < samples.length; i++) {
            this.setUniform("samples[" + i + "]", samples[i]);
        }
    }

    public void setProjection(Projection projection) {
        this.setUniform("projectionMatrix", projection.toProjectionMatrix());
    }
}

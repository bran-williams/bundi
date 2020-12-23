package com.branwilliams.bundi.pbr.pipeline.shaders;

import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.util.IOUtils;

import java.nio.file.Path;

/**
 * Created by Brandon Williams on 6/30/2018.
 */
public class PbrLightShaderProgram extends ShaderProgram {

    public static final int MAX_LIGHTS_PER_PASS = 16;

    public PbrLightShaderProgram(EngineContext engineContext) throws ShaderInitializationException, ShaderUniformException {
        Path directory = engineContext.getAssetDirectory();
        this.setVertexShader(IOUtils.readFile(directory, "shaders/deferred/pbr/light/vertexShader.vert", null));
        this.setFragmentShader(IOUtils.readFile(directory, "shaders/deferred/pbr/light/fragmentShader.frag", null));
        this.link();

        for (int i = 0; i < MAX_LIGHTS_PER_PASS; i++) {
            String index = "[" + i + "]";
            this.createUniform("lights" + index + ".position");
            this.createUniform("lights" + index + ".color");
//            this.createUniform("lights" + index + ".ambiance");
            this.createUniform("lights" + index + ".constant");
            this.createUniform("lights" + index + ".linear");
            this.createUniform("lights" + index + ".quadratic");

        }
        this.createUniform("projectionMatrix");
        this.createUniform("viewMatrix");
        this.createUniform("numLights");
        this.createUniform("cameraPosition");

        this.createUniform("albedoTexture");
        this.createUniform("normalTexture");
        this.createUniform("depthTexture");

        this.bind();
        this.setUniform("albedoTexture", 0);
        this.setUniform("normalTexture", 1);
        this.setUniform("depthTexture", 2);
        ShaderProgram.unbind();
        this.validate();
    }

    /***
     * Sets the light array for this shader program. Do not pass in null lights!!
     */
    public void setLights(PointLight[] lights) throws ShaderUniformException {
        for (int i = 0; i < lights.length; i++) {
            String index = "[" + i + "]";
            PointLight light = lights[i];
            if (light == null) {
                throw new ShaderUniformException("Lights cannot be null!");
            }
            this.setUniform("lights" + index + ".position", light.getPosition());
            this.setUniform("lights" + index + ".color", light.getDiffuse());
//            this.setUniform("lights" + index + ".ambiance", light.getAmbiance());
            this.setUniform("lights" + index + ".constant", light.getAttenuation().getConstant());
            this.setUniform("lights" + index + ".linear", light.getAttenuation().getLinear());
            this.setUniform("lights" + index + ".quadratic", light.getAttenuation().getQuadratic());
        }
        this.setUniform("numLights", lights.length);
    }

    /**
     *
     * */
    public void setProjectionMatrix(Projection projection) {
        this.setUniform("projectionMatrix", projection.toProjectionMatrix());
    }

    public void setViewMatrix(Camera camera) {
        this.setUniform("cameraPosition", camera.getPosition());
        this.setUniform("viewMatrix", camera.toViewMatrix());

    }
}

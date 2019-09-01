package com.branwilliams.bundi.pbr.pipeline.shaders;

import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.util.IOUtils;
import com.branwilliams.bundi.engine.util.Mathf;
import org.joml.Matrix4f;

import java.nio.file.Path;

public class PbrGeometryShaderProgram extends ShaderProgram {

    private final Matrix4f worldMatrix = new Matrix4f();

    public PbrGeometryShaderProgram(EngineContext engineContext) throws ShaderInitializationException, ShaderUniformException {
        Path directory = engineContext.getAssetDirectory();
        this.setVertexShader(IOUtils.readFile(directory, "shaders/deferred/pbr/geometry/vertexShader.vert", null));
        this.setFragmentShader(IOUtils.readFile(directory, "shaders/deferred/pbr/geometry/fragmentShader.frag", null));
        this.link();

        this.createUniform("projectionMatrix");
        this.createUniform("viewMatrix");
        this.createUniform("modelMatrix");

        this.createUniform("albedoSampler");
        this.createUniform("normalSampler");
        this.bind();
        this.setUniform("albedoSampler", 0);
        this.setUniform("normalSampler", 1);

        ShaderProgram.unbind();
        this.validate();
    }

    public void setMaterial(Material material) {
        // nothing
    }

    public void setProjectionMatrix(Projection projection) {
        this.setUniform("projectionMatrix", projection.toProjectionMatrix());
    }

    public void setViewMatrix(Camera camera) {
        this.setUniform("viewMatrix", camera.toViewMatrix());
    }

    public void setModelMatrix(Transformable transformable) {
        this.setUniform("modelMatrix", Mathf.toModelMatrix(worldMatrix, transformable));
    }
}

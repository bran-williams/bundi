package com.branwilliams.terrain;

import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.util.IOUtils;
import com.branwilliams.bundi.engine.util.Mathf;
import org.joml.Matrix4f;

import java.nio.file.Path;

/**
 * Created by Brandon Williams on 6/29/2018.
 */
public class TerrainShaderProgram extends ShaderProgram {

    private final Matrix4f worldMatrix = new Matrix4f();

    public TerrainShaderProgram(EngineContext engineContext) throws ShaderInitializationException, ShaderUniformException {
        Path directory = engineContext.getAssetDirectory();
        this.setVertexShader(IOUtils.readFile(directory,"shaders/deferred/phong/terrain/vertexShader.vert", null));
        this.setFragmentShader(IOUtils.readFile(directory, "shaders/deferred/phong/terrain/fragmentShader.frag", null));
        this.link();

        this.createUniform("projectionMatrix");
        this.createUniform("viewMatrix");
        this.createUniform("modelMatrix");
        this.createUniform("textureSampler");
        this.createUniform("normalSampler");
        this.createUniform("blendmapSampler");
        this.createUniform("hasNormalTexture");
        this.createUniform("materialShininess");
        this.createUniform("tiling");
        this.bind();
        this.setUniform("textureSampler", 0);
        this.setUniform("normalSampler", 1);
        this.setUniform("blendmapSampler", 2);

        ShaderProgram.unbind();
        this.validate();
    }

    public void setMaterial(Material material) {
        this.setUniform("materialShininess", (float) material.getPropertyOrDefault("materialShininess", 0F));
        this.setUniform("hasNormalTexture", (boolean) material.getPropertyOrDefault("hasNormalTexture", false));
        this.setUniform("tiling", (int) material.getPropertyOrDefault("tiling", 0));
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

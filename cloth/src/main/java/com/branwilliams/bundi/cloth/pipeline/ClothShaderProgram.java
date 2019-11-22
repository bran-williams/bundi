package com.branwilliams.bundi.cloth.pipeline;

import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.util.IOUtils;
import com.branwilliams.bundi.engine.util.Mathf;
import org.joml.Matrix4f;

import java.nio.file.Path;

/**
 * Created by Brandon Williams on 6/29/2018.
 */
public class ClothShaderProgram extends ShaderProgram {

    private final Matrix4f worldMatrix = new Matrix4f();

    public ClothShaderProgram(EngineContext engineContext) throws ShaderInitializationException, ShaderUniformException {
        Path directory = engineContext.getAssetDirectory();
        this.setVertexShader(IOUtils.readFile(directory,"shaders/cloth/vertexShader.vert", null));
        this.setFragmentShader(IOUtils.readFile(directory, "shaders/cloth/fragmentShader.frag", null));
        this.link();

        this.createUniform("projectionMatrix");
        this.createUniform("viewMatrix");
        this.createUniform("modelMatrix");

        this.createUniform("viewPos");

        this.createUniform("directionalLight.direction");
        this.createUniform("directionalLight.ambient");
        this.createUniform("directionalLight.diffuse");
        this.createUniform("directionalLight.specular");

//        this.createUniform("material.diffuse");
//        this.createUniform("material.specular");
//        this.createUniform("material.normal");
//        this.createUniform("material.emission");
//
//        this.createUniform("material.shininess");


//        this.bind();
//        this.setUniform("material.diffuse", 0);
//        this.setUniform("material.specular", 1);
//        this.setUniform("material.normal", 2);
//        this.setUniform("material.emission", 3);
//
//        ShaderProgram.unbind();
        this.validate();
    }

//    public void setMaterial(Material material) {
//        this.setUniform("material.shininess", (float) material.getPropertyOrDefault("materialShininess", 0F));
//    }

    public void setLight(DirectionalLight light) {
        this.setUniform("directionalLight.direction", light.getDirection());
        this.setUniform("directionalLight.ambient", light.getAmbient());
        this.setUniform("directionalLight.diffuse", light.getDiffuse());
        this.setUniform("directionalLight.specular", light.getSpecular());
    }

    public void setProjectionMatrix(Projection projection) {
        this.setUniform("projectionMatrix", projection.toProjectionMatrix());
    }

    public void setViewMatrix(Camera camera) {
        this.setUniform("viewMatrix", camera.toViewMatrix());
        this.setUniform("viewPos", camera.getPosition());
    }

    public void setModelMatrix(Transformable transformable) {
        this.setUniform("modelMatrix", Mathf.toModelMatrix(worldMatrix, transformable));
    }
}

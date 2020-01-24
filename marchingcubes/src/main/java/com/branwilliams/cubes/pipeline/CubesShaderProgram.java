package com.branwilliams.cubes.pipeline;

import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.util.IOUtils;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.bundi.engine.util.ShaderUtils;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.nio.file.Path;
import java.util.regex.Pattern;

/**
 * Created by Brandon Williams on 6/29/2018.
 */
public class CubesShaderProgram extends ShaderProgram {

    private final Matrix4f worldMatrix = new Matrix4f();

    public CubesShaderProgram(EngineContext engineContext) throws ShaderInitializationException, ShaderUniformException {
        Path directory = engineContext.getAssetDirectory();

        this.setVertexShader(IOUtils.readFile(directory,"shaders/cubes/vertexShader.vert", null));

        String fragmentShaderCode = IOUtils.readFile(directory, "shaders/cubes/fragmentShader.frag", null);
        fragmentShaderCode = ShaderUtils.replaceComment(fragmentShaderCode,
                "emission",
                (comment) -> "vec3 emission = vec3(0);");
        this.setFragmentShader(fragmentShaderCode);

        this.link();

        this.createUniform("projectionMatrix");
        this.createUniform("viewMatrix");
        this.createUniform("modelMatrix");

        this.createUniform("viewPos");
        this.createUniform("textureColor");

        this.createUniform("directionalLight.direction");
        this.createUniform("directionalLight.ambient");
        this.createUniform("directionalLight.diffuse");
        this.createUniform("directionalLight.specular");

        this.validate();
    }

    public void setTextureColor(Vector4f color) {
        this.setUniform("textureColor", color);
    }

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

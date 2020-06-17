package com.branwilliams.bundi.voxel.render.pipeline.shaders;

import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.material.Material;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.util.IOUtils;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.bundi.engine.util.ShaderUtils;
import com.branwilliams.bundi.voxel.components.Atmosphere;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.nio.file.Path;

/**
 * @author Brandon
 * @since August 16, 2019
 */
public class ChunkShaderProgram extends ShaderProgram {

    private final Vector4f fogColor = new Vector4f(0.5F, 0.6F, 0.7F, 1F);

    private final String fragmentShaderDefines = "#define FOG_EXPONENTIAL\n";

    private final Matrix4f worldMatrix = new Matrix4f();

    public ChunkShaderProgram(EngineContext engineContext) throws ShaderInitializationException, ShaderUniformException {
        super();
        Path directory = engineContext.getAssetDirectory();
        this.setVertexShader(IOUtils.readFile(directory, "voxel/shaders/world/vertexShader.vert", null));
        this.setFragmentShader(ShaderUtils.addDefines(IOUtils.readFile(directory, "voxel/shaders/world/fragmentShader.frag", null), fragmentShaderDefines));
        this.link();

        this.createUniform("projectionMatrix");
        this.createUniform("viewMatrix");
        this.createUniform("modelMatrix");

        this.createUniform("viewPos");

        this.createUniform("directionalLight.direction");
        this.createUniform("directionalLight.ambient");
        this.createUniform("directionalLight.diffuse");
        this.createUniform("directionalLight.specular");

        this.createUniform("material.diffuse");
        this.createUniform("material.specular");
        this.createUniform("material.normal");
        this.createUniform("material.emission");
        this.createUniform("material.shininess");

        this.createUniform("fogDensity");
        this.createUniform("skyColor");
        this.createUniform("sunColor");

//        this.createUniform("fogColor");

        this.bind();
        this.setUniform("material.diffuse", 0);
        this.setUniform("material.specular", 1);
        this.setUniform("material.normal", 2);
        this.setUniform("material.emission", 3);

        ShaderProgram.unbind();
        this.validate();
    }

    public void setMaterial(Material material) {
        this.setUniform("material.shininess", (float) material.getPropertyOrDefault("materialShininess", 0F));
    }

    public void setLight(DirectionalLight light) {
        this.setUniform("directionalLight.direction", light.getDirection());
        this.setUniform("directionalLight.ambient", light.getAmbient());
        this.setUniform("directionalLight.diffuse", light.getDiffuse());
        this.setUniform("directionalLight.specular", light.getSpecular());
//        this.setUniform("fogColor", fogColor);
    }

    public void setAtmosphere(Atmosphere atmosphere) {
        this.setUniform("fogDensity", atmosphere.getFog().getDensity());
        this.setUniform("skyColor", atmosphere.getSkyColor());
        this.setUniform("sunColor", atmosphere.getSunColor());
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

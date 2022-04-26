package com.branwilliams.bundi.voxel.render.pipeline.shaders;

import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.material.Material;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.util.IOUtils;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.bundi.voxel.components.Atmosphere;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.nio.file.Path;

import static com.branwilliams.bundi.voxel.VoxelConstants.*;

/**
 * @author Brandon
 * @since August 16, 2019
 */
public class ChunkShaderProgram extends ShaderProgram {

    private final Vector4f fogColor = new Vector4f(0.5F, 0.6F, 0.7F, 1F);

    private final Matrix4f worldMatrix = new Matrix4f();

    public ChunkShaderProgram(EngineContext engineContext) throws ShaderInitializationException, ShaderUniformException {
        super();
        this.setVertexShader(IOUtils.readResource( "voxel/shaders/world/vertexShader.vert", null));
        this.setFragmentShader(IOUtils.readResource("voxel/shaders/world/fragmentShader.frag", null));
        this.link();

        this.createUniform("projectionMatrix");
        this.createUniform("viewMatrix");
        this.createUniform("modelMatrix");

        this.createUniform("minBlockLight");

        this.createUniform("material.diffuse");
        this.createUniform("material.normal");
        this.createUniform("material.emission");

        this.bind();
        this.setUniform("material.diffuse", 0);
        this.setUniform("material.normal", 1);
        this.setUniform("material.emission", 3);

        ShaderProgram.unbind();
        this.validate();
    }

    public void setMinimumLight(int light) {
        this.setUniform("minBlockLight", Math.max(MIN_LIGHT, Math.min(MAX_LIGHT, light)));
    }

    public void setMaterial(Material material) {
//        this.setUniform("material.shininess", (float) material.getPropertyOrDefault("materialShininess", 0F));
    }

    public void setLight(DirectionalLight light) {
//        this.setUniform("sunPosition", light.getDirection());
//        this.setUniform("fogColor", fogColor);
    }

    public void setAtmosphere(Atmosphere atmosphere) {
//        this.setUniform("fogDensity", atmosphere.getFog().getDensity());
//        this.setUniform("skyColor", atmosphere.getSkyColor());
//        this.setUniform("sunColor", atmosphere.getSunColor());
    }

    public void setProjectionMatrix(Projection projection) {
        this.setUniform("projectionMatrix", projection.toProjectionMatrix());
    }

    public void setViewMatrix(Camera camera) {
        this.setUniform("viewMatrix", camera.toViewMatrix());
//        this.setUniform("viewPos", camera.getPosition());
    }

    public void setModelMatrix(Transformable transformable) {
        this.setUniform("modelMatrix", Mathf.toModelMatrix(worldMatrix, transformable));
    }
}

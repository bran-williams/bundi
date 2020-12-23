package com.branwilliams.bundi.engine.shader;

import com.branwilliams.bundi.engine.material.MaterialFormat;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.shader.patching.ShaderPatch;

import java.util.List;

public interface ShaderModule {

    void createUniforms(ShaderProgram shaderProgram, VertexFormat<?> vertexFormat,
                        MaterialFormat materialFormat, String materialName) throws ShaderUniformException;

    List<ShaderPatch> getShaderPatches();

    void update(ShaderProgram shaderProgram, Projection projection, Camera camera);
}

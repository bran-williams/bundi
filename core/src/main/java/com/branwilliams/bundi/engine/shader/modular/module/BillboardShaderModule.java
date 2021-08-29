package com.branwilliams.bundi.engine.shader.modular.module;

import com.branwilliams.bundi.engine.material.MaterialFormat;
import com.branwilliams.bundi.engine.shader.Camera;
import com.branwilliams.bundi.engine.shader.Projection;
import com.branwilliams.bundi.engine.shader.ShaderProgram;
import com.branwilliams.bundi.engine.shader.ShaderUniformException;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.shader.modular.AbstractShaderModule;
import com.branwilliams.bundi.engine.shader.modular.ModularShaderConstants;
import com.branwilliams.bundi.engine.shader.patching.CommentShaderPatch;
import com.branwilliams.bundi.engine.util.IOUtils;

public class BillboardShaderModule extends AbstractShaderModule {

    private static final String BILLBOARD_LOCATION = "bundi/shaders/modular/billboard.glsl";

    public BillboardShaderModule() {
        this.addShaderPatches(new CommentShaderPatch(ModularShaderConstants.VERTEX_MAIN_COMMENT,
                (s) -> IOUtils.readResource(BILLBOARD_LOCATION, null),
                CommentShaderPatch.ModificationType.PREPEND));
    }

    @Override
    public void createUniforms(ShaderProgram shaderProgram, VertexFormat<?> vertexFormat, MaterialFormat materialFormat,
                               String materialName) throws ShaderUniformException {

    }

    @Override
    public void update(ShaderProgram shaderProgram, Projection projection, Camera camera) {

    }
}

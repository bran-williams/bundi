package com.branwilliams.bundi.engine.shader.modular.patches;

import com.branwilliams.bundi.engine.shader.modular.ModularShaderConstants;
import com.branwilliams.bundi.engine.shader.patching.CommentShaderPatch;


public class FragUniformPatch extends CommentShaderPatch {

    private final String uniform;

    public FragUniformPatch(String uniform) {
        super(ModularShaderConstants.FRAG_UNIFORMS_COMMENT,
                (s) -> "uniform " + uniform + ";\n",
                CommentShaderPatch.ModificationType.PREPEND);
        this.uniform = uniform;
    }

    @Override
    public String toString() {
        return "FragUniformPatch{" +
                "uniform='" + uniform + '\'' +
                '}';
    }
}

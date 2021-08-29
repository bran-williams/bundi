package com.branwilliams.bundi.engine.shader.modular;

import com.branwilliams.bundi.engine.shader.ShaderModule;
import com.branwilliams.bundi.engine.shader.patching.ShaderPatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractShaderModule implements ShaderModule {

    private final List<ShaderPatch> shaderPatches;

    public AbstractShaderModule() {
        shaderPatches = new ArrayList<>();
    }

    public void addShaderPatches(ShaderPatch... shaderPatches) {
        this.shaderPatches.addAll(Arrays.asList(shaderPatches));
    }

    public void addShaderPatches(List<ShaderPatch> shaderPatches) {
        this.shaderPatches.addAll(shaderPatches);
    }

    @Override
    public List<ShaderPatch> getShaderPatches() {
        return shaderPatches;
    }
}

package com.branwilliams.bundi.engine.shader.patching;

import com.branwilliams.bundi.engine.shader.ShaderInitializationException;
import com.branwilliams.bundi.engine.shader.ShaderProgram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Brandon Williams on 11/17/2018.
 */
public class AbstractShaderBuilder implements ShaderBuilder {

    private List<ShaderPatch> patches = new ArrayList<>();

    private ShaderProgram shaderProgram;

    public AbstractShaderBuilder(ShaderPatch... patches) throws ShaderInitializationException {
        this();
        this.setShaderPatches(Arrays.asList(patches));
    }


    public AbstractShaderBuilder(List<ShaderPatch> patches) throws ShaderInitializationException {
        this();
        this.setShaderPatches(patches);
    }

    public AbstractShaderBuilder() throws ShaderInitializationException {
        this.shaderProgram = new ShaderProgram();
    }

    @Override
    public ShaderBuilder vertexShader(String code) throws ShaderInitializationException {
        shaderProgram.setVertexShader(code);
        return this;
    }

    @Override
    public ShaderBuilder fragmentShader(String code) throws ShaderInitializationException {
        shaderProgram.setFragmentShader(code);
        return this;
    }

    @Override
    public ShaderProgram build() throws ShaderInitializationException {
        shaderProgram.link();
        ShaderProgram shaderProgram_ = shaderProgram;
        this.shaderProgram = null;
        return shaderProgram_;
    }

    public List<ShaderPatch> getShaderPatches() {
        return patches;
    }

    public void setShaderPatches(List<ShaderPatch> patches) {
        this.patches = patches;
    }

    public boolean addShaderPatch(ShaderPatch shaderPatch) {
        return patches.add(shaderPatch);
    }

    public boolean removeShaderPatch(Object o) {
        return patches.remove(o);
    }

    public ShaderPatch removeShaderPatch(int index) {
        return patches.remove(index);
    }

    public void clearShaderPatches() {
        patches.clear();
    }
}

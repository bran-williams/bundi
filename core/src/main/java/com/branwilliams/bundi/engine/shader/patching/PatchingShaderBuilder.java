package com.branwilliams.bundi.engine.shader.patching;

import com.branwilliams.bundi.engine.shader.ShaderInitializationException;
import com.branwilliams.bundi.engine.shader.ShaderProgram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by Brandon Williams on 11/17/2018.
 */
public abstract class PatchingShaderBuilder <T extends ShaderProgram> implements ShaderBuilder<T> {

    private List<ShaderPatch> patches = new ArrayList<>();

    private T shaderProgram;

    public PatchingShaderBuilder(ShaderPatch... patches) throws ShaderInitializationException {
        this();
        this.setShaderPatches(new ArrayList<>(Arrays.asList(patches)));
    }


    public PatchingShaderBuilder(List<ShaderPatch> patches) throws ShaderInitializationException {
        this();
        this.setShaderPatches(patches);
    }

    public PatchingShaderBuilder() throws ShaderInitializationException {
        this.shaderProgram = instantiateShaderProgram();
    }

    public static PatchingShaderBuilder<ShaderProgram> defaultShaderBuilder() throws ShaderInitializationException {
        return new PatchingShaderBuilder<ShaderProgram>() {
            @Override
            protected ShaderProgram instantiateShaderProgram() throws ShaderInitializationException {
                return new ShaderProgram();
            }

            @Override
            protected void onBuild(ShaderProgram shaderProgram) {

            }
        };
    }

    /**
     * Creates an instance of shader program named 't'.
     * */
    protected abstract T instantiateShaderProgram() throws ShaderInitializationException;

    /**
     * Invoked when shader program is built.
     * */
    protected abstract void onBuild(T shaderProgram);

    @Override
    public ShaderBuilder<T> vertexShader(String code) throws ShaderInitializationException {
        code = patchCode(code);
        System.out.println("vertexShader=");
        System.out.println(code);
        shaderProgram.setVertexShader(code);
        return this;
    }

    @Override
    public ShaderBuilder<T> fragmentShader(String code) throws ShaderInitializationException {
        code = patchCode(code);
        System.out.println("fragmentShader=");
        System.out.println(code);
        shaderProgram.setFragmentShader(code);
        return this;
    }

    @Override
    public ShaderBuilder<T> geometryShader(String code) throws ShaderInitializationException {
        code = patchCode(code);
        shaderProgram.setGeometryShader(patchCode(code));
        return this;
    }

    @Override
    public ShaderBuilder<T> tessellationControlShader(String code) throws ShaderInitializationException {
        code = patchCode(code);
        shaderProgram.setTessellationControlShader(patchCode(code));
        return this;
    }

    @Override
    public ShaderBuilder<T> tessellationEvaluationShader(String code) throws ShaderInitializationException {
        code = patchCode(code);
        shaderProgram.setTessellationEvaluationShader(patchCode(code));
        return this;
    }

    @Override
    public T build() throws ShaderInitializationException {
        shaderProgram.link();

        // local copy
        T shaderProgram_ = shaderProgram;

        // set instance variable to null for future shader builds?
        this.shaderProgram = null;

        onBuild(shaderProgram_);

        return shaderProgram_;
    }

    protected String patchCode(String code) {
        for (ShaderPatch shaderPatch : patches) {
            code = shaderPatch.patch(code);
        }
        return code;
    }

    public void addShaderPatches(Collection<ShaderPatch> shaderPatches) {
        this.patches.addAll(shaderPatches);
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

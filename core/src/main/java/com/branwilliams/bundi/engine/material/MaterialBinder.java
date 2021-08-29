package com.branwilliams.bundi.engine.material;

import com.branwilliams.bundi.engine.shader.ShaderProgram;
import com.branwilliams.bundi.engine.shader.ShaderUniformException;
import com.branwilliams.bundi.engine.texture.Texture;
import org.joml.*;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class MaterialBinder {

    public static void bindMaterialTextures(Material material) {
        if (material != null && material.hasTextures()) {
            Texture[] textures = material.getTextures();
            for (int i = 0; i < textures.length; i++) {
                if (textures[i] != null) {
                    glActiveTexture(GL_TEXTURE0 + i);
                    textures[i].bind();
                }
            }
        }
    }

    public static void setMaterialTextureUnits(ShaderProgram shaderProgram, MaterialFormat materialFormat,
                                              String materialName)
            throws ShaderUniformException {
        for (MaterialFormat.MaterialEntry materialEntry : materialFormat.getElements().values()) {
            if (materialEntry.elementType.isSampler) {
                String uniformName = getMaterialEntryUniformName(materialName, materialEntry);
                shaderProgram.setUniform(uniformName, materialEntry.textureIndex);
            }
        }
    }

    public static void createMaterialUniforms(ShaderProgram shaderProgram, MaterialFormat materialFormat,
                                              String materialName)
            throws ShaderUniformException {
        if (materialFormat != null) {
            for (MaterialElement element : materialFormat.getElements().keySet()) {
                MaterialFormat.MaterialEntry materialEntry = materialFormat.getElement(element);
                String uniformName = getMaterialEntryUniformName(materialName, materialEntry);
                shaderProgram.createUniform(uniformName);
            }
        }
    }

    public static void setMaterialUniforms(ShaderProgram shaderProgram, Material material,
                                           MaterialFormat materialFormat, String materialName) {
        if (materialFormat != null) {
            for (MaterialElement element : materialFormat.getElements().keySet()) {
                MaterialFormat.MaterialEntry materialEntry = materialFormat.getElement(element);
                setMaterialUniform(shaderProgram, materialName, materialEntry, material);
            }
        }
    }

    public static void createMaterialUniform(ShaderProgram shaderProgram, String materialName,
                                             MaterialFormat materialFormat, MaterialElement materialElement)
            throws ShaderUniformException {
        MaterialFormat.MaterialEntry materialEntry = materialFormat.getElement(materialElement);
        String uniformName = getMaterialEntryUniformName(materialName, materialEntry);

        shaderProgram.createUniform(uniformName);

        if (materialEntry.elementType.isSampler) {
            shaderProgram.setUniform(uniformName, materialEntry.textureIndex);
        }
    }

    public static void setMaterialUniform(ShaderProgram shaderProgram, String materialName,
                                          MaterialFormat.MaterialEntry materialEntry, Material material) {
        String uniformName = getMaterialEntryUniformName(materialName, materialEntry);

        switch (materialEntry.elementType) {
            case VEC2:
                Vector2f vec2 = material.getProperty(materialEntry.variableName);
                shaderProgram.setUniform(uniformName, vec2);
                break;
            case VEC3:
                Vector3f vec3 = material.getProperty(materialEntry.variableName);
                shaderProgram.setUniform(uniformName, vec3);
                break;
            case VEC4:
                Vector4f vec4 = material.getProperty(materialEntry.variableName);
                shaderProgram.setUniform(uniformName, vec4);
                break;
            case MAT3:
                Matrix3f mat3 = material.getProperty(materialEntry.variableName);
                shaderProgram.setUniform(uniformName, mat3);
                break;
            case MAT4:
                Matrix4f mat4 = material.getProperty(materialEntry.variableName);
                shaderProgram.setUniform(uniformName, mat4);
                break;
            default:
                break;
        }
    }

    public static String getMaterialEntryUniformName(String materialName,
                                                     MaterialFormat.MaterialEntry materialEntry) {
        return materialName + "." + materialEntry.variableName;
    }

}

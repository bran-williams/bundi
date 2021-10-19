package com.branwilliams.bundi.engine.util;

import com.branwilliams.bundi.engine.material.MaterialElement;
import com.branwilliams.bundi.engine.material.MaterialElementType;
import com.branwilliams.bundi.engine.material.MaterialFormat;
import com.branwilliams.bundi.engine.shader.dynamic.VertexElement;
import com.branwilliams.bundi.engine.shader.dynamic.VertexElements;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.shader.modular.ModularShaderConstants;
import com.branwilliams.bundi.engine.shader.patching.CommentShaderPatch;
import com.branwilliams.bundi.engine.shader.patching.ShaderPatch;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Brandon
 * @since January 05, 2020
 */
public class ShaderUtils {

    private static final Pattern C_STYLE_COMMENT_PATTERN = Pattern.compile("\\/\\*(.*)\\*\\/");

    /**
     * Creates a GLSL layout with the location qualifier.
     *
     * @param location the value for the location qualifier. must be an integer.
     * @param code the code following this layout declaration.
     *
     * @return A GLSL layout declaration with location=someValue.
     * */
    public static String createLayout(int location, String code) {
        return String.format("layout (location = %s) %s\n", location, code);
    }

    public static String addDefines(String code, String defines) {
        int firstNewlineIndex = code.indexOf("\n");

        String versionDefine = code.substring(0, firstNewlineIndex);
        code = code.substring(firstNewlineIndex);

        return versionDefine + "\n" + defines + "\n" + code;
    }

    /**
     * Replaces comments within some code using a function to provide the new value. A regex for said comment will be
     * applied to each comment found by an initial regex pattern which searches for every c-style comment.
     *
     * @param code The code to modify.
     * @param commentRegex The regex pattern used to identify a comment in need of replacement.
     * @param commentModifier The function which replaces a comment.
     *
     * @return The modified code.
     * */
    public static String replaceComment(String code, String commentRegex, Function<String, String> commentModifier) {
        Matcher commentMatcher = C_STYLE_COMMENT_PATTERN.matcher(code);
        if (!commentMatcher.find()) {
            throw new IllegalStateException("No match found");
        }

        Pattern commentPattern = Pattern.compile(commentRegex);

        for (int i = 0; i < commentMatcher.groupCount(); i++) {
            String text = commentMatcher.group(i);
            if (commentPattern.matcher(text).find()) {
                code = code.substring(0, code.indexOf(text)) + commentModifier.apply(text) +
                        code.substring(code.indexOf(text) + text.length());
            }
        }

        return code;
    }

    /**
     * See {@link ShaderUtils#createInputOutputShaderPatches(List, VertexElement, Function, String, String, String)}.
     * <br/> <br/>
     * This function uses the following for passValueFunction: (variableName) -> variableName
     * */
    public static void createInputOutputShaderPatches(List<ShaderPatch> shaderPatches, VertexElement vertexElement,
                                                      String shaderOutPattern, String shaderMainPattern,
                                                      String nextShaderInPattern) {
        createInputOutputShaderPatches(shaderPatches, vertexElement, (variableName) -> variableName, shaderOutPattern,
                shaderMainPattern, nextShaderInPattern);
    }

    /**
     * Creates three shader patches which generate lines for the in, out, and variable assignment for a given vertex
     * element. This also adds them to the list of shader patches.
     *
     * @param shaderPatches The list of patches to add to.
     * @param vertexElement The {@link VertexElement} which defines the shader 'in' variable name, 'out' variable name
     *                      (passName), and variable type.
     * @param passValueFunction Provides the value assigned to the out variable.
     * @param shaderOutPattern regex pattern for the shaders 'out' comment.
     * @param shaderMainPattern regex pattern for the shaders 'main' comment.
     * @param nextShaderInPattern revex pattern for the next shaders 'in' comment.
     * */
    public static void createInputOutputShaderPatches(List<ShaderPatch> shaderPatches, VertexElement vertexElement,
                                                      Function<String, String> passValueFunction,
                                                      String shaderOutPattern, String shaderMainPattern,
                                                      String nextShaderInPattern) {
        String variableName = vertexElement.getVariableName();
        String passVariableName = vertexElement.getPassName();
        String variableType = vertexElement.getType();
        createInputOutputShaderPatches(shaderPatches, variableName, passVariableName, variableType, passValueFunction,
                shaderOutPattern, shaderMainPattern, nextShaderInPattern);
    }

    /**
     * Creates three shader patches which generate lines for the in, out, and variable assignment for a given vertex
     * element. This also adds them to the list of shader patches.
     *
     * @param shaderPatches The list of patches to add to.
     * @param variableName The 'in' variable name.
     * @param passVariableName The 'out' variable name.
     * @param variableType The GLSL type of this variable.
     * @param passValueFunction Provides the value assigned to the out variable.
     * @param shaderOutPattern regex pattern for the shaders 'out' comment.
     * @param shaderMainPattern regex pattern for the shaders 'main' comment.
     * @param nextShaderInPattern revex pattern for the next shaders 'in' comment.
     * */
    public static void createInputOutputShaderPatches(List<ShaderPatch> shaderPatches, String variableName,
                                                      String passVariableName, String variableType,
                                                      Function<String, String> passValueFunction,
                                                      String shaderOutPattern, String shaderMainPattern,
                                                      String nextShaderInPattern) {
        shaderPatches.add(new CommentShaderPatch(shaderOutPattern,
                (s) -> String.format("out %s %s;\n", variableType, passVariableName),
                CommentShaderPatch.ModificationType.PREPEND));

        shaderPatches.add(new CommentShaderPatch(shaderMainPattern,
                (s) -> String.format("%s = %s;\n", passVariableName, passValueFunction.apply(variableName)),
                CommentShaderPatch.ModificationType.PREPEND));

        shaderPatches.add(new CommentShaderPatch(nextShaderInPattern,
                (s) -> String.format("in %s %s;\n", variableType, passVariableName),
                CommentShaderPatch.ModificationType.PREPEND));
    }

    public static String getMaterialNormalAsVec4(MaterialFormat materialFormat, String materialName) {
        return getMaterialElementAsVec4(materialFormat, materialName, MaterialElement.NORMAL, "0.0");
    }
    public static String getMaterialNormalAsVec3(MaterialFormat materialFormat, String materialName) {
        return getMaterialElementAsVec3(materialFormat, materialName, MaterialElement.NORMAL, "0.0");
    }

    public static String getMaterialDiffuseAsVec4(MaterialFormat materialFormat, String materialName) {
        return getMaterialElementAsVec4(materialFormat, materialName, MaterialElement.DIFFUSE, "1.0");
    }
    public static String getMaterialDiffuseAsVec3(MaterialFormat materialFormat, String materialName) {
        return getMaterialElementAsVec3(materialFormat, materialName, MaterialElement.DIFFUSE, "1.0");
    }

    public static String getMaterialSpecularAsVec4(MaterialFormat materialFormat, String materialName) {
        return getMaterialElementAsVec4(materialFormat, materialName, MaterialElement.SPECULAR, "0.0");
    }
    public static String getMaterialSpecularAsVec3(MaterialFormat materialFormat, String materialName) {
        return getMaterialElementAsVec3(materialFormat, materialName, MaterialElement.SPECULAR, "0.0");
    }

    public static String getMaterialEmissiveAsVec4(MaterialFormat materialFormat, String materialName) {
        return getMaterialElementAsVec4(materialFormat, materialName, MaterialElement.EMISSIVE, "0.0");
    }
    public static String getMaterialEmissiveAsVec3(MaterialFormat materialFormat, String materialName) {
        return getMaterialElementAsVec3(materialFormat, materialName, MaterialElement.EMISSIVE, "0.0");
    }

    public static String getMaterialElementAsVec4(MaterialFormat materialFormat, String materialName,
                                                  MaterialElement materialElement, String defaultValue) {
        return getMaterialElementAs(materialFormat, materialElement, MaterialElementType.VEC4,
                materialName, VertexElements.UV.getPassName(), "vec4(" + defaultValue + ")");
    }

    public static String getMaterialElementAsVec3(MaterialFormat materialFormat, String materialName,
                                                  MaterialElement materialElement, String defaultValue) {
        return getMaterialElementAs(materialFormat, materialElement, MaterialElementType.VEC3,
                materialName, VertexElements.UV.getPassName(), "vec3(" + defaultValue + ")");
    }

    private static String getMaterialElementAs(MaterialFormat materialFormat, MaterialElement materialElement,
                                               MaterialElementType elementType, String materialName, String uv,
                                               String defaultValue) {
        if (materialFormat.hasElement(materialElement)) {
            MaterialFormat.MaterialEntry entry = materialFormat.getElement(materialElement);
            if (entry.elementType == elementType) {
                return materialName + "." + entry.variableName;
            }
            if (elementType == MaterialElementType.VEC3) {
                switch (entry.elementType) {
                    case VEC4:
                        return materialName + "." + entry.variableName + ".rgb";
                    case SAMPLER_2D:
                        return "texture(" + materialName + "." + entry.variableName + ", " + uv + ").rgb";
                    default:
                        break;
                }
            }

            if (elementType == MaterialElementType.VEC4) {
                switch (entry.elementType) {
                    case VEC3:
                        return "vec4(" + materialName + "." + entry.variableName + ", 1.0)";
                    case SAMPLER_2D:
                        return "texture(" + materialName + "." + entry.variableName + ", " + uv + ")";
                    default:
                        break;
                }
            }
        }
        return defaultValue;
    }

    public static String capitalizeFirstChar(String string) {
        return string == null || string.isEmpty() ? "" : string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static String patchCode(String code, List<ShaderPatch> shaderPatches) {
        for (ShaderPatch shaderPatch : shaderPatches) {
            code = shaderPatch.patch(code);
        }
        return code;
    }
}

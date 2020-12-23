package com.branwilliams.bundi.engine.util;

import com.branwilliams.bundi.engine.material.MaterialElement;
import com.branwilliams.bundi.engine.material.MaterialElementType;
import com.branwilliams.bundi.engine.material.MaterialFormat;
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
     * Creates a layout with the location provided.
     * */
    public static String createLayout(int location, String code) {
        return "layout (location = " + location + ") " + code + "\n";
    }

    public static String addDefines(String code, String defines) {
        int firstNewlineIndex = code.indexOf("\n");

        String versionDefine = code.substring(0, firstNewlineIndex);
        code = code.substring(firstNewlineIndex);

        return versionDefine + "\n" + defines + "\n" + code;
    }

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

    public static String getMaterialDiffuseAsVec4(MaterialFormat materialFormat, String materialName, String uv) {
        return getMaterialElementAs(materialFormat, MaterialElement.DIFFUSE, MaterialElementType.VEC4,
                materialName, uv, "vec4(1.0)");
    }

    public static String getMaterialDiffuseAsVec3(MaterialFormat materialFormat, String materialName, String uv) {
        return getMaterialElementAs(materialFormat, MaterialElement.DIFFUSE, MaterialElementType.VEC3,
                materialName, uv, "vec3(1.0)");
    }

    public static String getMaterialSpecularAsVec3(MaterialFormat materialFormat, String materialName, String uv) {
        return getMaterialElementAs(materialFormat, MaterialElement.SPECULAR, MaterialElementType.VEC3,
                materialName, uv, "vec3(1.0)");
    }

    public static String getMaterialEmissiveAsVec4(MaterialFormat materialFormat, String materialName, String uv) {
        return getMaterialElementAs(materialFormat, MaterialElement.EMISSIVE, MaterialElementType.VEC4,
                materialName, uv, "vec4(0.0)");
    }

    public static String getMaterialEmissiveAsVec3(MaterialFormat materialFormat, String materialName, String uv) {
        return getMaterialElementAs(materialFormat, MaterialElement.EMISSIVE, MaterialElementType.VEC3,
                materialName, uv, "vec3(0.0)");
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

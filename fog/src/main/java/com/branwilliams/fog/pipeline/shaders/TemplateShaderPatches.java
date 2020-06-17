package com.branwilliams.fog.pipeline.shaders;


import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.material.*;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.*;
import com.branwilliams.bundi.engine.shader.patching.PatchingShaderBuilder;
import com.branwilliams.bundi.engine.shader.patching.CommentShaderPatch;
import com.branwilliams.bundi.engine.shader.patching.ShaderPatch;
import com.branwilliams.bundi.engine.util.IOUtils;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Brandon Williams on 1/5/2018.
 */
public class TemplateShaderPatches {

    public static ShaderProgram buildTemplateShaderProgram(EngineContext engineContext, VertexFormat vertexFormat,
                                                           MaterialFormat materialFormat,
                                                           String materialName,
                                                           String lightName)
            throws ShaderInitializationException, ShaderUniformException {
        Path directory = engineContext.getAssetDirectory();


        PatchingShaderBuilder shaderBuilder = PatchingShaderBuilder.defaultShaderBuilder();
        shaderBuilder.addShaderPatches(getVertexShaderInputPatches(vertexFormat));
        shaderBuilder.addShaderPatches(getMaterialShaderPatches(materialFormat, materialName));
        shaderBuilder.addShaderPatches(getDirectionalLightShaderPatches(lightName, materialFormat, materialName));

        ShaderProgram shaderProgram = shaderBuilder
                .vertexShader(IOUtils.readFile(directory, "fog/shaders/com.branwilliams.demo.template/vertexShader.vert", null))
                .fragmentShader(IOUtils.readFile(directory, "fog/shaders/com.branwilliams.demo.template/fragmentShader.frag", null))
                .build();

        shaderProgram.bind();
        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("viewMatrix");
        shaderProgram.createUniform("viewPos");
        shaderProgram.createUniform("modelMatrix");
        shaderProgram.createUniform(lightName + ".direction");
        shaderProgram.createUniform(lightName + ".ambient");
        shaderProgram.createUniform(lightName + ".diffuse");
        shaderProgram.createUniform(lightName + ".specular");

        MaterialBinder.createMaterialUniforms(shaderProgram, materialFormat, materialName);
        MaterialBinder.setMaterialTextureUnits(shaderProgram, materialFormat, materialName);

//        shaderProgram.createUniform("fogColor");
//        shaderProgram.createUniform("fogDensity");

        ShaderProgram.unbind();
        return shaderProgram;
    }

    /**
     * Creates a layout with the location provided.
     * */
    private static String createLayout(int location, String code) {
        return "layout (location = " + location + ") " + code + "\n";
    }

    public static String getVertexShaderInput(VertexFormat vertexFormat) {
        StringBuilder vertexShaderInput = new StringBuilder();
        // Create input lines.
        for (int i = 0; i < vertexFormat.getVertexElements().size(); i++) {
            VertexElements vertexElements = vertexFormat.getVertexElements().get(i);
            vertexShaderInput.append(createLayout(i, "in "
                    + vertexElements.getType()
                    + " "
                    + vertexElements.getVariableName()
                    + ";"));
        }
        return vertexShaderInput.toString();
    }

    public static void setProjectionMatrix(ShaderProgram shaderProgram, Projection projection) {
        shaderProgram.setUniform("projectionMatrix", projection.toProjectionMatrix());
    }

    public static void setViewMatrix(ShaderProgram shaderProgram, Camera camera) {
        shaderProgram.setUniform("viewMatrix", camera.toViewMatrix());
    }

    public static void setModelMatrix(ShaderProgram shaderProgram, Transformable transformable) {
        shaderProgram.setUniform("modelMatrix", transformable.toMatrix(new Matrix4f()));
    }

    public static void setViewPos(ShaderProgram shaderProgram, Camera camera) {
        shaderProgram.setUniform("viewPos", camera.getPosition());
    }

    public static void setDirectionalLight(ShaderProgram shaderProgram, DirectionalLight directionalLight,
                                           String lightName) {
        shaderProgram.setUniform(lightName + ".direction", directionalLight.getDirection());
        shaderProgram.setUniform(lightName + ".ambient", directionalLight.getAmbient());
        shaderProgram.setUniform(lightName + ".diffuse", directionalLight.getDiffuse());
        shaderProgram.setUniform(lightName + ".specular", directionalLight.getSpecular());
    }

    public static void setFog(ShaderProgram shaderProgram, Fog fog) {
//        shaderProgram.setUniform("fogColor", fog.getColor());
//        shaderProgram.setUniform("fogDensity", fog.getDensity());
    }

    public static List<ShaderPatch> getVertexShaderInputPatches(VertexFormat vertexFormat) {
        List<ShaderPatch> shaderPatches = new ArrayList<>();
        shaderPatches.add(new CommentShaderPatch(Pattern.compile("vslayout"),
                (s) -> getVertexShaderInput(vertexFormat)));

        if (vertexFormat.hasElement(VertexElements.UV)) {
            shaderPatches.add(new CommentShaderPatch(Pattern.compile("vsout"),
                    (s) -> "out vec2 passTextureCoordinates;\n", CommentShaderPatch.ModificationType.PREPEND));
            shaderPatches.add(new CommentShaderPatch(Pattern.compile("vsmain"),
                    (s) -> "passTextureCoordinates = textureCoordinates;\n",
                    CommentShaderPatch.ModificationType.PREPEND));
            shaderPatches.add(new CommentShaderPatch(Pattern.compile("fragin"),
                    (s) -> "in vec2 passTextureCoordinates;\n", CommentShaderPatch.ModificationType.PREPEND));
        }

        if (vertexFormat.hasElement(VertexElements.NORMAL)) {
            shaderPatches.add(new CommentShaderPatch(Pattern.compile("vsout"),
                    (s) -> "out vec3 passNormal;\n", CommentShaderPatch.ModificationType.PREPEND));
            shaderPatches.add(new CommentShaderPatch(Pattern.compile("vsmain"),
                    (s) -> "passNormal = mat3(transpose(inverse(modelMatrix))) * normal;\n",
                    CommentShaderPatch.ModificationType.PREPEND));
            shaderPatches.add(new CommentShaderPatch(Pattern.compile("fragin"),
                    (s) -> "in vec3 passNormal;\n", CommentShaderPatch.ModificationType.PREPEND));
        }
        return shaderPatches;
    }

    public static List<ShaderPatch> getMaterialShaderPatches(MaterialFormat materialFormat, String materialName) {
        List<ShaderPatch> shaderPatches = new ArrayList<>();
        shaderPatches.add(new CommentShaderPatch(Pattern.compile("material"),
                (s) -> materialFormat.toGLSLUniform(materialName)));

        shaderPatches.add(new CommentShaderPatch(Pattern.compile("fragcolor"),
                (s) -> "pixelColor = "
                        + getMaterialDiffuseAsVec4(materialFormat, materialName, "passTextureCoordinates")
                        + ";\n",
                CommentShaderPatch.ModificationType.PREPEND));
        return shaderPatches;
    }

    public static List<ShaderPatch> getDirectionalLightShaderPatches(String lightName, MaterialFormat materialFormat,
                                                                     String materialName) {
        List<ShaderPatch> shaderPatches = new ArrayList<>();

//        shaderPatches.add(new CommentShaderPatch(Pattern.compile("defines"),
//                (s) -> "#define FOG 1\n", CommentShaderPatch.ModificationType.PREPEND));

        shaderPatches.add(new CommentShaderPatch(Pattern.compile("fraguniforms"),
                (s) -> "struct DirLight {\n"
                        + "    vec3 direction;\n"
                        + "\n"
                        + "    vec3 ambient;\n"
                        + "    vec3 diffuse;\n"
                        + "    vec3 specular;\n"
                        + "};\n"
                        + "uniform DirLight " + lightName + ";\n"
                        + ""));

        String diffuse = getMaterialDiffuseAsVec3(materialFormat, materialName, "passTextureCoordinates");
        String specular = getMaterialSpecularAsVec3(materialFormat, materialName, "passTextureCoordinates");
        String emissive = getMaterialEmissiveAsVec3(materialFormat, materialName, "passTextureCoordinates");

        shaderPatches.add(new CommentShaderPatch(Pattern.compile("fragcolor"),
                (s) -> "vec3 normal   = passNormal;\n"
                        + "vec3 viewDir  = normalize(viewPos - passFragPos);\n"
                        + "\n"
                        + "// directional lighting below:\n"
                        + "vec3 lightDir = normalize(-" + lightName + ".direction);\n"
                        + "\n"
                        + "// diffuse calculation\n"
                        + "float diff = max(dot(normal, lightDir), 0.0);\n"
                        + "\n"
                        + "// specular calculation\n"
                        + "vec3 reflectDir = reflect(-lightDir, normal);\n"
                        + "float spec = pow(max(dot(viewDir, reflectDir), 0.0), "
                        + getMaterialShininess(materialFormat) + ");\n"
                        + "\n"
                        + "vec3 ambient  = " + lightName + ".ambient  *        " + diffuse + ";\n"
                        + "vec3 diffuse  = " + lightName + ".diffuse  * diff * " + diffuse + ";\n"
                        + "vec3 specular = " + lightName + ".specular * spec * " + specular + ";\n"
                        + "\n"
                        + "vec3 emission = " + emissive + ";\n"
                        + "\n"
                        + "vec4 lightColor = vec4(ambient + diffuse + specular + emission, 1.0);\n"
                        + "pixelColor = lightColor;"));
        return shaderPatches;
    }

    private static String getMaterialShininess(MaterialFormat materialFormat) {
        return "16.0";
    }

    private static String getMaterialDiffuseAsVec4(MaterialFormat materialFormat, String materialName, String uv) {
        return getMaterialElementAs(materialFormat, MaterialElement.DIFFUSE, MaterialElementType.VEC4,
                materialName, uv, "vec4(1.0)");
    }

    private static String getMaterialDiffuseAsVec3(MaterialFormat materialFormat, String materialName, String uv) {
        return getMaterialElementAs(materialFormat, MaterialElement.DIFFUSE, MaterialElementType.VEC3,
                materialName, uv, "vec3(1.0)");
    }

    private static String getMaterialSpecularAsVec3(MaterialFormat materialFormat, String materialName, String uv) {
        return getMaterialElementAs(materialFormat, MaterialElement.SPECULAR, MaterialElementType.VEC3,
                materialName, uv, "vec3(1.0)");
    }

    private static String getMaterialEmissiveAsVec3(MaterialFormat materialFormat, String materialName, String uv) {
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

    public class AlphaShaderModule {

    }
}

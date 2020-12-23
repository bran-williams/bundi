package com.branwilliams.bundi.engine.shader.module;

import com.branwilliams.bundi.engine.material.MaterialElement;
import com.branwilliams.bundi.engine.material.MaterialFormat;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.shader.patching.CommentShaderPatch;
import com.branwilliams.bundi.engine.shader.patching.ShaderPatch;
import com.branwilliams.bundi.engine.util.IOUtils;
import com.branwilliams.bundi.engine.util.ShaderUtils;

import java.util.ArrayList;
import java.util.List;

import static com.branwilliams.bundi.engine.shader.EnvironmentShaderProgram.*;

public class EnvironmentShaderModule extends AbstractShaderModule {

    protected final Environment environment;

    protected final MaterialFormat materialFormat;

    public EnvironmentShaderModule(Environment environment, MaterialFormat materialFormat, String materialName) {
        this.environment = environment;
        this.materialFormat = materialFormat;
        this.addShaderPatches(createShaderPatches(materialFormat, materialName));
    }

    protected List<ShaderPatch> createShaderPatches(MaterialFormat materialFormat, String materialName) {
        List<ShaderPatch> shaderPatches = new ArrayList<>(createFogShaderPatches(materialFormat, materialName));

        // Sets up variables needed for light rendering
        shaderPatches.addAll(createAllLightVariablesShaderPatches(materialFormat, materialName));

        // Calculate directional light factor
        shaderPatches.addAll(createDirectionalLightShaderPatches(materialFormat, materialName));

        // Calculate point light factor
        shaderPatches.addAll(createPointLightShaderPatches(materialFormat, materialName));

        // Add emissive factor LAST
        shaderPatches.add(createEmissiveMaterialShaderPatch(materialFormat, materialName));

        return shaderPatches;
    }

    @Override
    public void createUniforms(ShaderProgram shaderProgram, VertexFormat<?> vertexFormat,
                               MaterialFormat materialFormat, String materialName) throws ShaderUniformException {

        if (needViewPos(materialFormat))
            shaderProgram.createUniform("viewPos");

        if (environment.hasLights()) {
            if (environment.hasDirectionalLights()) {
                for (int i = 0; i < environment.getDirectionalLights().length; i++) {
                    shaderProgram.createUniform("dirLights[" + i + "].direction");
                    shaderProgram.createUniform("dirLights[" + i + "].ambient");
                    shaderProgram.createUniform("dirLights[" + i + "].diffuse");
                    shaderProgram.createUniform("dirLights[" + i + "].specular");
                }
            }

            if (environment.hasPointLights()) {
                for (int i = 0; i < environment.getPointLights().length; i++) {
                    shaderProgram.createUniform("pointLights[" + i + "].position");

                    shaderProgram.createUniform("pointLights[" + i + "].constant");
                    shaderProgram.createUniform("pointLights[" + i + "].linear");
                    shaderProgram.createUniform("pointLights[" + i + "].quadratic");

                    shaderProgram.createUniform("pointLights[" + i + "].ambient");
                    shaderProgram.createUniform("pointLights[" + i + "].diffuse");
                    shaderProgram.createUniform("pointLights[" + i + "].specular");
                }
            }
        }

        if (environment.hasFog()) {
            shaderProgram.createUniform("fogColor");
            shaderProgram.createUniform("fogDensity");
        }
    }

    @Override
    public void update(ShaderProgram shaderProgram, Projection projection, Camera camera) {
        setEnvironment(shaderProgram);

        if (needViewPos(materialFormat))
            shaderProgram.setUniform("viewPos", camera.getPosition());
    }

    public void setEnvironment(ShaderProgram shaderProgram) {
        if (environment.hasDirectionalLights()) {
            for (int i = 0; i < environment.getDirectionalLights().length; i++) {
                DirectionalLight light = environment.getDirectionalLights()[i];
                shaderProgram.setUniform("dirLights[" + i + "].direction", light.getDirection());
                shaderProgram.setUniform("dirLights[" + i + "].ambient", light.getAmbient());
                shaderProgram.setUniform("dirLights[" + i + "].diffuse", light.getDiffuse());
                shaderProgram.setUniform("dirLights[" + i + "].specular", light.getSpecular());
            }
        }

        if (environment.hasPointLights()) {
            for (int i = 0; i < environment.getPointLights().length; i++) {
                PointLight light = environment.getPointLights()[i];
                shaderProgram.setUniform("pointLights[" + i + "].position", light.getPosition());

                shaderProgram.setUniform("pointLights[" + i + "].constant", light.getAttenuation().getConstant());
                shaderProgram.setUniform("pointLights[" + i + "].linear", light.getAttenuation().getLinear());
                shaderProgram.setUniform("pointLights[" + i + "].quadratic", light.getAttenuation().getQuadratic());

                shaderProgram.setUniform("pointLights[" + i + "].ambient", light.getAmbient());
                shaderProgram.setUniform("pointLights[" + i + "].diffuse", light.getDiffuse());
                shaderProgram.setUniform("pointLights[" + i + "].specular", light.getSpecular());
            }
        }

        if (environment.hasFog()) {
            shaderProgram.setUniform("fogColor", environment.getFog().getColor());
            shaderProgram.setUniform("fogDensity", environment.getFog().getDensity());
        }
    }

    protected boolean needViewPos(MaterialFormat materialFormat) {
        return environment.hasLights() && materialFormat.hasElement(MaterialElement.SPECULAR);
    }

    protected static String getMaterialShininess(MaterialFormat materialFormat) {
        return "8.0";
    }

    public List<ShaderPatch> createFogShaderPatches(MaterialFormat materialFormat,
                                                    String materialName) {
        List<ShaderPatch> shaderPatches = new ArrayList<>();

        if (environment.getFog() == null)
            return shaderPatches;

        shaderPatches.add(new CommentShaderPatch(DEFINES_COMMENT,
                (s) -> "#define FOG 1\n", CommentShaderPatch.ModificationType.PREPEND));

        return shaderPatches;
    }

    protected List<ShaderPatch> createAllLightVariablesShaderPatches(MaterialFormat materialFormat,
                                                                     String materialName) {
        List<ShaderPatch> shaderPatches = new ArrayList<>();

        // if (needViewPos(materialFormat))
        shaderPatches.add(new CommentShaderPatch(FRAG_UNIFORMS_COMMENT,
                (s) -> "uniform vec3 viewPos;\n", CommentShaderPatch.ModificationType.PREPEND));

        if (!environment.hasLights()) {
            shaderPatches.add(new CommentShaderPatch(FRAG_COLOR_COMMENT,
                    (s) -> "pixelColor = "
                            + ShaderUtils.getMaterialDiffuseAsVec4(materialFormat, materialName,
                            "passTextureCoordinates")
                            + ";\n", CommentShaderPatch.ModificationType.PREPEND));

            return shaderPatches;
        }

        shaderPatches.add(new CommentShaderPatch(FRAG_COLOR_COMMENT,
                (s) -> "    vec3 viewDir  = normalize(viewPos - passFragPos);\n\n",
                CommentShaderPatch.ModificationType.PREPEND));

        return shaderPatches;
    }

    protected List<ShaderPatch> createPointLightShaderPatches(MaterialFormat materialFormat, String materialName) {
        List<ShaderPatch> shaderPatches = new ArrayList<>();

        if (!environment.hasPointLights())
            return shaderPatches;

        int numLights = environment.getPointLights().length;

        shaderPatches.add(new CommentShaderPatch(FRAG_UNIFORMS_COMMENT,
                (s) -> PointLight.toGLSLStruct()
                        + "uniform " + PointLight.getStructName() + " pointLights[" + numLights + "];\n"
                        + "", CommentShaderPatch.ModificationType.PREPEND));

        shaderPatches.add(new CommentShaderPatch(FRAG_COLOR_COMMENT,
                (s) -> patchMaterialEntries(materialFormat, materialName, numLights,
                        IOUtils.readResource("bundi/shaders/environment/pointLight.glsl", null)),
                CommentShaderPatch.ModificationType.PREPEND));

        return shaderPatches;
    }

    protected List<ShaderPatch> createDirectionalLightShaderPatches(MaterialFormat materialFormat,
                                                                    String materialName) {
        List<ShaderPatch> shaderPatches = new ArrayList<>();

        if (!environment.hasDirectionalLights())
            return shaderPatches;

        int numLights = environment.getDirectionalLights().length;

        shaderPatches.add(new CommentShaderPatch(FRAG_UNIFORMS_COMMENT,
                (s) -> DirectionalLight.toGLSLStruct()
                        + "uniform " + DirectionalLight.getStructName() + " dirLights[" + numLights + "];\n"
                        + "", CommentShaderPatch.ModificationType.PREPEND));

        shaderPatches.add(new CommentShaderPatch(FRAG_COLOR_COMMENT,
                (s) -> patchMaterialEntries(materialFormat, materialName, numLights,
                        IOUtils.readResource("bundi/shaders/environment/dirLight.glsl", null)),
                CommentShaderPatch.ModificationType.PREPEND));

        return shaderPatches;
    }

    protected ShaderPatch createEmissiveMaterialShaderPatch(MaterialFormat materialFormat, String materialName) {
        String emissive = ShaderUtils.getMaterialEmissiveAsVec4(materialFormat, materialName,
                "passTextureCoordinates");

        return new CommentShaderPatch(FRAG_COLOR_COMMENT,
                (s) -> "    pixelColor += " + emissive + ";\n", CommentShaderPatch.ModificationType.PREPEND);
    }

    protected String patchMaterialEntries(MaterialFormat materialFormat, String materialName, int numLights,
                                          String code) {
        String diffuse = ShaderUtils.getMaterialDiffuseAsVec3(materialFormat, materialName,
                "passTextureCoordinates");
        String specular = ShaderUtils.getMaterialSpecularAsVec3(materialFormat, materialName,
                "passTextureCoordinates");
        String shininess = getMaterialShininess(materialFormat);

        return code.replaceAll("material_diffuse", diffuse)
                .replaceAll("material_specular", specular)
                .replaceAll("material_shininess", shininess)
                .replaceAll("light_count", String.valueOf(numLights));
    }
}

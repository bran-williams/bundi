package com.branwilliams.bundi.engine.shader.modular.module;

import com.branwilliams.bundi.engine.material.MaterialElement;
import com.branwilliams.bundi.engine.material.MaterialFormat;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.VertexElements;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.shader.modular.AbstractShaderModule;
import com.branwilliams.bundi.engine.shader.modular.ModularShaderConstants;
import com.branwilliams.bundi.engine.shader.modular.patches.FragUniformPatch;
import com.branwilliams.bundi.engine.shader.patching.CommentShaderPatch;
import com.branwilliams.bundi.engine.shader.patching.LineShaderPatch;
import com.branwilliams.bundi.engine.shader.patching.ShaderPatch;
import com.branwilliams.bundi.engine.util.IOUtils;
import com.branwilliams.bundi.engine.util.ShaderUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class EnvironmentShaderModule extends AbstractShaderModule {

    private static final String POINT_LIGHT_GLSL_LOCATION = "bundi/shaders/environment/pointLight.glsl";

    private static final String DIR_LIGHT_GLSL_LOCATION = "bundi/shaders/environment/dirLight.glsl";

    protected final Supplier<Environment> environment;

    protected final MaterialFormat materialFormat;

    public EnvironmentShaderModule(Supplier<Environment> environment, VertexFormat<?> vertexFormat,
                                   MaterialFormat materialFormat) {
        this(environment, vertexFormat, materialFormat, ModularShaderConstants.DEFAULT_MATERIAL_NAME);
    }

    public EnvironmentShaderModule(Supplier<Environment> environment, VertexFormat<?> vertexFormat,
                                   MaterialFormat materialFormat, String materialName) {
        this.environment = environment;
        this.materialFormat = materialFormat;
        if (!isVertexFormatValidForEnvironment(vertexFormat, environment)) {
            throw new IllegalArgumentException("VertexFormat must have a normal element for the environment!");
        }
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

        shaderPatches.addAll(createMaterialElementPatches(materialFormat, materialName,
                environment.get().getPointLightCount(), environment.get().getDirectionalLightCount()));

        return shaderPatches;
    }

    @Override
    public void createUniforms(ShaderProgram shaderProgram, VertexFormat<?> vertexFormat,
                               MaterialFormat materialFormat, String materialName) throws ShaderUniformException {

        if (needViewPos(materialFormat)) {
            shaderProgram.createUniform("viewPos");
        }

        Environment environment = this.environment.get();
        if (environment.hasLights()) {
            if (environment.hasDirectionalLights()) {
                for (int i = 0; i < environment.getDirectionalLights().length; i++) {
                    shaderProgram.createUniform("dirLights[" + i + "].direction");
                    shaderProgram.createUniform("dirLights[" + i + "].ambient");
                    shaderProgram.createUniform("dirLights[" + i + "].diffuse");
                    if (materialFormat.hasElement(MaterialElement.SPECULAR)) {
                        shaderProgram.createUniform("dirLights[" + i + "].specular");
                    }
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
                    if (materialFormat.hasElement(MaterialElement.SPECULAR)) {
                        shaderProgram.createUniform("pointLights[" + i + "].specular");
                    }
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

        if (needViewPos(materialFormat)) {
            shaderProgram.setUniform("viewPos", camera.getPosition());
        }
    }

    public void setEnvironment(ShaderProgram shaderProgram) {
        Environment environment = this.environment.get();

        if (environment.hasDirectionalLights()) {
            for (int i = 0; i < environment.getDirectionalLights().length; i++) {
                DirectionalLight light = environment.getDirectionalLights()[i];
                shaderProgram.setUniform("dirLights[" + i + "].direction", light.getDirection());
                shaderProgram.setUniform("dirLights[" + i + "].ambient", light.getAmbient());
                shaderProgram.setUniform("dirLights[" + i + "].diffuse", light.getDiffuse());

                if (materialFormat.hasElement(MaterialElement.SPECULAR)) {
                    shaderProgram.setUniform("dirLights[" + i + "].specular", light.getSpecular());
                }
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

                if (materialFormat.hasElement(MaterialElement.SPECULAR)) {
                    shaderProgram.setUniform("pointLights[" + i + "].specular", light.getSpecular());
                }
            }
        }

        if (environment.hasFog()) {
            shaderProgram.setUniform("fogColor", environment.getFog().getColor());
            shaderProgram.setUniform("fogDensity", environment.getFog().getDensity());
        }
    }

    protected boolean needViewPos(MaterialFormat materialFormat) {
        return environment.get().hasLights() && materialFormat.hasElement(MaterialElement.SPECULAR);
    }

    protected static String getMaterialShininess(MaterialFormat materialFormat) {
        return "64.0";
    }

    public List<ShaderPatch> createFogShaderPatches(MaterialFormat materialFormat, String materialName) {
        List<ShaderPatch> shaderPatches = new ArrayList<>();

        if (environment.get().getFog() == null)
            return shaderPatches;

        shaderPatches.add(new CommentShaderPatch(ModularShaderConstants.DEFINES_COMMENT,
                (s) -> "#define FOG 1\n",
                CommentShaderPatch.ModificationType.PREPEND));

        return shaderPatches;
    }

    protected List<ShaderPatch> createAllLightVariablesShaderPatches(MaterialFormat materialFormat,
                                                                     String materialName) {
        List<ShaderPatch> shaderPatches = new ArrayList<>();

        // if (needViewPos(materialFormat))
        shaderPatches.add(new FragUniformPatch("vec3 viewPos"));

        if (!environment.get().hasLights()) {
            shaderPatches.add(new CommentShaderPatch(ModularShaderConstants.FRAG_MAIN_COMMENT,
                    (s) -> "pixelColor = "
                            + ShaderUtils.getMaterialDiffuseAsVec4(materialFormat, materialName)
                            + ";\n",
                    CommentShaderPatch.ModificationType.PREPEND));

            return shaderPatches;
        }

        shaderPatches.add(new CommentShaderPatch(ModularShaderConstants.FRAG_MAIN_COMMENT,
                (s) -> "    vec3 viewDir  = normalize(viewPos - passFragPos);\n\n",
                CommentShaderPatch.ModificationType.PREPEND));

        return shaderPatches;
    }

    protected List<ShaderPatch> createPointLightShaderPatches(MaterialFormat materialFormat, String materialName) {
        List<ShaderPatch> shaderPatches = new ArrayList<>();

        if (!environment.get().hasPointLights())
            return shaderPatches;

        int numLights = environment.get().getPointLights().length;

        shaderPatches.add(new CommentShaderPatch(ModularShaderConstants.FRAG_UNIFORMS_COMMENT,
                (s) -> PointLight.toGLSLStruct() + "\n",
                CommentShaderPatch.ModificationType.PREPEND));

        shaderPatches.add(new FragUniformPatch(PointLight.getStructName() + " pointLights[" + numLights + "]"));

        shaderPatches.add(new CommentShaderPatch(ModularShaderConstants.FRAG_MAIN_COMMENT,
                (s) -> IOUtils.readResource(POINT_LIGHT_GLSL_LOCATION, null),
                CommentShaderPatch.ModificationType.PREPEND));

        return shaderPatches;
    }

    protected List<ShaderPatch> createDirectionalLightShaderPatches(MaterialFormat materialFormat,
                                                                    String materialName) {
        List<ShaderPatch> shaderPatches = new ArrayList<>();

        if (!environment.get().hasDirectionalLights())
            return shaderPatches;

        int numLights = environment.get().getDirectionalLights().length;

        shaderPatches.add(new CommentShaderPatch(ModularShaderConstants.FRAG_UNIFORMS_COMMENT,
                (s) -> DirectionalLight.toGLSLStruct() + "\n",
                CommentShaderPatch.ModificationType.PREPEND));

        shaderPatches.add(new FragUniformPatch(DirectionalLight.getStructName() + " dirLights[" + numLights + "]"));

        shaderPatches.add(new CommentShaderPatch(ModularShaderConstants.FRAG_MAIN_COMMENT,
                (s) -> IOUtils.readResource(DIR_LIGHT_GLSL_LOCATION, null),
                CommentShaderPatch.ModificationType.PREPEND));

        return shaderPatches;
    }

    protected ShaderPatch createEmissiveMaterialShaderPatch(MaterialFormat materialFormat, String materialName) {
        String emissive = ShaderUtils.getMaterialEmissiveAsVec3(materialFormat, materialName);

        return new CommentShaderPatch(ModularShaderConstants.FRAG_MAIN_COMMENT,
                (s) -> "    vec3 emissiveFactor = " + emissive +";\n" +
                       "    pixelColor.r += emissiveFactor.r;\n" +
                       "    pixelColor.g += emissiveFactor.g;\n" +
                       "    pixelColor.b += emissiveFactor.b;\n",
                CommentShaderPatch.ModificationType.PREPEND);
    }

    protected List<ShaderPatch>  createMaterialElementPatches(MaterialFormat materialFormat, String materialName,
                                                              int numPointLights, int numDirectionalLights) {
        List<ShaderPatch> shaderPatches = new ArrayList<>();
        String shininess = getMaterialShininess(materialFormat);
        shaderPatches.add(new LineShaderPatch("materialShininess", (s) -> shininess));
        shaderPatches.add(new LineShaderPatch("pointLightCount", (s) -> String.valueOf(numPointLights)));
        shaderPatches.add(new LineShaderPatch("dirLightCount",
                (s) -> String.valueOf(numDirectionalLights)));
        return shaderPatches;
    }

    public static boolean isVertexFormatValidForEnvironment(VertexFormat<?> vertexFormat,
                                                            Supplier<Environment> environment) {
        return !environment.get().hasLights() || vertexFormat.hasElement(VertexElements.NORMAL);
    }

}

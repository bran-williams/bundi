package com.branwilliams.bundi.engine.shader;

import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.material.*;
import com.branwilliams.bundi.engine.shader.dynamic.VertexElement;
import com.branwilliams.bundi.engine.shader.dynamic.VertexElements;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.shader.patching.CommentShaderPatch;
import com.branwilliams.bundi.engine.shader.patching.ShaderPatch;
import com.branwilliams.bundi.engine.util.IOUtils;
import com.branwilliams.bundi.engine.util.ShaderUtils;
import org.joml.Matrix4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class EnvironmentShaderProgram extends ShaderProgram {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String MATERIAL_NAME = "material";

    public static final String DEFINES_COMMENT = "defines";

    public static final String VERTEX_LAYOUT_COMMENT = "vertexlayout";

    public static final String VERTEX_OUT_COMMENT = "vertexout";

    public static final String VERTEX_MAIN_COMMENT = "vertexmain";

    public static final String FRAG_IN_COMMENT = "fragin";

    public static final String MATERIAL_COMMENT = "material";

    public static final String FRAG_UNIFORMS_COMMENT = "fraguniforms";

    public static final String FRAG_FUNCTIONS_COMMENT = "fragfunctions";

    public static final String FRAG_COLOR_COMMENT = "fragcolor";

    private final Matrix4f modelMatrix = new Matrix4f();

    private final VertexFormat<?> vertexFormat;

    private final MaterialFormat materialFormat;

    private Environment environment;

    public EnvironmentShaderProgram(EngineContext engineContext, VertexFormat<?> vertexFormat,
                                    MaterialFormat materialFormat, Environment environment)
            throws ShaderInitializationException, ShaderUniformException {
        super();
        this.environment = environment;
        this.vertexFormat = vertexFormat;
        this.materialFormat = materialFormat;

        List<ShaderPatch> shaderPatches = createShaderPatches(MATERIAL_NAME);

        String vertexShader = IOUtils.readResource("bundi/shaders/environment/vertexShader.vert",  null);
        log.info(patchCode(vertexShader, shaderPatches));
        this.setVertexShader(patchCode(vertexShader, shaderPatches));

        String fragmentShader = IOUtils.readResource("bundi/shaders/environment/fragmentShader.frag", null);
        log.info(patchCode(fragmentShader, shaderPatches));
        this.setFragmentShader(patchCode(fragmentShader, shaderPatches));

        this.link();

        this.bind();

        this.createUniform("projectionMatrix");
        this.createUniform("modelMatrix");
        this.createUniform("viewMatrix");

        // Create the materials diffuse uniform since it is necessary.
        MaterialBinder.createMaterialUniform(this, MATERIAL_NAME, materialFormat, MaterialElement.DIFFUSE);

        if (needViewPos())
            this.createUniform("viewPos");

        if (environment.hasLights()) {
            for (MaterialElement element : materialFormat.getElements().keySet()) {
                if (element == MaterialElement.DIFFUSE)
                    continue;
                MaterialBinder.createMaterialUniform(this, MATERIAL_NAME, materialFormat, element);
            }

            if (environment.hasDirectionalLights()) {
                for (int i = 0; i < environment.getDirectionalLights().length; i++) {
                    this.createUniform("dirLights[" + i + "].direction");
                    this.createUniform("dirLights[" + i + "].ambient");
                    this.createUniform("dirLights[" + i + "].diffuse");
                    this.createUniform("dirLights[" + i + "].specular");
                }
            }

            if (environment.hasPointLights()) {
                for (int i = 0; i < environment.getPointLights().length; i++) {
                    this.createUniform("pointLights[" + i + "].position");

                    this.createUniform("pointLights[" + i + "].constant");
                    this.createUniform("pointLights[" + i + "].linear");
                    this.createUniform("pointLights[" + i + "].quadratic");

                    this.createUniform("pointLights[" + i + "].ambient");
                    this.createUniform("pointLights[" + i + "].diffuse");
                    this.createUniform("pointLights[" + i + "].specular");
                }
            }
        }

        if (environment.hasFog()) {
            this.createUniform("fogColor");
            this.createUniform("fogDensity");
        }

        this.validate();

        ShaderProgram.unbind();
    }

    public void setMaterial(Material material) {
        MaterialBinder.setMaterialUniform(this, MATERIAL_NAME,
                materialFormat.getElement(MaterialElement.DIFFUSE), material);

        for (MaterialElement element : materialFormat.getElements().keySet()) {
            if (element == MaterialElement.DIFFUSE)
                continue;

            MaterialFormat.MaterialEntry materialEntry = materialFormat.getElement(element);
            MaterialBinder.setMaterialUniform(this, MATERIAL_NAME, materialEntry, material);
        }
    }

    public void setProjectionMatrix(Projection projection) {
        this.setUniform("projectionMatrix", projection.toProjectionMatrix());
    }


    public void setModelMatrix(Transformable transformable) {
        this.setUniform("modelMatrix", transformable.toMatrix(modelMatrix));
    }

    public void setViewMatrix(Camera camera) {
        this.setUniform("viewMatrix", camera.toViewMatrix());
        if (needViewPos())
            this.setUniform("viewPos", camera.getPosition());
    }

    protected boolean needViewPos() {
        return environment.hasLights() && materialFormat.hasElement(MaterialElement.SPECULAR);
    }

    public void setEnvironment(Environment environment) {
        if (environment.hasDirectionalLights()) {
            for (int i = 0; i < environment.getDirectionalLights().length; i++) {
                DirectionalLight light = environment.getDirectionalLights()[i];
                this.setUniform("dirLights[" + i + "].direction", light.getDirection());
                this.setUniform("dirLights[" + i + "].ambient", light.getAmbient());
                this.setUniform("dirLights[" + i + "].diffuse", light.getDiffuse());
                this.setUniform("dirLights[" + i + "].specular", light.getSpecular());
            }
        }

        if (environment.hasPointLights()) {
            for (int i = 0; i < environment.getPointLights().length; i++) {
                PointLight light = environment.getPointLights()[i];
                this.setUniform("pointLights[" + i + "].position", light.getPosition());

                this.setUniform("pointLights[" + i + "].constant", light.getAttenuation().getConstant());
                this.setUniform("pointLights[" + i + "].linear", light.getAttenuation().getLinear());
                this.setUniform("pointLights[" + i + "].quadratic", light.getAttenuation().getQuadratic());

                this.setUniform("pointLights[" + i + "].ambient", light.getAmbient());
                this.setUniform("pointLights[" + i + "].diffuse", light.getDiffuse());
                this.setUniform("pointLights[" + i + "].specular", light.getSpecular());
            }
        }

        if (environment.hasFog()) {
            this.setUniform("fogColor", environment.getFog().getColor());
            this.setUniform("fogDensity", environment.getFog().getDensity());
        }
    }

    protected String patchCode(String code, List<ShaderPatch> shaderPatches) {
        for (ShaderPatch shaderPatch : shaderPatches) {
            code = shaderPatch.patch(code);
        }
        return code;
    }


    private List<ShaderPatch> createShaderPatches(String materialName) {
        List<ShaderPatch> shaderPatches = new ArrayList<>(createVertexShaderInputPatches(vertexFormat));

        // enables fog
        shaderPatches.addAll(createFogShaderPatches());

        // Makes sure the pixel color is the one specified by the material.
        shaderPatches.addAll(createMaterialShaderPatches(materialName));

        // Sets up variables needed for light rendering
        shaderPatches.addAll(createAllLightVariablesShaderPatches(materialName));

        // Calculator directional light factor
        shaderPatches.addAll(createDirectionalLightShaderPatches(materialName));

        // Calculate point light factor
        shaderPatches.addAll(createPointLightShaderPatches(materialName));

        // Add emissive factor LAST
        shaderPatches.add(createEmissiveMaterialShaderPatch(materialName));
        return shaderPatches;
    }


    public List<ShaderPatch> createFogShaderPatches() {
        List<ShaderPatch> shaderPatches = new ArrayList<>();
        if (environment.getFog() == null)
            return shaderPatches;

        shaderPatches.add(new CommentShaderPatch(DEFINES_COMMENT,
                (s) -> "#define FOG 1\n", CommentShaderPatch.ModificationType.PREPEND));
        return shaderPatches;
    }

    public static String getVertexShaderInput(VertexFormat<?> vertexFormat) {
        StringBuilder vertexShaderInput = new StringBuilder();
        // Create input lines.
        for (int i = 0; i < vertexFormat.getVertexElements().size(); i++) {
            VertexElement vertexElement = vertexFormat.getVertexElements().get(i);
            vertexShaderInput.append(ShaderUtils.createLayout(i, "in "
                    + vertexElement.getType()
                    + " "
                    + vertexElement.getVariableName()
                    + ";"));
        }
        return vertexShaderInput.toString();
    }

    public static List<ShaderPatch> createVertexShaderInputPatches(VertexFormat<?> vertexFormat) {
        List<ShaderPatch> shaderPatches = new ArrayList<>();

        shaderPatches.add(new CommentShaderPatch(VERTEX_LAYOUT_COMMENT,
                (s) -> getVertexShaderInput(vertexFormat)));

        if (vertexFormat.hasElement(VertexElements.UV)) {
            String uvVariableName = VertexElements.UV.getVariableName();
            String passUvVariableName = "pass" + ShaderUtils.capitalizeFirstChar(uvVariableName);

            shaderPatches.add(new CommentShaderPatch(VERTEX_OUT_COMMENT,
                    (s) -> "out vec2 " + passUvVariableName + ";\n", CommentShaderPatch.ModificationType.PREPEND));

            shaderPatches.add(new CommentShaderPatch(VERTEX_MAIN_COMMENT,
                    (s) -> passUvVariableName + " = " + uvVariableName + ";\n",
                    CommentShaderPatch.ModificationType.PREPEND));

            shaderPatches.add(new CommentShaderPatch(FRAG_IN_COMMENT,
                    (s) -> "in vec2 " + passUvVariableName + ";\n", CommentShaderPatch.ModificationType.PREPEND));
        }

        if (vertexFormat.hasElement(VertexElements.NORMAL)) {
            String normalVariableName = VertexElements.NORMAL.getVariableName();
            String passNormalVariableName = "pass" + ShaderUtils.capitalizeFirstChar(normalVariableName);

            shaderPatches.add(new CommentShaderPatch(VERTEX_OUT_COMMENT,
                    (s) -> "out vec3 " + passNormalVariableName + ";\n", CommentShaderPatch.ModificationType.PREPEND));

            shaderPatches.add(new CommentShaderPatch(VERTEX_MAIN_COMMENT,
                    (s) -> "    " + passNormalVariableName + " = mat3(transpose(inverse(modelMatrix))) * normal;\n",
                    CommentShaderPatch.ModificationType.PREPEND));

            shaderPatches.add(new CommentShaderPatch(FRAG_IN_COMMENT,
                    (s) -> "in vec3 " + passNormalVariableName + ";\n", CommentShaderPatch.ModificationType.PREPEND));
        }
        return shaderPatches;
    }

    protected List<ShaderPatch> createMaterialShaderPatches(String materialName) {
        List<ShaderPatch> shaderPatches = new ArrayList<>();
        shaderPatches.add(new CommentShaderPatch(MATERIAL_COMMENT,
                (s) -> materialFormat.toGLSLUniform(materialName)));
        if (!environment.hasLights()) {
            shaderPatches.add(new CommentShaderPatch(FRAG_COLOR_COMMENT,
                    (s) -> "pixelColor = "
                            + ShaderUtils.getMaterialDiffuseAsVec4(materialFormat, materialName,
                            "passTextureCoordinates")
                            + ";\n", CommentShaderPatch.ModificationType.PREPEND));
        }

        return shaderPatches;
    }

    protected ShaderPatch createEmissiveMaterialShaderPatch(String materialName) {
            String emissive = ShaderUtils.getMaterialEmissiveAsVec4(materialFormat, materialName,
                    "passTextureCoordinates");
            return new CommentShaderPatch(FRAG_COLOR_COMMENT,
                    (s) -> "pixelColor += " + emissive + ";\n", CommentShaderPatch.ModificationType.PREPEND);
    }

    protected List<ShaderPatch> createAllLightVariablesShaderPatches(String materialName) {
        List<ShaderPatch> shaderPatches = new ArrayList<>();

        if (!environment.hasLights())
            return shaderPatches;

        shaderPatches.add(new CommentShaderPatch(FRAG_COLOR_COMMENT,
                (s) -> "vec3 normal   = passNormal;\n"
                        + "vec3 viewDir  = normalize(viewPos - passFragPos);\n",
                CommentShaderPatch.ModificationType.PREPEND));

        return shaderPatches;
    }

    protected List<ShaderPatch> createPointLightShaderPatches(String materialName) {
        List<ShaderPatch> shaderPatches = new ArrayList<>();

        if (!environment.hasPointLights())
            return shaderPatches;

        int numLights = environment.getPointLights().length;

        shaderPatches.add(new CommentShaderPatch(FRAG_UNIFORMS_COMMENT,
                (s) -> "struct PointLight {\n"
                        + "    vec3 position;\n"
                        + "\n"
                        + "    float constant;\n"
                        + "    float linear;\n"
                        + "    float quadratic;\n"
                        + "\n"
                        + "    vec3 ambient;\n"
                        + "    vec3 diffuse;\n"
                        + "    vec3 specular;\n"
                        + "};\n"
                        + "uniform PointLight pointLights[" + numLights + "];\n"
                        + "", CommentShaderPatch.ModificationType.PREPEND));

        String diffuse = ShaderUtils.getMaterialDiffuseAsVec3(materialFormat, materialName,
                "passTextureCoordinates");
        String specular = ShaderUtils.getMaterialSpecularAsVec3(materialFormat, materialName,
                "passTextureCoordinates");

        shaderPatches.add(new CommentShaderPatch(FRAG_COLOR_COMMENT,
                (s) -> "\n"
                        + "// point lighting below:\n"
                        + "for (int i = 0; i < " + numLights + "; i++) {\n"
                        + "    PointLight pointLight = pointLights[i];\n"
                        + "    vec3 lightDir = normalize(pointLight.position - passFragPos);\n"
                        + "    vec3 halfwayDir = normalize(lightDir + viewDir);\n"
                        + "\n"
                        + "    // diffuse calculation\n"
                        + "    float diff = max(dot(normal, lightDir), 0.0);\n"
                        + "\n"
                        + "    // specular calculation\n"
//                        + "    vec3 reflectDir = reflect(-lightDir, normal);\n"
                        + "    float spec = pow(max(dot(normal, halfwayDir), 0.0), "
                        + "    " + getMaterialShininess(materialFormat) + ");\n"
                        + "\n"
                        + "    float distance = length(pointLight.position - passFragPos);\n"
                        + "    float attenuation = 1.0 / (pointLight.constant + pointLight.linear * distance "
                        + "    + pointLight.quadratic * (distance * distance));\n"
                        + "\n"
                        + "    vec3 ambient  = pointLight.ambient  *        " + diffuse + ";\n"
                        + "    vec3 diffuse  = pointLight.diffuse  * diff * " + diffuse + ";\n"
                        + "    vec3 specular = pointLight.specular * spec * " + specular + ";\n"
                        + "\n"
                        + "    ambient *= attenuation;\n"
                        + "    diffuse *= attenuation;\n"
                        + "    specular *= attenuation;\n"
                        + "\n"
                        + "    vec4 lightColor = vec4(ambient + diffuse + specular, 0.0);\n"
                        + "    pixelColor += lightColor;\n"
                        + "}\n", CommentShaderPatch.ModificationType.PREPEND));
        return shaderPatches;
    }

    protected List<ShaderPatch> createDirectionalLightShaderPatches(String materialName) {
        List<ShaderPatch> shaderPatches = new ArrayList<>();

        if (!environment.hasDirectionalLights())
            return shaderPatches;

        int numLights = environment.getDirectionalLights().length;

        shaderPatches.add(new CommentShaderPatch(FRAG_UNIFORMS_COMMENT,
                (s) -> "struct DirLight {\n"
                        + "    vec3 direction;\n"
                        + "\n"
                        + "    vec3 ambient;\n"
                        + "    vec3 diffuse;\n"
                        + "    vec3 specular;\n"
                        + "};\n"
                        + "uniform DirLight dirLights[" + numLights + "];\n"
                        + "", CommentShaderPatch.ModificationType.PREPEND));

        String diffuse = ShaderUtils.getMaterialDiffuseAsVec3(materialFormat, materialName,
                "passTextureCoordinates");
        String specular = ShaderUtils.getMaterialSpecularAsVec3(materialFormat, materialName,
                "passTextureCoordinates");

        shaderPatches.add(new CommentShaderPatch(FRAG_COLOR_COMMENT,
                (s) -> "\n"
                        + "// directional lighting below:\n"
                        + "for (int i = 0; i < " + numLights + "; i++) {\n"
                        + "    DirLight dirLight = dirLights[i];\n"
                        + "    vec3 lightDir = normalize(-dirLight.direction);\n"
                        + "    vec3 halfwayDir = normalize(lightDir + viewDir);\n"
                        + "\n"
                        + "    // diffuse calculation\n"
                        + "    float diff = max(dot(normal, lightDir), 0.0);\n"
                        + "\n"
                        + "    // specular calculation\n"
//                        + "    vec3 reflectDir = reflect(-lightDir, normal);\n"
                        + "    float spec = pow(max(dot(normal, halfwayDir), 0.0), "
                        + "    " + getMaterialShininess(materialFormat) + ");\n"
                        + "\n"
                        + "    vec3 ambient  = dirLight.ambient  *        " + diffuse + ";\n"
                        + "    vec3 diffuse  = dirLight.diffuse  * diff * " + diffuse + ";\n"
                        + "    vec3 specular = dirLight.specular * spec * " + specular + ";\n"
                        + "\n"
                        + "    vec4 lightColor = vec4(ambient + diffuse + specular, 0.0);\n"
                        + "    pixelColor += lightColor;\n"
                        + "}\n", CommentShaderPatch.ModificationType.PREPEND));
        return shaderPatches;
    }

    private static String getMaterialShininess(MaterialFormat materialFormat) {
        return "8.0";
    }

}

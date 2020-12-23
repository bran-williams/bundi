package com.branwilliams.bundi.engine.shader;


import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.material.Material;
import com.branwilliams.bundi.engine.material.MaterialBinder;
import com.branwilliams.bundi.engine.material.MaterialElement;
import com.branwilliams.bundi.engine.material.MaterialFormat;
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

public class ModularShaderProgram extends ShaderProgram {

    public static final String MATERIAL_NAME = "material";

    /**
     * ************************************************************************
     * These are the comments expected be patched by some modules
     * ************************************************************************
     * */
    public static final String DEFINES_COMMENT = "defines";

    public static final String VERTEX_LAYOUT_COMMENT = "vertexlayout";

    public static final String VERTEX_OUT_COMMENT = "vertexout";

    public static final String VERTEX_MAIN_COMMENT = "vertexmain";

    public static final String FRAG_IN_COMMENT = "fragin";

    public static final String MATERIAL_COMMENT = "material";

    public static final String FRAG_UNIFORMS_COMMENT = "fraguniforms";

    public static final String FRAG_FUNCTIONS_COMMENT = "fragfunctions";

    public static final String FRAG_COLOR_COMMENT = "fragcolor";

    /**
     * ************************************************************************
     * */

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Matrix4f modelMatrix = new Matrix4f();

    private final VertexFormat<?> vertexFormat;

    private final MaterialFormat materialFormat;

    private List<ShaderModule> shaderModules;

    public ModularShaderProgram(EngineContext engineContext, VertexFormat<?> vertexFormat,
                                MaterialFormat materialFormat, List<ShaderModule> shaderModules)
            throws ShaderInitializationException, ShaderUniformException {
        super();
        this.vertexFormat = vertexFormat;
        this.materialFormat = materialFormat;
        this.shaderModules = shaderModules;

        List<ShaderPatch> shaderPatches = createShaderPatches();

        String vertexShader = IOUtils.readResource("bundi/shaders/modular/vertexShader.vert",
                null);
        log.info(ShaderUtils.patchCode(vertexShader, shaderPatches));
        this.setVertexShader(ShaderUtils.patchCode(vertexShader, shaderPatches));

        String fragmentShader = IOUtils.readResource("bundi/shaders/modular/fragmentShader.frag",
                null);
        log.info(ShaderUtils.patchCode(fragmentShader, shaderPatches));
        this.setFragmentShader(ShaderUtils.patchCode(fragmentShader, shaderPatches));

        this.link();
        this.bind();

        this.createUniform("projectionMatrix");
        this.createUniform("modelMatrix");
        this.createUniform("viewMatrix");

        // Create the materials diffuse uniform since it is necessary.
        MaterialBinder.createMaterialUniform(this, MATERIAL_NAME, materialFormat, MaterialElement.DIFFUSE);

        for (MaterialElement element : materialFormat.getElements().keySet()) {
            if (element == MaterialElement.DIFFUSE)
                continue;
            MaterialBinder.createMaterialUniform(this, MATERIAL_NAME, materialFormat, element);
        }

        this.createUniforms();
        this.validate();

        ShaderProgram.unbind();
    }

    private List<ShaderPatch> createShaderPatches() {
        List<ShaderPatch> shaderPatches = new ArrayList<>();

        shaderPatches.addAll(createVertexShaderInputPatches());
        shaderPatches.addAll(createMaterialShaderPatches(MATERIAL_NAME));

        for (ShaderModule module : shaderModules) {
            shaderPatches.addAll(module.getShaderPatches());
        }

        return shaderPatches;
    }

    protected List<ShaderPatch> createVertexShaderInputPatches() {
        List<ShaderPatch> shaderPatches = new ArrayList<>();

        shaderPatches.add(new CommentShaderPatch(VERTEX_LAYOUT_COMMENT,
                (s) -> getVertexShaderInput(vertexFormat)));

        if (vertexFormat.hasElement(VertexElements.UV)) {
            String uvVariableName = VertexElements.UV.getVariableName();
            String passUvVariableName = "pass" + ShaderUtils.capitalizeFirstChar(uvVariableName);

            shaderPatches.add(new CommentShaderPatch(VERTEX_OUT_COMMENT,
                    (s) -> "out vec2 " + passUvVariableName + ";\n",
                    CommentShaderPatch.ModificationType.PREPEND));

            shaderPatches.add(new CommentShaderPatch(VERTEX_MAIN_COMMENT,
                    (s) -> passUvVariableName + " = " + uvVariableName + ";\n",
                    CommentShaderPatch.ModificationType.PREPEND));

            shaderPatches.add(new CommentShaderPatch(FRAG_IN_COMMENT,
                    (s) -> "in vec2 " + passUvVariableName + ";\n",
                    CommentShaderPatch.ModificationType.PREPEND));
        }

        if (vertexFormat.hasElement(VertexElements.NORMAL)) {
            String normalVariableName = VertexElements.NORMAL.getVariableName();
            String passNormalVariableName = "pass" + ShaderUtils.capitalizeFirstChar(normalVariableName);

            shaderPatches.add(new CommentShaderPatch(VERTEX_OUT_COMMENT,
                    (s) -> "out vec3 " + passNormalVariableName + ";\n",
                    CommentShaderPatch.ModificationType.PREPEND));

            shaderPatches.add(new CommentShaderPatch(VERTEX_MAIN_COMMENT,
                    (s) -> "    " + passNormalVariableName + " = mat3(transpose(inverse(modelMatrix))) * normal;\n",
                    CommentShaderPatch.ModificationType.PREPEND));

            shaderPatches.add(new CommentShaderPatch(FRAG_IN_COMMENT,
                    (s) -> "in vec3 " + passNormalVariableName + ";\n",
                    CommentShaderPatch.ModificationType.PREPEND));
        }

        if (vertexFormat.hasElement(VertexElements.TANGENT)) {
            String tangentVariableName = VertexElements.TANGENT.getVariableName();
            String passTangentVariableName = "pass" + ShaderUtils.capitalizeFirstChar(tangentVariableName);

            shaderPatches.add(new CommentShaderPatch(VERTEX_OUT_COMMENT,
                    (s) -> "out vec3 " + passTangentVariableName + ";\n",
                    CommentShaderPatch.ModificationType.PREPEND));

            shaderPatches.add(new CommentShaderPatch(VERTEX_MAIN_COMMENT,
                    (s) -> "    " + passTangentVariableName + " = " + tangentVariableName + ";\n",
                    CommentShaderPatch.ModificationType.PREPEND));

            shaderPatches.add(new CommentShaderPatch(FRAG_IN_COMMENT,
                    (s) -> "in vec3 " + passTangentVariableName + ";\n",
                    CommentShaderPatch.ModificationType.PREPEND));
        }
        return shaderPatches;
    }

    protected String getVertexShaderInput(VertexFormat<?> vertexFormat) {
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

    protected List<ShaderPatch> createMaterialShaderPatches(String materialName) {
        List<ShaderPatch> shaderPatches = new ArrayList<>();

        shaderPatches.add(new CommentShaderPatch(MATERIAL_COMMENT,
                (s) -> materialFormat.toGLSLUniform(materialName)));

        if (vertexFormat.hasElement(VertexElements.NORMAL)) {
            String normalVal = "passNormal";

            MaterialFormat.MaterialEntry normalEntry = materialFormat.getElement(MaterialElement.NORMAL);
            if (normalEntry != null && normalEntry.elementType.isSampler()) {
                String normalFunction = IOUtils.readResource("bundi/shaders/modular/normalFunction.spatch",
                        null);
                shaderPatches.add(new CommentShaderPatch(FRAG_UNIFORMS_COMMENT,
                        (s) -> normalFunction,
                        CommentShaderPatch.ModificationType.PREPEND));

                normalVal = "calculateMappedNormal()";
            }

            String finalNormalVal = normalVal;
            shaderPatches.add(new CommentShaderPatch(FRAG_COLOR_COMMENT,
                    (s) -> "vec3 normal   = " + finalNormalVal + ";\n",
                    CommentShaderPatch.ModificationType.PREPEND));
        }

        return shaderPatches;
    }

    private void createUniforms() throws ShaderUniformException {
        for (ShaderModule module : shaderModules) {
            module.createUniforms(this, vertexFormat, materialFormat, MATERIAL_NAME);
        }
    }

    public void update(Projection projection, Camera camera) {
        this.setProjectionMatrix(projection);
        this.setViewMatrix(camera);
        this.setModelMatrix(Transformable.empty());

        for (ShaderModule module : shaderModules) {
            module.update(this, projection, camera);
        }
    }

    // TODO why I did this with diffuse?
    public void setMaterial(Material material) {
        MaterialBinder.bindMaterialTextures(material);

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
    }

}

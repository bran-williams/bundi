package com.branwilliams.bundi.engine.shader.modular;


import com.branwilliams.bundi.engine.material.Material;
import com.branwilliams.bundi.engine.material.MaterialBinder;
import com.branwilliams.bundi.engine.material.MaterialElement;
import com.branwilliams.bundi.engine.material.MaterialFormat;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.VertexElements;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.shader.patching.CommentShaderPatch;
import com.branwilliams.bundi.engine.shader.patching.LineShaderPatch;
import com.branwilliams.bundi.engine.shader.patching.ShaderPatch;
import com.branwilliams.bundi.engine.util.IOUtils;
import com.branwilliams.bundi.engine.util.ShaderUtils;
import org.joml.Matrix4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ModularShaderProgram extends ShaderProgram {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Matrix4f modelMatrix = new Matrix4f();

    private final VertexFormat<?> vertexFormat;

    private final MaterialFormat materialFormat;

    private final String materialName;

    private List<ShaderModule> shaderModules;

    public ModularShaderProgram(VertexFormat<?> vertexFormat, MaterialFormat materialFormat,
                                List<ShaderModule> shaderModules)
            throws ShaderInitializationException, ShaderUniformException {
        this(vertexFormat, materialFormat, ModularShaderConstants.DEFAULT_MATERIAL_NAME, shaderModules);
    }

    public ModularShaderProgram(VertexFormat<?> vertexFormat, MaterialFormat materialFormat, String materialName,
                                List<ShaderModule> shaderModules)
            throws ShaderInitializationException, ShaderUniformException {
        super();
        this.vertexFormat = vertexFormat;
        this.materialFormat = materialFormat;
        this.materialName = materialName;
        this.shaderModules = shaderModules;

        if (!vertexFormat.hasPositionElement()) {
            throw new IllegalArgumentException("VertexFormat must have a position element!");
        }

        if (!vertexFormat.hasElement(VertexElements.UV) && materialFormat.hasSampler()) {
            throw new IllegalArgumentException("VertexFormat must have a uv element for the Material samplers: "
                    + materialFormat);
        }

        List<ShaderPatch> shaderPatches = createShaderPatches();

        String vertexShader = IOUtils.readResource(ModularShaderConstants.VERTEX_SHADER_LOCATION, null);
        log.info("vertexShader: \n" + ShaderUtils.patchCode(vertexShader, shaderPatches));
        this.setVertexShader(ShaderUtils.patchCode(vertexShader, shaderPatches));

        String fragmentShader = IOUtils.readResource(ModularShaderConstants.FRAGMENT_SHADER_LOCATION, null);
        log.info("fragmentShader: \n" + ShaderUtils.patchCode(fragmentShader, shaderPatches));
        this.setFragmentShader(ShaderUtils.patchCode(fragmentShader, shaderPatches));

        this.link();
        this.bind();

        this.createUniforms();
        this.validate();

        ShaderProgram.unbind();
    }

    private List<ShaderPatch> createShaderPatches() {
        List<ShaderPatch> shaderPatches = new ArrayList<>();

        shaderPatches.addAll(createVertexShaderInputPatches());
        shaderPatches.addAll(createMaterialShaderPatches());

        for (ShaderModule module : shaderModules) {
            shaderPatches.addAll(module.getShaderPatches());
        }

        shaderPatches.add(getVertexPosPatcher());
        shaderPatches.addAll(createMaterialElementPatches());
        return shaderPatches;
    }

    protected ShaderPatch getVertexPosPatcher() {
        return new LineShaderPatch("vertexPos",
                (s) -> vertexFormat.hasElement(VertexElements.POSITION_2D) ? "vec4(position, 0, 1.0)"
                        : "vec4(position, 1.0)");
    }

    protected List<ShaderPatch> createVertexShaderInputPatches() {
        List<ShaderPatch> shaderPatches = new ArrayList<>();

        shaderPatches.add(new CommentShaderPatch(ModularShaderConstants.VERTEX_LAYOUT_COMMENT,
                (s) -> vertexFormat.toVertexShaderInputGLSL()));

        if (vertexFormat.hasElement(VertexElements.UV)) {
            String uvVariableName = VertexElements.UV.getVariableName();
            String passUvVariableName = VertexElements.UV.getPassName();

            shaderPatches.add(new CommentShaderPatch(ModularShaderConstants.VERTEX_OUT_COMMENT,
                    (s) -> "out vec2 " + passUvVariableName + ";\n",
                    CommentShaderPatch.ModificationType.PREPEND));

            shaderPatches.add(new CommentShaderPatch(ModularShaderConstants.VERTEX_MAIN_COMMENT,
                    (s) -> passUvVariableName + " = " + uvVariableName + ";\n",
                    CommentShaderPatch.ModificationType.PREPEND));

            shaderPatches.add(new CommentShaderPatch(ModularShaderConstants.FRAG_IN_COMMENT,
                    (s) -> "in vec2 " + passUvVariableName + ";\n",
                    CommentShaderPatch.ModificationType.PREPEND));
        }

        if (vertexFormat.hasElement(VertexElements.NORMAL)) {
            String normalVariableName = VertexElements.NORMAL.getVariableName();
            String passNormalVariableName = VertexElements.NORMAL.getPassName();

            shaderPatches.add(new CommentShaderPatch(ModularShaderConstants.VERTEX_OUT_COMMENT,
                    (s) -> "out vec3 " + passNormalVariableName + ";\n",
                    CommentShaderPatch.ModificationType.PREPEND));

            shaderPatches.add(new CommentShaderPatch(ModularShaderConstants.VERTEX_MAIN_COMMENT,
                    (s) -> "    " + passNormalVariableName + " = mat3(transpose(inverse(modelMatrix))) * normal;\n",
                    CommentShaderPatch.ModificationType.PREPEND));

            shaderPatches.add(new CommentShaderPatch(ModularShaderConstants.FRAG_IN_COMMENT,
                    (s) -> "in vec3 " + passNormalVariableName + ";\n",
                    CommentShaderPatch.ModificationType.PREPEND));
        }

        if (vertexFormat.hasElement(VertexElements.TANGENT)) {
            String tangentVariableName = VertexElements.TANGENT.getVariableName();
            String passTangentVariableName = VertexElements.TANGENT.getPassName();

            shaderPatches.add(new CommentShaderPatch(ModularShaderConstants.VERTEX_OUT_COMMENT,
                    (s) -> "out vec3 " + passTangentVariableName + ";\n",
                    CommentShaderPatch.ModificationType.PREPEND));

            shaderPatches.add(new CommentShaderPatch(ModularShaderConstants.VERTEX_MAIN_COMMENT,
                    (s) -> "    " + passTangentVariableName + " = " + tangentVariableName + ";\n",
                    CommentShaderPatch.ModificationType.PREPEND));

            shaderPatches.add(new CommentShaderPatch(ModularShaderConstants.FRAG_IN_COMMENT,
                    (s) -> "in vec3 " + passTangentVariableName + ";\n",
                    CommentShaderPatch.ModificationType.PREPEND));
        }

        return shaderPatches;
    }

    protected List<ShaderPatch> createMaterialShaderPatches() {
        List<ShaderPatch> shaderPatches = new ArrayList<>();

        // define material struct
        shaderPatches.add(new CommentShaderPatch(ModularShaderConstants.MATERIAL_STRUCT_COMMENT,
                (s) -> materialFormat.toGLSLUniform(materialName)));

        // create normal mapping if necessary
        if (vertexFormat.hasElement(VertexElements.NORMAL)) {
            String normalVal = createNormalMappedNormal(shaderPatches);

            // create normal value for the fragment shader to use
            final String finalNormalVal = normalVal;
            shaderPatches.add(new CommentShaderPatch(ModularShaderConstants.FRAG_COLOR_COMMENT,
                    (s) -> "vec3 normal   = " + finalNormalVal + ";\n",
                    CommentShaderPatch.ModificationType.PREPEND));
        }

        return shaderPatches;
    }

    protected List<ShaderPatch> createMaterialElementPatches() {
        List<ShaderPatch> shaderPatches = new ArrayList<>();

        String diffuse = ShaderUtils.getMaterialDiffuseAsVec4(materialFormat, materialName);
        String specular = ShaderUtils.getMaterialSpecularAsVec4(materialFormat, materialName);

        shaderPatches.add(new LineShaderPatch("materialDiffuse", (s) -> diffuse));
        shaderPatches.add(new LineShaderPatch("materialSpecular", (s) -> specular));
        return shaderPatches;
    }

    private String createNormalMappedNormal(List<ShaderPatch> shaderPatches) {
        String normalVal = VertexElements.NORMAL.getPassName();

        MaterialFormat.MaterialEntry materialNormalEntry = materialFormat.getElement(MaterialElement.NORMAL);
        if (materialNormalEntry != null && materialNormalEntry.elementType.isSampler) {
            shaderPatches.add(new CommentShaderPatch(ModularShaderConstants.FRAG_UNIFORMS_COMMENT,
                    (s) -> IOUtils.readResource(ModularShaderConstants.NORMAL_FUNCTION_LOCATION, null),
                    CommentShaderPatch.ModificationType.PREPEND));

            normalVal = String.format("calculateMappedNormal(normalize(%s), normalize(%s), texture(%s, %s).xyz)",
                    VertexElements.NORMAL.getPassName(), VertexElements.TANGENT.getPassName(),
                    materialNormalEntry.getVariable(materialName), VertexElements.UV.getPassName());
        }
        return normalVal;
    }

    private void createUniforms() throws ShaderUniformException {
        this.createUniform("projectionMatrix");
        this.createUniform("modelMatrix");
        this.createUniform("viewMatrix");

        // Create the materials diffuse uniform since it is necessary.
        MaterialBinder.createMaterialUniform(this, materialName, materialFormat, MaterialElement.DIFFUSE);

        for (MaterialElement element : materialFormat.getElements().keySet()) {
            if (element == MaterialElement.DIFFUSE)
                continue;
            MaterialBinder.createMaterialUniform(this, materialName, materialFormat, element);
        }

        for (ShaderModule module : shaderModules) {
            module.createUniforms(this, vertexFormat, materialFormat, materialName);
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

    public void setMaterial(Material material) {
        MaterialBinder.bindMaterialTextures(material);

        MaterialBinder.setMaterialUniform(this, materialName,
                materialFormat.getElement(MaterialElement.DIFFUSE), material);

        for (MaterialElement element : materialFormat.getElements().keySet()) {
            if (element == MaterialElement.DIFFUSE)
                continue;

            MaterialFormat.MaterialEntry materialEntry = materialFormat.getElement(element);
            MaterialBinder.setMaterialUniform(this, materialName, materialEntry, material);
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

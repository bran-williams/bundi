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

import static com.branwilliams.bundi.engine.shader.modular.ModularShaderConstants.TRIPLANAR_FUNCTION_LOCATION;
import static com.branwilliams.bundi.engine.shader.modular.ModularShaderConstants.TRIPLANAR_NORMAL_FUNCTION_LOCATION;

public class ModularShaderProgram extends ShaderProgram {

    private static final float DEFAULT_TRIPLANAR_TILE_VALUE = 0.5F;

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

        // no longer needed for triplanar mapping?
//        if (!vertexFormat.hasElement(VertexElements.UV) && materialFormat.hasSampler()) {
//            throw new IllegalArgumentException("VertexFormat must have a uv element for the Material samplers: "
//                    + materialFormat);
//        }

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
        shaderPatches.addAll(createTriplanarPatches());
        shaderPatches.addAll(createMaterialShaderPatches());


        for (ShaderModule module : shaderModules) {
            shaderPatches.addAll(module.getShaderPatches());
        }

        shaderPatches.add(createVertexPosPatch());

        return shaderPatches;
    }

    protected ShaderPatch createVertexPosPatch() {
        return new LineShaderPatch("vertexPos",
                (s) -> vertexFormat.hasElement(VertexElements.POSITION_2D) ? "vec4(position, 0, 1.0)"
                        : "vec4(position, 1.0)");
    }


    protected List<ShaderPatch> createTriplanarPatches() {
        List<ShaderPatch> shaderPatches = new ArrayList<>();
        if (shouldUseTriplanarMapping()) {
            shaderPatches.add(new CommentShaderPatch(ModularShaderConstants.FRAG_UNIFORMS_COMMENT,
                    (s) -> "uniform mat4 fragModelMatrix;\n",
                    CommentShaderPatch.ModificationType.PREPEND));
            shaderPatches.add(new CommentShaderPatch(ModularShaderConstants.FRAG_UNIFORMS_COMMENT,
                    (s) -> "uniform float triplanarBlendOffset;\n",
                    CommentShaderPatch.ModificationType.PREPEND));
            shaderPatches.add(new CommentShaderPatch(ModularShaderConstants.FRAG_UNIFORMS_COMMENT,
                    (s) -> "uniform float triplanarTile;\n",
                    CommentShaderPatch.ModificationType.PREPEND));

            shaderPatches.add(new CommentShaderPatch(ModularShaderConstants.FRAG_UNIFORMS_COMMENT,
                    (s) -> IOUtils.readResource(TRIPLANAR_FUNCTION_LOCATION, null),
                    CommentShaderPatch.ModificationType.PREPEND));

            if (materialFormat.hasElementAsSampler(MaterialElement.NORMAL)) {
                shaderPatches.add(new CommentShaderPatch(ModularShaderConstants.FRAG_UNIFORMS_COMMENT,
                        (s) -> IOUtils.readResource(TRIPLANAR_NORMAL_FUNCTION_LOCATION, null),
                        CommentShaderPatch.ModificationType.PREPEND));
            }

            // we need to pass the model matrix to the fragment shader to calculate the modelNormal
//            ShaderUtils.createInputOutputShaderPatches(shaderPatches, "modelMatrix", "fragModelMatrix", "mat4",
//                    (variableName) -> variableName, ModularShaderConstants.VERTEX_OUT_COMMENT,
//                    ModularShaderConstants.VERTEX_MAIN_COMMENT, ModularShaderConstants.FRAG_IN_COMMENT);
        }
        return shaderPatches;
    }
    /**
     * Creates the shader patches for the vertex input in the vertex shader,
     * */
    protected List<ShaderPatch> createVertexShaderInputPatches() {
        List<ShaderPatch> shaderPatches = new ArrayList<>();

        shaderPatches.add(new CommentShaderPatch(ModularShaderConstants.VERTEX_LAYOUT_COMMENT,
                (s) -> vertexFormat.toVertexShaderInputGLSL()));

        if (vertexFormat.hasElement(VertexElements.UV)) {
            ShaderUtils.createInputOutputShaderPatches(shaderPatches, VertexElements.UV,
                    ModularShaderConstants.VERTEX_OUT_COMMENT, ModularShaderConstants.VERTEX_MAIN_COMMENT,
                    ModularShaderConstants.FRAG_IN_COMMENT);
        }

        if (vertexFormat.hasElement(VertexElements.NORMAL)) {
            ShaderUtils.createInputOutputShaderPatches(shaderPatches, VertexElements.NORMAL,
                    (variableName) -> "mat3(transpose(inverse(modelMatrix))) * " + variableName,
                    ModularShaderConstants.VERTEX_OUT_COMMENT, ModularShaderConstants.VERTEX_MAIN_COMMENT,
                    ModularShaderConstants.FRAG_IN_COMMENT);
        }

        if (vertexFormat.hasElement(VertexElements.TANGENT)) {
            ShaderUtils.createInputOutputShaderPatches(shaderPatches, VertexElements.TANGENT,
                    ModularShaderConstants.VERTEX_OUT_COMMENT, ModularShaderConstants.VERTEX_MAIN_COMMENT,
                    ModularShaderConstants.FRAG_IN_COMMENT);
        }

        return shaderPatches;
    }

    protected List<ShaderPatch> createMaterialShaderPatches() {
        List<ShaderPatch> shaderPatches = new ArrayList<>();
        boolean hasNormals = vertexFormat.hasElement(VertexElements.NORMAL);

        // define material struct
        shaderPatches.add(new CommentShaderPatch(ModularShaderConstants.MATERIAL_STRUCT_COMMENT,
                (s) -> materialFormat.toGLSLUniform(materialName)));

        // create normal mapping if necessary
        if (hasNormals) {
            // create normal value for the fragment shader to use
            shaderPatches.add(new CommentShaderPatch(ModularShaderConstants.FRAG_MAIN_COMMENT,
                    (s) -> "vec3 normal = "+ VertexElements.NORMAL.getPassName() + ";\n",
                    CommentShaderPatch.ModificationType.PREPEND));
        }

        // define materialDiffuse, materialSpecular, and materialNormal within the fragment shader's main function.
        shaderPatches.add(new CommentShaderPatch(ModularShaderConstants.FRAG_MAIN_COMMENT,
                (s) -> String.format("    vec4 %s = %s;\n", ModularShaderConstants.FRAG_MATERIAL_DIFFUSE,
                        getMaterialDiffuseAsVec4()), CommentShaderPatch.ModificationType.PREPEND));

        shaderPatches.add(new CommentShaderPatch(ModularShaderConstants.FRAG_MAIN_COMMENT,
                (s) -> String.format("    vec4 %s = %s;\n", ModularShaderConstants.FRAG_MATERIAL_SPECULAR,
                        getMaterialSpecularAsVec4()), CommentShaderPatch.ModificationType.PREPEND));

        shaderPatches.add(new CommentShaderPatch(ModularShaderConstants.FRAG_MAIN_COMMENT,
                (s) -> String.format("    vec4 %s = %s;\n", ModularShaderConstants.FRAG_MATERIAL_NORMAL,
                        getMaterialNormalAsVec4()), CommentShaderPatch.ModificationType.PREPEND));

        if (hasNormals && materialFormat.hasElementAsSampler(MaterialElement.NORMAL)) {
            String materialMappedNormal = getMaterialMappedNormal();

            if (!shouldUseTriplanarMapping()) {
                shaderPatches.add(new CommentShaderPatch(ModularShaderConstants.FRAG_UNIFORMS_COMMENT,
                        (s) -> IOUtils.readResource(ModularShaderConstants.NORMAL_FUNCTION_LOCATION, null),
                        CommentShaderPatch.ModificationType.PREPEND));
            }

            shaderPatches.add(new CommentShaderPatch(ModularShaderConstants.FRAG_MAIN_COMMENT,
                    (s) -> "normal = " + materialMappedNormal + ";\n",
                    CommentShaderPatch.ModificationType.PREPEND));
        }

        return shaderPatches;
    }

    private String getMaterialElementTriplanarVec4(String materialName, MaterialFormat materialFormat,
                                                   MaterialElement materialElement) {
        return String.format("sampleTriplanar(%s.%s, %s, %s, %s, %s)", materialName,
                materialFormat.getElement(materialElement).variableName, "passFragPos", "normal",
                "triplanarTile", "triplanarBlendOffset");
    }

    private String getMaterialNormalTriplanarVec4(String materialName, MaterialFormat materialFormat) {
        return String.format("sampleTriplanarNormal(%s.%s, %s, %s, %s, %s)", materialName,
                materialFormat.getElement(MaterialElement.NORMAL).variableName, "passFragPos", "normal",
                "triplanarTile", "triplanarBlendOffset");
    }

    private String getMaterialDiffuseAsVec4() {
        if (shouldUseTriplanarMapping() && materialFormat.hasElementAsSampler(MaterialElement.DIFFUSE)) {
            return getMaterialElementTriplanarVec4(materialName, materialFormat, MaterialElement.DIFFUSE);
        }
        return ShaderUtils.getMaterialDiffuseAsVec4(materialFormat, materialName);
    }

    private String getMaterialSpecularAsVec4() {
        if (shouldUseTriplanarMapping() && materialFormat.hasElementAsSampler(MaterialElement.SPECULAR)) {
            return getMaterialElementTriplanarVec4(materialName, materialFormat, MaterialElement.SPECULAR);
        }
        return ShaderUtils.getMaterialSpecularAsVec4(materialFormat, materialName);
    }

    private String getMaterialNormalAsVec4() {
        if (shouldUseTriplanarMapping() && materialFormat.hasElement(MaterialElement.NORMAL)) {
            return getMaterialNormalTriplanarVec4(materialName, materialFormat);
        }
        return ShaderUtils.getMaterialNormalAsVec4(materialFormat, materialName);
    }


    private String getMaterialMappedNormal() {
        if (shouldUseTriplanarMapping()) {
            return getMaterialNormalTriplanarVec4(materialName, materialFormat);
        } else {
            return createNormalMappedNormal();
        }
    }

    private String createNormalMappedNormal() {
        return String.format("calculateMappedNormal(normalize(%s), normalize(%s), %s.xyz)",
                VertexElements.NORMAL.getPassName(), VertexElements.TANGENT.getPassName(),
                ModularShaderConstants.FRAG_MATERIAL_NORMAL);
    }


    private void createUniforms() throws ShaderUniformException {
        this.createUniform("projectionMatrix");
        this.createUniform("modelMatrix");
        this.createUniform("viewMatrix");

        if (shouldUseTriplanarMapping()) {
            this.createUniform("triplanarBlendOffset");
            this.createUniform("triplanarTile");
            this.createUniform("fragModelMatrix");
        }
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

    private boolean shouldUseTriplanarMapping() {
        return !vertexFormat.hasElement(VertexElements.UV) && vertexFormat.hasElement(VertexElements.NORMAL)
                && materialFormat.hasSampler();
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

        if (shouldUseTriplanarMapping()) {
            this.setUniform("triplanarBlendOffset",
                    material.getPropertyOrDefault("triplanarBlendOffset", 0F));
            this.setUniform("triplanarTile",
                    material.getPropertyOrDefault("triplanarTile", DEFAULT_TRIPLANAR_TILE_VALUE));
        }
    }

    public void setProjectionMatrix(Projection projection) {
        this.setUniform("projectionMatrix", projection.toProjectionMatrix());
    }

    public void setModelMatrix(Transformable transformable) {
        Matrix4f matrix = transformable.toMatrix(modelMatrix);
        this.setUniform("modelMatrix", matrix);

        if (shouldUseTriplanarMapping()) {
            this.setUniform("fragModelMatrix", matrix);
        }
    }

    public void setViewMatrix(Camera camera) {
        this.setUniform("viewMatrix", camera.toViewMatrix());
    }

}

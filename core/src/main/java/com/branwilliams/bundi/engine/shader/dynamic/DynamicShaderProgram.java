package com.branwilliams.bundi.engine.shader.dynamic;

import com.branwilliams.bundi.engine.shader.*;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Dynamically creates a {@link ShaderProgram} using the {@link VertexFormat} provided.
 * <p>
 * There are two masks that can be provided to this ShaderProgram that will determine it's functionality.
 * </p>
 *
 * <pre>
 * {@link DynamicShaderProgram#VIEW_MATRIX} // will add a view matrix to this shader program.
 * {@link DynamicShaderProgram#SPHERICAL_BILLBOARDING} // will force the modelview matrix rotation to be the identity
 * matrix, forcing all rendering to have no rotation.
 * {@link DynamicShaderProgram#CYLINDRICAL_BILLBOARDING} // will force the modelview matrix rotation to ALMOST be the
 * identity matrix, forcing the rotation in the x axis to remain it's original state.
 * </pre>
 *
 * Created by Brandon Williams on 2/3/2018.
 */
public class DynamicShaderProgram extends ShaderProgram {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final float ALPHA_THRESHOLD = 0F;

    /**
     * Adds a view matrix to the shader program.
     * */
    public static final int VIEW_MATRIX = 0x00000001;

    /**
     * Forces the modelview matrix rotation to be the identity matrix like so:
     * <pre>
     * [ 1   0   0   X ]
     * [ 0   1   0   Y ]
     * [ 0   0   1   Z ]
     * [ m30 m31 m32 W ]
     * </pre>
     * This ensure that whatever is rendered will be facing the viewer.
     * {@link DynamicShaderProgram#VIEW_MATRIX} must also be present within the mode mask.
     * */
    public static final int SPHERICAL_BILLBOARDING = 0x00000002;

    /**
     * Forces the modelview matrix rotation to be ALMOST the identity matrix like so:
     * <pre>
     * [ 1   m01 0   X ]
     * [ 0   m11 0   Y ]
     * [ 0   m21 1   Z ]
     * [ m30 m31 m32 W ]
     * </pre>
     * This ensure that whatever is rendered will be facing the viewer with the exception that the x rotation remains
     * the same.
     * {@link DynamicShaderProgram#VIEW_MATRIX} must also be present within the mode mask.
     * */
    public static final int CYLINDRICAL_BILLBOARDING = 0x00000004;


    /**
     *
     * */
    public static final int FOG = 0x00000008;

    /**
     *
     * */
    public static final int SUN = 0x00000010;

    private final Matrix4f modelMatrix = new Matrix4f();

    /** The {@link VertexFormat} this shader program accepts. */
    private final VertexFormat vertexFormat;

    /** The masks specified to this constructor. */
    private final int mode;

    private Fog fog;

    public DynamicShaderProgram() throws ShaderInitializationException, ShaderUniformException {
        this(0);
    }

    public DynamicShaderProgram(int mode) throws ShaderInitializationException, ShaderUniformException {
        this(VertexFormat.POSITION_UV_COLOR, mode);
    }

    public DynamicShaderProgram(VertexFormat vertexFormat) throws ShaderInitializationException, ShaderUniformException {
        this(vertexFormat, 0);
    }

    public DynamicShaderProgram(VertexFormat vertexFormat, int mode) throws ShaderInitializationException, ShaderUniformException {
        super();
        this.vertexFormat = vertexFormat;
        this.mode = mode;

        if (!vertexFormat.hasPositionElement()) {
            this.destroy();
            throw new ShaderInitializationException("Vertex Format must have a position element!");
        }
        boolean hasTexture = vertexFormat.hasElement(VertexElements.UV);
        boolean hasColor = vertexFormat.hasElement(VertexElements.COLOR);
        boolean hasUniformColor = !hasTexture && !hasColor;

        StringBuilder vertexShader = new StringBuilder();
        vertexShader.append("#version 330\n");

        // Create input lines.
        for (int i = 0; i < vertexFormat.getVertexElements().size(); i++) {
            VertexElements vertexElements = vertexFormat.getVertexElements().get(i);
            vertexShader.append(createLayout(i, "in "
                    + vertexElements.getType()
                    + " "
                    + vertexElements.getVariableName()
                    + ";"));
        }

        vertexShader.append((hasTexture ? "out vec2 passTextureCoordinates;\n" : "\n"))
                .append((hasColor ? "out vec4 passColor;\n" : "\n"))
                .append((hasBit(mode, FOG) ? "out vec4 passViewSpace;\n": "\n"))
                .append("\n")
                .append("uniform mat4 projectionMatrix;\n")
                .append("uniform mat4 modelMatrix;\n")
                .append(hasBit(mode, VIEW_MATRIX) ? "uniform mat4 viewMatrix;\n" : "\n")
                .append("\n")
                .append("void main() {\n")
                .append(getVertexShaderOutput(vertexFormat, mode));

        if (hasTexture)
            vertexShader.append("    passTextureCoordinates = " + VertexElements.UV.getVariableName() + ";\n");

        if (hasColor)
            vertexShader.append("    passColor = " + VertexElements.COLOR.getVariableName() + ";\n");

        if (hasBit(mode, FOG))
            vertexShader.append(getFogVertEval());

        vertexShader.append("}");
        //System.out.println(vertexShader.toString());

        this.setVertexShader(vertexShader.toString());

        StringBuilder fragmentShader = new StringBuilder();
        fragmentShader.append("#version 330\n")
                .append((hasTexture ? "in vec2 passTextureCoordinates;\n" : "\n"))
                .append((hasColor ? "in vec4 passColor;\n" : "\n"))
                .append(hasBit(mode, FOG) ? "in vec4 passViewSpace;\n" : "\n")
                .append("\n")
                .append("out vec4 fragColor;\n")

                .append((hasUniformColor ? "uniform vec4 color;\n" : "\n"))
                .append((hasTexture ? "uniform sampler2D textureSampler;\n" : "\n"))
                .append(getFogFragUniforms())
                .append(getFogFragFunctions())
                .append("void main() {\n")
                .append("    ").append(getFragmentShaderOutput(hasColor, hasTexture))
                .append("}");

        //System.out.println(fragmentShader.toString());
        this.setFragmentShader(fragmentShader.toString());
        this.link();

        this.createUniform("projectionMatrix");
        this.createUniform("modelMatrix");

        if (hasBit(mode, VIEW_MATRIX))
            this.createUniform("viewMatrix");


        if (hasUniformColor)
            this.createUniform("color");

        if (hasTexture) {
            this.createUniform("textureSampler");
            this.bind();
            this.setUniform("textureSampler", 0);
            ShaderProgram.unbind();
        }

        if (hasBit(mode, FOG)) {
            this.createUniform("fogDensity");
            this.createUniform("fogColor");
        }

        this.validate();
        log.info("Dynamic Shader Program created with mode: " + mode);

//        System.out.println("vertexShader=");
//        System.out.println(vertexShader);
//        System.out.println("fragmentShader=");
//        System.out.println(fragmentShader);
    }

    private String getFogVertEval() {
        String position = vertexFormat.hasElement(VertexElements.POSITION_2D) ?
                "vec4(position, 0.0, 1.0)" : "vec4(position, 1.0)";
        String worldSpacePosition = "modelMatrix * " + position;

        return "passViewSpace = viewMatrix * " + worldSpacePosition + ";\n";
    }

    private String getFogFragUniforms() {
        String uniforms = "";
        if (hasBit(mode, FOG)) {
            uniforms += "uniform vec4 fogColor;\n"
                    + "\n"
                    + "uniform float fogDensity;\n"
                    + "\n";
        }
        return uniforms;
    }

    private String getFogFragFunctions() {
        String functions = "";
        if (hasBit(mode, FOG)) {
            functions += "vec4 computeFog(vec4 pixelColor, float dist) {\n" +
                    "float fogAmount = 1.0 - exp( -dist * fogDensity );\n" +
                    "    return mix( pixelColor, fogColor, fogAmount );\n" +
                    "}\n" +
                    "\n" +
                    "// plane based distance.\n" +
                    "float computeDist(vec4 viewSpace) {\n" +
                    "    return -(viewSpace.z);\n" +
                    "}\n";
        }
        return functions;
    }

    /**
     * Creates a layout with the location provided.
     * */
    private String createLayout(int location, String code) {
        return "layout (location = " + location + ") " + code + "\n";
    }

    /**
     * @return The vertex shader output determined by the matrix mode.
     * */
    private String getVertexShaderOutput(VertexFormat vertexFormat, int mode) {
        String position = vertexFormat.hasElement(VertexElements.POSITION_2D) ?
                "vec4(position, 0.0, 1.0)" : "vec4(position, 1.0)";

        if (hasBit(mode, VIEW_MATRIX)) {
            if (hasBit(mode, SPHERICAL_BILLBOARDING | CYLINDRICAL_BILLBOARDING)) {
                String vsOutput = "    mat4 modelViewMatrix = viewMatrix * modelMatrix;\n"
                        // column 0
                        + "    modelViewMatrix[0][0] = 1;\n"
                        + "    modelViewMatrix[0][1] = 0;\n"
                        + "    modelViewMatrix[0][2] = 0;\n";

                if (hasBit(mode, SPHERICAL_BILLBOARDING)) {
                    // column 1
                    vsOutput +=
                              "    modelViewMatrix[1][0] = 0;\n"
                            + "    modelViewMatrix[1][1] = 1;\n"
                            + "    modelViewMatrix[1][2] = 0;\n";
                }

                return vsOutput
                        // column 2
                        + "    modelViewMatrix[2][0] = 0;\n"
                        + "    modelViewMatrix[2][1] = 0;\n"
                        + "    modelViewMatrix[2][2] = 1;\n"
                        + "    gl_Position = projectionMatrix * modelViewMatrix * " + position + ";\n";
            } else {
                return "    gl_Position = projectionMatrix * viewMatrix * modelMatrix * " + position + ";\n";
            }
        } else {
            return "    gl_Position = projectionMatrix * modelMatrix * " + position + ";\n";
        }
    }

    /**
     * Returns the fragment shader's color evaluation based on the mode provided.
     * */
    private String getFragmentShaderOutput(boolean hasColor, boolean hasTexture) throws ShaderInitializationException {
        if (hasColor && hasTexture) {
            return "vec4 textureColor = texture(textureSampler, passTextureCoordinates);" +
                    "if (textureColor.a <= " + ALPHA_THRESHOLD + ") {" +
                    "    discard;" +
                    "}" +
                    "fragColor = " + applyFog("passColor * textureColor") + ";\n";
        } else if (hasColor) {
            return "fragColor = " + applyFog("passColor") + ";\n";
        } else if (hasTexture) {
            return "vec4 textureColor = texture(textureSampler, passTextureCoordinates);" +
                    "if (textureColor.a <= " + ALPHA_THRESHOLD + ") {" +
                    "    discard;" +
                    "}" +
                    "fragColor = " + applyFog("textureColor") + ";\n";
        } else {
            return "fragColor = " + applyFog("color") + ";\n";
        }
    }

    private String applyFog(String colorVec4) {
        if (hasBit(mode, FOG)) {
            return String.format("computeFog(%s, computeDist(passViewSpace));", colorVec4);
        } else {
            return colorVec4;
        }
    }

    /**
     * Tests if the bit at the given position of the provided integer is set to one.
     * */
    private boolean hasBit(int integer, int bits) {
        return (integer & bits) != 0;
    }

    public void setProjectionMatrix(Projection projection) {
        this.setProjectionMatrix(projection.toProjectionMatrix());
    }

    public void setProjectionMatrix(Matrix4f matrix) {
        this.setUniform("projectionMatrix", matrix);
    }

    public void setModelMatrix(Transformable transformable) {
        this.setModelMatrix(transformable.toMatrix(modelMatrix));
    }

    public void setModelMatrix(Matrix4f matrix) {
        this.setUniform("modelMatrix", matrix);
    }

    public void setViewMatrix(Camera camera) {
        this.setUniform("viewMatrix", camera.toViewMatrix());
    }

    /** This is the color used when no UV and/or no COLOR is specified by the vertex format.
     *
     * */
    public void setColor(Vector4f color) {
        this.setUniform("color", color);
    }

    public void setFog(Fog fog) {
        this.setUniform("fogDensity", fog.getDensity());
        this.setUniform("fogColor", fog.getColor());
    }

    public VertexFormat getVertexFormat() {
        return vertexFormat;
    }

    public int getMode() {
        return mode;
    }
}

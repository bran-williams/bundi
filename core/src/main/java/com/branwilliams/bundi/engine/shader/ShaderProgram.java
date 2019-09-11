package com.branwilliams.bundi.engine.shader;

import org.joml.*;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;
import static org.lwjgl.opengl.GL40.GL_TESS_CONTROL_SHADER;
import static org.lwjgl.opengl.GL40.GL_TESS_EVALUATION_SHADER;

/**
 * ShaderProgram .
 * */
public class ShaderProgram {

    private final int id;

    // Each uniform variable has an id associated with it.
    private final Map<String, Integer> uniforms;

    private int vertexShader;

    private int tessellationControlShader;

    private int tessellationEvaluationShader;

    private int geometryShader;

    private int fragmentShader;

    public ShaderProgram() throws ShaderInitializationException {
        this.id = glCreateProgram();
        if (id == 0)
            throw new ShaderInitializationException("Unable to create shader program!");

        uniforms = new HashMap<>();
    }

    /**
     * Creates and sets the vertex shader of this shader program from the provided code.
     */
    public void setVertexShader(String code) throws ShaderInitializationException {
        vertexShader = createShader(code, GL_VERTEX_SHADER);
    }

    /**
     * Creates and sets the fragment shader of this shader program from the provided code.
     */
    public void setTessellationControlShader(String code) throws ShaderInitializationException {
        tessellationControlShader = createShader(code, GL_TESS_CONTROL_SHADER);
    }


    /**
     * Creates and sets the fragment shader of this shader program from the provided code.
     */
    public void setTessellationEvaluationShader(String code) throws ShaderInitializationException {
        tessellationEvaluationShader = createShader(code, GL_TESS_EVALUATION_SHADER);
    }


    /**
     * Creates and sets the fragment shader of this shader program from the provided code.
     */
    public void setGeometryShader(String code) throws ShaderInitializationException {
        geometryShader = createShader(code, GL_GEOMETRY_SHADER);
    }

    /**
     * Creates and sets the fragment shader of this shader program from the provided code.
     */
    public void setFragmentShader(String code) throws ShaderInitializationException {
        fragmentShader = createShader(code, GL_FRAGMENT_SHADER);
    }

    /**
     * Default method for creating a shader from the provided code.
     * */
    protected int createShader(String code, int shaderType) throws ShaderInitializationException {

        if (code == null) {
            throw new ShaderInitializationException("Cannot create a shader from null code!");
        }

        int shader = glCreateShader(shaderType);

        if (shader == 0) {
            throw new ShaderInitializationException("Unable to create shader of type: " + shaderType);
        }

        glShaderSource(shader, code);
        glCompileShader(shader);

        if (glGetShaderi(shader, GL_COMPILE_STATUS) == 0) {
            throw new ShaderInitializationException("Unable to compile shader code: " + glGetShaderInfoLog(shader));
        }

        glAttachShader(id, shader);

        return shader;
    }

    /**
     * Links this shader program. <br/>
     * This essentially verifies that all the shader code is working correctly.
     * */
    public void link() throws ShaderInitializationException {
        glLinkProgram(id);

        if (glGetProgrami(id, GL_LINK_STATUS) == 0) {
            throw new ShaderInitializationException("Unable to link shader program: " + glGetProgramInfoLog(id));
        }

        if (vertexShader != 0) {
            glDetachShader(id, vertexShader);
        }

        if (tessellationControlShader != 0) {
            glDetachShader(id, tessellationControlShader);
        }

        if (tessellationEvaluationShader != 0) {
            glDetachShader(id, tessellationEvaluationShader);
        }

        if (geometryShader != 0) {
            glDetachShader(id, geometryShader);
        }

        if (fragmentShader != 0) {
            glDetachShader(id, fragmentShader);
        }
    }

    /**
     * Validates that this shader program has been correctly set up, given the current opengl state.
     * */
    public void validate() {
        glValidateProgram(id);

        if (glGetProgrami(id, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(id, 1024));
        }
    }

    /**
     * Finds the location of a uniform variable and stores it within the uniform map.
     * */
    public void createUniform(String name) throws ShaderUniformException {
        int location = glGetUniformLocation(id, name);
        if (location < 0)
            throw new ShaderUniformException("Unable to find uniform: " + name);

        uniforms.put(name, location);
    }

    /**
     * Assigns the mat4 uniform variable a new value.
     * */
    public void setUniform(String name, Matrix4f matrix) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer floatBuffer = stack.mallocFloat(16);
            matrix.get(floatBuffer);
            glUniformMatrix4fv(uniforms.get(name), false, floatBuffer);
        }
    }

    /**
     * Assigns the mat4 uniform variable a new value.
     * */
    public void setUniform(String name, Matrix3f matrix) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer floatBuffer = stack.mallocFloat(9);
            matrix.get(floatBuffer);
            glUniformMatrix3fv(uniforms.get(name), false, floatBuffer);
        }
    }

    /**
     * Assigns the vec2 uniform variable a new value.
     * */
    public void setUniform(String name, Vector2f vector) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer floatBuffer = stack.mallocFloat(2);
            vector.get(floatBuffer);
            glUniform2fv(uniforms.get(name), floatBuffer);
        }
    }

    /**
     * Assigns the vec3 uniform variable a new value.
     * */
    public void setUniform(String name, Vector3f vector) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer floatBuffer = stack.mallocFloat(3);
            vector.get(floatBuffer);
            glUniform3fv(uniforms.get(name), floatBuffer);
        }
    }

    /**
     * Assigns the vec4 uniform variable a new value.
     * */
    public void setUniform(String name, Vector4f vector) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer floatBuffer = stack.mallocFloat(4);
            vector.get(floatBuffer);
            glUniform4fv(uniforms.get(name), floatBuffer);
        }
    }

    /**
     * Assigns the vec4 uniform array variable a new value.
     * */
    public void setUniform(String name, Vector4f[] vectors) {
        int vectorSize = 4;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer floatBuffer = stack.mallocFloat(vectorSize * vectors.length);
            int pos = 0;
            for (Vector4f v : vectors) {
                v.get(pos, floatBuffer);
                pos += vectorSize;
            }
            glUniform4fv(uniforms.get(name), floatBuffer);
        }
    }

    /**
     * Assigns the vec3 uniform array variable a new value.
     * */
    public void setUniform(String name, Vector3f[] vectors) {
        int vectorSize = 3;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer floatBuffer = stack.mallocFloat(vectorSize * vectors.length);
            int pos = 0;
            for (Vector3f v : vectors) {
                v.get(pos, floatBuffer);
                pos += vectorSize;
            }
            glUniform3fv(uniforms.get(name), floatBuffer);
        }
    }

    /**
     * Assigns the vec2 uniform array variable a new value.
     * */
    public void setUniform(String name, Vector2f[] vectors) {
        int vectorSize = 2;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer floatBuffer = stack.mallocFloat(vectorSize * vectors.length);
            int pos = 0;
            for (Vector2f v : vectors) {
                v.get(pos, floatBuffer);
                pos += vectorSize;
            }
            glUniform2fv(uniforms.get(name), floatBuffer);
        }
    }

    /**
     * Assigns the integer uniform variable to a new value.
     * */
    public void setUniform(String name, int value) {
        glUniform1i(uniforms.get(name), value);
    }

    /**
     * Assigns the float uniform variable to a new value.
     * */
    public void setUniform(String name, float value) {
        glUniform1f(uniforms.get(name), value);
    }

    /**
     * Assigns the boolean uniform variable to a new value.
     * */
    public void setUniform(String name, boolean value) {
        glUniform1i(uniforms.get(name), value ? 1 : 0);
    }

    /**
     * Binds this shader program.
     * */
    public void bind() {
        glUseProgram(id);
    }

    /**
     * Unbinds this shader program.
     * */
    public static void unbind() {
        glUseProgram(0);
    }

    /**
     * Destroys this shader program.
     * */
    public void destroy() {
        unbind();
        if (id != 0) {
            glDeleteProgram(id);
        }
    }

}

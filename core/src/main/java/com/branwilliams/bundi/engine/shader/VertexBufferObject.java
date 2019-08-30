package com.branwilliams.bundi.engine.shader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;

/**
 * Simple wrapper class for a vertex buffer object.
 * */
public class VertexBufferObject {

    private final int id;

    private int target;

    public VertexBufferObject(int target) {
        this.id = glGenBuffers();
        this.target = target;
    }

    public VertexBufferObject() {
        this(GL_ARRAY_BUFFER);
    }

    /**
     * Stores the buffer data into this vertex buffer object with the provided usage.
     * @param buffer The float buffer stored within this vbo. This buffer must not be larger than the size allocated
     *               for this buffer object.
     * @param usage The usage this float buffer will have. See {@link org.lwjgl.opengl.GL15#glBufferData(int, FloatBuffer, int)} for more information.
     * */
    public void storeBuffer(FloatBuffer buffer, int usage) {
        glBufferData(target, buffer, usage);
    }

    /**
     * Stores the buffer data into this vertex buffer object with the provided usage.
     * @param buffer The float buffer stored within this vbo. This buffer must not be larger than the size allocated
     *               for this buffer object.
     * @param usage The usage this float buffer will have. See {@link org.lwjgl.opengl.GL15#glBufferData(int, FloatBuffer, int)} for more information.
     * */
    public void storeBuffer(float[] buffer, int usage) {
        glBufferData(target, buffer, usage);
    }

    /**
     * Initializes this buffer with the size provided.
     * @param size The size of the buffer object's data store (in bytes).
     * @param usage The usage this float buffer will have. See {@link org.lwjgl.opengl.GL15#glBufferData(int, long, int)} for more information.
     * */
    public void storeBuffer(long size, int usage) {
        glBufferData(target, size, usage);
    }

    /**
     *
     * Updates this vbo object's data with the provided buffer.
     *
     */
    public void updateBuffer(FloatBuffer buffer, long offset) {
        glBufferSubData(target, offset, buffer);
    }


    /**
     * Stores the buffer into this vertex buffer object with the provided usage.
     * @param buffer The given int buffer to be stored within this vbo.
     * @param usage The usage this int buffer will have. See {@link org.lwjgl.opengl.GL15#glBufferData(int, IntBuffer, int) } for more information.
     * */
    public void storeBuffer(IntBuffer buffer, int usage) {
        glBufferData(target, buffer, usage);
    }

    /**
     * Binds this vertex buffer object.
     * */
    public void bind() {
        glBindBuffer(target, id);
    }

    /**
     * Unbinds this vertex buffer object.
     * */
    public void unbind() {
        glBindBuffer(target, 0);
    }

    /**
     * Destroys this vertex buffer object.
     * */
    public void destroy() {
        glDeleteBuffers(id);
    }

    public int getId() {
        return id;
    }

    public int getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return "[type=vbo, id=" + id + ", target=" + target + "]";
    }
}

package com.branwilliams.bundi.engine.shader;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

/**
 * Simple wrapper class for a vertex array object.
 * */
public class VertexArrayObject {

    private final int id;

    public VertexArrayObject() {
        this.id = glGenVertexArrays();
    }

    /**
     * Stores the given vbo as an attribute within this vao.
     * @param index The index this vbo is stored under.
     * @param vertexSize The radius of each vertex.
     * @param vbo The vertex buffer object to be stored within this vao.
     * */
    public void storeAttribute(int index, int vertexSize, VertexBufferObject vbo) {
        storeAttribute(index, vertexSize, vbo, 0, 0);
    }

    public void enableAttribute(int index) {
        glEnableVertexAttribArray(index);
    }

    public void disableAttribute(int index) {
        glDisableVertexAttribArray(index);
    }

    /**
     * Stores the given vbo as an attribute within this vao.
     * @param index The index this vbo is stored under.
     * @param vertexSize The radius of each vertex.
     * @param vbo The vertex buffer object to be stored within this vao.
     * */
    public void storeAttribute(int index, int vertexSize, VertexBufferObject vbo, int stride, long offset) {
        vbo.bind();
        //enableAttribute(index);
        glVertexAttribPointer(index, vertexSize, GL_FLOAT, false, stride, offset);
    }

    public void setAttributeDivisor(int index, int divisor) {
        glVertexAttribDivisor(index, divisor);
    }

    /**
     * Binds this vertex array object.
     * */
    public void bind() {
        glBindVertexArray(id);
    }

    /**
     * Unbinds this vertex array object.
     * */
    public static void unbind() {
        glBindVertexArray(0);
    }

    /**
     * Destroys this vertex array object.
     * */
    public void destroy() {
        glDeleteVertexArrays(id);
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "[type=vao, id=" + id + "]";
    }
}

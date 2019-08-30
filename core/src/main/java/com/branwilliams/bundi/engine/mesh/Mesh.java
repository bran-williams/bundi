package com.branwilliams.bundi.engine.mesh;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL15.*;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.shader.VertexArrayObject;
import com.branwilliams.bundi.engine.shader.VertexBufferObject;
import org.lwjgl.system.MemoryUtil;

/**
 * Contains a {@link VertexArrayObject} with various {@link VertexBufferObject}s stored as attributes. It also contains
 * an EBO (Element Buffer Object, a VertexBufferObject with indices)
 * */
public class Mesh implements Destructible {

    private final VertexArrayObject vao;

    // Vertices, colors, normals, etc are stored within vbos and those vbos are mapped to some id within the vao of this
    // mesh. This map keeps track of that information.
    private final Map<Integer, VertexBufferObject> attributes = new HashMap<>();

    // This is the number of vertices within this mesh. This is not automatically determined, so it is important to
    // assign it when this mesh is used without indices.
    private int vertexCount = 0;

    // This vbo stores the indices for rendering this mesh.
    private VertexBufferObject ebo;

    // This is the number of indices for this mesh.
    private int indiceCount;

    private int renderMode = GL_TRIANGLES;

    public Mesh() {
        // Create the vao to store the vbos within.
        vao = new VertexArrayObject();
    }

    /**
     * Stores the provided list of integers as the indices for this mesh.
     * */
    public Mesh storeIndices(int[] indices) {
        IntBuffer buffer = MemoryUtil.memAllocInt(indices.length);
        buffer.put(indices).flip();
        return storeIndices(buffer);
    }

    /**
     * Stores the provided list of integers as the indices for this mesh.
     * */
    public Mesh storeIndices(IntBuffer indices) {
        this.indiceCount = indices.remaining();
        try {
            // Create the vbo if necessary.
            if (ebo == null) {
                ebo = new VertexBufferObject(GL_ELEMENT_ARRAY_BUFFER);
            }

            ebo.bind();
            ebo.storeBuffer(indices, GL_STATIC_DRAW);
            ebo.unbind();
        } finally {
            if (indices != null) {
                MemoryUtil.memFree(indices);
            }
        }
        return this;
    }

    /**
     * Stores the provided vertices to the provided vertex buffer object. Will create a new vertex buffer object if the
     * provided is null.
     * */
    public VertexBufferObject storeAttribute(int attributeId, float[] vertices, int vertexSize) {
        return storeAttribute(attributes.get(attributeId), attributeId, vertices, vertexSize);
    }

    /**
     * Stores the provided vertices to the provided vertex buffer object. Will create a new vertex buffer object if the
     * provided is null.
     * */
    public VertexBufferObject storeAttribute(VertexBufferObject vbo, int attributeId, float[] vertices, int vertexSize) {
        // Create a float buffer with the vertices provided.
        FloatBuffer buffer = MemoryUtil.memAllocFloat(vertices.length);
        buffer.put(vertices).flip();
        return storeAttribute(vbo, attributeId, buffer, vertexSize);
    }

    /**
     * Stores the provided vertices to the provided vertex buffer object. Will create a new vertex buffer object if the
     * provided is null.
     * This will free the float buffer provided.
     * */
    public VertexBufferObject storeAttribute(VertexBufferObject vbo, int attributeId, FloatBuffer vertices,
                                             int vertexSize) {
        try {
            // Create the vbo if necessary and bind it to it's appropriate attribute id within the vao.
            if (vbo == null) {
                vbo = new VertexBufferObject();
                this.vao.storeAttribute(attributeId, vertexSize, vbo);
                this.attributes.put(attributeId, vbo);
            }
            vbo.bind();
            vbo.storeBuffer(vertices, GL_STATIC_DRAW);
            vbo.unbind();
        } finally {
            if (vertices != null) {
                MemoryUtil.memFree(vertices);
            }
        }
        return vbo;
    }

    /**
     * Updates the data in the VBO stored at the attribute id provided.
     * */
    public VertexBufferObject updateAttribute(int attributeId, float[] vertices, long offset) {
        // Create a float buffer with the vertices provided.
        FloatBuffer buffer = MemoryUtil.memAllocFloat(vertices.length);
        buffer.put(vertices).flip();
        return updateAttribute(attributeId, buffer, offset);
    }

    /**
     * Updates the data in the VBO stored at the attribute id provided.
     * */
    public VertexBufferObject updateAttribute(int attributeId, FloatBuffer vertices, long offset) {
        VertexBufferObject vbo;

        try {
            vbo = attributes.get(attributeId);

            vbo.bind();
            vbo.updateBuffer(vertices, offset);
            vbo.unbind();
        } finally {
            if (vertices != null) {
                MemoryUtil.memFree(vertices);
            }
        }
        return vbo;
    }

    /**
     * Initializes the data in the VBO stored at the attribute id provided.
     * */
    public VertexBufferObject initializeAttribute(int attributeId, int vertexSize, long size) {
        VertexBufferObject vbo = attributes.get(attributeId);
        if (vbo == null) {
            vbo = new VertexBufferObject();
            this.vao.storeAttribute(attributeId, vertexSize, vbo);
            attributes.put(attributeId, vbo);
        }

        vbo.bind();
        vbo.storeBuffer(size, GL_STATIC_DRAW);
        vbo.unbind();

        return vbo;
    }

    /**
     * @return True if the given attribute id is assigned to a vbo within this mesh.
     * */
    public boolean hasAttribute(int id) {
        return attributes.containsKey(id);
    }

    public boolean hasIndices() {
        return ebo != null;
    }

    /**
     * @return The number of indices within this mesh.
     * */
    public int getIndiceCount() {
        return indiceCount;
    }

    /**
     * @return The number of vertices within this mesh.
     * */
    public int getVertexCount() {
        return vertexCount;
    }

    public void setVertexCount(int vertexCount) {
        this.vertexCount = vertexCount;
    }

    /**
     * Destroys the vao and vbos associated with this mesh.
     * */
    @Override
    public void destroy() {
        for (VertexBufferObject vbo : this.attributes.values())
            vbo.destroy();
        if (ebo != null)
            ebo.destroy();
        vao.destroy();
    }

    public void enable() {
        for (int id : this.attributes.keySet())
            vao.enableAttribute(id);
    }

    public void disable() {
        for (int id : this.attributes.keySet())
            vao.disableAttribute(id);
    }

    public VertexArrayObject getVao() {
        return vao;
    }

    public VertexBufferObject getEbo() {
        return ebo;
    }

    public int getRenderMode() {
        return renderMode;
    }

    public void setRenderMode(int renderMode) {
        this.renderMode = renderMode;
    }

    public void bind() {
        this.vao.bind();
    }

    public void unbind() {
        VertexArrayObject.unbind();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.vao);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Mesh) {
            Mesh mesh = (Mesh) object;
            return mesh.vao.equals(this.vao);
        }
        return super.equals(object);
    }

    @Override
    public String toString() {
        return "[type=mesh, vao="
                + vao.getId()
                + ", attributes="
                + attributes.toString()
                + ", renderMode="
                + renderMode
                + ", ebo="
                + ebo.toString()
                + "]";
    }
}

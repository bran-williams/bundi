package com.branwilliams.bundi.engine.mesh.primitive;

import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.shader.dynamic.VertexElement;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.util.MeshUtils;

/**
 * Created by Brandon Williams on 7/7/2018.
 */
public class CubeMesh extends Mesh {

    private final float[] normalsVertices = {
            // bottom
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,

            // top
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,

            // front
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,

            // back
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,

            // left
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,

            // right
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f
    };
    private final float[] textureCoordsVertices = {
            // bottom
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,

            // top
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,

            // front
            1.0f, 0.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,

            // back
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,

            // left
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,

            // right
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
    };

    public CubeMesh(float xsize, float ysize, float zsize, VertexFormat vertexFormat) {
        super();
        float[] vertices = {
                // bottom
                -xsize, -ysize, -zsize,
                xsize, -ysize, -zsize,
                -xsize, -ysize, zsize,
                xsize, -ysize, -zsize,
                xsize, -ysize, zsize,
                -xsize, -ysize, zsize,

                // top
                -xsize, ysize, -zsize,
                -xsize, ysize, zsize,
                xsize, ysize, -zsize,
                xsize, ysize, -zsize,
                -xsize, ysize, zsize,
                xsize, ysize, zsize,

                // front
                -xsize, -ysize, zsize,
                xsize, -ysize, zsize,
                -xsize, ysize, zsize,
                xsize, -ysize, zsize,
                xsize, ysize, zsize,
                -xsize, ysize, zsize,

                // back
                -xsize, -ysize, -zsize,
                -xsize, ysize, -zsize,
                xsize, -ysize, -zsize,
                xsize, -ysize, -zsize,
                -xsize, ysize, -zsize,
                xsize, ysize, -zsize,

                // left
                -xsize, -ysize, zsize,
                -xsize, ysize, -zsize,
                -xsize, -ysize, -zsize,
                -xsize, -ysize, zsize,
                -xsize, ysize, zsize,
                -xsize, ysize, -zsize,

                // right
                xsize, -ysize, zsize,
                xsize, -ysize, -zsize,
                xsize, ysize, -zsize,
                xsize, -ysize, zsize,
                xsize, ysize, -zsize,
                xsize, ysize, zsize,
        };


        float[] tangents = null;
        float[] bitangents = null;
        if (vertexFormat.hasElement(VertexElement.TANGENT) || vertexFormat.hasElement(VertexElement.BITANGENT)) {
            tangents = new float[vertices.length];
            bitangents = new float[vertices.length];
            MeshUtils.calculateTangentBitangents(vertices, textureCoordsVertices, tangents, bitangents);
        }

        bind();
        for (VertexElement vertexElement : vertexFormat.getVertexElements()) {
            int index = vertexFormat.getElementIndex(vertexElement);
            int size = vertexElement.size;
            switch (vertexElement) {
                case POSITION:
                    storeAttribute(index, vertices, size);
                    setVertexCount(vertices.length / size);
                    break;
                case UV:
                    storeAttribute(index, textureCoordsVertices, size);
                    break;
                case NORMAL:
                    storeAttribute(index, normalsVertices, size);
                    break;
                case TANGENT:
                    storeAttribute(index, tangents, size);
                    break;
                case BITANGENT:
                    storeAttribute(index, bitangents, size);
                    break;
            }
        }
        unbind();
    }
    
    public CubeMesh(float size, VertexFormat vertexFormat) {
        this(size, size, size, vertexFormat);
    }
}

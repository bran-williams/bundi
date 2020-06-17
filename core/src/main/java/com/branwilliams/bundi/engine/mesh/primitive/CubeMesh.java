package com.branwilliams.bundi.engine.mesh.primitive;

import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.shader.dynamic.VertexElements;
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
        if (vertexFormat.hasElement(VertexElements.TANGENT) || vertexFormat.hasElement(VertexElements.BITANGENT)) {
            tangents = new float[vertices.length];
            bitangents = new float[vertices.length];
            MeshUtils.calculateTangentBitangents(vertices, textureCoordsVertices, tangents, bitangents);
        }

        bind();
        for (VertexElements vertexElements : vertexFormat.getVertexElements()) {
            int index = vertexFormat.getElementIndex(vertexElements);
            int size = vertexElements.getSize();
            switch (vertexElements) {
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
        this.setVertexFormat(vertexFormat);
    }
    
    public CubeMesh(float size, VertexFormat vertexFormat) {
        this(size, size, size, vertexFormat);
    }
}

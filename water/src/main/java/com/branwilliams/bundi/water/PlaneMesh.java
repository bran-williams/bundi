package com.branwilliams.bundi.water;

import com.branwilliams.bundi.engine.mesh.Mesh;

/**
 * TODO incorporate the VertexFormat class in the generation of this mesh.
 * @author Brandon
 * @since September 06, 2019
 */
public class PlaneMesh extends Mesh {

    public PlaneMesh(int vertexCount) {
        this(vertexCount, vertexCount);
    }

    public PlaneMesh(int vertexCountX, int vertexCountZ) {
        super();
        int count = vertexCountX * vertexCountZ;

        float[] vertices = new float[count * 3];
        int[] indices = new int[6 * (vertexCountX - 1) * (vertexCountZ - 1)];

        int vertexPointer = 0;

        for (int i = 0; i < vertexCountX; i++) {
            for (int j = 0; j < vertexCountZ; j++) {
                vertices[vertexPointer * 3] = i;// (float) i / ((float) vertexCountX - 1) * size;
                vertices[vertexPointer * 3 + 1] = 0;
                vertices[vertexPointer * 3 + 2] = j;// (float) j / ((float) vertexCountZ - 1) * size;
                vertexPointer++;
            }
        }

        int pointer = 0;
        for (int gx = 0; gx < vertexCountZ - 1; gx++) {
            for (int gz = 0; gz < vertexCountX - 1; gz++) {
                int topLeft = (gx * vertexCountX) + gz;
                int topRight = topLeft + 1;
                int bottomLeft = ((gx + 1) * vertexCountZ) + gz;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomRight;
                indices[pointer++] = bottomLeft;
            }
        }
        this.bind();
        this.storeAttribute(0, vertices, 3);
        this.storeIndices(indices);
        this.unbind();
    }
}

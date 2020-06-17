package com.branwilliams.bundi.engine.mesh.primitive;

import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.shader.dynamic.VertexElements;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.util.MeshUtils;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_LINES;

/***
 *
 */
public class GridMesh extends Mesh {

    public static final VertexFormat VERTEX_FORMAT = VertexFormat.POSITION_COLOR;

    /**
     *
     * */
    public GridMesh(int numX, float sizeX) {
        this(numX, numX, sizeX, sizeX);
    }

    /**
     * @param numX The number of rows in the x-axis.
     * @param numZ The number of rows in the z-axis.
     * @param sizeX The total size of the grid in the x-axis.
     * @param sizeZ The total size of the grid in the z-axis.
     * */
    public GridMesh(int numX, int numZ, float sizeX, float sizeZ) {
        super();
        float halfX = sizeX * 0.5F;
        float halfZ = sizeZ * 0.5F;

        float stepX = sizeX / numX;
        float stepZ = sizeZ / numZ;

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector4f> colors = new ArrayList<>();

        for (int i = 0; i < numX + 1; i++) {
            vertices.add(new Vector3f(-halfX, 0, -halfZ + stepZ * i));
            vertices.add(new Vector3f(halfX, 0, -halfZ + stepZ * i));
            colors.add(new Vector4f(1, 1, 1, 1));
            colors.add(new Vector4f(1, 1, 1, 1));
        }

        for (int i = 0; i < numZ + 1; i++) {
            vertices.add(new Vector3f(-halfX + stepX * i, 0, -halfZ));
            vertices.add(new Vector3f(-halfX + stepX * i, 0, halfZ));
            colors.add(new Vector4f(1, 1, 1, 1));
            colors.add(new Vector4f(1, 1, 1, 1));
        }

        setRenderMode(GL_LINES);
        bind();
        storeAttribute(0, MeshUtils.toArray3f(vertices), VertexElements.POSITION.getSize());
        storeAttribute(1, MeshUtils.toArray4f(colors), VertexElements.COLOR.getSize());
        setVertexCount(vertices.size());
        unbind();
    }
}

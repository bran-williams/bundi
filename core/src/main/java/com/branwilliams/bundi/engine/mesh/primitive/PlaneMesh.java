package com.branwilliams.bundi.engine.mesh.primitive;

import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.shader.dynamic.VertexElement;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.util.MeshUtils;
import org.joml.Planef;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_LINES;

/***
 *
 */
public class PlaneMesh extends Mesh {

    public static final Vector3f UP = new Vector3f(0F, 1F, 0F);

    public static final VertexFormat VERTEX_FORMAT = VertexFormat.POSITION_COLOR;

    /**
     *
     * */
    public PlaneMesh(VertexFormat vertexFormat, int numX, float sizeX) {
        this(vertexFormat, numX, numX, sizeX, sizeX, UP);
    }

    /**
     * @param numX The number of rows in the x-axis.
     * @param numZ The number of rows in the z-axis.
     * @param sizeX The total size of the grid in the x-axis.
     * @param sizeZ The total size of the grid in the z-axis.
     * */
    public PlaneMesh(VertexFormat vertexFormat, int numX, int numZ, float sizeX, float sizeZ, Vector3f normal) {
        super();
        float halfX = sizeX * 0.5F;
        float halfZ = sizeZ * 0.5F;

        float stepX = sizeX / numX;
        float stepZ = sizeZ / numZ;

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector4f> colors = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();

        for (int i = 0; i < numX + 1; i++) {
            vertices.add(new Vector3f(-halfX, 0, -halfZ + stepZ * i));
            vertices.add(new Vector3f(halfX, 0, -halfZ + stepZ * i));
            colors.add(new Vector4f(1, 1, 1, 1));
            colors.add(new Vector4f(1, 1, 1, 1));
            normals.add(normal);
            normals.add(normal);
        }

        for (int i = 0; i < numZ + 1; i++) {
            vertices.add(new Vector3f(-halfX + stepX * i, 0, -halfZ));
            vertices.add(new Vector3f(-halfX + stepX * i, 0, halfZ));
            colors.add(new Vector4f(1, 1, 1, 1));
            colors.add(new Vector4f(1, 1, 1, 1));
            normals.add(normal);
            normals.add(normal);
        }

        setRenderMode(GL_LINES);
        bind();
        storeAttribute(0, MeshUtils.toArray3f(vertices), VertexElement.POSITION.size);
        storeAttribute(1, MeshUtils.toArray4f(colors), VertexElement.COLOR.size);
        storeAttribute(2, MeshUtils.toArray3f(normals), VertexElement.NORMAL.size);
        setVertexCount(vertices.size());
        unbind();
    }
}

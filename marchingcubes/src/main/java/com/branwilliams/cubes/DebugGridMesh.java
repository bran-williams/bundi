package com.branwilliams.cubes;

import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.shader.dynamic.VertexElement;
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
public class DebugGridMesh extends Mesh {

    public static final VertexFormat VERTEX_FORMAT = VertexFormat.POSITION_COLOR;

    /**
     *
     * */
    public DebugGridMesh(Vector3f origin, int vertices, float step) {
        this(origin, vertices, vertices, step, step);
    }

    /**
     * @param verticesX The number of rows in the x-axis.
     * @param verticesZ The number of rows in the z-axis.
     * @param stepX The total size of the grid in the x-axis.
     * @param stepZ The total size of the grid in the z-axis.
     * */
    public DebugGridMesh(Vector3f origin, int verticesX, int verticesZ, float stepX, float stepZ) {
        super();
        float halfX = stepX * 0.5F;
        float halfZ = stepZ * 0.5F;

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector4f> colors = new ArrayList<>();

        for (int i = 0; i < verticesX + 1; i++) {
            vertices.add(new Vector3f(origin).add(0, 0, stepZ * i));
            vertices.add(new Vector3f(origin).add(stepX, 0, stepZ * i));
            colors.add(new Vector4f(1, 1, 1, 1));
            colors.add(new Vector4f(1, 1, 1, 1));
        }

        for (int i = 0; i < verticesZ + 1; i++) {
            vertices.add(new Vector3f(origin).add(stepX * i, 0, 0));
            vertices.add(new Vector3f(origin).add(stepX * i, 0, stepZ));
            colors.add(new Vector4f(1, 1, 1, 1));
            colors.add(new Vector4f(1, 1, 1, 1));
        }

        setRenderMode(GL_LINES);
        bind();
        storeAttribute(0, MeshUtils.toArray3f(vertices), VertexElement.POSITION.size);
        storeAttribute(1, MeshUtils.toArray4f(colors), VertexElement.COLOR.size);
        setVertexCount(vertices.size());
        unbind();
    }
}

package com.branwilliams.bundi.engine.mesh.primitive;

import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.shader.dynamic.VertexElements;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.util.MeshUtils;
import org.joml.*;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_LINES;

/***
 *
 */
public class PlaneMesh extends Mesh {

    public static final float DEFAULT_SCALE = 1F;

    public static final Vector3f UP = new Vector3f(0F, 1F, 0F);

    private final Planef plane;

    public PlaneMesh(VertexFormat vertexFormat, Planef plane) {
        this(vertexFormat, plane, DEFAULT_SCALE, DEFAULT_SCALE);
    }

    public PlaneMesh(VertexFormat vertexFormat, Planef plane, float scale) {
        this(vertexFormat, plane, scale, scale);
    }

    /**
     * */
    public PlaneMesh(VertexFormat vertexFormat, Planef plane, float scaleX, float scaleY) {
        super();
        this.plane = plane;
        Vector3f normal = new Vector3f(plane.a, plane.b, plane.c);
        float distance = plane.d;

        Vector3f forward = new Vector3f(normal.x, normal.y, normal.z);
        Vector3f right = new Vector3f(forward).cross(UP);
        Vector3f up = new Vector3f(right).cross(forward);

        Matrix4f rot = new Matrix4f(right.x, up.x, -forward.x, 0.0f,
                right.y, up.y, -forward.y, 0.0f,
                right.z, up.z, -forward.z, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f);

        Matrix4f trans = new Matrix4f().translate(normal.mul(distance, new Vector3f()));

        Matrix4f planeTransformation = new Matrix4f(trans).mul(rot);

        Matrix3f rotMatrix = new Matrix3f(planeTransformation);


        List<Vector3f> vertices = new ArrayList<>();

        vertices.add(rotMatrix.transform(new Vector3f(-scaleX, scaleY, 0F)));
        vertices.add(rotMatrix.transform(new Vector3f(scaleX, scaleY, 0F)));
        vertices.add(rotMatrix.transform(new Vector3f(-scaleX, -scaleY, 0F)));
        vertices.add(rotMatrix.transform(new Vector3f(scaleX, -scaleY, 0F)));

        bind();
        storeAttribute(vertexFormat.getElementIndex(VertexElements.POSITION), MeshUtils.toArray3f(vertices),
                VertexElements.POSITION.getSize());

        if (vertexFormat.hasElement(VertexElements.COLOR)) {
            List<Vector4f> colors = new ArrayList<>();

            colors.add(new Vector4f(1, 1, 1, 1));
            colors.add(new Vector4f(1, 1, 1, 1));
            colors.add(new Vector4f(1, 1, 1, 1));
            colors.add(new Vector4f(1, 1, 1, 1));
            storeAttribute(vertexFormat.getElementIndex(VertexElements.COLOR), MeshUtils.toArray4f(colors),
                    VertexElements.COLOR.getSize());
        }
        if (vertexFormat.hasElement(VertexElements.NORMAL)) {
            List<Vector3f> normals = new ArrayList<>();

            normals.add(normal);
            normals.add(normal);
            normals.add(normal);
            normals.add(normal);
            storeAttribute(vertexFormat.getElementIndex(VertexElements.NORMAL), MeshUtils.toArray3f(normals),
                    VertexElements.NORMAL.getSize());
        }

        if (vertexFormat.hasElement(VertexElements.UV)) {
            List<Vector2f> uvs = new ArrayList<>();

            uvs.add(new Vector2f(0, 1));
            uvs.add(new Vector2f(1, 1));
            uvs.add(new Vector2f(0, 0));
            uvs.add(new Vector2f(1, 0));
            storeAttribute(vertexFormat.getElementIndex(VertexElements.UV), MeshUtils.toArray2f(uvs),
                    VertexElements.UV.getSize());
        }

        this.storeIndices(new int[] { 0, 2, 1, 1, 2, 3 });
        unbind();

        this.setVertexFormat(vertexFormat);
    }

    public Planef getPlane() {
        return plane;
    }
}

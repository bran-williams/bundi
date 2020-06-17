package com.branwilliams.bundi.engine.mesh.primitive;

import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.shader.dynamic.VertexElements;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.bundi.engine.util.MeshUtils;

import java.util.ArrayList;
import java.util.List;

import static com.branwilliams.bundi.engine.util.MeshUtils.toArrayf;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;

/**
 * Created by Brandon Williams on 7/1/2018.
 * @see <a href="http://legacy.lwjgl.org/javadoc/org/lwjgl/util/glu/Sphere.html">GLU Sphere</a>
 */
public class SphereMesh extends Mesh {

    public SphereMesh(float radius, int slices, int stacks, VertexFormat vertexFormat) {
        this(radius, slices, stacks, vertexFormat, false);
    }

    /**
     *
     * Creates a sphere-shaped mesh of the given radius centered around the origin.
     * The sphere is subdivided around the z axis into slices and along the z axis
     * into stacks (similar to lines of longitude and latitude).
     *
     * @param invNormals True if this mesh needs inverted normals. (facing inward)
     */
    public SphereMesh(float radius, int slices, int stacks, VertexFormat vertexFormat, boolean invNormals) {
        super();
        this.setRenderMode(GL_TRIANGLE_STRIP);
        List<Float> vertices = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Float> textureCoordinates = new ArrayList<>();
        float rho, drho, theta, dtheta;
        float x, y, z;
        float s, t, ds, dt;
        float nsign = 1.0f;
        if (invNormals) {
            nsign = -1.0f;
        }
        drho = Mathf.PI / stacks;
        dtheta = 2.0f * Mathf.PI / slices;

        ds = 1.0f / slices;
        dt = 1.0f / stacks;
        t = 1.0f; // because loop now runs from 0

        // draw intermediate stacks as quad strips
        for (int i = 0; i < stacks; i++) {
            rho = i * drho;
            s = 0.0f;
            for (int j = 0; j <= slices; j++) {
                theta = (j == slices) ? 0.0f : j * dtheta;
                x = -Mathf.sin(theta) * Mathf.sin(rho);
                y = Mathf.cos(theta) * Mathf.sin(rho);
                z = nsign * Mathf.cos(rho);
                normals.add(x * nsign);
                normals.add(y * nsign);
                normals.add(z * nsign);
                textureCoordinates.add(s);
                textureCoordinates.add(t);
                vertices.add(x * radius);
                vertices.add(y * radius);
                vertices.add(z * radius);
                x = -Mathf.sin(theta) * Mathf.sin(rho + drho);
                y = Mathf.cos(theta) * Mathf.sin(rho + drho);
                z = nsign * Mathf.cos(rho + drho);
                normals.add(x * nsign);
                normals.add(y * nsign);
                normals.add(z * nsign);

                textureCoordinates.add(s);
                textureCoordinates.add(t - dt);
                s += ds;
                vertices.add(x * radius);
                vertices.add(y * radius);
                vertices.add(z * radius);
            }
            t -= dt;
        }

        bind();

        float[] verticesArray = toArrayf(vertices);
        if (vertexFormat.hasElement(VertexElements.POSITION)) {
            storeAttribute(vertexFormat.getElementIndex(VertexElements.POSITION), verticesArray, VertexElements.POSITION.getSize());
            setVertexCount(verticesArray.length / 3);
        }

        float[] texCoordsArray = toArrayf(textureCoordinates);
        if (vertexFormat.hasElement(VertexElements.UV)) {
            storeAttribute(vertexFormat.getElementIndex(VertexElements.UV), texCoordsArray, VertexElements.UV.getSize());
        }

        if (vertexFormat.hasElement(VertexElements.NORMAL)) {
            storeAttribute(vertexFormat.getElementIndex(VertexElements.NORMAL), toArrayf(normals), VertexElements.NORMAL.getSize());
        }



        if (vertexFormat.hasElement(VertexElements.TANGENT) || vertexFormat.hasElement(VertexElements.BITANGENT)) {
            float[] tangents = new float[verticesArray.length];
            float[] bitangents = new float[verticesArray.length];

            MeshUtils.calculateTangentBitangents(verticesArray, texCoordsArray, tangents, bitangents);

            if (vertexFormat.hasElement(VertexElements.TANGENT)) {
                storeAttribute(vertexFormat.getElementIndex(VertexElements.TANGENT), tangents, VertexElements.TANGENT.getSize());
            }

            if (vertexFormat.hasElement(VertexElements.BITANGENT)) {
                storeAttribute(vertexFormat.getElementIndex(VertexElements.BITANGENT), bitangents, VertexElements.BITANGENT.getSize());
            }
        }
        unbind();
    }

}

package com.branwilliams.bundi.engine.mesh.primitive;

import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.bundi.engine.util.MeshUtils;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;

/**
 * Created by Brandon Williams on 7/1/2018.
 * @see <a href="http://legacy.lwjgl.org/javadoc/org/lwjgl/util/glu/Sphere.html">GLU Sphere</a>
 */
public class SphereMesh extends Mesh {

    public SphereMesh(float radius, int slices, int stacks) {
        this(radius, slices, stacks, true, false);
    }

    /**
     *
     * Creates a sphere-shaped mesh of the given radius centered around the origin.
     * The sphere is subdivided around the z axis into slices and along the z axis
     * into stacks (similar to lines of longitude and latitude).
     *
     * @param tangentBitangent True if this mesh needs tangents/bitangents.
     * @param invNormals True if this mesh needs inverted normals. (facing inward)
     */
    public SphereMesh(float radius, int slices, int stacks, boolean tangentBitangent, boolean invNormals) {
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

        float[] verticesArray = new float[vertices.size()];
        for (int k = 0; k < vertices.size(); k++) {
            verticesArray[k] = vertices.get(k);
        }
        float[] texCoordsArray = new float[textureCoordinates.size()];
        for (int k = 0; k < textureCoordinates.size(); k++) {
            texCoordsArray[k] = textureCoordinates.get(k);
        }
        float[] normalsArray = new float[normals.size()];
        for (int k = 0; k < normals.size(); k++) {
            normalsArray[k] = normals.get(k);
        }
        bind();
        storeAttribute(0, verticesArray,3);
        storeAttribute(1, texCoordsArray, 2);
        storeAttribute(2, normalsArray, 3);
        if (tangentBitangent) {
            float[] tangents = new float[verticesArray.length];
            float[] bitangents = new float[verticesArray.length];
            MeshUtils.calculateTangentBitangents(verticesArray, texCoordsArray, tangents, bitangents);
            storeAttribute(3, tangents, 3);
            storeAttribute(4, bitangents, 3);
        }
        setVertexCount(verticesArray.length / 3);
        unbind();
    }

}

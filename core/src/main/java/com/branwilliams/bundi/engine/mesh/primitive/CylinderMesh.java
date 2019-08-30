package com.branwilliams.bundi.engine.mesh.primitive;

import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.bundi.engine.util.MeshUtils;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;

public class CylinderMesh extends Mesh {
    /**
     * draws a cylinder oriented along the z axis. The base of the
     * cylinder is placed at z = 0, and the top at z=height. Like a sphere, a
     * cylinder is subdivided around the z axis into slices, and along the z axis
     * into stacks.
     *
     * Note that if topRadius is set to zero, then this routine will generate a
     * cone.
     *
     * If the orientation is set to GLU.OUTSIDE (with glu.quadricOrientation), then
     * any generated normals point away from the z axis. Otherwise, they point
     * toward the z axis.
     *
     * If texturing is turned on (with glu.quadricTexture), then texture
     * coordinates are generated so that t ranges linearly from 0.0 at z = 0 to
     * 1.0 at z = height, and s ranges from 0.0 at the +y axis, to 0.25 at the +x
     * axis, to 0.5 at the -y axis, to 0.75 at the -x axis, and back to 1.0 at the
     * +y axis.
     *
     * @param baseRadius  Specifies the radius of the cylinder at z = 0.
     * @param topRadius   Specifies the radius of the cylinder at z = height.
     * @param height      Specifies the height of the cylinder.
     * @param slices      Specifies the number of subdivisions around the z axis.
     * @param stacks      Specifies the number of subdivisions along the z axis.
     */
    public CylinderMesh(float baseRadius, float topRadius, float height, int slices, int stacks, boolean tangentBitangent, boolean invNormals) {
        super();
        setRenderMode(GL_TRIANGLE_STRIP);

        List<Float> vertices = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Float> uvs = new ArrayList<>();
        float da, r, dr, dz;
        float x, y, z, nz, nsign;
        int i, j;

        if (invNormals) {
            nsign = -1.0f;
        } else {
            nsign = 1.0f;
        }

        da = 2.0f * Mathf.PI / slices;
        dr = (topRadius - baseRadius) / stacks;
        dz = height / stacks;
        nz = (baseRadius - topRadius) / height;
        // Z component of normal vectors
            float ds = 1.0f / slices;
            float dt = 1.0f / stacks;
            float t = 0.0f;
            z = 0.0f;
            r = baseRadius;
            for (j = 0; j < stacks; j++) {
                float s = 0.0f;
                for (i = 0; i <= slices; i++) {
                    if (i == slices) {
                        x = Mathf.sin(0.0f);
                        y = Mathf.cos(0.0f);
                    } else {
                        x = Mathf.sin((i * da));
                        y = Mathf.cos((i * da));
                    }
                    if (nsign == 1.0f) {
                        normals.add(x * nsign);
                        normals.add(y * nsign);
                        normals.add(nz * nsign);
                        uvs.add(s);
                        uvs.add(t);
                        vertices.add(x * r);
                        vertices.add(y * r);
                        vertices.add(z);
                        normals.add(x * nsign);
                        normals.add(y * nsign);
                        normals.add(nz * nsign);
                        uvs.add(s);
                        uvs.add(t + dt);
                        vertices.add(x * (r + dr));
                        vertices.add(y * (r + dr));
                        vertices.add(z + dz);
                    } else {
                        normals.add(x * nsign);
                        normals.add(y * nsign);
                        normals.add(nz * nsign);
                        uvs.add(s);
                        uvs.add(t);
                        vertices.add(x * r);
                        vertices.add(y * r);
                        vertices.add(z);
                        normals.add(x * nsign);
                        normals.add(y * nsign);
                        normals.add(nz * nsign);
                        uvs.add(s);
                        uvs.add(t + dt);
                        vertices.add(x * (r + dr));
                        vertices.add(y * (r + dr));
                        vertices.add(z + dz);
                    }
                    s += ds;
                } // for slices
                r += dr;
                t += dt;
                z += dz;
            } // for stacks
        float[] verticesArray = new float[vertices.size()];
        for (int k = 0; k < vertices.size(); k++) {
            verticesArray[k] = vertices.get(k);
        }
        float[] uvsArray = new float[uvs.size()];
        for (int k = 0; k < uvs.size(); k++) {
            uvsArray[k] = uvs.get(k);
        }
        float[] normalsArray = new float[normals.size()];
        for (int k = 0; k < normals.size(); k++) {
            normalsArray[k] = normals.get(k);
        }
        bind();
        storeAttribute(0, verticesArray,3);
        storeAttribute(1, uvsArray, 2);
        storeAttribute(2, normalsArray, 3);
        if (tangentBitangent) {
            float[] tangents = new float[verticesArray.length];
            float[] bitangents = new float[verticesArray.length];
            MeshUtils.calculateTangentBitangents(verticesArray, uvsArray, tangents, bitangents);
            storeAttribute(3, tangents, 3);
            storeAttribute(4, bitangents, 3);
        }
        setVertexCount(verticesArray.length / 3);
        unbind();
    }
}

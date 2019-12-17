package com.branwilliams.mcskin.steve;

import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.bundi.engine.shader.Material;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static com.branwilliams.bundi.engine.util.MeshUtils.*;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;

/**
 * @author Brandon
 * @since November 26, 2019
 */
public final class ModelPart {

    public SteveVertex[] vertices;
    public TexturedQuad[] quads;
    private int u;
    private int v;
    public float x;
    public float y;
    public float z;
    public float pitch;
    public float yaw;
    public float roll;
    public boolean hasMesh = false;
    public int list = 0;
    public boolean mirror = false;
    public boolean render = true;
    private boolean unused = false;

    private Mesh mesh;
    private Material material;

    public ModelPart(Material material, int u, int v) {
        this.material = material;
        this.u = u;
        this.v = v;
    }

    public final void setBounds(float minX, float minY, float minZ, int sizeX, int sizeY, int sizeZ, float scale) {
        this.vertices = new SteveVertex[8];
        this.quads = new TexturedQuad[6];
        float maxX = minX + (float) sizeX;
        float maxY = minY + (float) sizeY;
        float maxZ = minZ + (float) sizeZ;

        minX -= scale;
        minY -= scale;
        minZ -= scale;
        maxX += scale;
        maxY += scale;
        maxZ += scale;

        if(this.mirror) {
            scale = maxX;
            maxX = minX;
            minX = scale;
        }

        SteveVertex vec0 = new SteveVertex(minX, minY, minZ, 0.0F, 0.0F);
        SteveVertex vec1 = new SteveVertex(maxX, minY, minZ, 0.0F, 8.0F);
        SteveVertex vec2 = new SteveVertex(maxX, maxY, minZ, 8.0F, 8.0F);
        SteveVertex vec3 = new SteveVertex(minX, maxY, minZ, 8.0F, 0.0F);
        SteveVertex vec4 = new SteveVertex(minX, minY, maxZ, 0.0F, 0.0F);
        SteveVertex vec5 = new SteveVertex(maxX, minY, maxZ, 0.0F, 8.0F);
        SteveVertex vec6 = new SteveVertex(maxX, maxY, maxZ, 8.0F, 8.0F);
        SteveVertex vec7 = new SteveVertex(minX, maxY, maxZ, 8.0F, 0.0F);

        this.vertices[0] = vec0;
        this.vertices[1] = vec1;
        this.vertices[2] = vec2;
        this.vertices[3] = vec3;
        this.vertices[4] = vec4;
        this.vertices[5] = vec5;
        this.vertices[6] = vec6;
        this.vertices[7] = vec7;

        this.quads[0] = new TexturedQuad(new SteveVertex[] {vec5, vec1, vec2, vec6}, this.u + sizeZ + sizeX, this.v + sizeZ, this.u + sizeZ + sizeX + sizeZ, this.v + sizeZ + sizeY);
        this.quads[1] = new TexturedQuad(new SteveVertex[]{vec0, vec4, vec7, vec3}, this.u, this.v + sizeZ, this.u + sizeZ, this.v + sizeZ + sizeY);
        this.quads[2] = new TexturedQuad(new SteveVertex[]{vec5, vec4, vec0, vec1}, this.u + sizeZ, this.v, this.u + sizeZ + sizeX, this.v + sizeZ);
        this.quads[3] = new TexturedQuad(new SteveVertex[]{vec2, vec3, vec7, vec6}, this.u + sizeZ + sizeX, this.v, this.u + sizeZ + sizeX + sizeX, this.v + sizeZ);
        this.quads[4] = new TexturedQuad(new SteveVertex[]{vec1, vec0, vec3, vec2}, this.u + sizeZ, this.v + sizeZ, this.u + sizeZ + sizeX, this.v + sizeZ + sizeY);
        this.quads[5] = new TexturedQuad(new SteveVertex[]{vec4, vec5, vec6, vec7}, this.u + sizeZ + sizeX + sizeZ, this.v + sizeZ, this.u + sizeZ + sizeX + sizeZ + sizeX, this.v + sizeZ + sizeY);

        if (this.mirror) {
            for (int i = 0; i < this.quads.length; i++) {
                TexturedQuad quad = this.quads[i];
                SteveVertex[] vertices = new SteveVertex[quad.vertices.length];

                for (sizeX = 0; sizeX < quad.vertices.length; sizeX++) {
                    vertices[sizeX] = quad.vertices[quad.vertices.length - sizeX - 1];
                }

                quad.vertices = vertices;
            }
        }

    }

    public final void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public final void render(float scale) {
        if (!this.hasMesh)
            buildMesh(scale);

        if (this.render) {
            MeshRenderer.render(mesh, material);
        }
    }

    public void buildMesh(float scale) {
        mesh = new Mesh();
        List<Vector3f> positions = new ArrayList<>();
        List<Vector2f> uvs = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        int index = 0;
        for (int i = 0; i < this.quads.length; i++) {
            TexturedQuad quad = this.quads[i];
            Vector3f var5 = quad.vertices[1].position.sub(quad.vertices[0].position, new Vector3f()).normalize();
            Vector3f var6 = quad.vertices[1].position.sub(quad.vertices[2].position, new Vector3f()).normalize();
            Vector3f normal = new Vector3f(var5.y * var6.z - var5.z * var6.y, var5.z * var6.x - var5.x * var6.z, var5.x * var6.y - var5.y * var6.x).normalize();

            for (int j = 0; j < quad.vertices.length; j++) {
                SteveVertex vertex = quad.vertices[j];
                positions.add(new Vector3f(vertex.position.x * scale, vertex.position.y * scale, vertex.position.z * scale));
                uvs.add(new Vector2f(vertex.u, vertex.v));
                normals.add(normal);
            }

            indices.add(index + 0);
            indices.add(index + 1);
            indices.add(index + 2);

            indices.add(index + 0);
            indices.add(index + 2);
            indices.add(index + 3);

            // quad.vertices.length = 4
            index += quad.vertices.length;
        }

        mesh.bind();
        mesh.storeAttribute(0, toArray3f(positions), 3);
        mesh.storeAttribute(1, toArray2f(uvs), 2);
        mesh.storeAttribute(2, toArray3f(normals), 3);
        mesh.storeIndices(toArrayi(indices));
        mesh.unbind();

        this.hasMesh = true;
    }
}

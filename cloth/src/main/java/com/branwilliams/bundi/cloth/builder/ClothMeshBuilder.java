package com.branwilliams.bundi.cloth.builder;

import com.branwilliams.bundi.cloth.Cloth;
import com.branwilliams.bundi.cloth.ClothParticle;
import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.shader.dynamic.VertexElement;
import com.branwilliams.bundi.engine.shader.dynamic.VertexElements;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.util.MeshUtils;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static com.branwilliams.bundi.engine.util.MeshUtils.toArray2f;
import static com.branwilliams.bundi.engine.util.MeshUtils.toArray3f;

/**
 * @author Brandon
 * @since November 20, 2019
 */
public class ClothMeshBuilder {

    public void rebuildMesh(Mesh mesh, Cloth cloth) {
        for (int x = 0; x < cloth.getParticleSizeX() - 1; x++) {
            for (int y = 0; y < cloth.getParticleSizeY() - 1; y++) {
                Vector3f normal = cloth.calculateTriangleNormal(cloth.getParticle(x + 1, y),
                        cloth.getParticle(x, y), cloth.getParticle(x, y + 1));
                cloth.getParticle(x + 1, y).addToNormal(normal);
                cloth.getParticle(x, y).addToNormal(normal);
                cloth.getParticle(x, y + 1).addToNormal(normal);

                normal = cloth.calculateTriangleNormal(cloth.getParticle(x + 1, y + 1),
                        cloth.getParticle(x + 1, y), cloth.getParticle(x, y + 1));
                cloth.getParticle(x + 1, y + 1).addToNormal(normal);
                cloth.getParticle(x + 1, y).addToNormal(normal);
                cloth.getParticle(x, y + 1).addToNormal(normal);
            }
        }

//        int vertexCount = cloth.getWidth() * cloth.getHeight();
        List<Vector3f> positions = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector3f> tangents = new ArrayList<>();
        List<Vector2f> uvs = new ArrayList<>();

        float uIncrement = (1F / (float) cloth.getParticleSizeY());
        float vIncrement = (1F / (float) cloth.getParticleSizeY());
        for (int x = 0; x < cloth.getParticleSizeX() - 1; x++) {
            for (int y = 0; y < cloth.getParticleSizeY() - 1; y++) {
                float u = (float) x / (float) cloth.getParticleSizeX();
                float v = (float) y / (float) cloth.getParticleSizeY();
                float s = Math.min(1F, u + uIncrement);
                float t = Math.min(1F, v + vIncrement);

                // front faces
                addTriangle(positions, normals, tangents, uvs,
                        cloth.getParticle(x + 1, y), new Vector2f(s, v),
                        cloth.getParticle(x, y), new Vector2f(u, v),
                        cloth.getParticle(x, y + 1), new Vector2f(u, t));
                addTriangle(positions, normals, tangents, uvs,
                        cloth.getParticle(x + 1, y + 1), new Vector2f(s, t),
                        cloth.getParticle(x + 1, y), new Vector2f(s, v),
                        cloth.getParticle(x, y + 1), new Vector2f(u, t));

                // back faces
                addTriangle(positions, normals, tangents, uvs,
                        cloth.getParticle(x, y + 1), new Vector2f(u, t),
                        cloth.getParticle(x, y), new Vector2f(u, v),
                        cloth.getParticle(x + 1, y), new Vector2f(s, v));
                addTriangle(positions, normals, tangents, uvs,
                        cloth.getParticle(x, y + 1), new Vector2f(u, t),
                        cloth.getParticle(x + 1, y), new Vector2f(s, v),
                        cloth.getParticle(x + 1, y + 1), new Vector2f(s, t));
            }
        }

        mesh.bind();
        mesh.storeAttribute(mesh.getVertexFormat().getElementIndex(VertexElements.POSITION), toArray3f(positions),
                VertexElements.POSITION.getSize());
        if (mesh.getVertexFormat().hasElement(VertexElements.NORMAL)) {
            mesh.storeAttribute(mesh.getVertexFormat().getElementIndex(VertexElements.NORMAL), toArray3f(normals),
                    VertexElements.NORMAL.getSize());
        }
        if (mesh.getVertexFormat().hasElement(VertexElements.TANGENT)) {
            mesh.storeAttribute(mesh.getVertexFormat().getElementIndex(VertexElements.TANGENT), toArray3f(tangents),
                    VertexElements.TANGENT.getSize());
        }
        if (mesh.getVertexFormat().hasElement(VertexElements.UV)) {
            mesh.storeAttribute(mesh.getVertexFormat().getElementIndex(VertexElements.UV), toArray2f(uvs),
                    VertexElements.UV.getSize());
        }
        mesh.setVertexCount(positions.size());
        mesh.unbind();
    }

    private void addTriangle(List<Vector3f> positions, List<Vector3f> normals, List<Vector3f> tangents,
                             List<Vector2f> uvs,
                             ClothParticle particle1, Vector2f uv1,
                             ClothParticle particle2, Vector2f uv2,
                             ClothParticle particle3, Vector2f uv3) {
        Vector3f tangent = MeshUtils.calculateTangent(particle1.getPosition(), particle2.getPosition(),
                particle3.getPosition(), uv1, uv2, uv3);

        positions.add(particle1.getPosition());
        normals.add(particle1.getAccumulatedNormal().normalize());
        tangents.add(tangent);
        uvs.add(uv1);

        positions.add(particle2.getPosition());
        normals.add(particle2.getAccumulatedNormal().normalize());
        tangents.add(tangent);
        uvs.add(uv2);

        positions.add(particle3.getPosition());
        normals.add(particle3.getAccumulatedNormal().normalize());
        tangents.add(tangent);
        uvs.add(uv3);
    }

}

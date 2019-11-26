package com.branwilliams.bundi.cloth;

import com.branwilliams.bundi.engine.mesh.Mesh;
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

    public void buildMesh(Cloth cloth) {
        cloth.setMesh(new Mesh());
        rebuildMesh(cloth);
    }

    public void rebuildMesh(Cloth cloth) {
        for (int x = 0; x < cloth.getParticleSizeX() - 1; x++) {
            for (int y = 0; y < cloth.getParticleSizeY() - 1; y++) {
                Vector3f normal = cloth.calculateTriangleNormal(cloth.getParticle(x + 1, y),cloth.getParticle(x, y),cloth.getParticle(x, y + 1));
                cloth.getParticle(x + 1, y).addToNormal(normal);
                cloth.getParticle(x, y).addToNormal(normal);
                cloth.getParticle(x, y + 1).addToNormal(normal);

                normal = cloth.calculateTriangleNormal(cloth.getParticle(x + 1, y + 1),cloth.getParticle(x + 1, y),cloth.getParticle(x, y + 1));
                cloth.getParticle(x + 1, y + 1).addToNormal(normal);
                cloth.getParticle(x + 1, y).addToNormal(normal);
                cloth.getParticle(x, y + 1).addToNormal(normal);
            }
        }

//        int vertexCount = cloth.getWidth() * cloth.getHeight();
        List<Vector3f> positions = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector2f> uvs = new ArrayList<>();

        for (int x = 0; x < cloth.getParticleSizeX() - 1; x++) {
            for (int y = 0; y < cloth.getParticleSizeY() - 1; y++) {
                float u = (float) x / (float) cloth.getParticleSizeX();
                float v = (float) y / (float) cloth.getParticleSizeY();
                // front faces
                addVertex(positions, normals, uvs, u, v, cloth.getParticle(x + 1, y), cloth.getParticle(x, y), cloth.getParticle(x, y + 1));
                addVertex(positions, normals, uvs, u, v, cloth.getParticle(x + 1, y + 1), cloth.getParticle(x + 1, y), cloth.getParticle(x, y + 1));

                // back faces
                addVertex(positions, normals, uvs, u, v, cloth.getParticle(x, y + 1), cloth.getParticle(x, y), cloth.getParticle(x + 1, y));
                addVertex(positions, normals, uvs, u, v, cloth.getParticle(x, y + 1), cloth.getParticle(x + 1, y), cloth.getParticle(x + 1, y + 1));
            }
        }

        Mesh mesh = cloth.getMesh();

        mesh.bind();
        mesh.storeAttribute(0, toArray3f(positions), 3);
        mesh.storeAttribute(1, toArray3f(normals), 3);
        mesh.storeAttribute(2, toArray2f(uvs), 2);
        mesh.setVertexCount(positions.size());
        mesh.unbind();
    }

    private void addVertex(List<Vector3f> positions, List<Vector3f> normals, List<Vector2f> uvs, float u, float v,
                           ClothParticle particle1, ClothParticle particle2, ClothParticle particle3) {
        positions.add(particle1.getPosition());
        normals.add(particle1.getAccumulatedNormal().normalize());
        uvs.add(new Vector2f(u, v));

        positions.add(particle2.getPosition());
        normals.add(particle2.getAccumulatedNormal().normalize());
        uvs.add(new Vector2f(u, v));

        positions.add(particle3.getPosition());
        normals.add(particle3.getAccumulatedNormal().normalize());
        uvs.add(new Vector2f(u, v));
    }

    private class ClothVertex {
        private Vector3f position;
        private Vector3f normal;

        public ClothVertex(Vector3f position, Vector3f normal) {
            this.position = position;
            this.normal = normal;
        }

        public Vector3f getPosition() {
            return position;
        }

        public void setPosition(Vector3f position) {
            this.position = position;
        }

        public Vector3f getNormal() {
            return normal;
        }

        public void setNormal(Vector3f normal) {
            this.normal = normal;
        }
    }

}

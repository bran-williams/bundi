package com.branwilliams.bundi.cloth;

import com.branwilliams.bundi.engine.mesh.Mesh;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static com.branwilliams.bundi.engine.util.MeshUtils.toArray3f;

/**
 * @author Brandon
 * @since November 20, 2019
 */
public class ClothMeshBuilder {

    public void buildMesh(Cloth cloth) {
        rebuildMesh(cloth, new Mesh());
    }

    public void rebuildMesh(Cloth cloth, Mesh mesh) {
        for (int x = 0; x < cloth.getWidth() - 1; x++) {
            for (int y = 0; y < cloth.getHeight() - 1; y++) {
                Vector3f normal = cloth.calculateTriangleNormal(cloth.getParticle(x+1,y),cloth.getParticle(x,y),cloth.getParticle(x,y+1));
                cloth.getParticle(x+1,y).addToNormal(normal);
                cloth.getParticle(x,y).addToNormal(normal);
                cloth.getParticle(x,y+1).addToNormal(normal);

                normal = cloth.calculateTriangleNormal(cloth.getParticle(x+1,y+1),cloth.getParticle(x+1,y),cloth.getParticle(x,y+1));
                cloth.getParticle(x+1,y+1).addToNormal(normal);
                cloth.getParticle(x+1,y).addToNormal(normal);
                cloth.getParticle(x,y+1).addToNormal(normal);
            }
        }

//        int vertexCount = cloth.getWidth() * cloth.getHeight();
        List<Vector3f> positions = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();

        for (int x = 0; x < cloth.getWidth() - 1; x++) {
            for (int y = 0; y < cloth.getHeight() - 1; y++) {
                addVertex(positions, normals, cloth.getParticle(x+1,y),cloth.getParticle(x,y),cloth.getParticle(x,y+1));
                addVertex(positions, normals, cloth.getParticle(x+1,y+1),cloth.getParticle(x+1,y),cloth.getParticle(x,y+1));
            }
        }

        mesh.bind();
        if (mesh.hasAttribute(0))
            mesh.updateAttribute(0, toArray3f(positions), 0);
        else
            mesh.storeAttribute(0, toArray3f(positions), 3);
//        if (mesh.hasAttribute(1))
//            mesh.updateAttribute(1, toArray3f(normals), 0);
//        else
//            mesh.storeAttribute(1, toArray3f(normals), 3);
        mesh.unbind();
    }

    private void addVertex(List<Vector3f> positions, List<Vector3f> normals,
                           ClothParticle particle1, ClothParticle particle2, ClothParticle particle3) {
        positions.add(particle1.getPosition());
        normals.add(particle1.getAccumulatedNormal().normalize());
        positions.add(particle2.getPosition());
        normals.add(particle2.getAccumulatedNormal().normalize());
        positions.add(particle3.getPosition());
        normals.add(particle3.getAccumulatedNormal().normalize());
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

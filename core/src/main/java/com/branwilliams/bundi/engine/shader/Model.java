package com.branwilliams.bundi.engine.shader;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.shader.dynamic.VertexElement;

import java.util.Arrays;

/**
 * Created by Brandon Williams on 10/30/2018.
 */
public class Model implements Destructible {

    private Mesh[] meshes;

    private Material[] material;

    public Model(Mesh mesh, Material material) {
        this.meshes = new Mesh[] { mesh };
        this.material = new Material[] { material };
    }

    public Model(Mesh[] meshes, Material[] material) {
        this.meshes = meshes;
        this.material = material;
    }

    public Mesh[] getMeshes() {
        return meshes;
    }

    public Material[] getMaterial() {
        return material;
    }

    public void setMeshes(Mesh... meshes) {
        this.meshes = meshes;
    }

    public void setMaterial(Material... material) {
        this.material = material;
    }

    @Override
    public void destroy() {
        for (Mesh mesh : meshes) {
            mesh.destroy();
        }
    }

    @Override
    public String toString() {
        return "Model{" +
                "meshes=" + Arrays.toString(meshes) +
                ", material=" + Arrays.toString(material) +
                '}';
    }
}

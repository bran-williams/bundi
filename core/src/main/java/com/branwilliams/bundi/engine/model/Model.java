package com.branwilliams.bundi.engine.model;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.shader.Material;
import com.branwilliams.bundi.engine.shader.dynamic.VertexElement;
import com.branwilliams.bundi.engine.shape.AABB;
import com.branwilliams.bundi.engine.shape.Shape3f;

import java.util.*;

/**
 * Created by Brandon Williams on 10/30/2018.
 */
public class Model implements Destructible {

    private Map<Material, List<Mesh>> data;

    public Model() {
        data = new HashMap<>();
    }

    public Model(Mesh mesh, Material material) {
        this();
        List<Mesh> meshes = new ArrayList<>();
        meshes.add(mesh);
        data.put(material, meshes);
    }

    public Model(Map<Material, List<Mesh>> data) {
        this.data = data;
    }

//    public void addMesh(Material material, Mesh mesh) {
//
//    }

    public Map<Material, List<Mesh>> getData() {
        return data;
    }

    @Override
    public void destroy() {
        for (Map.Entry<Material, List<Mesh>> entry : data.entrySet()) {
            entry.getKey().destroy();

            for (Mesh mesh : entry.getValue()) {
                mesh.destroy();
            }
        }
    }

}

package com.branwilliams.bundi.engine.skybox;

import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.material.Material;

/**
 * Created by Brandon Williams on 7/18/2018.
 */
public class Skybox {

    private Mesh mesh;

    private Material skyboxMaterial;

    public Skybox(float size, Material skyboxMaterial) {
        this(new SkyboxMesh(size), skyboxMaterial);
    }

    public Skybox(Mesh skyboxMesh, Material skyboxMaterial) {
        this.mesh = skyboxMesh;
        this.skyboxMaterial = skyboxMaterial;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    public Material getMaterial() {
        return skyboxMaterial;
    }

    public void setMaterial(Material skyboxMaterial) {
        this.skyboxMaterial = skyboxMaterial;
    }
}


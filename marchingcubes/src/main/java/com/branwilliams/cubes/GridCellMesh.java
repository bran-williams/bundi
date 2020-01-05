package com.branwilliams.cubes;

import com.branwilliams.bundi.engine.mesh.Mesh;

import static org.lwjgl.opengl.GL11.GL_POINTS;

public class GridCellMesh {

    private Mesh mesh;

    public GridCellMesh() {

    }

    public void init() {
        this.mesh = new Mesh();
        this.mesh.bind();
        this.mesh.initializeAttribute(0, 3, 3 * 4);
        this.mesh.setRenderMode(GL_POINTS);
        this.mesh.unbind();
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }
}

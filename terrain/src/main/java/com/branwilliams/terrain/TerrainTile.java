package com.branwilliams.terrain;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.material.Material;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.terrain.generator.TerrainVertex;
import org.joml.Vector3f;

/**
 * @author Brandon
 * @since August 30, 2019
 */
public class TerrainTile implements Destructible {

    private Transformable transform;

    private Mesh mesh;

    private Material material;

    private TerrainVertex[] heightmap;

    private int width;

    private int depth;

    public TerrainTile(Transformable transform, Material material) {
        this(null, transform, null, material);
    }

    public TerrainTile(TerrainVertex[] heightmap, Transformable transform, Mesh mesh, Material material) {
        this.heightmap = heightmap;
        this.transform = transform;
        this.mesh = mesh;
        this.material = material;
    }

    public Vector3f getNormal(int x, int z) {
        return new Vector3f();
    }

    public float getHeight(int x, int z) {
        return getVertex(x, z).getPosition().y;
    }

    public TerrainVertex getVertex(int x, int z) {
        int index = getIndex(x, z);
        return heightmap[index];
    }

    public int getIndex(int x, int z) {
        // clamp the x, z positions.
        x = Mathf.clamp(x, 0, width);
        z = Mathf.clamp(z, 0, depth);

        int index = (x + z * width);
        return Mathf.clamp(index, 0, heightmap.length - 1);
    }

    public TerrainVertex[] getHeightmap() {
        return heightmap;
    }

    public void setHeightmap(TerrainVertex[] heightmap, int width, int depth) {
        this.heightmap = heightmap;
        this.width = width;
        this.depth = depth;
    }

    public Transformable getTransform() {
        return transform;
    }

    public void setTransform(Transformable transform) {
        this.transform = transform;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public int getWidth() {
        return width;
    }

    public int getDepth() {
        return depth;
    }

    @Override
    public void destroy() {
        this.mesh.destroy();
    }

}

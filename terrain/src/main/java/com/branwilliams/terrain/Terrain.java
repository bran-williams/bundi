package com.branwilliams.terrain;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.shader.Material;
import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.engine.shape.AABB;

import java.util.*;

/**
 * Created by Brandon Williams on 11/4/2018.
 */
public class Terrain {

    /**
     * Level-of-detail levels (one being highest resolution, six lowest resolution).
     * */
    public enum LOD {
        ZERO(0), ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6);

        public final int level;

        LOD(int level) {
            this.level = level;
        }

        /**
         *
         *
         */
        public int getIncrement() {
            return level == 0 ? 1 : 2 * (level);
        }

        /** Calculates the next LOD from this LOD.
         * Will wrap back to the first LOD from the last.
         *
         * @return The next LOD level.
         * */
        public LOD next() {
            if (this.ordinal() == LOD.values().length - 1) {
                return LOD.values()[0];
            } else {
                return LOD.values()[this.ordinal() + 1];
            }
        }
    }

    private Map<LOD, List<TerrainTile>> terrains;

    public Terrain() {
        this.terrains = new HashMap<>();
    }

    public void add(LOD lod, TerrainTile terrainTile) {
        List<TerrainTile> terrainsList = terrains.computeIfAbsent(lod, (lod0) -> new ArrayList<>());
        terrainsList.add(terrainTile);
    }

    public List<TerrainTile> clearLOD(LOD lod) {
        return terrains.remove(lod);
    }

    public boolean remove(TerrainTile terrainTile) {
        boolean removed = false;
        for (List<TerrainTile> otherTerrainTiles : terrains.values()) {
            if (otherTerrainTiles.remove(terrainTile)) {
                removed = true;
            }
        }
        return removed;
    }

    /**
     * This function will run quite slow.
     * */
    public boolean remove(Collection<TerrainTile> terrainTileCollection) {
        boolean removed = false;
        for (List<TerrainTile> otherTerrainTiles : terrains.values()) {
            if (otherTerrainTiles.removeAll(terrainTileCollection)) {
                removed = true;
            }
        }
        return removed;
    }

    public List<TerrainTile> getAllTerrains() {
        List<TerrainTile> all = new ArrayList<>();
        for (List<TerrainTile> otherTerrainTiles : terrains.values()) {
            all.addAll(otherTerrainTiles);
        }
        return all;
    }

    public Map<LOD, List<TerrainTile>> getTerrains() {
        return terrains;
    }

    public void clear() {
        terrains.clear();
    }

    public void destroyTerrains() {
        for (List<TerrainTile> otherTerrainTiles : terrains.values()) {
            otherTerrainTiles.forEach(TerrainTile::destroy);
        }
    }

    public boolean isEmpty() {
        return this.terrains.isEmpty();
    }

    public static class TerrainTile implements Destructible {

        private Transformable transform;

        private Mesh mesh;

        private Material material;

        private AABB collisionBox;

        private float[][] heightmap;

        public TerrainTile(float[][] heightmap, Transformable transform, Mesh mesh, Material material) {
            this.heightmap = heightmap;
            this.transform = transform;
            this.mesh = mesh;
            this.material = material;
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

        public AABB getCollisionBox() {
            return collisionBox;
        }

        public void setCollisionBox(AABB collisionBox) {
            this.collisionBox = collisionBox;
        }

        @Override
        public void destroy() {
            this.mesh.destroy();
        }

    }
}

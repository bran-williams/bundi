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

    private List<TerrainTile> terrainTiles;

    public Terrain() {
        this.terrainTiles = new ArrayList<>();
    }

    public void addTile(TerrainTile terrainTile) {
        terrainTiles.add(terrainTile);
    }

    public void removeTime(TerrainTile terrainTile) {
        terrainTiles.remove(terrainTile);
    }
}

package com.branwilliams.terrain;

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

package com.branwilliams.frogger;

import org.jetbrains.annotations.NotNull;

public class Tile implements Comparable<Tile> {

    public static final int EMPTY_TILE_ID = -1;

    public static final Tile EMPTY = new Tile(EMPTY_TILE_ID);

    private int tileId;

    public Tile(int tileId) {
        this.tileId = tileId;
    }

    public int getTileId() {
        return tileId;
    }

    public void setTileId(int tileId) {
        this.tileId = tileId;
    }

    @Override
    public int compareTo(@NotNull Tile o) {
        return Integer.compare(this.tileId, o.tileId);
    }
}

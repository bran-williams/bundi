package com.branwilliams.frogger;

import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.util.Grid2i;

public class Tilemap {

    private final int tileWidth;

    private final int tileHeight;

    private final Grid2i<Tile> tiles;

    private Mesh mesh;

    private SpriteAtlas spriteAtlas;

    public Tilemap(int width, int height, int tileWidth, int tileHeight) {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.tiles = new Grid2i<>(Tile[]::new, width, height);
    }

    public Tile getTile(float x, float y) {
        return tiles.getValue(toTileX(x), toTileY(y));
    }

    public Tile setEmpty(float x, float y) {
        Tile tile = getTile(x, y);
        this.tiles.setValue(null, toTileX(x), toTileY(y));
        return tile;
    }
    public void setTile(int spriteIndex, float x, float y) {
        this.tiles.setValue(new Tile(spriteIndex), toTileX(x), toTileY(y));
    }

    public int toTileX(float x) {
        return (int) Math.max(x / tileWidth, 0F);
    }

    public int toTileY(float y) {
        return (int) Math.max(y / tileHeight, 0F);
    }

    public Grid2i<Tile> getTiles() {
        return tiles;
    }

    public int getWidth() {
        return tiles.getWidth();
    }

    public int getHeight() {
        return tiles.getHeight();
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    public SpriteAtlas getSpriteAtlas() {
        return spriteAtlas;
    }

    public void setSpriteAtlas(SpriteAtlas spriteAtlas) {
        this.spriteAtlas = spriteAtlas;
    }
}

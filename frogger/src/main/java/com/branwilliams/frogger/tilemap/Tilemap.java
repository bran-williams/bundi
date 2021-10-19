package com.branwilliams.frogger.tilemap;

import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.shape.AABB2f;
import com.branwilliams.bundi.engine.util.Grid2i;
import com.branwilliams.bundi.engine.util.Mathf;

public class Tilemap {

    public interface TileConsumer {
        void accept(int x, int y, Tile tile);
    }

    private final int tileWidth;

    private final int tileHeight;

    private final Grid2i<Tile> tiles;

    private Mesh mesh;

    private SpriteAtlas spriteAtlas;

    public Tilemap(int width, int height, int tileWidth, int tileHeight) {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.tiles = new Grid2i<>(Tile.class, width, height);
    }

    public void forTilesInRange(AABB2f range, TileConsumer consumer) {
        forTilesInRange(range.getMinX(), range.getMinY(), range.getMaxX(), range.getMaxY(), consumer);
    }

    public void forTilesInRange(float x, float y, float x1, float y1, TileConsumer consumer) {
        forTilesInRange(Mathf.floor(toTileXf(x)), Mathf.floor(toTileYf(y)), Mathf.ceil(toTileXf(x1)),
                Mathf.ceil(toTileYf(y1)), consumer);
    }

    public void forTilesInRange(int x, int y, int x1, int y1, TileConsumer consumer) {
        int startX = Math.max(0, x);
        int startY = Math.max(0, y);
        int remainingWidth = Math.max(0, tiles.getWidth() - startX);
        int remainingHeight = Math.max(0, tiles.getHeight() - startY);
        x1 = Math.min(x1, remainingWidth);
        y1 = Math.min(y1, remainingHeight);
        for (int i = startX; i < x1; i++) {
            for (int j = startY; j < y1; j++) {
                Tile tile = tiles.getValue(i, j);
                if (tile != null) {
                    consumer.accept(i, j, tile);
                }
            }
        }
    }

    public Tile getTile(float x, float y) {
        return getTile(toTileX(x), toTileY(y));
    }

    public Tile getTile(int x, int y) {
        return tiles.getValue(x, y);
    }

    public Tile setEmpty(float x, float y) {
        return setEmpty(toTileX(x), toTileY(y));
    }

    public Tile setEmpty(int x, int y) {
        Tile tile = getTile(x, y);
        this.tiles.setValue(null, x, y);
        return tile;
    }

    public void setTile(int spriteIndex, float x, float y) {
        this.tiles.setValue(new Tile(spriteIndex), toTileX(x), toTileY(y));
    }

    public float toTileXf(float x) {
        return Math.max(x / (float) tileWidth, 0F);
    }

    public float toTileYf(float y) {
        return Math.max(y / (float) tileHeight, 0F);
    }

    public int toTileX(float x) {
        return (int) toTileXf(x);
    }

    public int toTileY(float y) {
        return (int) toTileYf(y);
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

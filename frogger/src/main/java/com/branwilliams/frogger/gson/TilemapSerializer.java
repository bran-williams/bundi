package com.branwilliams.frogger.gson;

import com.branwilliams.bundi.engine.util.Grid2i;
import com.branwilliams.frogger.Tile;
import com.branwilliams.frogger.Tilemap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import static com.branwilliams.frogger.Tile.EMPTY_TILE_ID;

public class TilemapSerializer implements JsonSerializer<Tilemap> {

    @Override
    public JsonElement serialize(Tilemap src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject root = new JsonObject();

        root.addProperty("width", src.getWidth());
        root.addProperty("height", src.getHeight());
        root.addProperty("tileWidth", src.getTileWidth());
        root.addProperty("tileHeight", src.getTileHeight());

        saveTilemapWorldData(src, root);

        return root;
    }

    private void saveTilemapWorldData(Tilemap tilemap, JsonObject root) {
        JsonObject layer0 = new JsonObject();

        Grid2i<Tile> tiles = tilemap.getTiles();

        // for each row...
        for (int y = 0; y < tiles.getHeight(); y++) {
            String rowY = "";

            // formula for run-length-encoding (ty internet)
            for (int x = 0; x < tiles.getWidth(); x++) {

                // Count occurrences of current tile in a row
                int count = 1;
                while (x < tiles.getWidth() - 1 &&
                        getTileId(tiles, x, y) == getTileId(tiles, x + 1, y)) {
                    count++;
                    x++;
                }

                // "encode" step
                rowY += getTileId(tiles, x, y) + "t" + count + "c";
            }

            layer0.addProperty(String.valueOf(y), rowY);
        }

        root.add("layer0", layer0);
    }

    private int getTileId(Grid2i<Tile> tiles, int x, int y) {
        Tile tile = tiles.getValue(x, y);
        return tile == null ? EMPTY_TILE_ID : tile.getTileId();
    }
}

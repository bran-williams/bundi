package com.branwilliams.frogger.gson;

import com.branwilliams.bundi.engine.util.Grid2i;
import com.branwilliams.frogger.Tile;
import com.branwilliams.frogger.Tilemap;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Scanner;

import static com.branwilliams.frogger.Tile.EMPTY_TILE_ID;

public class TilemapDeserializer implements JsonDeserializer<Tilemap> {

    @Override
    public Tilemap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        JsonObject root = json.getAsJsonObject();
        int width = root.get("width").getAsInt();
        int height = root.get("height").getAsInt();
        int tileWidth = root.get("tileWidth").getAsInt();
        int tileHeight = root.get("tileHeight").getAsInt();
        Tilemap tilemap = new Tilemap(width, height, tileWidth, tileHeight);

        loadTilemapWorldData(tilemap, root);

        return tilemap;
    }

    private void loadTilemapWorldData(Tilemap tilemap, JsonObject root) {
        Grid2i<Tile> tiles = tilemap.getTiles();

        JsonObject layer0 = root.getAsJsonObject("layer0");

        for (int y = 0; y < tilemap.getHeight(); y++) {
            JsonElement rowElement = layer0.get(String.valueOf(y));
            if (rowElement != null) {
                String rowData = rowElement.getAsString();
                readRowData(tiles, rowData, y);
            }
        }
    }

    private void readRowData(Grid2i<Tile> tiles, String rowData, int y) {
        Scanner scanner = new Scanner(rowData);
        scanner.useDelimiter("[tc]");

        int x = 0;
        while (scanner.hasNext()) {
            String tileIdString = scanner.next();
            int tileId = Integer.parseInt(tileIdString);

            if (!scanner.hasNext()) {
                throw new JsonParseException(tileIdString + " in layer0 has no count!");
            }

            String tileCountString = scanner.next();
            int tileCount = Integer.parseInt(tileCountString);

            // skip over these mfers
            if (tileId == EMPTY_TILE_ID) {
                x += tileCount;
            } else {
                for (int i = 0; i < tileCount; i++) {
                    tiles.setValue(new Tile(tileId), x, y);
                    x++;
                }
            }

        }
    }
}

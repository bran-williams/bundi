package com.branwilliams.frogger.builder;

import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.shader.dynamic.VertexElements;
import com.branwilliams.bundi.engine.util.Grid2i;
import com.branwilliams.frogger.Tile;
import com.branwilliams.frogger.Tilemap;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static com.branwilliams.bundi.engine.util.MeshUtils.toArray2f;
import static com.branwilliams.bundi.engine.util.MeshUtils.toArray3f;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;

public class TilemapMeshBuilder {

    public Mesh buildTilemapMesh(Tilemap tilemap) {

        Mesh mesh = new Mesh();
        buildQuad(mesh, 0);

        tilemap.setMesh(mesh);

        return rebuildTilemapMesh(tilemap, mesh);
    }

    private void buildQuad(Mesh mesh, int index) {
        List<Vector2f> positions = new ArrayList<>();
        positions.add(new Vector2f(0, 0));
        positions.add(new Vector2f(1, 0));
        positions.add(new Vector2f(0, 1));
        positions.add(new Vector2f(1, 1));

        mesh.bind();
        mesh.setRenderMode(GL_TRIANGLE_STRIP);
        mesh.setVertexCount(positions.size());
        mesh.storeAttribute(index, toArray2f(positions), VertexElements.POSITION_2D.getSize());
//        mesh.setAttributeDivisor(0, 0);
    }

    public Mesh rebuildTilemapMesh(Tilemap tilemap, Mesh mesh) {
        List<Vector3f> instanceData = new ArrayList<>();

        Grid2i<Tile> tiles = tilemap.getTiles();

        for (int x = 0; x < tiles.getWidth(); x++) {
            for (int y = 0; y < tiles.getHeight(); y++) {
                Tile tile = tiles.getValue(x, y);

                if (tile == null)
                    continue;

                instanceData.add(new Vector3f(x * tilemap.getTileWidth(), y * tilemap.getTileHeight(),
                        tile.getTileId()));
            }
        }

        mesh.bind();
        mesh.storeAttribute(1, toArray3f(instanceData), 3);
        mesh.setAttributeDivisor(1, 1);
        mesh.setInstanceCount(instanceData.size());
        mesh.unbind();

        return mesh;
    }

}

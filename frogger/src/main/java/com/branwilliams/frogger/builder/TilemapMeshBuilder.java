package com.branwilliams.frogger.builder;

import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.shader.dynamic.VertexElements;
import com.branwilliams.bundi.engine.shape.AABB2f;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.frogger.Camera2D;
import com.branwilliams.frogger.Tilemap;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static com.branwilliams.bundi.engine.util.MeshUtils.toArray2f;
import static com.branwilliams.bundi.engine.util.MeshUtils.toArray3f;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;

public class TilemapMeshBuilder {

    private final int SCREEN_TILE_PADDING = 1;

    public Mesh buildTilemapMesh(Tilemap tilemap, Camera2D camera) {
        Mesh mesh = new Mesh();
        buildQuad(mesh, 0);

        tilemap.setMesh(mesh);

        return rebuildTilemapMesh(tilemap, camera, mesh);
    }

    public Mesh rebuildTilemapMesh(Tilemap tilemap, Camera2D camera, Mesh mesh) {
        List<Vector3f> instanceData = new ArrayList<>();
        AABB2f screenAABB = camera.getScreenAABB();

        int normalizedX = Math.max(0, Mathf.floor(tilemap.toTileX(screenAABB.getMinX())) - SCREEN_TILE_PADDING);
        int normalizedY = Math.max(0, Mathf.floor(tilemap.toTileY(screenAABB.getMinY())) - SCREEN_TILE_PADDING);
        int normalizedX1 = Mathf.ceil(tilemap.toTileX(screenAABB.getMaxX())) + SCREEN_TILE_PADDING;
        int normalizedY1 = Mathf.ceil(tilemap.toTileY(screenAABB.getMaxY())) + SCREEN_TILE_PADDING;

        tilemap.forTilesInRange(normalizedX, normalizedY, normalizedX1, normalizedY1, (x, y, tile) -> {
                    instanceData.add(new Vector3f(x * tilemap.getTileWidth(), y * tilemap.getTileHeight(),
                            tile.getTileId()));
        });

        mesh.bind();
        mesh.storeAttribute(1, toArray3f(instanceData), 3);
        mesh.setAttributeDivisor(1, 1);
        mesh.setInstanceCount(instanceData.size());
        mesh.unbind();

        return mesh;
    }

    public static void buildQuad(Mesh mesh, int index) {
        List<Vector2f> positions = new ArrayList<>();
        positions.add(new Vector2f(0, 0));
        positions.add(new Vector2f(1, 0));
        positions.add(new Vector2f(0, 1));
        positions.add(new Vector2f(1, 1));

        mesh.bind();
        mesh.setRenderMode(GL_TRIANGLE_STRIP);
        mesh.setVertexCount(positions.size());
        mesh.storeAttribute(index, toArray2f(positions), VertexElements.POSITION_2D.getSize());
    }

}

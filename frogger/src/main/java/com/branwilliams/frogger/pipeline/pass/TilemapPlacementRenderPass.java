package com.branwilliams.frogger.pipeline.pass;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.frogger.Camera2D;
import com.branwilliams.frogger.tilemap.Tilemap;
import com.branwilliams.frogger.builder.TilemapMeshBuilder;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.branwilliams.bundi.engine.util.MeshUtils.toArray3f;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class TilemapPlacementRenderPass extends RenderPass<RenderContext> {

    private final Supplier<Integer> currentTileId;

    private final Supplier<Camera2D> camera;

    private final Supplier<Tilemap> tilemap;

    private Mesh tileMesh;

    public TilemapPlacementRenderPass(Supplier<Integer> currentTileId,
                                      Supplier<Camera2D> camera, Supplier<Tilemap> tilemap) {
        this.currentTileId = currentTileId;
        this.camera = camera;
        this.tilemap = tilemap;
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {
        tileMesh = new Mesh();
        TilemapMeshBuilder.buildQuad(tileMesh, 0);
        // NO SHADER, relying on this being after the TilemapRenderPass
    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window, double deltaTime) {
        updateTileMesh(window.getMouseX(), window.getMouseY(), currentTileId.get());
        glActiveTexture(GL_TEXTURE0);
        tilemap.get().getSpriteAtlas().getTexture().bind();
        MeshRenderer.render(tileMesh, null);
    }

    @Override
    public void destroy() {
        super.destroy();
        if (this.tileMesh != null) {
            this.tileMesh.destroy();
        }
    }

    private void updateTileMesh(float mouseX, float mouseY, int tileId) {
        Vector2f adjustedMouse = camera.get().getMouseRelativeToFocalPoint(mouseX, mouseY);

        List<Vector3f> instanceData = new ArrayList<>();
        instanceData.add(new Vector3f(adjustedMouse.x - (adjustedMouse.x % tilemap.get().getTileWidth()),
                adjustedMouse.y  - (adjustedMouse.y % tilemap.get().getTileHeight()), tileId));
        tileMesh.bind();
        tileMesh.storeAttribute(1, toArray3f(instanceData), 3);
        tileMesh.setAttributeDivisor(1, 1);
        tileMesh.setInstanceCount(1);
    }
}

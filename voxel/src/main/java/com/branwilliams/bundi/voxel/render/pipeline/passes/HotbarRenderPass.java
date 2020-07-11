package com.branwilliams.bundi.voxel.render.pipeline.passes;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.voxel.VoxelScene;
import com.branwilliams.bundi.voxel.inventory.Inventory;
import com.branwilliams.bundi.voxel.inventory.Item;
import com.branwilliams.bundi.voxel.inventory.VoxelItem;
import com.branwilliams.bundi.voxel.render.pipeline.VoxelRenderContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Brandon
 * @since January 11, 2020
 */
public class HotbarRenderPass extends RenderPass<VoxelRenderContext> {

    private final VoxelScene scene;

    private Map<Item, Mesh> itemMeshes;

    private DynamicShaderProgram shaderProgram;

    private final Transformable transformable = new Transformation();

    public HotbarRenderPass(VoxelScene scene) {
        this.scene = scene;
        this.itemMeshes = new HashMap<>();
    }

    @Override
    public void init(VoxelRenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            shaderProgram = new DynamicShaderProgram(VertexFormat.POSITION_UV);
        } catch (ShaderInitializationException | ShaderUniformException e) {
            e.printStackTrace();
        }
        transformable.rotateFromEuler(30, 225, 0);
    }

    @Override
    public void render(VoxelRenderContext renderContext, Engine engine, Window window, double deltaTime) {
        shaderProgram.bind();
        shaderProgram.setProjectionMatrix(renderContext.getOrthoProjection());
        drawHotbar(scene.getPlayerState().getInventory(), window);
        ShaderProgram.unbind();
//        System.out.println(transformable);
    }

    public void drawHotbar(Inventory inventory, Window window) {
        float size = 32F;
        float padding = 6F;
        List<Item> items = inventory.getItems();

        float x = window.getWidth() * 0.5F - (items.size() * (size + padding)) * 0.5F;
        float y = window.getHeight() - size - padding;

        for (Item item : items) {
            Mesh mesh = getItemMesh(item);
            x += size + padding;
            drawItemMesh(mesh, x, y);
        }
    }

    private Mesh getItemMesh(Item item) {
        Mesh mesh = itemMeshes.get(item);
        if (mesh == null) {
            if (item instanceof VoxelItem) {
                float minX = -0.25F;
                float maxX = 0.25F;

                float minY = -0.25F;
                float maxY = 0.25F;

                float minZ = -0.25F;
                float maxZ = 0.25F;
                mesh = scene.getVoxelMeshBuilder().buildVoxelMesh(((VoxelItem) item).getVoxel(),
                        minX, maxX, minY, maxY, minZ, maxZ);
                itemMeshes.put(item, mesh);
            }
        }
        return mesh;
    }

    private void drawItemMesh(Mesh mesh, float x, float y) {
        shaderProgram.setModelMatrix(transformable.position(x, y, 0F));
        MeshRenderer.render(mesh, scene.getTexturePack().getMaterial());
    }

    @Override
    public void destroy() {
        super.destroy();
        this.shaderProgram.destroy();
        this.itemMeshes.forEach((item, mesh) -> mesh.destroy());
        this.itemMeshes.clear();
    }
}

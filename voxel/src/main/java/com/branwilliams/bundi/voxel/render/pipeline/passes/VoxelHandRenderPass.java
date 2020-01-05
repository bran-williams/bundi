package com.branwilliams.bundi.voxel.render.pipeline.passes;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.voxel.VoxelScene;
import com.branwilliams.bundi.voxel.inventory.Item;
import com.branwilliams.bundi.voxel.inventory.VoxelItem;
import com.branwilliams.bundi.voxel.render.pipeline.VoxelRenderContext;
import com.branwilliams.bundi.voxel.voxels.Voxel;
import org.joml.Vector3f;

/**
 * @author Brandon
 * @since August 10, 2019
 */
public class VoxelHandRenderPass extends RenderPass<VoxelRenderContext> {

    private final Camera staticCamera = new Camera();

    private final Transformable transformable = new Transformation();

    private final VoxelScene scene;

    private DynamicShaderProgram shaderProgram;

    private Mesh cubeMesh;

    private Voxel currentVoxel = null;

    public VoxelHandRenderPass(VoxelScene scene) {
        this.scene = scene;
    }

    @Override
    public void init(VoxelRenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            shaderProgram = new DynamicShaderProgram(VertexFormat.POSITION_UV, DynamicShaderProgram.VIEW_MATRIX);
        } catch (ShaderInitializationException | ShaderUniformException e) {
            e.printStackTrace();
        }

        cubeMesh = new Mesh();
    }

    @Override
    public void render(VoxelRenderContext renderContext, Engine engine, Window window, double deltaTime) {
        Vector3f handPos = new Vector3f(0.5F, -0.4F, -0.5F);
        Item heldItem = scene.getPlayerState().getInventory().getHeldItem();

        if (heldItem instanceof VoxelItem) {
            Voxel voxel = ((VoxelItem) heldItem).getVoxel();
            shaderProgram.bind();
            shaderProgram.setProjectionMatrix(renderContext.getProjection());
            shaderProgram.setViewMatrix(staticCamera);
            shaderProgram.setModelMatrix(transformable.position(handPos));

            updateCubeMesh(voxel);
            MeshRenderer.render(cubeMesh, scene.getTexturePack().getMaterial());

            ShaderProgram.unbind();
        } else {
            System.out.println("No held item!");
        }
    }

    private void updateCubeMesh(Voxel voxel) {
        if (currentVoxel == voxel) {
            return;
        }
        currentVoxel = voxel;

        float minX = -0.25F;
        float maxX = 0.25F;

        float minY = -0.25F;
        float maxY = 0.25F;

        float minZ = -0.25F;
        float maxZ = 0.25F;

        scene.getVoxelMeshBuilder().rebuildVoxelMesh(currentVoxel, minX, maxX, minY, maxY, minZ, maxZ, cubeMesh);
    }
}

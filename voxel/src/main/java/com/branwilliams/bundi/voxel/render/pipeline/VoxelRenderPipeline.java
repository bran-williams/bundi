package com.branwilliams.bundi.voxel.render.pipeline;

import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.shader.Projection;
import com.branwilliams.bundi.voxel.scene.VoxelScene;
import com.branwilliams.bundi.voxel.render.pipeline.passes.*;

/**
 * @author Brandon
 * @since August 10, 2019
 */
public class VoxelRenderPipeline extends RenderPipeline<VoxelRenderContext> {

    public VoxelRenderPipeline(VoxelScene scene, Projection projection) {
        super(new VoxelRenderContext(projection));
        this.addLast(new ChunkRenderPass(scene));
        this.addLast(new SSAORenderPass(scene));
        this.addLast(new SSAOBlurRenderPass());
        this.addLast(new BloomBlurRenderPass(scene));

        this.addLast(new AtmosphereRenderPass(scene));
        this.addLast(new VoxelPostProcessingRenderPass(scene::getCamera));

        this.addLast(new CowRenderPass(scene, scene::getCamera));

        this.addLast(new VoxelSelectionRenderPass(scene));
        this.addLast(new VoxelCrosshairRenderPass(scene));
        this.addLast(new VoxelHotbarRenderPass(scene));
        this.addLast(new VoxelGuiScreenRenderPass(scene));

//        this.addLast(new BloomViewRenderPass(scene::getCamera));
//        this.addLast(new SSAOViewRenderPass(scene::getCamera));
//        this.addLast(new ChunkBorderRenderPass(scene));
//        this.addLast(new VoxelDebugRenderPass(scene));
    }
}

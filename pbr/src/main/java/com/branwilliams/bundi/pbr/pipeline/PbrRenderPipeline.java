package com.branwilliams.bundi.pbr.pipeline;

import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.shader.Camera;
import com.branwilliams.bundi.engine.shader.Projection;
import com.branwilliams.bundi.pbr.pipeline.passes.PbrGeometryRenderPass;
import com.branwilliams.bundi.pbr.pipeline.passes.PbrLightRenderPass;
import com.branwilliams.bundi.pbr.pipeline.passes.PbrPostProcessingRenderPass;

import java.util.function.Supplier;

public class PbrRenderPipeline extends RenderPipeline {

    public PbrRenderPipeline(Scene scene, Supplier<Camera> camera, Supplier<Float> exposure, Projection projection) {
        super(new PbrRenderContext(projection));
        this.addLast(new PbrGeometryRenderPass(scene, camera));
        this.addLast(new PbrLightRenderPass(scene, camera));
        this.addLast(new PbrPostProcessingRenderPass(exposure));
    }
}

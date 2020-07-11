package com.branwilliams.bundi.voxel.render.pipeline;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.shader.Projection;
import com.branwilliams.bundi.voxel.math.Frustum;

/**
 * @author Brandon
 * @since August 12, 2019
 */
public class VoxelRenderContext extends RenderContext {

    private Projection orthoProjection;

    private Frustum frustum;

    public VoxelRenderContext(Projection worldProjection) {
        super(worldProjection);
    }

    @Override
    public void init(Engine engine, Window window) {
        super.init(engine, window);
        frustum = new Frustum();
        orthoProjection = new Projection(window);
    }

    @Override
    public void windowResized(Window window, int width, int height) {
        super.windowResized(window, width, height);
        this.orthoProjection.update();
    }

    public Frustum getFrustum() {
        return frustum;
    }

    public Projection getOrthoProjection() {
        return orthoProjection;
    }
}

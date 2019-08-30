package com.branwilliams.bundi.voxel.render;

import com.branwilliams.bundi.engine.core.AbstractRenderer;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.voxel.VoxelScene;
import com.branwilliams.bundi.voxel.render.pipeline.VoxelRenderPipeline;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Brandon Williams on 6/24/2018.
 */
public class VoxelRenderer extends AbstractRenderer {

    public VoxelRenderer(VoxelScene scene, VoxelRenderPipeline renderPipeline) {
        super("VoxelRenderer", scene, renderPipeline);
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        super.init(engine, window);
        glClearColor(0.3F, 0.3F, 0.3F, 1F);
        glEnable(GL_CULL_FACE);

        glEnable(GL_DEPTH_TEST);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void render(Engine engine, Window window, double deltaTime) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        super.render(engine, window, deltaTime);
    }

}

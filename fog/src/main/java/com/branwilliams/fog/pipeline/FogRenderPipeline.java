package com.branwilliams.fog.pipeline;

import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.core.pipeline.passes.DisableWireframeRenderPass;
import com.branwilliams.bundi.engine.core.pipeline.passes.EnableWireframeRenderPass;
import com.branwilliams.bundi.engine.material.MaterialFormat;
import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.shader.Camera;
import com.branwilliams.bundi.engine.shader.Environment;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.shader.modular.DynamicEnvironmentRenderPass;
import com.branwilliams.bundi.engine.shader.modular.EnvironmentRenderPass;
import com.branwilliams.bundi.gui.pipeline.GuiRenderPass;
import com.branwilliams.fog.Atmosphere;
import com.branwilliams.fog.FogScene;
import com.branwilliams.fog.pipeline.passes.AtmosphereRenderPass;
import com.branwilliams.fog.pipeline.passes.ParticleRenderPass;

import java.util.function.Supplier;

public class FogRenderPipeline extends RenderPipeline<RenderContext> {

    public FogRenderPipeline(RenderContext renderContext, Scene fogScene, Supplier<Boolean> isWireframe,
                             Supplier<Camera> camera, Supplier<Environment> environment,
                             Supplier<Atmosphere> atmosphere, Supplier<Mesh> skydome) {
        super(renderContext);

        this.addLast(new EnableWireframeRenderPass(isWireframe));
        this.addLast(new DynamicEnvironmentRenderPass(fogScene, camera, environment));
        this.addLast(new AtmosphereRenderPass<>(camera, atmosphere, skydome));
        this.addLast(new ParticleRenderPass(fogScene, camera, environment));
        this.addLast(new DisableWireframeRenderPass(isWireframe));
    }
}

package com.branwilliams.fog.pipeline;

import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.core.pipeline.passes.DisableWireframeRenderPass;
import com.branwilliams.bundi.engine.core.pipeline.passes.EnableWireframeRenderPass;
import com.branwilliams.bundi.engine.material.MaterialFormat;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.shader.modular.ModularRenderPass;
import com.branwilliams.bundi.gui.pipeline.GuiRenderPass;
import com.branwilliams.fog.FogScene;
import com.branwilliams.fog.pipeline.passes.AtmosphereRenderPass;
import com.branwilliams.fog.pipeline.passes.ParticleRenderPass;

public class FogRenderPipeline extends RenderPipeline<RenderContext> {

    public FogRenderPipeline(RenderContext renderContext, FogScene fogScene) {
        super(renderContext);

        this.addLast(new EnableWireframeRenderPass(fogScene::isWireframe));
        this.addLast(new ModularRenderPass(fogScene, fogScene::getCamera, fogScene::getEnvironment,
                VertexFormat.POSITION_UV_NORMAL, MaterialFormat.DIFFUSE_SPECULAR));
        this.addLast(new ModularRenderPass(fogScene, fogScene::getCamera, fogScene::getEnvironment,
                VertexFormat.POSITION_UV_NORMAL_TANGENT, MaterialFormat.DIFFUSE_NORMAL));
        this.addLast(new ModularRenderPass(fogScene, fogScene::getCamera, fogScene::getEnvironment,
                VertexFormat.POSITION_UV_NORMAL_TANGENT, MaterialFormat.DIFFUSE_NORMAL_SPECULAR));
        this.addLast(new ModularRenderPass(fogScene, fogScene::getCamera, fogScene::getEnvironment,
                VertexFormat.POSITION_UV_NORMAL, MaterialFormat.DIFFUSE_VEC4_SPECULAR_VEC4));
        this.addLast(new AtmosphereRenderPass<>(fogScene::getCamera, fogScene::getAtmosphere, fogScene::getSkydome));
        this.addLast(new DisableWireframeRenderPass(fogScene::isWireframe));
        this.addLast(new ParticleRenderPass(fogScene, fogScene::getCamera, fogScene::getEnvironment));
        this.addLast(new GuiRenderPass<>(fogScene, fogScene::getGuiScreenManager));
    }
}

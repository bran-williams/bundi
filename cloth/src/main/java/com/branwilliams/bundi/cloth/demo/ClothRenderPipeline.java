package com.branwilliams.bundi.cloth.demo;

import com.branwilliams.bundi.cloth.pipeline.SphereRenderPass;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.core.pipeline.passes.DisableWireframeRenderPass;
import com.branwilliams.bundi.engine.core.pipeline.passes.EnableWireframeRenderPass;
import com.branwilliams.bundi.engine.shader.modular.ModularRenderPass;
import com.branwilliams.bundi.engine.skybox.SkyboxRenderPass;

import static com.branwilliams.bundi.cloth.demo.ClothScene.*;

public class ClothRenderPipeline extends RenderPipeline<RenderContext> {

    public ClothRenderPipeline(ClothScene clothScene, RenderContext renderContext) {
        super(renderContext);
        addLast(new SkyboxRenderPass<>(clothScene::getCamera, clothScene::getSkybox));
        addLast(new EnableWireframeRenderPass(clothScene::isWireframe));
        addLast(new SphereRenderPass(clothScene::getCamera, clothScene::getSphere));
//        renderPipeline.addLast(new ClothRenderPass(this::getCamera, this));
        addLast(new ModularRenderPass(clothScene, clothScene::getCamera, clothScene::getEnvironment,
                REGULAR_VERTEX_FORMAT, DIFFUSE_MATERIAL_FORMAT));
        addLast(new ModularRenderPass(clothScene, clothScene::getCamera, clothScene::getEnvironment,
                NORMAL_MAPPED_VERTEX_FORMAT, DIFFUSE_NORMAL_MATERIAL_FORMAT));
        addLast(new DisableWireframeRenderPass(clothScene::isWireframe));
    }
}

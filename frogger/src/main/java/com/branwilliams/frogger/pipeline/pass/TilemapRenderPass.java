package com.branwilliams.frogger.pipeline.pass;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.frogger.Tilemap;
import com.branwilliams.frogger.builder.TilemapMeshBuilder;
import com.branwilliams.frogger.pipeline.shader.TilemapShaderProgram;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.branwilliams.bundi.engine.util.MeshUtils.toArray3f;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class TilemapRenderPass extends RenderPass<RenderContext> {

    private final Transformable transform = new Transformation();

    private final Supplier<Vector2f> focalPoint;

    private final Supplier<Tilemap> tilemap;

    private TilemapShaderProgram shaderProgram;

    public TilemapRenderPass(Supplier<Vector2f> focalPoint, Supplier<Tilemap> tilemap) {
        this.focalPoint = focalPoint;
        this.tilemap = tilemap;
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            shaderProgram = new TilemapShaderProgram(engine.getContext());
        } catch (ShaderInitializationException | ShaderUniformException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window, double deltaTime) {
        shaderProgram.bind();
        shaderProgram.setProjectionMatrix(renderContext.getProjection());
        shaderProgram.setModelMatrix(transform.position(-focalPoint.get().x, -focalPoint.get().y, 0F));
        shaderProgram.setTilemap(tilemap.get());

        glActiveTexture(GL_TEXTURE0);
        tilemap.get().getSpriteAtlas().getTexture().bind();

        MeshRenderer.render(tilemap.get().getMesh(), null);
//        ShaderProgram.unbind();
    }

}

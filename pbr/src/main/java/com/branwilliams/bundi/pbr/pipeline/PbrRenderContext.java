package com.branwilliams.bundi.pbr.pipeline;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.shader.Projection;
import com.branwilliams.bundi.engine.texture.Texture;

import static org.lwjgl.opengl.GL13.*;

public class PbrRenderContext extends RenderContext {

    private Projection orthoProjection;

    private GBuffer gbuffer;

    private SceneBuffer sceneBuffer;

    private Mesh renderPassMesh;

    public PbrRenderContext(Projection worldProjection) {
        super(worldProjection);
    }

    @Override
    public void init(Engine engine, Window window) {
        super.init(engine, window);
        this.gbuffer = new GBuffer(window.getWidth(), window.getHeight());
        this.sceneBuffer = new SceneBuffer(gbuffer, window.getWidth(), window.getHeight());
        this.orthoProjection = new Projection(window);

        this.renderPassMesh = new Mesh();
        this.renderPassMesh.bind();
        this.renderPassMesh.storeAttribute(0,
                new float[] {
                        -1.0F, 1.0F,
                        1.0F, 1.0F,
                        -1.0F, -1.0F,
                        1.0F, -1.0F, }, 2);
        this.renderPassMesh.storeIndices(new int[] { 0, 2, 1, 1, 2, 3 });
        this.renderPassMesh.unbind();
    }

    @Override
    public void windowResized(Window window, int width, int height) {
        super.windowResized(window, width, height);
        if (this.gbuffer != null) {
            this.gbuffer.destroy();
        }
        this.gbuffer = new GBuffer(width, height);

        if (this.sceneBuffer != null) {
            this.sceneBuffer.destroy();
        }
        this.sceneBuffer = new SceneBuffer(gbuffer, width, height);

        this.orthoProjection.update();
    }

    /**
     * Binds the GBuffer textures for use in the light render pass.
     * */
    public void bindGbufferTextures() {
        glActiveTexture(GL_TEXTURE0);
        this.gbuffer.getAlbedo().bind();
        glActiveTexture(GL_TEXTURE1);
        this.gbuffer.getNormal().bind();
        glActiveTexture(GL_TEXTURE2);
        this.gbuffer.getDepth().bind();
    }

    /**
     * Unbinds the GBuffer textures.
     * */
    public void unbindGbufferTextures() {
        glActiveTexture(GL_TEXTURE0);
        Texture.unbind(this.gbuffer.getAlbedo());
        glActiveTexture(GL_TEXTURE1);
        Texture.unbind(this.gbuffer.getNormal());
        glActiveTexture(GL_TEXTURE2);
        Texture.unbind(this.gbuffer.getDepth());
    }

    public void bindColorTexture() {
        glActiveTexture(GL_TEXTURE0);
        this.sceneBuffer.getColor().bind();
    }

    public void unbindColorTexture() {
        glActiveTexture(GL_TEXTURE0);
        Texture.unbind(this.sceneBuffer.getColor());
    }

    public Projection getOrthoProjection() {
        return orthoProjection;
    }

    public void setOrthoProjection(Projection orthoProjection) {
        this.orthoProjection = orthoProjection;
    }

    public GBuffer getGBuffer() {
        return gbuffer;
    }

    public void setGBuffer(GBuffer gbuffer) {
        this.gbuffer = gbuffer;
    }

    public SceneBuffer getSceneBuffer() {
        return sceneBuffer;
    }

    public void setSceneBuffer(SceneBuffer sceneBuffer) {
        this.sceneBuffer = sceneBuffer;
    }

    public Mesh getRenderPassMesh() {
        return renderPassMesh;
    }

    public void setRenderPassMesh(Mesh renderPassMesh) {
        this.renderPassMesh = renderPassMesh;
    }
}

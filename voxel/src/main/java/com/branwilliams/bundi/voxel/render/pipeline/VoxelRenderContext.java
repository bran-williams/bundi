package com.branwilliams.bundi.voxel.render.pipeline;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.shader.Projection;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.texture.Texture;
import com.branwilliams.bundi.voxel.math.Frustum;

import static org.lwjgl.opengl.GL13.*;

/**
 * @author Brandon
 * @since August 12, 2019
 */
public class VoxelRenderContext extends RenderContext {

    private Projection orthoProjection;

    private Frustum frustum;

    private VoxelScreenFrameBuffer screenFrameBuffer;

    private Mesh renderPassMesh;

    public VoxelRenderContext(Projection worldProjection) {
        super(worldProjection);
    }

    @Override
    public void init(Engine engine, Window window) {
        super.init(engine, window);
        this.screenFrameBuffer = new VoxelScreenFrameBuffer(window.getWidth(), window.getHeight());

        this.frustum = new Frustum();
        this.orthoProjection = new Projection(window);


        this.renderPassMesh = new Mesh();
        this.renderPassMesh.setVertexFormat(VertexFormat.POSITION_2D);
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
        if (this.screenFrameBuffer != null) {
            this.screenFrameBuffer.destroy();
        }
        this.screenFrameBuffer = new VoxelScreenFrameBuffer(width, height);

        this.orthoProjection.update();
    }

    @Override
    public void destroy() {
        super.destroy();
        this.screenFrameBuffer.destroy();
        this.renderPassMesh.destroy();
    }

    /**
     * Binds the GBuffer textures for use in the light render pass.
     * */
    public void bindScreenFrameBufferTextures() {
        glActiveTexture(GL_TEXTURE0);
        this.screenFrameBuffer.getAlbedo().bind();
        glActiveTexture(GL_TEXTURE1);
        this.screenFrameBuffer.getDepth().bind();
    }

    /**
     * Unbinds the GBuffer textures.
     * */
    public void unbindScreenFrameBufferTextures() {
        glActiveTexture(GL_TEXTURE0);
        Texture.unbind(this.screenFrameBuffer.getAlbedo());
        glActiveTexture(GL_TEXTURE1);
        Texture.unbind(this.screenFrameBuffer.getDepth());
        glActiveTexture(GL_TEXTURE0);
    }

    public Frustum getFrustum() {
        return frustum;
    }

    public Projection getOrthoProjection() {
        return orthoProjection;
    }


    public VoxelScreenFrameBuffer getScreenFrameBuffer() {
        return screenFrameBuffer;
    }

    public Mesh getRenderPassMesh() {
        return renderPassMesh;
    }

}

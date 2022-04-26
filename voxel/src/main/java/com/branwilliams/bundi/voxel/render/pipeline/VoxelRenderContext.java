package com.branwilliams.bundi.voxel.render.pipeline;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.shader.Projection;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.texture.Texture;
import com.branwilliams.bundi.engine.texture.TextureDataf;
import com.branwilliams.bundi.voxel.math.Frustum;
import com.branwilliams.bundi.voxel.render.pipeline.framebuffers.BloomPingPongFrameBuffer;
import com.branwilliams.bundi.voxel.render.pipeline.framebuffers.SSAOBlurFrameBuffer;
import com.branwilliams.bundi.voxel.render.pipeline.framebuffers.SSAOFrameBuffer;
import com.branwilliams.bundi.voxel.render.pipeline.framebuffers.VoxelGBuffer;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import static com.branwilliams.bundi.engine.util.Mathf.lerp;
import static com.branwilliams.bundi.engine.util.MeshUtils.toArray3f;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;

/**
 * @author Brandon
 * @since August 12, 2019
 */
public class VoxelRenderContext extends RenderContext {

    private static final int KERNEL_SIZE = 64;

    private Projection orthoProjection;

    private Frustum frustum;

    private VoxelGBuffer gBuffer;

    private SSAOFrameBuffer ssaoFrameBuffer;

    private SSAOBlurFrameBuffer ssaoBlurFrameBuffer;

    private BloomPingPongFrameBuffer bloomPingFrameBuffer;

    private BloomPingPongFrameBuffer bloomPongFrameBuffer;

    private Texture finalBloomTexture;
    private Texture ssaoNoiseTexture;

    private Vector3f[] ssaoKernel;

    private Mesh renderPassMesh;

    private Random random;

    public VoxelRenderContext(Projection worldProjection) {
        super(worldProjection);
        random = new Random();
    }

    @Override
    public void init(Engine engine, Window window) {
        super.init(engine, window);
        this.gBuffer = new VoxelGBuffer(window.getWidth(), window.getHeight());
        this.ssaoFrameBuffer = new SSAOFrameBuffer(window.getWidth(), window.getHeight());
        this.ssaoBlurFrameBuffer = new SSAOBlurFrameBuffer(window.getWidth(), window.getHeight());
        this.bloomPingFrameBuffer = new BloomPingPongFrameBuffer(window.getWidth(), window.getHeight());
        this.bloomPongFrameBuffer = new BloomPingPongFrameBuffer(window.getWidth(), window.getHeight());

        ssaoNoiseTexture = buildSSAONoiseTexture(random::nextFloat);
        ssaoKernel = buildSSAOKernel(random::nextFloat, KERNEL_SIZE);

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
        if (this.gBuffer != null) {
            this.gBuffer.destroy();
        }
        this.gBuffer = new VoxelGBuffer(width, height);

        if (this.ssaoFrameBuffer != null) {
            this.ssaoFrameBuffer.destroy();
        }
        this.ssaoFrameBuffer = new SSAOFrameBuffer(width, height);

        if (this.ssaoBlurFrameBuffer != null) {
            this.ssaoBlurFrameBuffer.destroy();
        }
        this.ssaoBlurFrameBuffer = new SSAOBlurFrameBuffer(width, height);

        if (this.bloomPingFrameBuffer != null) {
            this.bloomPingFrameBuffer.destroy();
        }
        this.bloomPingFrameBuffer = new BloomPingPongFrameBuffer(width, height);

        if (this.bloomPongFrameBuffer != null) {
            this.bloomPongFrameBuffer.destroy();
        }
        this.bloomPongFrameBuffer = new BloomPingPongFrameBuffer(width, height);


        this.orthoProjection.update();
    }

    private Texture buildSSAONoiseTexture(Supplier<Float> randomFloat) {
        TextureDataf textureData = new TextureDataf(4, 4, 3, GL_RGB);
        Texture.TextureFormat textureFormat = Texture.TextureFormat.of(GL_RGBA32F, GL_RGB, GL_FLOAT);

        textureData.forEachPixel((src, x, y, r, g, b, a) -> {
            src.setPixel(x, y, (randomFloat.get() * 2.0F - 1.0F), (randomFloat.get() * 2.0F - 1.0F), 0F, 0F);
        });

        Texture texture = new Texture(textureData, textureFormat, false);
        texture.bind().nearestFilter().repeatEdges();
        Texture.unbind(texture);

        return texture;
    }

    private Vector3f[] buildSSAOKernel(Supplier<Float> randomFloat, int kernelSize) {
        List<Vector3f> ssaoKernel = new ArrayList<>();
        for (int i = 0; i < kernelSize; ++i) {
            Vector3f sample = new Vector3f(randomFloat.get() * 2.0F - 1.0F,
                    randomFloat.get() * 2.0F - 1.0F,
                    randomFloat.get());

            sample.normalize();
            sample.mul(randomFloat.get());

            float scale = (float) i / kernelSize;
            scale = lerp(0.1F, 1.0F, scale * scale);
            sample.mul(scale);

            ssaoKernel.add(sample);
        }
        return ssaoKernel.toArray(new Vector3f[0]);
    }

    private float lerp(float a, float b, float f) {
        return a + f * (b - a);
    }

    @Override
    public void destroy() {
        super.destroy();
        this.gBuffer.destroy();
        this.ssaoFrameBuffer.destroy();
        this.ssaoBlurFrameBuffer.destroy();
        this.bloomPingFrameBuffer.destroy();
        this.bloomPongFrameBuffer.destroy();
        this.renderPassMesh.destroy();
        this.ssaoNoiseTexture.destroy();
    }

    /**
     * Binds the SSAO blur texture.
     * */
    public void bindTexturesForSSAOBlur() {
        glActiveTexture(GL_TEXTURE0);
        this.ssaoFrameBuffer.getTexture().bind();
    }


    /**
     * Unbinds the SSAO blur texture.
     * */
    public void unbindTexturesForSSAOBlur() {
        glActiveTexture(GL_TEXTURE0);
        Texture.unbind(this.ssaoFrameBuffer.getTexture());
    }

    /**
     * Binds the GBuffer textures for use in the light render pass.
     * */
    public void bindTexturesForPostProcessing() {
        glActiveTexture(GL_TEXTURE0);
        this.gBuffer.getAlbedo().bind();
        glActiveTexture(GL_TEXTURE1);
        this.ssaoBlurFrameBuffer.getTexture().bind();
        glActiveTexture(GL_TEXTURE2);
        this.getFinalBloomTexture().bind();
    }

    /**
     * Unbinds the GBuffer textures.
     * */
    public void unbindTexturesForPostProcessing() {
        glActiveTexture(GL_TEXTURE0);
        Texture.unbind(this.gBuffer.getAlbedo());
        glActiveTexture(GL_TEXTURE1);
        Texture.unbind(this.ssaoFrameBuffer.getTexture());
        glActiveTexture(GL_TEXTURE2);
        Texture.unbind(this.getFinalBloomTexture());
        glActiveTexture(GL_TEXTURE0);
    }

    /**
     * Binds the GBuffer textures for use in the light render pass.
     * */
    public void bindTexturesForSSAO() {
        glActiveTexture(GL_TEXTURE0);
        this.gBuffer.getAlbedo().bind();
        glActiveTexture(GL_TEXTURE1);
        this.gBuffer.getNormal().bind();
        glActiveTexture(GL_TEXTURE2);
        this.gBuffer.getDepth().bind();
        glActiveTexture(GL_TEXTURE3);
        this.ssaoNoiseTexture.bind();
    }

    /**
     * Unbinds the GBuffer textures.
     * */
    public void unbindTexturesForSSAO() {
        glActiveTexture(GL_TEXTURE0);
        Texture.unbind(this.gBuffer.getAlbedo());
        glActiveTexture(GL_TEXTURE1);
        Texture.unbind(this.gBuffer.getNormal());
        glActiveTexture(GL_TEXTURE2);
        Texture.unbind(this.gBuffer.getDepth());
        glActiveTexture(GL_TEXTURE3);
        Texture.unbind(this.ssaoNoiseTexture);
        glActiveTexture(GL_TEXTURE0);
    }

    public Texture getFinalBloomTexture() {
        return finalBloomTexture;
    }

    public void setFinalBloomTexture(Texture finalBloomTexture) {
        this.finalBloomTexture = finalBloomTexture;
    }

    public Texture getSSAONoiseTexture() {
        return ssaoNoiseTexture;
    }

    public Vector3f[] getSSAOKernel() {
        return ssaoKernel;
    }

    public Frustum getFrustum() {
        return frustum;
    }

    public Projection getOrthoProjection() {
        return orthoProjection;
    }

    public VoxelGBuffer getGBuffer() {
        return gBuffer;
    }

    public SSAOFrameBuffer getSSAOFrameBuffer() {
        return ssaoFrameBuffer;
    }

    public SSAOBlurFrameBuffer getSSAOBlurFrameBuffer() {
        return ssaoBlurFrameBuffer;
    }

    public BloomPingPongFrameBuffer getBloomPingFrameBuffer() {
        return bloomPingFrameBuffer;
    }

    public BloomPingPongFrameBuffer getBloomPongFrameBuffer() {
        return bloomPongFrameBuffer;
    }

    public Mesh getRenderPassMesh() {
        return renderPassMesh;
    }

}

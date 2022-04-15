package com.branwilliams.bundi.voxel.render.pipeline;

import com.branwilliams.bundi.engine.shader.FrameBufferObject;
import com.branwilliams.bundi.engine.texture.Texture;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;

/**
 * Created by Brandon Williams on 9/25/2018.
 */
public class VoxelScreenFrameBuffer extends FrameBufferObject {

    public static final int ALBEDO_ATTACHMENT = 0;

    private final Texture albedo, depth;

    public VoxelScreenFrameBuffer(int width, int height) {
        super(width, height);
        this.bind();

        this.albedo = new Texture((ByteBuffer) null, width, height, false, Texture.TextureType.COLOR);
        this.albedo.bind();
        this.albedo.nearestFilter();
        this.bindTexture(albedo, GL_FRAMEBUFFER, ALBEDO_ATTACHMENT);

        this.depth = new Texture((ByteBuffer) null, width, height, false,
                Texture.TextureType.DEPTH_COMPONENT);
        this.depth.bind();
        this.depth.nearestFilter();
//        this.bindDepthStencilTexture(depth, GL_FRAMEBUFFER);
        this.bindDepthTexture(depth, GL_FRAMEBUFFER);

        Texture.unbind(this.depth);
        Texture.unbind();

        drawAlbedoNormal();
        this.checkStatus();
        FrameBufferObject.unbind();
    }

    public void drawAlbedoNormal() {
        drawBuffers(ALBEDO_ATTACHMENT);
    }

    public Texture getAlbedo() {
        return albedo;
    }

    public Texture getDepth() {
        return depth;
    }

    @Override
    public void destroy() {
        super.destroy();
        albedo.destroy();
        depth.destroy();
    }
}
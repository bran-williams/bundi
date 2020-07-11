package com.branwilliams.bundi.pbr.pipeline;

import com.branwilliams.bundi.engine.shader.FrameBufferObject;
import com.branwilliams.bundi.engine.texture.Texture;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL30.*;

/**
 * Created by Brandon Williams on 9/25/2018.
 */
public class GBuffer extends FrameBufferObject {

    private final Texture albedo, normal, depth;

    public GBuffer(int width, int height) {
        super(width, height);
        this.bind();

        this.albedo = new Texture((ByteBuffer) null, width, height, false, Texture.TextureType.COLOR16F);
        this.albedo.bind();
        this.albedo.nearestFilter();
        this.bindTexture(albedo, GL_FRAMEBUFFER, 0);

        this.normal = new Texture((ByteBuffer) null, width, height, false, Texture.TextureType.COLOR16F);
        this.normal.bind();
        this.normal.nearestFilter();
        this.bindTexture(normal, GL_FRAMEBUFFER, 1);

        this.depth = new Texture((ByteBuffer) null, width, height, false, Texture.TextureType.DEPTH32F);
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
        drawBuffers(0, 1);
    }

    public Texture getAlbedo() {
        return albedo;
    }

    public Texture getNormal() {
        return normal;
    }

    public Texture getDepth() {
        return depth;
    }

    @Override
    public void destroy() {
        super.destroy();
        albedo.destroy();
        normal.destroy();
        depth.destroy();
    }
}
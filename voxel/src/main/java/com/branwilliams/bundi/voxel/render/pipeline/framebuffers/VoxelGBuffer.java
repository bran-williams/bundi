package com.branwilliams.bundi.voxel.render.pipeline.framebuffers;

import com.branwilliams.bundi.engine.shader.FrameBufferObject;
import com.branwilliams.bundi.engine.texture.Texture;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;

/**
 * Created by Brandon Williams on 9/25/2018.
 */
public class VoxelGBuffer extends FrameBufferObject {

    public static final int ALBEDO_ATTACHMENT = 0;
    public static final int NORMAL_ATTACHMENT = 1;
    public static final int EMISSION_ATTACHMENT = 2;

    private final Texture albedo, normal, emission, depth;

    public VoxelGBuffer(int width, int height) {
        super(width, height);
        this.bind();

        this.albedo = new Texture((ByteBuffer) null, width, height, false, Texture.TextureType.COLOR);
        this.albedo.bind();
        this.albedo.nearestFilter();
        this.bindTexture(albedo, GL_FRAMEBUFFER, ALBEDO_ATTACHMENT);

        this.normal = new Texture((ByteBuffer) null, width, height, false, Texture.TextureType.COLOR);
        this.normal.bind();
        this.normal.nearestFilter();
        this.bindTexture(normal, GL_FRAMEBUFFER, NORMAL_ATTACHMENT);

        this.emission = new Texture((ByteBuffer) null, width, height, false, Texture.TextureType.COLOR);
        this.emission.bind();
        this.emission.nearestFilter();
        this.bindTexture(emission, GL_FRAMEBUFFER, EMISSION_ATTACHMENT);

        this.depth = new Texture((ByteBuffer) null, width, height, false, Texture.TextureType.DEPTH_COMPONENT);
        this.depth.bind();
        this.depth.nearestFilter();
        this.bindDepthTexture(depth, GL_FRAMEBUFFER);

        Texture.unbind(this.depth);
        Texture.unbind();

        drawAlbedoNormalEmission();
        this.checkStatus();
        FrameBufferObject.unbind();
    }

    public void drawAlbedoNormalEmission() {
        drawBuffers(ALBEDO_ATTACHMENT, NORMAL_ATTACHMENT, EMISSION_ATTACHMENT);
    }

    public Texture getAlbedo() {
        return albedo;
    }

    public Texture getNormal() {
        return normal;
    }

    public Texture getEmission() {
        return emission;
    }

    public Texture getDepth() {
        return depth;
    }

    @Override
    public void destroy() {
        super.destroy();
        albedo.destroy();
        normal.destroy();
        emission.destroy();
        depth.destroy();
    }
}
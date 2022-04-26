package com.branwilliams.bundi.voxel.render.pipeline.framebuffers;

import com.branwilliams.bundi.engine.shader.FrameBufferObject;
import com.branwilliams.bundi.engine.texture.Texture;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;

/**
 * Created by Brandon Williams on 9/25/2018.
 */
public class SSAOBlurFrameBuffer extends FrameBufferObject {

    public static final int TEXTURE_ATTACHMENT = 0;

    private final Texture texture;

    public SSAOBlurFrameBuffer(int width, int height) {
        super(width, height);
        this.bind();

        this.texture = new Texture((FloatBuffer) null, width, height, false,
                Texture.TextureType.COLOR); // Texture.TextureFormat.of(GL_RED, GL_RED, GL_FLOAT)
        this.texture.bind();
        this.texture.nearestFilter();
        this.bindTexture(texture, GL_FRAMEBUFFER, TEXTURE_ATTACHMENT);

        Texture.unbind();

        drawTexture();
        this.checkStatus();
        FrameBufferObject.unbind();
    }

    public void drawTexture() {
        drawBuffers(TEXTURE_ATTACHMENT);
    }

    public Texture getTexture() {
        return texture;
    }

    @Override
    public void destroy() {
        super.destroy();
        texture.destroy();
    }
}
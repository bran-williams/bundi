package com.branwilliams.bundi.water.pipeline;

import com.branwilliams.bundi.engine.shader.FrameBufferObject;
import com.branwilliams.bundi.engine.texture.Texture;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;

/**
 * @author Brandon
 * @since September 03, 2019
 */
public class WaterNormalBuffer extends FrameBufferObject {

    private final Texture normal;

    public WaterNormalBuffer(int width, int height) {
        super(width, height);
        this.bind();

        // TODO Do mip maps work here??
        this.normal = new Texture((ByteBuffer) null, width, height, false, Texture.TextureType.COLOR16F);
        this.normal.bind();
        this.normal.linearFilter(false);
        this.bindTexture(normal, GL_FRAMEBUFFER, 0);

        drawBuffers(0);
        this.checkStatus();
        FrameBufferObject.unbind();
    }

    public Texture getNormal() {
        return normal;
    }

    @Override
    public void destroy() {
        super.destroy();
        normal.destroy();
    }
}

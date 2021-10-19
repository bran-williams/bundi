package com.branwilliams.bundi.pbr.pipeline;

import com.branwilliams.bundi.engine.shader.FrameBufferObject;
import com.branwilliams.bundi.engine.texture.Texture;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL30.*;

/**
 * Created by Brandon Williams on 6/30/2018.
 */
public class SceneBuffer extends FrameBufferObject {

    private static final int COLOR_ATTACHMENT = 0;

    private final Texture color;

    /**
     * @param gbuffer The depth buffer used in gbuffer is shared between the two frame buffer objects.
     * */
    public SceneBuffer(GBuffer gbuffer, int width, int height) {
        super(width, height);
        this.bindForDraw();

        this.color = new Texture((ByteBuffer) null, width, height, false, Texture.TextureType.COLOR32F);
        this.color.bind();
        this.color.nearestFilter();
        this.bindTexture(color, GL_FRAMEBUFFER, COLOR_ATTACHMENT);

        gbuffer.getDepth().bind();
        this.bindDepthTexture(gbuffer.getDepth(), GL_FRAMEBUFFER);
        Texture.unbind(gbuffer.getDepth());
        Texture.unbind();

        drawBuffers(COLOR_ATTACHMENT);
        this.checkStatus();
        FrameBufferObject.unbind();
    }

    public Texture getColor() {
        return color;
    }

    @Override
    public void destroy() {
        super.destroy();
        color.destroy();
    }
}

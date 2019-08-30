package com.branwilliams.bundi.engine.shader;

import com.branwilliams.bundi.engine.core.Destructible;
import org.lwjgl.opengl.GL30;

import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_RGBA;

/**
 * Created by Brandon Williams on 12/29/2017.
 */
public class RenderBuffer implements Destructible {

    /**
     * This enum is used to classify render buffer types.
     * */
    public enum RenderBufferType {
        COLOR, DEPTH
    }

    private int id;

    private final RenderBufferType renderBufferType;

    public RenderBuffer(int width, int height, RenderBufferType renderBufferType) {
        this.id = GL30.glGenRenderbuffers();
        this.renderBufferType = renderBufferType;
        int format = renderBufferType == RenderBufferType.COLOR ? GL_RGBA : GL_DEPTH_COMPONENT;
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, format, width, height);

    }

    /**
     * Binds this render buffer for use.
     * */
    public void bind() {
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, id);
    }

    /**
     * Unbinds this render buffer.
     * */
    public static void unbind() {
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);
    }

    @Override
    public void destroy() {
        GL30.glDeleteRenderbuffers(id);
    }

    public int getId() {
        return id;
    }

    public RenderBufferType getRenderBufferType() {
        return renderBufferType;
    }
}

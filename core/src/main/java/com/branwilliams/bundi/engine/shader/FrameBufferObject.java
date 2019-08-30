package com.branwilliams.bundi.engine.shader;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.texture.Texture;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.*;

/**
 * Wrapper class for OpenGL frame buffer objects.
 * Created by Brandon Williams on 12/28/2017.
 */
public class FrameBufferObject implements Destructible {

    private final int id, width, height;

    public FrameBufferObject(int width, int height) {
        this.id = glGenFramebuffers();
        this.width = width;
        this.height = height;
    }

    /**
     * Checks the status message of this frame buffer object.
     *
     * @return True if this frame buffer object status check was complete.
     * */
    public boolean checkStatus() {
        this.bind();
        int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if (status != GL_FRAMEBUFFER_COMPLETE) {
            String statusMessage = "";
            switch (status) {
                case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
                    statusMessage = "INCOMPLETE_ATTACHMENT";
                    break;
                case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
                    statusMessage = "INCOMPLETE_MISSING_ATTACHMENT";
                    break;
                case GL_FRAMEBUFFER_UNSUPPORTED:
                    statusMessage = "UNSUPPORTED";
            }
            System.err.println("Unable to create framebuffer: " + statusMessage);
            return false;
        }
        return true;
    }

    /**
     * Defines which color buffer is written to.
     * @param attachment The attachent to draw to. This is between 0~15.
     * */
    public void drawBuffer(int attachment) {
        glDrawBuffer(GL_COLOR_ATTACHMENT0 + attachment);
    }

    /**
     * Defines which color buffer(s) are written to.
     * @param attachments The attachment numbers between 0~15.
     * */
    public void drawBuffers(int... attachments) {
        for (int i = 0; i < attachments.length; i++)
            attachments[i] = GL_COLOR_ATTACHMENT0 + attachments[i];
        glDrawBuffers(attachments);
    }

    /**
     * Sets the draw color buffer to none.
     * */
    public void drawNone() {
        glDrawBuffer(GL_NONE);
    }

    /**
     * Defines which color attachment is read from when reading from this frame buffer object.
     * @param attachment The attachment to read from. This is between 0~15.
     * */
    public void readBuffer(int attachment) {
        glReadBuffer(GL_COLOR_ATTACHMENT0 + attachment);
    }

    /**
     * Sets the read color buffer to none.
     * */
    public void readNone() {
        glReadBuffer(GL_NONE);
    }

    /**
     * Binds this frame buffer object for reading/writing.
     * */
    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, id);
    }

    /**
     * Binds the default framebuffer object for reading/writing (id = 0).
     * */
    public static void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    /**
     * Binds this frame buffer object for writing.
     * */
    public void bindForDraw() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, id);
    }

    /**
     * Binds the default framebuffer object for writing (id = 0).
     * */
    public static void unbindForDraw() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
    }

    /**
     * Binds this frame buffer object for reading.
     * */
    public void bindForRead() {
        glBindFramebuffer(GL_READ_FRAMEBUFFER, id);
    }

    /**
     * Binds the default framebuffer object for reading (id = 0).
     * */
    public static void unbindForRead() {
        glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);
    }

    /*public void blitAttachmentToFbo(FrameBufferObject other, int attachment) {
        bindForRead();
        readBuffer(attachment);
        other.bindForDraw();
        other.drawBuffer();
        glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL_COLOR_BUFFER_BIT, GL_NEAREST);
    }*/

    public void blitDepthToFbo(FrameBufferObject other) {
        bindForRead();
        other.bindForDraw();
        glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL_DEPTH_BUFFER_BIT, GL_NEAREST);
    }

    public void blitDepthToScreen() {
        bindForRead();
        unbindForDraw();
        glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL_DEPTH_BUFFER_BIT, GL_NEAREST);
    }

    /**
     * Binds the provided texture to this frame buffer at the given attachment.
     * */
    protected void bindTexture(Texture texture, int target, int attachment) {
        glFramebufferTexture2D(target, GL_COLOR_ATTACHMENT0 + attachment, GL_TEXTURE_2D,
                texture.getId(),0);
    }

    /**
     * Binds the provided texture to this frame buffer's depth attachment.
     * */
    protected void bindDepthTexture(Texture texture, int target) {
        glFramebufferTexture2D(target, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, texture.getId(),
                0);
    }

    protected void bindDepthStencilTexture(Texture texture, int target) {
        glFramebufferTexture2D(target, GL_DEPTH_STENCIL_ATTACHMENT, GL_TEXTURE_2D, texture.getId(),
                0);
    }

    @Override
    public void destroy() {
        glDeleteFramebuffers(id);
    }

    public int getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}

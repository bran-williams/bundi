package com.branwilliams.bundi.engine.core.scenes;

import com.branwilliams.bundi.engine.shader.dynamic.DynamicVAO;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.font.BasicFontRenderer;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Renderer;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.font.FontRenderer;

import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

/**
 * Created by Brandon Williams on 2/3/2018.
 */
public class ErrorSceneRenderer implements Renderer {

    private FontRenderer fontRenderer;

    private Projection projection;

    private DynamicShaderProgram shaderProgram;

    private Transformable worldTransform = new Transformation();

    private String[] stackTrace;

    private int messageWidth, messageHeight;

    public ErrorSceneRenderer(Exception exception) {
        // Print the stacktrace into a stringwriter and split by the newline character.
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        exception.printStackTrace(printWriter);
        stackTrace = stringWriter.toString().split("\n");
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        try {
            this.shaderProgram = new DynamicShaderProgram();
        } catch (ShaderInitializationException | ShaderUniformException e) {
            e.printStackTrace();
        }

        projection = new Projection(window);
        fontRenderer = new BasicFontRenderer(new DynamicVAO());
        fontRenderer.getFontData().setFont(new Font("Default", Font.BOLD, 20), true);

        // Calculate the width and height of this message.
        for (int i = 0; i < stackTrace.length; i++) {
            String stackTraceElement = stackTrace[i];
            int elementWidth = fontRenderer.getFontData().getStringWidth(stackTraceElement);
            if (elementWidth > messageWidth) {
                messageWidth = elementWidth;
            }
        }
        messageHeight = fontRenderer.getFontData().getFontHeight() * stackTrace.length;

        glClearColor(0F, 0F,0F, 1F);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
    }

    @Override
    public void render(Engine engine, Window window, double deltaTime) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        worldTransform.setPosition(window.getWidth() * 0.5F - messageWidth * 0.5F, window.getHeight() * 0.5F - messageHeight * 0.5F, 0F);
        this.shaderProgram.bind();
        this.shaderProgram.setProjectionMatrix(projection);
        this.shaderProgram.setModelMatrix(worldTransform);
        this.fontRenderer.drawString("An error occured! Here's the stack trace:", 0, -fontRenderer.getFontData().getFontHeight(), 0xFFFFFFFF);
        for (int i = 0; i < stackTrace.length; i++) {
            String stackTraceElement = stackTrace[i];
            this.fontRenderer.drawString(stackTraceElement, 0, i * fontRenderer.getFontData().getFontHeight(),0xFF990000);
        }
        ShaderProgram.unbind();
    }

    @Override
    public void destroy() {
        this.shaderProgram.destroy();
    }

    @Override
    public String getName() {
        return "ErrorRenderer";
    }
}

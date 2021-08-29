package com.branwilliams.bundi.engine.core.scenes;

import com.branwilliams.bundi.engine.core.AbstractScene;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Renderer;
import com.branwilliams.bundi.engine.core.window.MouseListener;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.context.Ignore;
import com.branwilliams.bundi.engine.font.BasicFontRenderer;
import com.branwilliams.bundi.engine.font.FontRenderer;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicVAO;

import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Supplier;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Brandon Williams on 2/2/2018.
 */
@Ignore
public class ErrorScene extends AbstractScene {

    private Transformable mouseOffset = new Transformation();

    private boolean movingMouse;

    public ErrorScene(Exception exception) {
        super("error_scene");
        this.setRenderer(new ErrorSceneRenderer(this::getMouseOffset, exception));
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
    }

    @Override
    public void play(Engine engine) {
        engine.getWindow().showCursor();
    }

    @Override
    public void pause(Engine engine) {

    }

    @Override
    public  boolean destroyUponReplacement() {
        return true;
    }

    @Override
    public void move(Window window, float newMouseX, float newMouseY, float oldMouseX, float oldMouseY) {
        super.move(window, newMouseX, newMouseY, oldMouseX, oldMouseY);
        if (movingMouse) {
            float moveSpeed = 0.2F;
            mouseOffset.move((newMouseX - oldMouseX) * moveSpeed, (newMouseY - oldMouseY) * moveSpeed, 0F);
        }
    }

    @Override
    public void press(Window window, float mouseX, float mouseY, int buttonId) {
        super.press(window, mouseX, mouseY, buttonId);
        movingMouse = true;
    }

    @Override
    public void release(Window window, float mouseX, float mouseY, int buttonId) {
        super.release(window, mouseX, mouseY, buttonId);
        movingMouse = false;
    }


    public Transformable getMouseOffset() {
        return mouseOffset;
    }

    /**
     * Created by Brandon Williams on 2/3/2018.
     */
    public static class ErrorSceneRenderer implements Renderer {

        private static final int ERROR_TITLE_COLOR = 0xFFFFFFFF;

        private static final int STACKTRACE_COLOR = 0xFF990000;

        private final Supplier<Transformable> transformation;

        private final String[] stackTrace;

        private FontRenderer fontRenderer;

        private Projection projection;

        private DynamicShaderProgram shaderProgram;

        private int messageWidth, messageHeight;

        public ErrorSceneRenderer(Supplier<Transformable> transformation, Exception exception) {
            this.transformation = transformation;

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

            // Initialize the transformation at the center of the message.
            transformation.get().setPosition(window.getWidth() * 0.5F - messageWidth * 0.5F,
                    window.getHeight() * 0.5F - messageHeight * 0.5F,
                    0F);
        }

        @Override
        public void render(Engine engine, Window window, double deltaTime) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            this.shaderProgram.bind();
            this.shaderProgram.setProjectionMatrix(projection);
            this.shaderProgram.setModelMatrix(transformation.get());

            this.fontRenderer.drawString("An error occured! Here's the stack trace:", 0,
                    -fontRenderer.getFontData().getFontHeight(), ERROR_TITLE_COLOR);

            for (int i = 0; i < stackTrace.length; i++) {
                String stackTraceElement = stackTrace[i];
                this.fontRenderer.drawString(stackTraceElement, 0,
                        i * fontRenderer.getFontData().getFontHeight(), STACKTRACE_COLOR);
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

}

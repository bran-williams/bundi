package com.branwilliams.frogger.pipeline.pass;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.font.BasicFontRenderer;
import com.branwilliams.bundi.engine.font.FontRenderer;
import com.branwilliams.bundi.engine.shader.ShaderInitializationException;
import com.branwilliams.bundi.engine.shader.ShaderUniformException;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;

import java.awt.Font;

public class HelpUIRenderPass extends RenderPass<RenderContext> {

    private DynamicShaderProgram shaderProgram;

    private FontRenderer fontRenderer;

    private final String[] helpLines;

    public HelpUIRenderPass(String[] helpLines) {
        this.helpLines = helpLines;
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            shaderProgram = new DynamicShaderProgram();
        } catch (ShaderInitializationException | ShaderUniformException e) {
            e.printStackTrace();
        }
        fontRenderer = new BasicFontRenderer();
        fontRenderer.getFontData()
                .setFont(new Font("D-DIN", Font.BOLD, 32), true);
    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window, double deltaTime) {
        shaderProgram.bind();
        shaderProgram.setProjectionMatrix(renderContext.getProjection());
        shaderProgram.setModelMatrix(Transformable.empty());
        int y = 2;
        for (String line : this.helpLines) {
            fontRenderer.drawStringWithShadow(line, 2, y + 2, 0xFFFFFFFF);
            y += fontRenderer.getFontData().getFontHeight();
        }
    }
}

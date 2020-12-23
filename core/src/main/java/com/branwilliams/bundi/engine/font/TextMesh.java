package com.branwilliams.bundi.engine.font;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.shader.VertexArrayObject;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicVAO;

public class TextMesh implements Destructible {

    private FontData font;

    private Text text;

    private DynamicVAO vao;

    public TextMesh(FontData font, Text text, DynamicVAO vao) {
        this.font = font;
        this.text = text;
        this.vao = vao;
    }

    public FontData getFont() {
        return font;
    }

    public DynamicVAO getVao() {
        return vao;
    }

    @Override
    public void destroy() {
        vao.destroy();
    }
}

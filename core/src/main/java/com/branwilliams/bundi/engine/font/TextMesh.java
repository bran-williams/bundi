package com.branwilliams.bundi.engine.font;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.shader.VertexArrayObject;

public class TextMesh implements Destructible {

    private FontData font;

    private Text text;

    private VertexArrayObject vao;

    public TextMesh(FontData font, Text text, VertexArrayObject vao) {
        this.font = font;
        this.text = text;
        this.vao = vao;
    }

    public FontData getFont() {
        return font;
    }

    public VertexArrayObject getVao() {
        return vao;
    }

    @Override
    public void destroy() {
        vao.destroy();
    }
}

package com.branwilliams.bundi.engine.font;

import com.branwilliams.bundi.engine.shader.VertexArrayObject;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicVAO;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;

public class TextMeshBuilder {

    private int spacesPerTab = 4;

    private float kerning = 0F;

    private DynamicVAO dynamicVao;

    public TextMeshBuilder() {
        this.dynamicVao = new DynamicVAO(VertexFormat.POSITION_2D_UV);
    }

    public TextMesh rebuildTextMesh(TextMesh textMesh) {
        return null;
    }

    public TextMesh buildTextMesh(FontData fontData, Text text) {
        String string = text.getText();
        float x = 0F;
        float y = 0F;


        dynamicVao.begin();

        int size = string.length();
        for (int i = 0; i < size; i++) {
            char character = string.charAt(i);
            if (character == '\t') {
                x += spacesPerTab * fontData.getSpaceWidth();
            } else if (fontData.hasBounds(character)) {
                FontData.CharacterData area = fontData.getCharacterBounds(character);
                dynamicVao.addRect(x, y, x + area.width, y + area.height,
                        area.getU(), area.getV(), area.getS(), area.getT());
                x += (area.width + kerning);
            }
        }

        dynamicVao.compile();
        VertexArrayObject vao = dynamicVao.pop();
        TextMesh textMesh = new TextMesh(fontData, text, vao);
        return textMesh;
    }

}

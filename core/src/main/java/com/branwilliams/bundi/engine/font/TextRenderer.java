package com.branwilliams.bundi.engine.font;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextRenderer {

    private Map<Text, TextMesh> renderedText;

    private Map<FontData, List<TextRenderInfo>> renders;

    public TextRenderer() {
        this.renderedText = new HashMap<>();
        this.renders = new HashMap<>();
    }

    public void renderStaticText() {
        for (FontData font : renders.keySet()) {
            List<TextRenderInfo> renderInfos = renders.get(font);
            if (!renderInfos.isEmpty()) {
                font.bind();

                for (TextRenderInfo renderInfo : renders.get(font)) {

                    TextMesh textMesh = this.renderedText.get(renderInfo.getText());
                    textMesh.getVao();
                }
            }
        }
    }

    public void renderDynamicText() {

    }

    public void removeRenderedText(TextRenderInfo renderInfo) {
        this.renders.get(renderInfo.font).remove(renderInfo);
    }

    public TextRenderInfo addRenderedText(FontData font, String text, int x, int y, int color) {
        return this.addRenderedText(font, new Text(text), x, y, color);
    }

    public TextRenderInfo addRenderedText(FontData font, Text text, int x, int y, int color) {
        TextRenderInfo renderInfo = new TextRenderInfo(font, text, x, y, color);

        List<TextRenderInfo> renderInfos = renders.computeIfAbsent(font, (k) -> new ArrayList<>());
        renderInfos.add(renderInfo);

        return renderInfo;
    }

    public static class TextRenderInfo {

        private final FontData font;

        private final Text text;

        private int x;

        private int y;

        private int color;

        public TextRenderInfo(FontData font, Text text, int x, int y, int color) {
            this.font = font;
            this.text = text;
            this.x = x;
            this.y = y;
            this.color = color;
        }

        public Text getText() {
            return text;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }
    }

}

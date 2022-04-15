package com.branwilliams.bundi.engine.font;

import com.branwilliams.bundi.engine.shader.dynamic.DynamicVAO;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.util.ColorUtils;

/**
 * Basic implementation of the FontRenderer interface.
 * */
public class BasicFontRenderer implements FontRenderer {

    protected final DynamicVAO dynamicVao;

    protected int kerning = 0;

    protected FontData fontData = new BasicFontData();

    public BasicFontRenderer(DynamicVAO dynamicVao) {
        this.dynamicVao = dynamicVao;
    }

    public BasicFontRenderer() {
        this(new DynamicVAO(VertexFormat.POSITION_UV_COLOR));
    }

	@Override
	public int drawString(FontData fontData, String text, int x, int y, int color) {
        if (!fontData.hasFont())
            return 0;
        float r = ColorUtils.redf(color) / 255F;
        float g = ColorUtils.greenf(color) / 255F;
        float b = ColorUtils.bluef(color) / 255F;
        float a = ColorUtils.alphaf(color) / 255F;

        fontData.bind();
        dynamicVao.begin();
		int size = text.length();
		for (int i = 0; i < size; i++) {
			char character = text.charAt(i);
			if (character == '\t') {
			    x += 4 * fontData.getSpaceWidth();
            } else if (fontData.hasBounds(character)) {
                FontData.CharacterData area = fontData.getCharacterBounds(character);
                dynamicVao.addRect(x, y, x + area.width, y + area.height,
                        (float) area.x / fontData.getTexture().getWidth(),
                        (float) area.y / fontData.getTexture().getHeight(),
                        (float) (area.x + area.width) / fontData.getTexture().getWidth(),
                        (float) (area.y + area.height) / fontData.getTexture().getHeight(),
                        r, g, b, a);
				x += (area.width + kerning);
			}
		}
		dynamicVao.draw();
		return x;
	}

	@Override
	public int drawString(String text, int x, int y, int color) {
        return drawString(fontData, text, x, y, color);
    }

    public int getKerning() {
        return kerning;
    }

    public void setKerning(int kerning) {
        this.kerning = kerning;
    }

    @Override
    public FontData getFontData() {
        return fontData;
    }

    @Override
    public void setFontData(FontData fontData) {
        this.fontData = fontData;
    }

    @Override
    public void destroy() {
        dynamicVao.destroy();
        fontData.destroy();
    }
}

package com.branwilliams.bundi.engine.asset;


import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.font.FontData;

import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Deprecated
public class FontLoader {

    private final Path directory;

    public FontLoader(EngineContext context) {
        this(context.getAssetDirectory());
    }

    public FontLoader(Path directory) {
        this.directory = directory;
    }

    public FontData load(String font) throws Exception {
        return load(font, 18);
    }

    public FontData load(String font, int size) throws Exception {

        Path fontPath = directory.resolve(font);

        FontData fontData = new FontData();

        Font fontObject = Font.createFont(Font.TRUETYPE_FONT, fontPath.toFile());

        fontData.setFont(fontObject.deriveFont(Font.PLAIN, size), true);

        return fontData;
    }
}

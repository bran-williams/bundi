package com.branwilliams.bundi.engine.shader.patching;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Brandon Williams on 11/18/2018.
 */
public class LineShaderPatch implements ShaderPatch {

    private final Pattern linePattern;

    private final Function<String, String> lineModifier;

    private final ModificationType modificationType;

    public enum ModificationType {
        REPLACE,
        PREPEND;
    }

    public LineShaderPatch(String linePattern, Function<String, String> lineModifier) {
        this(linePattern, lineModifier, ModificationType.REPLACE);
    }

    public LineShaderPatch(String linePattern, Function<String, String> lineModifier,
                           ModificationType modificationType) {
        this.linePattern = Pattern.compile(linePattern);
        this.lineModifier = lineModifier;
        this.modificationType = modificationType;
    }

    public LineShaderPatch(Pattern linePattern, Function<String, String> lineModifier) {
        this(linePattern, lineModifier, ModificationType.REPLACE);
    }

    public LineShaderPatch(Pattern linePattern, Function<String, String> lineModifier,
                           ModificationType modificationType) {
        this.linePattern = linePattern;
        this.lineModifier = lineModifier;
        this.modificationType = modificationType;
    }

    @Override
    public String patch(String code) {
        Matcher lineMatcher = linePattern.matcher(code);
        while (lineMatcher.find()) {

            String line = lineMatcher.group();

            String beforeLine = code.substring(0, code.indexOf(line));

            switch (modificationType) {
                case PREPEND:
                    code = beforeLine + lineModifier.apply(line) + code.substring(code.indexOf(line));
                    break;
                case REPLACE:
                    code = beforeLine + lineModifier.apply(line) + code.substring(code.indexOf(line) + line.length());
                    break;
                default:
            }
        }
        return code;
    }

    @Override
    public String toString() {
        return "LineShaderPatch{" +
                "linePattern=" + linePattern +
                ", modificationType=" + modificationType +
                '}';
    }
}

package com.branwilliams.bundi.engine.shader.patching;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Brandon Williams on 11/18/2018.
 */
public class CommentShaderPatch implements ShaderPatch {

    public static final Pattern COMMENT_PATTERN = Pattern.compile("/\\*.*\\*/");

    private final Pattern linePattern;

    private final Function<String, String> lineModifier;

    public CommentShaderPatch(Pattern linePattern, Function<String, String> lineModifier) {
        this.linePattern = linePattern;
        this.lineModifier = lineModifier;
    }

    @Override
    public String patch(String code) {
        StringBuilder patchedCode = new StringBuilder();
        for (String line : code.split("\\s+")) {
            Matcher commentMatcher = COMMENT_PATTERN.matcher(line);
            for (int i = 0; i < commentMatcher.groupCount(); i++) {
                String text = commentMatcher.group(i);
                if (linePattern.matcher(text).find()) {
                    line = lineModifier.apply(line);
                }
            }
            if (COMMENT_PATTERN.matcher(line).matches()) {

            }
            patchedCode.append(line + "\n");
        }
        return patchedCode.toString();
    }

}

package com.branwilliams.bundi.engine.shader.patching;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Brandon Williams on 11/18/2018.
 */
public class CommentShaderPatch implements ShaderPatch {

    public static final Pattern COMMENT_PATTERN = Pattern.compile("\\/\\*(\\*(?!\\/)|[^*])*\\*\\/");

    private final Pattern linePattern;

    private final Function<String, String> lineModifier;

    public CommentShaderPatch(Pattern linePattern, Function<String, String> lineModifier) {
        this.linePattern = linePattern;
        this.lineModifier = lineModifier;
    }

    @Override
    public String patch(String code) {
        Matcher commentMatcher = COMMENT_PATTERN.matcher(code);
        for (int i = 0; i < commentMatcher.groupCount(); i++) {
            String comment = commentMatcher.group(i);
            if (linePattern.matcher(comment).find()) {
                code = code.substring(0, code.indexOf(comment)) + lineModifier.apply(comment) +  code.substring(code.indexOf(comment));
            }
        }
        return code;
    }

}

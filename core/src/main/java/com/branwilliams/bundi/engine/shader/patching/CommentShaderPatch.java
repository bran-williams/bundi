package com.branwilliams.bundi.engine.shader.patching;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Brandon Williams on 11/18/2018.
 */
public class CommentShaderPatch implements ShaderPatch {

    public static final Pattern COMMENT_PATTERN = Pattern.compile("/\\*((?:.|[\\r\\n])*?)\\*/");

    private final Pattern linePattern;

    private final Function<String, String> lineModifier;

    private final ModificationType modificationType;

    public enum ModificationType {
        REPLACE,
        PREPEND;
    }
    public CommentShaderPatch(Pattern linePattern, Function<String, String> lineModifier) {
        this(linePattern, lineModifier, ModificationType.REPLACE);
    }

    public CommentShaderPatch(Pattern linePattern, Function<String, String> lineModifier,
                              ModificationType modificationType) {
        this.linePattern = linePattern;
        this.lineModifier = lineModifier;
        this.modificationType = modificationType;
    }

    @Override
    public String patch(String code) {
        Matcher commentMatcher = COMMENT_PATTERN.matcher(code);
        while (commentMatcher.find()) {

            String comment = commentMatcher.group();
//            System.out.println("comment=" + comment);
            if (linePattern.matcher(comment).find()) {
                System.out.println("match found: comment=" + comment);
                switch (modificationType) {
                    case PREPEND:
                        return code.substring(0, code.indexOf(comment)) + lineModifier.apply(comment) + code.substring(code.indexOf(comment));
                    case REPLACE:
                        return code.substring(0, code.indexOf(comment)) + lineModifier.apply(comment) + code.substring(code.indexOf(comment) + comment.length());
                    default:
                }
            }
        }
        return code;
    }

    @Override
    public String toString() {
        return "CommentShaderPatch{" +
                "linePattern=" + linePattern +
                ", modificationType=" + modificationType +
                '}';
    }
}

package com.branwilliams.bundi.engine.util;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Brandon
 * @since January 05, 2020
 */
public class ShaderUtils {

    private static final Pattern C_STYLE_COMMENT_PATTERN = Pattern.compile("\\/\\*(.*)\\*\\/");

    public static String addDefines(String code, String defines) {
        int firstNewlineIndex = code.indexOf("\n");

        String versionDefine = code.substring(0, firstNewlineIndex);
        code = code.substring(firstNewlineIndex);

        return versionDefine + "\n" + defines + "\n" + code;
    }

    public static String replaceComment(String code, String commentRegex, Function<String, String> commentModifier) {
        Matcher commentMatcher = C_STYLE_COMMENT_PATTERN.matcher(code);
        if (!commentMatcher.find()) {
            throw new IllegalStateException("No match found");
        }

        Pattern commentPattern = Pattern.compile(commentRegex);

        for (int i = 0; i < commentMatcher.groupCount(); i++) {
            String text = commentMatcher.group(i);
            if (commentPattern.matcher(text).find()) {
                code = code.substring(0, code.indexOf(text)) + commentModifier.apply(text) +  code.substring(code.indexOf(text) + text.length());
            }
        }

        return code;
    }

}

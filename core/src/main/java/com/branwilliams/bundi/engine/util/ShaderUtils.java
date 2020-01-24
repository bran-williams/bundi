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

    public static String replaceComment(String code, Pattern commentPattern, Function<String, String> lineModifier) {
        Matcher commentMatcher = C_STYLE_COMMENT_PATTERN.matcher(code);
        if (!commentMatcher.find()) {
            throw new IllegalStateException("No match found");
        }

        for (int i = 0; i < commentMatcher.groupCount(); i++) {
            String text = commentMatcher.group(i);
            if (commentPattern.matcher(text).find()) {
                code = code.substring(0, code.indexOf(text)) + lineModifier.apply(text) +  code.substring(code.indexOf(text) + text.length());
            }
        }

        return code;
    }

}

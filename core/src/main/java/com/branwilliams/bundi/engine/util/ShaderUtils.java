package com.branwilliams.bundi.engine.util;

/**
 * @author Brandon
 * @since January 05, 2020
 */
public class ShaderUtils {

    public static String addDefines(String defines, String code) {
        int firstNewlineIndex = code.indexOf("\n");

        String versionDefine = code.substring(0, firstNewlineIndex);
        code = code.substring(firstNewlineIndex);

        return versionDefine + "\n" + defines + "\n" + code;
    }

}

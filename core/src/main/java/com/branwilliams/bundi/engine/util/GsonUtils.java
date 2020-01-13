package com.branwilliams.bundi.engine.util;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * @author Brandon
 * @since January 09, 2020
 */
public class GsonUtils {

    public static <T> Type arrayListType(Class<T> clazz) {
        return new TypeToken<ArrayList<T>>(){}.getType();
    }
}

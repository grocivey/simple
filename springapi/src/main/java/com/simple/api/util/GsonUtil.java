package com.simple.api.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

/**
 *
 * @author gzy
 */
public class GsonUtil {
    public static final Gson GSON = new GsonBuilder().serializeNulls().create();
    private GsonUtil(){}

    public static <T> String toJson(T str){
        return GSON.toJson(str);
    }
    public static <T> T fromJson(String json, Class<T> clazz){
        return GSON.fromJson(json, clazz);
    }
    public static <T> T fromJson(String str, Type typeOfT){
        return GSON.fromJson(str, typeOfT);
    }


}

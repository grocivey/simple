package com.simple.api.util;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class GsonUtil {
    public static final Gson GSON = new Gson();
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

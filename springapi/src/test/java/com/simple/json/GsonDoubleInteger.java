package com.simple.json;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * gson反序列化int变double的解决
 */
public class GsonDoubleInteger {

    public static Gson getGson() {
        Gson gson = new GsonBuilder().registerTypeAdapter(HashMap.class, new JsonDeserializer<HashMap>() {
            @Override
            public HashMap<String, Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                HashMap<String, Object> resultMap = new HashMap<>();
                JsonObject jsonObject = json.getAsJsonObject();
                Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
                for (Map.Entry<String, JsonElement> entry : entrySet) {
                    resultMap.put(entry.getKey(), entry.getValue());
                }
                return resultMap;
            }

        }).create();
        return gson;
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        String aaa = "{ a: 1, b: 2.3 }";
        Map<String, Object> map = getGson().fromJson(aaa, HashMap.class);
        System.out.println(((JsonPrimitive) map.get("a")).getAsString());
        System.out.println(map.get("a"));
        System.out.println(map);
    }
}

package cc.jweb.boot.utils.lang;


import cc.jweb.boot.utils.gson.GsonUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonUtils {

    public static String toJson(Object object) {
        return GsonUtils.get().toJson(object);
    }


    public static Map json2Map(String json) {
        return GsonUtils.get().fromJson(json, LinkedHashMap.class);
    }


    public static List json2List(String json) {
        return GsonUtils.get().fromJson(json, ArrayList.class);
    }
}

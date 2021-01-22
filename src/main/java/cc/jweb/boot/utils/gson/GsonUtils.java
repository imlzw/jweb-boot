/*
 * Copyright  (c) 2020-2021 imlzw@vip.qq.com jweb.cc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.jweb.boot.utils.gson;

import cc.jweb.boot.utils.gson.model.TypeFactory;
import cc.jweb.boot.utils.lang.interf.JsonUtilsInterf;
import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * gson统一管理类，全局保持一个gson对象
 * <p>
 * 需要jar包:
 * <ul>
 * <li>gson-xxx.jar</li>
 * </ul>
 * GSON更新日志:https://github.com/google/gson/blob/master/CHANGELOG.md
 *
 * @author ag777
 * @version create on 2017年05月27日,last modify at 2020年08月18日
 */
public class GsonUtils implements JsonUtilsInterf {

    private static GsonUtils mInstance;

    private GsonBuilder builder;
    private Gson gson;

    private GsonUtils() {
        builder = getDefaultBuilder();
    }

    private GsonUtils(GsonBuilder builder) {
        this.builder = builder;
    }

    /*==================入口函数========================*/

    /**
     * 获取一般情况下的gson
     *
     * @return
     */
    public static GsonUtils get() {
        if (mInstance == null) {
            synchronized (GsonUtils.class) {
                if (mInstance == null) {
                    mInstance = new GsonUtils();
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取默认的gson
     *
     * @return
     */
    public static GsonUtils def() {
        return new GsonUtils(new GsonBuilder());
    }

    /**
     * 自定义构建gson
     *
     * @param builder builder
     * @return GsonUtils
     */
    public static GsonUtils custom(GsonBuilder builder) {
        return new GsonUtils(builder);
    }

    /*==================下面添加配置(可拓展)========================*/

    /**
     * 获取该工具默认的builder
     *
     * @return
     */
    public static GsonBuilder getDefaultBuilder() {
        MapTypeAdapter objAdapter = new MapTypeAdapter();
        return new GsonBuilder()
                .disableHtmlEscaping()    //html标签不转义 (避免符号被转义)
                .setDateFormat("yyyy-MM-dd HH:mm:ss")    //序列化和反序化时将时间以此形式输出
                .registerTypeAdapter(
                        new TypeToken<Map<String, Object>>() {
                        }.getType(),
                        objAdapter
                )
                .registerTypeAdapter(
                        new TypeToken<List<Object>>() {
                        }.getType(),
                        objAdapter
                )
                .registerTypeAdapter(Class.class, new ClassTypeAdapter());
    }

    /**
     * 转换json串为JsonObject
     *
     * @param json json
     * @return JsonObject
     */
    public static JsonObject toJsonObject(String json) {
        /*源码说明(下同):No need to instantiate this class, use the static methods instead.*/
        return new JsonParser().parse(json).getAsJsonObject();
    }

    /**
     * 转换json对象为JsonArray
     *
     * @param json json
     * @return JsonArray
     */
    public static JsonArray toJsonArray(String json) {
        return new JsonParser().parse(json).getAsJsonArray();
    }

    /*=================测试(示例)方法============*/
    public static void main(String[] args) throws Exception {
        String json = "{\"success\":true, \"a\":20, \"b\":20.0, \"c\":20.1}";
        Map<String, Object> map = GsonUtils.get().fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());
//		System.out.println(
//				map.get("a").getClass().getName()+"||"+
//				map.get("b").getClass().getName()+"||"+
//				map.get("c").getClass().getName());
//		System.out.println(GsonUtils.get().toJson(map));

//		Map<String, Object> map = new HashMap<>();
//		map.put("a", new Date());
//		map.put("b", null);
//		System.out.println(GsonUtils.get().toJson(map));
//		System.out.println(GsonUtils.get()
//				.dateFormat("yyyyMMdd")
//				.serializeNulls()
//				.toJson(map));	//会产生没用的中间产物
//
//		System.out.println(GsonUtils.custom(
//				GsonUtils.getDefaultBuilder()
//					.setDateFormat("yyyy_MM")
//				).toJson(map));
//		System.out.println(GsonUtils.get().toMapWithException(null));
//		System.out.println(GsonUtils.get().prettyFormat("{\"a\":1"));

        System.out.println(GsonUtils.get().toPrettyJson(map));
    }

    /**
     * 定制时间格式
     *
     * @param pattern pattern
     * @return GsonUtils
     */
    public GsonUtils dateFormat(String pattern) {
        return new GsonUtils(builder.setDateFormat(pattern));
    }

    /**
     * null值也参与序列化
     *
     * @return
     */
    public GsonUtils serializeNulls() {
        return new GsonUtils(builder.serializeNulls());
    }

    /**
     * 格式化输出
     *
     * @return
     */
    public GsonUtils prettyPrinting() {
        return new GsonUtils(builder.setPrettyPrinting());
    }

    /**
     * 禁此序列化内部类
     *
     * @return
     */
    public GsonUtils disableInnerClassSerialization() {
        return new GsonUtils(builder.disableInnerClassSerialization());
    }

    /*==================工具方法========================*/

    /**
     * 自定义实现序列化方法
     *
     * @param baseType    baseType
     * @param typeAdapter typeAdapter
     * @return GsonUtils
     */
    public GsonUtils registerTypeAdapter(Class<?> baseType, Object typeAdapter) {
        return new GsonUtils(builder.registerTypeHierarchyAdapter(baseType, typeAdapter));
    }

    /**
     * 自定义实现序列化方法
     *
     * @param type        type
     * @param typeAdapter typeAdapter
     * @return GsonUtils
     */
    public GsonUtils registerTypeAdapter(Type type, Object typeAdapter) {
        return new GsonUtils(builder.registerTypeAdapter(type, typeAdapter));
    }

    /**
     * 自定义实现序列化方法
     *
     * @param factory factory
     * @return GsonUtils
     */
    public GsonUtils registerTypeAdapterFactory(TypeAdapterFactory factory) {
        return new GsonUtils(builder.registerTypeAdapterFactory(factory));
    }

    /*==================内部方法========================*/
    private Gson gson() {
        if (gson == null) {
            synchronized (GsonUtils.class) {
                if (gson == null) {
                    gson = builder.create();
                }
            }
        }
        return gson;
    }

    /**
     * 格式化字符串
     * <p>
     * 每次都new一个用于格式化的新gson
     * </p>
     *
     * @param src src
     * @return String
     */
    @Override
    public String prettyFormat(String src) {
        if (src == null || src.isEmpty()) {
            return src;
        }
        JsonReader reader = new JsonReader(new StringReader(src));
        reader.setLenient(true);
        JsonElement jsonEl = new JsonParser().parse(reader);
        return toPrettyJson(jsonEl);

    }

    /**
     * 转换任意类为json串（类型不支持会报错，这里不做捕获也不做抛出, 就当业务有问题应当报错,免得写try-catch）
     *
     * @param obj obj
     * @return json串，当obj为null的时候返回null
     */
    @Override
    public String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        return gson().toJson(obj);
    }

    /**
     * @param obj 任意对象
     * @return 格式化的json串
     */
    public String toPrettyJson(Object obj) {
        if (obj == null) {
            return null;
        }
        return prettyPrinting().toJson(obj);

    }

    /**
     * 转化json串为map
     *
     * @param json json
     * @return 返回null则表示json转换失败
     */
    @Override
    public Map<String, Object> toMap(String json) {
        try {
            return toMapWithException(json);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Map<String, Object> toMapWithException(String json) throws JsonSyntaxException {
        try {
            return fromJsonWithException(json, new TypeFactory(Map.class, String.class, Object.class));
        } catch (Exception ex) {
            throw new JsonSyntaxException(ex);
        }
    }

    @Override
    public List<Map<String, Object>> toListMap(String json) {
        try {
            return toListMapWithException(json);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> toListMapWithException(String json) throws JsonSyntaxException {
        try {
            return fromJsonWithException(json, new TypeToken<List<Map<String, Object>>>() {
            }.getType());
        } catch (Exception ex) {
            throw new JsonSyntaxException(ex);
        }
    }

    /**
     * 转化json为对象列表
     *
     * @param json     json
     * @param classOfT classOfT
     * @return
     */
    @Override
    public <T> List<T> toList(String json, Class<T> classOfT) {
        try {
            return toListWithException(json, classOfT);
        } catch (JsonSyntaxException e) {
        }
        return null;
    }

    /**
     * 转化json为对象列表
     * <p><pre>{@code
     * 利用ParameterizedType类获取对应List<T>的Type作为转化媒介
     * }</pre>
     *
     * @param json     json
     * @param classOfT classOfT
     * @return
     * @throws JsonSyntaxException JsonSyntaxException
     */
    @Override
    public <T> List<T> toListWithException(String json, Class<T> classOfT) throws JsonSyntaxException {
        try {
            return gson().fromJson(json, new TypeFactory(List.class, classOfT));
        } catch (Exception ex) {
            throw new JsonSyntaxException(ex);
        }
    }

    /**
     * 转换对象为JsonElement
     *
     * @param obj obj
     * @return
     */
    public JsonElement toJsonTree(Object obj) {
        try {
            return toJsonTreeWithException(obj);
        } catch (JsonSyntaxException e) {
        }
        return null;
    }

    /**
     * 转换对象为JsonElement
     *
     * @param obj obj
     * @return
     * @throws JsonSyntaxException JsonSyntaxException
     */
    public JsonElement toJsonTreeWithException(Object obj) throws JsonSyntaxException {
        try {
            return gson().toJsonTree(obj);
        } catch (Exception ex) {
            throw new JsonSyntaxException(ex);
        }
    }

    /**
     * 转化json串为javaBean,效率低
     *
     * @param json     json
     * @param classOfT classOfT
     * @return 返回null则表示json转换失败
     */
    @Override
    public <T> T fromJson(String json, Class<T> classOfT) {
        try {
            return fromJsonWithException(json, classOfT);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public <T> T fromJsonWithException(String json, Class<T> classOfT) throws JsonSyntaxException {
        try {
            return gson().fromJson(json, (Type) classOfT);
        } catch (Exception ex) {
            throw new JsonSyntaxException(ex);
        }

    }

    /**
     * 转化json串为javaBean，类型不支持时会报错
     *
     * @param json json
     * @param type 例:<pre>{@code new TypeToken<Map<String, Object>>() {}.getType() }</pre>
     * @return 返回null则表示json转换失败
     */
    @Override
    public <T> T fromJson(String json, Type type) {
        try {
            return fromJsonWithException(json, type);
        } catch (Exception ex) {
            return null;
        }
    }

    /*=================辅助类============*/

    @Override
    public <T> T fromJsonWithException(String json, Type type) throws JsonSyntaxException {
        try {
            return gson().fromJson(json, type);
        } catch (Exception ex) {
            throw new JsonSyntaxException(ex);
        }
    }

    /**
     * 重载json转换类,主要目的是为了防止转为map时double型变量错误地转换为long型变量
     *
     * @author ag777
     * Time: created at 2017/6/6. last modify at 2018/8/10.
     */
    public static class MapTypeAdapter extends TypeAdapter<Object> {

        private TypeAdapter<Object> defaultAdapter = new Gson().getAdapter(Object.class);

        @Override
        public Object read(JsonReader in) throws IOException {
            JsonToken token = in.peek();
            switch (token) {
                case BEGIN_ARRAY:
                    List<Object> list = new ArrayList<Object>();
                    in.beginArray();
                    while (in.hasNext()) {
                        list.add(read(in));
                    }
                    in.endArray();
                    return list;

                case BEGIN_OBJECT:
                    Map<String, Object> map = new LinkedTreeMap<String, Object>();
                    in.beginObject();
                    while (in.hasNext()) {
                        map.put(in.nextName(), read(in));
                    }
                    in.endObject();
                    return map;

                case STRING:
                    return in.nextString();

                case NUMBER:
                    /**
                     * 改写数字的处理逻辑，将数字值分为整型与浮点型。
                     * 流程（规则）:
                     * 1.先读取字符串，并转为为double类型
                     * 2.如果字符串包含小数点,则直接返回double类型
                     * 3.如果数值大于Long型的最大值，则返回double类型
                     * 4.其余情况均返回Long型
                     */
                    String temp = in.nextString();

                    double dbNum = Double.parseDouble(temp);
                    if (temp.contains(".")) {
                        return dbNum;
                    }

                    // 数字超过long的最大值，返回浮点类型
                    if (dbNum > Long.MAX_VALUE) {
                        return dbNum;
                    }
                    return (long) dbNum;
                case BOOLEAN:
                    return in.nextBoolean();

                case NULL:
                    in.nextNull();
                    return null;

                default:
                    throw new IllegalStateException();
            }
        }

        @Override
        public void write(JsonWriter out, Object value) throws IOException {
            /*
             * 坑:如果不写,内部类包含对应的成员变量时将无法转化为json串
             * 这里使用原生gson的typeAdapter来代理进行解析,希望有更好的解决方案
             */
            defaultAdapter.write(out, value);
        }

    }

    /**
     * 实现Class类型变量的解析器
     *
     * @author ag777
     */
    public static class ClassTypeAdapter implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {

        @Override
        public Class<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            try {
                return Class.forName(json.getAsString());
            } catch (ClassNotFoundException e) {
            }
            return null;
        }

        @Override
        public JsonElement serialize(Class<?> clazz, Type typeOfSrc, JsonSerializationContext context) {
            return clazz == null ? null : new JsonPrimitive(clazz.getName());
        }

    }

}

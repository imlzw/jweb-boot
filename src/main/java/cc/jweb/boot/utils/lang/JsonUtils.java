/*
 * Copyright (c) 2020-2021 imlzw@vip.qq.com jweb.cc.
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

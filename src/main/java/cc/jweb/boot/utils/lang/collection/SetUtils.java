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

package cc.jweb.boot.utils.lang.collection;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 有关 <code>Set</code> 集合工具类。
 *
 * @author ag777
 * @version create on 2018年03月23日,last modify at 2018年06月14日
 */
public class SetUtils {

    private SetUtils() {
    }

    public static <T> Set<T> newSet() {
        return newHashSet();
    }

    public static <T> HashSet<T> newHashSet() {
        return CollectionAndMapUtils.newHashSet();
    }


    /**
     * 根据分隔符拆分Set获取字符串
     *
     * @param set       set
     * @param separator separator
     * @return
     */
    public static <T> String toString(Set<T> set, String separator) {
        if (CollectionAndMapUtils.isEmpty(set)) {
            return "";
        }
        StringBuilder sb = null;
        Iterator<T> it = set.iterator();
        while (it.hasNext()) {
            if (sb == null) {
                sb = new StringBuilder();
            } else if (separator != null) {
                sb.append(separator);
            }
            T item = it.next();
            if (item != null) {
                sb.append(item.toString());
            } else {
                sb.append("null");
            }
        }
        return sb.toString();
    }

    /**
     * 求两个集合的交集
     *
     * @param set1 set1
     * @param set2 set2
     * @return
     */
    public static <T> Set<T> intersection(Set<T> set1, Set<T> set2) {
        Set<T> result = newSet();
        result.addAll(set1);
        result.retainAll(set2);
        return result;
    }

    /**
     * 求两个集合的并集
     *
     * @param set1 set1
     * @param set2 set2
     * @return
     */
    public static <T> Set<T> union(Set<T> set1, Set<T> set2) {
        Set<T> result = newSet();
        result.addAll(set1);
        result.addAll(set2);
        return result;
    }

    /**
     * 求两个集合的补集(差集)
     *
     * @param set1 set1
     * @param set2 set2
     * @return
     */
    public static <T> Set<T> complement(Set<T> set1, Set<T> set2) {
        Set<T> result = newSet();
        result.addAll(set1);
        result.removeAll(set2);
        return result;
    }

}
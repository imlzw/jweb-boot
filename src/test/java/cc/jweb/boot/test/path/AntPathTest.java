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

package cc.jweb.boot.test.path;

import cc.jweb.boot.utils.lang.path.JwebAntPathMatcher;

public class AntPathTest {
    public static void main(String[] args) {
        JwebAntPathMatcher jwebAntPathMatcher = new JwebAntPathMatcher();
        System.out.println(jwebAntPathMatcher.match("/**", "/"));
        System.out.println(jwebAntPathMatcher.match("/**", "/1/1.jsp"));
        System.out.println(jwebAntPathMatcher.match("/**", "/2"));
        System.out.println(jwebAntPathMatcher.match("/**", "/3/4/5/11"));
        System.out.println(jwebAntPathMatcher.match("/1/*", "/3/4/5/11"));
        System.out.println(jwebAntPathMatcher.match("/1/*", "/1/2/3"));
        System.out.println(jwebAntPathMatcher.match("/1/*", "/1/2/"));
        System.out.println(jwebAntPathMatcher.match("/1/*", "/1/2"));
        System.out.println(jwebAntPathMatcher.match("/1/**", "/1/2/3"));
        System.out.println(jwebAntPathMatcher.match("/1/**", "/1/2/"));
        System.out.println(jwebAntPathMatcher.match("/1/**", "/1/2"));
        System.out.println(jwebAntPathMatcher.match("/1/**", "/1/"));
        System.out.println(jwebAntPathMatcher.match("/1/**", "/1"));
        System.out.println(jwebAntPathMatcher.match("/1*", "/1"));
        System.out.println(jwebAntPathMatcher.match("/1*", "/1/"));
        System.out.println(jwebAntPathMatcher.match("/1**/", "/1/"));
        System.out.println(jwebAntPathMatcher.match("/1/**/", "/1/2/2"));
        System.out.println(jwebAntPathMatcher.match("/**/2", "/1/2/2"));
    }
}

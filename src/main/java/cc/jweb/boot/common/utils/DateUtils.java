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

package cc.jweb.boot.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {


    /**
     * 通用时间格式解析方法
     *
     * @param dateString
     * @return
     */
    public static Date commonDateParse(String dateString) {
        if (dateString == null) {
            return null;
        }
        Date date = null;
        try {
            date = new SimpleDateFormat("HH:mm:ss").parse(dateString);
        } catch (ParseException e) {
        }
        if (date == null) {
            try {
                date = new SimpleDateFormat("HH:mm").parse(dateString);
            } catch (ParseException e1) {
            }
        }
        if (date == null) {
            try {
                date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
            } catch (ParseException e2) {
            }
        }
        if (date == null) {
            try {
                date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateString);
            } catch (ParseException e3) {
            }
        }
        if (date == null) {
            try {
                date = new SimpleDateFormat("yyyy-MM-dd HH").parse(dateString);
            } catch (ParseException exception) {
            }
        }
        if (date == null) {
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
            } catch (ParseException ex) {
            }
        }
        if (date == null) {
            try {
                date = new SimpleDateFormat("yyyy-MM").parse(dateString);
            } catch (ParseException exc) {
            }
        }
        if (date == null) {
            try {
                date = new SimpleDateFormat("yyyy").parse(dateString);
            } catch (ParseException e0) {
            }
        }
        return date;
    }

    public static void main(String[] args) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(simpleDateFormat.format(commonDateParse("2012")));
        System.out.println(simpleDateFormat.format(commonDateParse("2012-02")));
        System.out.println(simpleDateFormat.format(commonDateParse("2012-03-29")));
        System.out.println(simpleDateFormat.format(commonDateParse("2012-04-29 23")));
        System.out.println(simpleDateFormat.format(commonDateParse("2012-05-29 23:32")));
        System.out.println(simpleDateFormat.format(commonDateParse("2012-06-29 23:32:32")));
        System.out.println(simpleDateFormat.format(commonDateParse("23:32:32")));
        System.out.println(simpleDateFormat.format(commonDateParse("21:31")));
    }
}

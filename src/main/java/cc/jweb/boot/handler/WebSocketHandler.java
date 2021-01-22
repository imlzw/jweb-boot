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

package cc.jweb.boot.handler;

import cc.jweb.boot.utils.lang.StringUtils;
import com.jfinal.handler.Handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;

public class WebSocketHandler extends Handler {

    private Pattern filterUrlRegxPattern;

    public WebSocketHandler(String filterUrlRegx) {
        if (StringUtils.isBlank(filterUrlRegx))
            throw new IllegalArgumentException("The para filterUrlRegx can not be blank.");
        filterUrlRegxPattern = Pattern.compile(filterUrlRegx);
    }
    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        if (filterUrlRegxPattern.matcher(target).find())
            return ;
        else
            next.handle(target, request, response, isHandled);

    }

}
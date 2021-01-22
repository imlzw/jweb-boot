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

package cc.jweb.boot.web.render;

import cc.jweb.boot.utils.lang.HumpNameUtils;
import cc.jweb.boot.utils.lang.JsonUtils;
import cc.jweb.boot.utils.lang.StringUtils;
import com.jfinal.config.Constants;
import com.jfinal.core.Action;
import com.jfinal.render.ContentType;
import com.jfinal.render.Render;
import com.jfinal.template.Engine;
import io.jboot.Jboot;
import io.jboot.utils.StrUtil;
import io.jboot.web.render.*;

import javax.servlet.ServletContext;
import java.util.Set;

/**
 * 渲染工厂
 */
public class JwebBootRenderFactory extends JbootRenderFactory {

    private static final JwebBootRenderFactory ME = new JwebBootRenderFactory();

    @Override
    public void init(Engine engine, Constants constants, ServletContext servletContext) {
        super.init(engine, constants, servletContext);
        initEngine(engine);
    }

    public void initEngine(Engine engine) {
        engine.setEncoding("UTF-8");
        engine.setDevMode(Jboot.isDevMode());
        // 添加模板引擎共享方法类
        engine.addSharedMethod(new StringUtils());
        engine.addSharedMethod(new HumpNameUtils());
        engine.addSharedMethod(new JsonUtils());
        //
        EngineConfig config = Jboot.config(EngineConfig.class, "jweb.render.template.engine");
        Set<String> classes = StrUtil.splitToSet(config.getShareMethodClasses(), ",");
        if (classes != null) {
            for (String aClass : classes) {
                try {
                    Class<?> forName = Class.forName(aClass);
                    engine.addSharedMethod(forName);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static final JwebBootRenderFactory getInstance() {
        return ME;
    }


    @Override
    public Render getRender(String view) {
        return new JbootRender(view);
    }

    @Override
    public Render getHtmlRender(String htmlText) {
        return new JbootHtmlRender(htmlText);
    }

    @Override
    public Render getTextRender(String text) {
        return new JbootTextRender(text);
    }

    @Override
    public Render getTextRender(String text, String contentType) {
        return new JbootTextRender(text, contentType);
    }

    @Override
    public Render getTextRender(String text, ContentType contentType) {
        return new JbootTextRender(text, contentType);
    }

    @Override
    public Render getJavascriptRender(String jsText) {
        return new JbootJavascriptRender(jsText);
    }

    @Override
    public Render getErrorRender(int errorCode) {
        return new JwebBootErrorRender(errorCode, constants.getErrorView(errorCode));
    }

    @Override
    public Render getErrorRender(int errorCode, String view) {
        return new JwebBootErrorRender(errorCode, view);
    }

    @Override
    public Render getJsonRender() {
        return new JbootJsonRender();
    }

    @Override
    public Render getJsonRender(String key, Object value) {
        return new JbootJsonRender(key, value);
    }

    @Override
    public Render getJsonRender(String[] attrs) {
        return new JbootJsonRender(attrs);
    }

    @Override
    public Render getJsonRender(String jsonText) {
        return new JbootJsonRender(jsonText);
    }

    @Override
    public Render getJsonRender(Object object) {
        return new JbootJsonRender(object);
    }

    @Override
    public Render getTemplateRender(String view) {
        return new JbootTemplateRender(view);
    }

    @Override
    public Render getXmlRender(String view) {
        return new JbootXmlRender(view);
    }


    @Override
    public Render getRedirectRender(String url) {
        return new JbootRedirectRender(url);
    }

    @Override
    public Render getRedirectRender(String url, boolean withQueryString) {
        return new JbootRedirectRender(url, withQueryString);
    }

    @Override
    public Render getRedirect301Render(String url) {
        return new JbootRedirect301Render(url);
    }

    @Override
    public Render getRedirect301Render(String url, boolean withQueryString) {
        return new JbootRedirect301Render(url, withQueryString);
    }

    @Override
    public Render getCaptchaRender() {
        return new JbootCaptchaRender();
    }

    public Render getReturnValueRender(Action action, Object returnValue) {
        return new JbootReturnValueRender(action, returnValue);
    }
}

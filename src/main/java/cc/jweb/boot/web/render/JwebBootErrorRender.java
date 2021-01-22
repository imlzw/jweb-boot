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

import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Ret;
import com.jfinal.render.RenderException;
import com.jfinal.render.RenderManager;
import io.jboot.exception.JbootExceptionHolder;
import io.jboot.web.render.JbootErrorRender;

import java.io.PrintWriter;
import java.util.List;

/**
 * 构建错误信息渲染器
 */
public class JwebBootErrorRender extends JbootErrorRender {

    protected static final String htmlContentType = "text/html;charset=" + getEncoding();
    protected static final String jsonContentType = "application/json;charset=" + getEncoding();

    protected static final String poweredBy = "<center><a href='http://www.jweb.cc' target='_blank'><b style=\"font-size:16px;color:#0000EE;\">Powered by jweb</b></a></center>";

    protected static final String html404 = "<html><head><title>404 Not Found</title></head><body bgcolor='white'><center><h1 style=\"font-size:32px;margin:24px;font-weight: bold;\">404 Not Found</h1></center><hr>" + poweredBy + "</body></html>";
    protected static final String html401 = "<html><head><title>401 Unauthorized</title></head><body bgcolor='white'><center><h1 style=\"font-size:32px;margin:24px;font-weight: bold;\">401 Unauthorized</h1></center><hr>" + poweredBy + "</body></html>";
    protected static final String html403 = "<html><head><title>403 Forbidden</title></head><body bgcolor='white'><center><h1 style=\"font-size:32px;margin:24px;font-weight: bold;\">403 Forbidden</h1></center><hr>" + poweredBy + "</body></html>";

    protected static final String html500_header = "<html><head><title>500 Internal Server Error</title></head>" +
            "<body bgcolor='white'><center><h1 style=\"font-size:32px;margin:24px;font-weight: bold;\">500 Internal Server Error</h1></center>" +
            "<hr>";

    protected static final String html500_footer = "<hr>" + poweredBy + "</body></html>";


    protected static final String json401 = JsonKit.toJson(Ret.fail().set("errorCode", 401).set("message", "401 Unauthorized"));
    protected static final String json403 = JsonKit.toJson(Ret.fail().set("errorCode", 403).set("message", "403 Forbidden"));
    protected static final String json404 = JsonKit.toJson(Ret.fail().set("errorCode", 404).set("message", "404 Not Found"));
    private final static String fix = "<div style=display:none >\"\'>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>标签容错！</div>";
    private final static String wrapPre = "<div style=\"" +
            "position: fixed; " +
            "width: 100vw;" +
            "height: 100vh; " +
            "background: #fff; " +
            "left:0;right:0;" +
            "top:0;" +
            "bottom:0;" +
            "font-size:16px;" +
            "overflow: auto;" +
            "padding: 0 8px 16px 8px;" +
            "box-sizing: border-box;" +
            "\">";

    public JwebBootErrorRender(int errorCode, String view) {
        super(errorCode, view);
    }

    @Override
    public void render() {
        response.setStatus(getErrorCode());


        //render with view
        String view = getView();
        if (view != null) {
            RenderManager.me().getRenderFactory()
                    .getRender(view)
                    .setContext(request, response)
                    .render();
            return;
        }

        try {
            String contentType = request.getContentType();
            boolean needRenderJson = contentType != null && contentType.toLowerCase().contains("application/json");

            response.setContentType(needRenderJson ? jsonContentType : htmlContentType);

            PrintWriter writer = response.getWriter();
            writer.write(needRenderJson ? getErrorJson() : getErrorHtml());
        } catch (Exception ex) {
            throw new RenderException(ex);
        }

    }

    public String getErrorHtml() {
        int errorCode = getErrorCode();
        if (errorCode == 404) {
            return html404;
        }
        if (errorCode == 401) {
            return html401;
        }
        if (errorCode == 403) {
            return html403;
        }
        if (errorCode == 500) {
            return build500ErrorInfo();
        }
        return "<html><head><title>" + errorCode + " Error</title></head><body bgcolor='white'><center><h1>" + errorCode + " Error</h1></center><hr>" + poweredBy + "</body></html>";
    }

    public String getErrorJson() {
        int errorCode = getErrorCode();
        if (errorCode == 404) {
            return json404;
        }
        if (errorCode == 401) {
            return json401;
        }
        if (errorCode == 403) {
            return json403;
        }
        if (errorCode == 500 || errorCode == 400) {
            return buildErrorJson();
        }

        return JsonKit.toJson(Ret.fail().set("errorCode", errorCode).set("message", errorCode + " Error"));
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String build500ErrorInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> messages = JbootExceptionHolder.getMessages();
        for (String message : messages) {
            stringBuilder.append(message).append("<br />");
        }

        List<Throwable> throwables = JbootExceptionHolder.getThrowables();
        for (Throwable throwable : throwables) {
            stringBuilder.append(throwable.getClass().getName() + " : " + throwable.getMessage()).append("<br />");
            StackTraceElement[] elems = throwable.getStackTrace();
            for (StackTraceElement element : elems) {
                stringBuilder.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;at ")
                        .append(element)
                        .append("<br />");
            }
        }
        return wrapFixHtml(stringBuilder.insert(0, html500_header).append(html500_footer)).toString();
    }

    /**
     * 包裹错误信息内容进行标签容错处理，并统一错误信息显示样式。
     *
     * @param html
     * @return
     */
    private StringBuilder wrapFixHtml(StringBuilder html) {
        return html.insert(0, wrapPre).append("</div>").insert(0, fix);
    }

    public String buildErrorJson() {

        Ret ret = Ret.fail().set("errorCode", getErrorCode()).set("message", getErrorCode() + " Internal Server Error");

        StringBuilder errorMsgBuilder = new StringBuilder();
        List<String> messages = JbootExceptionHolder.getMessages();
        for (String message : messages) {
            errorMsgBuilder.append(message);
        }

        StringBuilder throwableMsgBuilder = new StringBuilder();
        List<Throwable> throwables = JbootExceptionHolder.getThrowables();
        for (Throwable throwable : throwables) {
            throwableMsgBuilder.append(throwable.getClass().getName() + ": " + throwable.getMessage());
        }

        return JsonKit.toJson(ret.set("errorMessage", errorMsgBuilder.toString()).set("throwable", throwableMsgBuilder.toString()));
    }

}

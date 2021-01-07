package cc.jweb.boot.web.demo.controller;

import cc.jweb.boot.controller.BaseController;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping(value="/")
public class IndexController extends BaseController {
    public void index(){
        renderText("OK");
    }

    public void actuator() {
        renderText("{\n" +
                "    \"_links\": {\n" +
                "        \"self\": {\n" +
                "            \"href\": \"http://localhost:82/actuator\",\n" +
                "            \"templated\": false\n" +
                "        },\n" +
                "        \"archaius\": {\n" +
                "            \"href\": \"http://localhost:82/actuator/archaius\",\n" +
                "            \"templated\": false\n" +
                "        },\n" +
                "        \"nacos-config\": {\n" +
                "            \"href\": \"http://localhost:82/actuator/nacos-config\",\n" +
                "            \"templated\": false\n" +
                "        },\n" +
                "        \"nacos-discovery\": {\n" +
                "            \"href\": \"http://localhost:82/actuator/nacos-discovery\",\n" +
                "            \"templated\": false\n" +
                "        },\n" +
                "        \"sentinel\": {\n" +
                "            \"href\": \"http://localhost:82/actuator/sentinel\",\n" +
                "            \"templated\": false\n" +
                "        },\n" +
                "        \"beans\": {\n" +
                "            \"href\": \"http://localhost:82/actuator/beans\",\n" +
                "            \"templated\": false\n" +
                "        },\n" +
                "        \"caches-cache\": {\n" +
                "            \"href\": \"http://localhost:82/actuator/caches/{cache}\",\n" +
                "            \"templated\": true\n" +
                "        },\n" +
                "        \"caches\": {\n" +
                "            \"href\": \"http://localhost:82/actuator/caches\",\n" +
                "            \"templated\": false\n" +
                "        },\n" +
                "        \"health\": {\n" +
                "            \"href\": \"http://localhost:82/actuator/health\",\n" +
                "            \"templated\": false\n" +
                "        },\n" +
                "        \"health-path\": {\n" +
                "            \"href\": \"http://localhost:82/actuator/health/{*path}\",\n" +
                "            \"templated\": true\n" +
                "        },\n" +
                "        \"info\": {\n" +
                "            \"href\": \"http://localhost:82/actuator/info\",\n" +
                "            \"templated\": false\n" +
                "        },\n" +
                "        \"conditions\": {\n" +
                "            \"href\": \"http://localhost:82/actuator/conditions\",\n" +
                "            \"templated\": false\n" +
                "        },\n" +
                "        \"configprops\": {\n" +
                "            \"href\": \"http://localhost:82/actuator/configprops\",\n" +
                "            \"templated\": false\n" +
                "        },\n" +
                "        \"env\": {\n" +
                "            \"href\": \"http://localhost:82/actuator/env\",\n" +
                "            \"templated\": false\n" +
                "        },\n" +
                "        \"env-toMatch\": {\n" +
                "            \"href\": \"http://localhost:82/actuator/env/{toMatch}\",\n" +
                "            \"templated\": true\n" +
                "        },\n" +
                "        \"loggers\": {\n" +
                "            \"href\": \"http://localhost:82/actuator/loggers\",\n" +
                "            \"templated\": false\n" +
                "        },\n" +
                "        \"loggers-name\": {\n" +
                "            \"href\": \"http://localhost:82/actuator/loggers/{name}\",\n" +
                "            \"templated\": true\n" +
                "        },\n" +
                "        \"heapdump\": {\n" +
                "            \"href\": \"http://localhost:82/actuator/heapdump\",\n" +
                "            \"templated\": false\n" +
                "        },\n" +
                "        \"threaddump\": {\n" +
                "            \"href\": \"http://localhost:82/actuator/threaddump\",\n" +
                "            \"templated\": false\n" +
                "        },\n" +
                "        \"metrics\": {\n" +
                "            \"href\": \"http://localhost:82/actuator/metrics\",\n" +
                "            \"templated\": false\n" +
                "        },\n" +
                "        \"metrics-requiredMetricName\": {\n" +
                "            \"href\": \"http://localhost:82/actuator/metrics/{requiredMetricName}\",\n" +
                "            \"templated\": true\n" +
                "        },\n" +
                "        \"scheduledtasks\": {\n" +
                "            \"href\": \"http://localhost:82/actuator/scheduledtasks\",\n" +
                "            \"templated\": false\n" +
                "        },\n" +
                "        \"mappings\": {\n" +
                "            \"href\": \"http://localhost:82/actuator/mappings\",\n" +
                "            \"templated\": false\n" +
                "        },\n" +
                "        \"refresh\": {\n" +
                "            \"href\": \"http://localhost:82/actuator/refresh\",\n" +
                "            \"templated\": false\n" +
                "        },\n" +
                "        \"features\": {\n" +
                "            \"href\": \"http://localhost:82/actuator/features\",\n" +
                "            \"templated\": false\n" +
                "        },\n" +
                "        \"service-registry\": {\n" +
                "            \"href\": \"http://localhost:82/actuator/service-registry\",\n" +
                "            \"templated\": false\n" +
                "        },\n" +
                "        \"dubborestmetadata\": {\n" +
                "            \"href\": \"http://localhost:82/actuator/dubbo/rest/metadata\",\n" +
                "            \"templated\": false\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }
}

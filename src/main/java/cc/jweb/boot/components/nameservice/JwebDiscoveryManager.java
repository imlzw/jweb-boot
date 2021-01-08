package cc.jweb.boot.components.nameservice;

import cc.jweb.boot.utils.lang.StringUtils;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.jfinal.log.Log;
import io.jboot.Jboot;
import io.jboot.app.config.JbootConfigUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Jweb命名服务管理器，管理服务注册与发现
 *
 * @author imlzw@vip.qq.com
 * @platform http://www.jweb.cc
 */
public class JwebDiscoveryManager {

    private static final Log log = Log.getLog(JwebDiscoveryManager.class);

    final static private JwebDiscoveryManager me = new JwebDiscoveryManager();
    private NamingService namingService = null;

    public static JwebDiscoveryManager me() {
        return me;
    }

    public static void main(String[] args) throws NacosException {
        Properties properties = new Properties();
        properties.setProperty("serverAddr", "http://192.168.2.202:8848");
        NamingService namingService = NamingFactory.createNamingService(properties);
        namingService.subscribe("webgw", new EventListener() {
            @Override
            public void onEvent(Event event) {
                System.out.println(((NamingEvent) event).getServiceName());
                System.out.println(((NamingEvent) event).getInstances());
            }
        });
        List<Instance> webgw = namingService.getAllInstances("webgw");
        for (Instance instance : webgw) {
            System.out.println(instance.toString());
        }
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化管理
     */
    public void init() {
        // 初始化配置信息
        Map<String, JwebDiscoveryConfig> configMap = JbootConfigUtil.getConfigModels(JwebDiscoveryConfig.class, "jweb.discovery");
        JwebDiscoveryConfig config = configMap.get("default");
        if (!config.isEnable()) {
            System.out.println("Jweb Discovery disabled!");
            return;
        } else {
            System.out.println("Jweb Discovery enabled!");
        }
        try {
            initNamingService(config);
            // 注册本服务
            try {
                registerLocalInstance(config);
            } catch (NacosException e) {
                log.error("Jweb Rregister LocalService failure! ", e);
            }
        } catch (NacosException e) {
            log.error("Jweb Create NamingService instance failure!", e);
        }

    }

    /**
     * 初始化命名服务对象
     *
     * @param config
     */
    private void initNamingService(JwebDiscoveryConfig config) throws NacosException {
        System.out.print("Jweb Creating NamingService instance ("+config.getRegisterAddress()+") ...");
        Properties properties = new Properties();
        properties.setProperty("serverAddr", config.getRegisterAddress());
        if (StringUtils.notBlank(config.getNamespace())) {
            properties.setProperty("namespace", config.getNamespace());
        }
        namingService = NamingFactory.createNamingService(properties);
        System.out.println(" --> OK!");
    }

    /**
     * 注册本服务到服务发现中心
     *
     * @param config
     */
    public void registerLocalInstance(JwebDiscoveryConfig config) throws NacosException {
        String localInetAddress = JwebDiscoveryKit.getLocalInetAddress(config);
        int port = Integer.parseInt(Jboot.configValue("undertow.port"));
        System.out.print("Jweb Registering LocalService (" + localInetAddress + ":" + port + ") ...");
        Instance instance = new Instance();
        instance.setClusterName(config.getClusterName());
        instance.setServiceName(config.getServiceName());
        instance.setIp(localInetAddress);
        instance.setPort(port);
        instance.setHealthy(true);
        instance.setWeight(1.0);
        // 临时实例会生成心跳反馈程序，持久不会，那如何健康检查呢？
        instance.setEphemeral(config.isEphemeral());
        Map<String, String> instanceMeta = new HashMap<>();
//        instanceMeta.put("site", "et2");
//        instanceMeta.put("site1", "et2");
//        instanceMeta.put("sit3e", "et2");
//        instanceMeta.put("site4", "et2");
        instance.setMetadata(instanceMeta);
        String groupName = config.getGroupName();
        if (StringUtils.notBlank(groupName)) {
            namingService.registerInstance(config.getServiceName(), groupName, instance);
        } else {
            namingService.registerInstance(config.getServiceName(), instance);
        }
        System.out.println(" --> OK!");
//
//        Service service = new Service("nacos.test.4");
//        service.setApp("nacos-naming");
//        service.sethealthCheckMode("server");
//        service.setEnableHealthCheck(true);
//        service.setProtectThreshold(0.8F);
//        service.setGroup("CNCF");
//        Map<String, String> serviceMeta = new HashMap<>();
//        serviceMeta.put("symmetricCall", "true");
//        service.setMetadata(serviceMeta);
//        instance.setService(service);
//
//        Cluster cluster = new Cluster();
//        cluster.setName("TEST");
//        AbstractHealthChecker.None noneHealthChecker = HealthCheckerFactory.createNoneHealthChecker();
//        AbstractHealthChecker.Http healthChecker = new AbstractHealthCheHealthCheckerFactorycker.Http();
//        healthChecker.setExpectedResponseCode(400);
//        healthChecker.setCurlHost("USer-Agent|Nacos");
//        healthChecker.setCurlPath("/xxx.html");
//        cluster.setHealthChecker(healthChecker);
//        Map<String, String> clusterMeta = new HashMap<>();
//        clusterMeta.put("xxx", "yyyy");
//        cluster.setMetadata(clusterMeta);
//
//        instance.setCluster(cluster);

//        naming.registerInstance("nacos.test.4", instance);
    }

    public NamingService getNamingService() {
        return namingService;
    }
}

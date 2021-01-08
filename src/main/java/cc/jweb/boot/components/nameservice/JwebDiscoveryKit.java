package cc.jweb.boot.components.nameservice;

import cc.jweb.boot.utils.lang.StringUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Jweb 服务发现 工具包
 */
public class JwebDiscoveryKit {

    public static String getLocalInetAddress() {
        List<String[]> localIPs = getLocalIPs(null);
        if (localIPs.size() > 0) {
            return localIPs.get(0)[1];
        }
        return null;
    }

    public static String getLocalInetAddress(JwebDiscoveryConfig config) {
        List<String[]> localIPs = getLocalIPs(config);
        if (localIPs.size() > 0) {
            return localIPs.get(0)[1];
        }
        return null;
    }

    /**
     * 获取本机ip地址
     *
     * @param config
     * @return
     */
    public static List<String[]> getLocalIPs(JwebDiscoveryConfig config) {
        LinkedHashMap<String, InetAddress> localIps = getLocalHostAddresses(new ConfigInetAddressFilter(config));
        List<String[]> list = new ArrayList<>();
        if (localIps != null && !localIps.isEmpty()) {
            for (String key : localIps.keySet()) {
                list.add(new String[]{key, localIps.get(key).getHostAddress()});
            }
        }
        if (config != null) {
            String preferredNetworks = config.getPreferredNetworks();
            if (StringUtils.notBlank(preferredNetworks)) {
                String[] ipSorts = preferredNetworks.split(",");
                list.sort((interfacess1, interfacess2) -> {
                    int sort1 = -1;
                    int sort2 = -1;
                    for (int i = 0; i < ipSorts.length; i++) {
                        if (interfacess1[1].indexOf(ipSorts[i]) == 0) {
                            sort1 = ipSorts.length - i;
                            break;
                        }
                    }
                    for (int i = 0; i < ipSorts.length; i++) {
                        if (interfacess2[1].indexOf(ipSorts[i]) == 0) {
                            sort2 = ipSorts.length - i;
                            break;
                        }
                    }
                    return sort2 - sort1;
                });
            }
        }
        return list;
    }

    public static LinkedHashMap<String, InetAddress> getLocalHostAddresses(NameFilter nameFilter) {
        LinkedHashMap<String, InetAddress> map = new LinkedHashMap<>();
        Enumeration<NetworkInterface> netInterfaces;
        try {
            // 拿到所有网卡
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip;
            // 遍历每个网卡，拿到ip
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                if (nameFilter.filter(ni.getDisplayName())) {
                    Enumeration<InetAddress> addresses = ni.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        ip = addresses.nextElement();
                        if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(':') == -1) {
                            map.put(ni.getName() + ":" + ni.getDisplayName(), ip);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    public static void main(String[] args) {
    }

    public static interface NameFilter {
        public boolean filter(String name);
    }

    /**
     * 通过配置过滤网卡
     */
    private static class ConfigInetAddressFilter implements NameFilter {

        private JwebDiscoveryConfig config;

        public ConfigInetAddressFilter(JwebDiscoveryConfig config) {
            this.config = config;
        }

        @Override
        public boolean filter(String name) {
            String lowerCase = name.toLowerCase();
            if (config != null) {
                String ignoredInterfaces = config.getIgnoredInterfaces();
                if (StringUtils.notBlank(ignoredInterfaces)) {
                    String[] splitInterfacess = ignoredInterfaces.split(",");
                    for (String netInterfacess : splitInterfacess) {
                        if (StringUtils.notBlank(netInterfacess) && lowerCase.indexOf(netInterfacess.trim()) >= 0) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
    }
}

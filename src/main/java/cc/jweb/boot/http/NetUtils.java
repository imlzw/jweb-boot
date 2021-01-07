package cc.jweb.boot.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 网络工具集
 */
public class NetUtils {
    // returned port range is [30000, 39999]
    private static final int RND_PORT_START = 30000;
    private static final int RND_PORT_RANGE = 10000;

    public static int getAvailablePort() {
        try (ServerSocket ss = new ServerSocket()) {
            ss.bind(null);
            return ss.getLocalPort();
        } catch (IOException e) {
            return getRandomPort();
        }
    }

    public static int getRandomPort() {
        return RND_PORT_START + ThreadLocalRandom.current().nextInt(RND_PORT_RANGE);
    }


    public static InetAddressFilter nameFilter = new InetAddressFilter();

    public static String getLocalInetAddress() {
        List<String> localIPs = getLocalIPs();
        if (localIPs.size() > 0) {
            return localIPs.get(0);
        }
        return null;
    }
    
    /**
     * 获取本机ip地址
     *
     * @return
     */
    public static List<String> getLocalIPs() {
        LinkedHashMap<String, InetAddress> localIps = getLocalHostAddresses(nameFilter);
        List<String> ips = new ArrayList<>();
        if (localIps != null && !localIps.isEmpty()) {
            for (String key : localIps.keySet()) {
                ips.add(localIps.get(key).getHostAddress());
            }
        }
        return ips;
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


    public static interface NameFilter{
        public boolean filter(String name);
    }


    /**
     * 网卡过滤器，过滤掉虚拟网卡等。
     */
    private static class InetAddressFilter implements NameFilter {

        @Override
        public boolean filter(String name) {
            if (name.toLowerCase().indexOf("virtual") >= 0) {
                return false;
            }
            if (name.toLowerCase().indexOf("hyper-v") >= 0) {
                return false;
            }
            if (name.toLowerCase().indexOf("vmware") >= 0) {
                return false;
            }
            if (name.toLowerCase().indexOf("vmnet") >= 0) {
                return false;
            }
            if (name.toLowerCase().indexOf("tap") >= 0) {
                return false;
            }
            return true;
        }
    }

    public static void main(String[] args) {
        System.out.println(getLocalHostAddresses(nameFilter));
    }
}

package com.acupt.acuprpc.util;

import lombok.SneakyThrows;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * @author liujie
 */
public class IpUtil {

    public static final String HOSTNAME = getHostname();

    public static final String INTRANET_IP = getIntranetIp(); // 内网IP

    public static final String INTERNET_IP = getInternetIp(); // 外网IP

    /**
     * 获得内网IP
     */
    @SneakyThrows
    private static String getHostname() {
        return InetAddress.getLocalHost().getHostName();
    }

    /**
     * 获得内网IP
     */
    @SneakyThrows
    private static String getIntranetIp() {
        return InetAddress.getLocalHost().getHostAddress();
    }

    /**
     * 获得外网IP
     */
    @SneakyThrows
    private static String getInternetIp() {
        Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
        InetAddress ip = null;
        Enumeration<InetAddress> addrs;
        while (networks.hasMoreElements()) {
            addrs = networks.nextElement().getInetAddresses();
            while (addrs.hasMoreElements()) {
                ip = addrs.nextElement();
                if (ip != null
                        && ip instanceof Inet4Address
                        && ip.isSiteLocalAddress()
                        && !ip.getHostAddress().equals(INTRANET_IP)) {
                    System.out.println("outer");
                    return ip.getHostAddress();
                }
            }
        }
        System.out.println("inner");
        // 如果没有外网IP，就返回内网IP
        return INTRANET_IP;
    }

    public static void main(String[] args) {
        System.out.println("内网" + INTRANET_IP);
        System.out.println("外网" + INTERNET_IP);
    }
}

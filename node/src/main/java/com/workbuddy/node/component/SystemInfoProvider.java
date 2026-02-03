package com.workbuddy.node.component;

import com.workbuddy.node.model.NodeInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

@Component
@Slf4j
public class SystemInfoProvider {
    private final OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    private final Runtime runtime = Runtime.getRuntime();


    public NodeInfo buildNodeInfo(String nodeName) {


        return new NodeInfo(
                nodeName,
                getHostName(),
                getIp(),
                getTailscaleIp(),
                getMac(),
                getAvailableProcessors(),
                getCpuLoad(),
                getTotalMemory(),
                memoryUsage(),
                getDiskTotalGb(),
                round(getDiskUsage()),
                getUptimeSeconds()
        );

    }
    /* ===================== Network ===================== */

    private String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    private String getIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "0.0.0.0";
        }
    }

    private String getTailscaleIp() {
        try {
            Enumeration<NetworkInterface> interfaces =
                    NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if (!ni.isUp() || ni.isLoopback()) continue;

                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress adder = addresses.nextElement();
                    String ip = adder.getHostAddress();
                    if (ip.startsWith("100.")) {
                        return ip;
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return "NA";
    }

    private String getMac() {
        try {
            Enumeration<NetworkInterface> interfaces =
                    NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();

                if (ni.isLoopback() || ni.isVirtual() || !ni.isUp()) {
                    continue;
                }

                byte[] mac = ni.getHardwareAddress();
                if (mac == null || mac.length == 0) {
                    continue;
                }

                StringBuilder sb = new StringBuilder();
                for (byte b : mac) {
                    sb.append(String.format("%02X:", b));
                }
                sb.setLength(sb.length() - 1); // remove last ':'

                return sb.toString();
            }
        } catch (Exception e) {
            log.error("Failed to get MAC address", e);
        }
        return "NA";
    }


    /* ===================== CPU & MEMORY ===================== */

    private int getAvailableProcessors() {return runtime.availableProcessors();}

    private long getTotalMemory() {return runtime.totalMemory();}

    private double getCpuLoad() {return round(osBean.getSystemLoadAverage());}

    public double memoryUsage() {
        long used = runtime.totalMemory() - runtime.freeMemory();
        return ((double) used / runtime.totalMemory()) * 100;
    }

    /* ===================== DISK ===================== */

    public long getDiskTotalGb() {
        File root = new File("/");
        return root.getTotalSpace() / (1024 * 1024 * 1024);
    }

    public double getDiskUsage() {
        File root = new File("/");
        long total = root.getTotalSpace();
        long free = root.getFreeSpace();
        long used = total - free;

        return ((double) used / total) * 100;
    }

    /* ===================== UPTIME ===================== */

    public long getUptimeSeconds() {
        return ManagementFactory.getRuntimeMXBean().getUptime() / 1000;
    }

    /* ===================== UTIL ===================== */

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

}

package top.dzurl.pushwebpage.core.util;


import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;

public class OperatingSystemUtil {


    /**
     * 获取可用的内存
     *
     * @return
     */
    public static long getAvailableRAM() {
        return ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getFreePhysicalMemorySize();
    }

    /**
     * 获取可用的CPU使用率
     *
     * @return
     */
    public static double getAvailableCPU() {
        return 1 - ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getSystemCpuLoad();
    }


}

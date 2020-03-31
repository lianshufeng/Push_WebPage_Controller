package top.dzurl.pushwebpage.core.util;


import com.sun.management.OperatingSystemMXBean;

import java.io.File;
import java.lang.management.ManagementFactory;

public class OSUtil {


    /**
     * 获取剩余内存
     *
     * @return
     */
    public static long getAvailableRAM() {
        return ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getFreePhysicalMemorySize();
    }

    /**
     * 获取剩余的CPU
     *
     * @return
     */
    public static long getAvailableCPU() {
        return ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getFreePhysicalMemorySize();
    }


}

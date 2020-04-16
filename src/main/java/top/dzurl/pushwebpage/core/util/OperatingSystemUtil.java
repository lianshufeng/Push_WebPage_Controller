package top.dzurl.pushwebpage.core.util;


import com.sun.management.OperatingSystemMXBean;
import top.dzurl.pushwebpage.core.model.OSAvailableInfo;

import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class OperatingSystemUtil {


    private static final int numberSize = 3;

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


    /**
     * 获取总内存
     *
     * @return
     */
    public static long getTotalRAM() {
        return ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
    }


    /**
     * 获取系统配置
     *
     * @return
     */
    public static OSAvailableInfo getOSAvailableInfo() {
        OSAvailableInfo osAvailableInfo = new OSAvailableInfo();
        osAvailableInfo.setCpu(formatNumber(getAvailableCPU()));
        osAvailableInfo.setMem(formatNumber((double) OperatingSystemUtil.getAvailableRAM() / OperatingSystemUtil.getTotalRAM()));
        return osAvailableInfo;
    }


    /**
     * 格式化
     *
     * @param val
     * @return
     */
    private static double formatNumber(double val) {
        return new BigDecimal(val).setScale(numberSize, RoundingMode.HALF_UP).doubleValue();
    }


}

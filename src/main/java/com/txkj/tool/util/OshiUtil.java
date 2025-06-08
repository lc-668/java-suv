package com.txkj.tool.util;


import com.txkj.tool.util.core.YLazyValue;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OshiUtil {
    private static YLazyValue<OperatingSystem> os = new YLazyValue<>(() -> {
        // 创建一个SystemInfo对象
        SystemInfo si = new SystemInfo();

        // 获取当前的操作系统实例
        OperatingSystem os = si.getOperatingSystem();

        return os;
    });

    public static List<OSProcess> listOSP() {
        // 获取所有正在运行的进程列表
        List<OSProcess> processes = os.getValue().getProcesses();
        // 遍历并打印进程信息
        for (OSProcess p : processes) {
            System.out.println("Process Name: " + p.getName());
            System.out.println("PID: " + p.getProcessID());
            // 可以获取更多进程相关信息，如CPU占用率、内存使用情况等
        }
        return processes;
    }

    public static List<Integer> getAllChild(int pid) {
        List<Integer> collection;
        List<OSProcess> list = listOSP();
        Map<Integer, OSProcess> kvMap = new HashMap<>();
        Map<Integer, List<OSProcess>> parentIdMap = new HashMap<>();
        for (OSProcess osProcess : list) {
            kvMap.put(osProcess.getProcessID(), osProcess);
        }
        parentIdMap = list.stream().collect(Collectors.groupingBy(OSProcess::getParentProcessID));
        if (parentIdMap.get(pid) == null) {
            return Collections.emptyList();
        }
        collection = parentIdMap.get(pid).stream().map(OSProcess::getProcessID).collect(Collectors.toList());
        return collection;
    }

    public static void main(String[] args) {
        listOSP();
        System.out.println(getAllChild(4604));
    }

}

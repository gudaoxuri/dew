package com.tairanchina.csp.dew.core.metric;



import com.ecfront.dew.common.$;

import java.util.Map;

public class MonitorInfo {

    /**
     * 系统总的的cpu占用比
     */
    private double cpuUsage;

    /**
     * 系统总的内存占用比
     */
    private double memUsage;

    /**
     * 系统总的的磁盘占用比
     */
    private double diskUsage;

    /**
     * cpu核心数.
     */
    private int processors;

    /**
     * 每块cpu具体参数->每块cpu百分比参数
     */
//    private Map<CpuInfo, CpuPerc> cpuInfoCpuPercMap;

    /**
     * 总的物理内存.
     */
    private long totalMemorySize;

    /**
     * 剩余的物理内存.
     */
    private long freeMemorySize;

    /**
     * 已使用的物理内存.
     */
    private long usedMemorySize;

    /**
     * 交换区总内存
     */
    private int totalSwapSpaceSize;

    /**
     * 可用的交换区内存
     */
    private int freeSwapSpaceSize;

    /**
     * 可用于当前进程的虚拟内存量
     */
    private int committedVirtualMemorySize;


    // 下方jvm信息

    /**
     * jvm可使用内存.
     */
    private long totalMemory;

    /**
     * jvm剩余内存.
     */
    private long freeMemory;

    /**
     * jvm最大可使用内存.
     */
    private long maxMemory;

    /**
     * 当前线程总数
     */
    private int threadCount;

    /**
     * 自从 Java 虚拟机启动或峰值重置以来峰值活动线程计数
     */
    private int peakThreadCount;

    /**
     * 启动过的线程总数
     */
    private long totalStartedThreadCount;

    /**
     * 当前的守护线程数目
     */
    private int daemonThreadCount;

    /**
     * 线程占用cpu时间集合
     */
    private Map<String, Double> threadTimes;

    public double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public double getMemUsage() {
        return memUsage;
    }

    public void setMemUsage(double memUsage) {
        this.memUsage = memUsage;
    }

    public double getDiskUsage() {
        return diskUsage;
    }

    public void setDiskUsage(double diskUsage) {
        this.diskUsage = diskUsage;
    }

    public int getProcessors() {
        return processors;
    }

    public void setProcessors(int processors) {
        this.processors = processors;
    }



    public long getTotalMemorySize() {
        return totalMemorySize;
    }

    public void setTotalMemorySize(long totalMemorySize) {
        this.totalMemorySize = totalMemorySize;
    }

    public long getFreeMemorySize() {
        return freeMemorySize;
    }

    public void setFreeMemorySize(long freeMemorySize) {
        this.freeMemorySize = freeMemorySize;
    }

    public long getUsedMemorySize() {
        return usedMemorySize;
    }

    public void setUsedMemorySize(long usedMemorySize) {
        this.usedMemorySize = usedMemorySize;
    }

    public int getTotalSwapSpaceSize() {
        return totalSwapSpaceSize;
    }

    public void setTotalSwapSpaceSize(int totalSwapSpaceSize) {
        this.totalSwapSpaceSize = totalSwapSpaceSize;
    }

    public int getFreeSwapSpaceSize() {
        return freeSwapSpaceSize;
    }

    public void setFreeSwapSpaceSize(int freeSwapSpaceSize) {
        this.freeSwapSpaceSize = freeSwapSpaceSize;
    }

    public int getCommittedVirtualMemorySize() {
        return committedVirtualMemorySize;
    }

    public void setCommittedVirtualMemorySize(int committedVirtualMemorySize) {
        this.committedVirtualMemorySize = committedVirtualMemorySize;
    }

    public long getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(long totalMemory) {
        this.totalMemory = totalMemory;
    }

    public long getFreeMemory() {
        return freeMemory;
    }

    public void setFreeMemory(long freeMemory) {
        this.freeMemory = freeMemory;
    }

    public long getMaxMemory() {
        return maxMemory;
    }

    public void setMaxMemory(long maxMemory) {
        this.maxMemory = maxMemory;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public int getPeakThreadCount() {
        return peakThreadCount;
    }

    public void setPeakThreadCount(int peakThreadCount) {
        this.peakThreadCount = peakThreadCount;
    }

    public long getTotalStartedThreadCount() {
        return totalStartedThreadCount;
    }

    public void setTotalStartedThreadCount(long totalStartedThreadCount) {
        this.totalStartedThreadCount = totalStartedThreadCount;
    }

    public int getDaemonThreadCount() {
        return daemonThreadCount;
    }

    public void setDaemonThreadCount(int daemonThreadCount) {
        this.daemonThreadCount = daemonThreadCount;
    }

    public Map<String, Double> getThreadTimes() {
        return threadTimes;
    }

    public void setThreadTimes(Map<String, Double> threadTimes) {
        this.threadTimes = threadTimes;
    }

    @Override
    public String toString() {
        return $.json.toJsonString(this);
    }
}
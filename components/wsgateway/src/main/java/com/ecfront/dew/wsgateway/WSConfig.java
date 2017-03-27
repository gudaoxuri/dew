package com.ecfront.dew.wsgateway;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "server")
public class WSConfig {

    private String wsServiceName;
    private int eventLoopPoolSize;
    private int workerPoolSize;
    private int internalBlockingPoolSize;
    private int maxEventLoopExecuteTime;
    private int maxWorkerExecuteTime;
    private int warningExceptionTime;

    public String getWsServiceName() {
        return wsServiceName;
    }

    public void setWsServiceName(String wsServiceName) {
        this.wsServiceName = wsServiceName;
    }

    public int getEventLoopPoolSize() {
        return eventLoopPoolSize;
    }

    public void setEventLoopPoolSize(int eventLoopPoolSize) {
        this.eventLoopPoolSize = eventLoopPoolSize;
    }

    public int getWorkerPoolSize() {
        return workerPoolSize;
    }

    public void setWorkerPoolSize(int workerPoolSize) {
        this.workerPoolSize = workerPoolSize;
    }

    public int getInternalBlockingPoolSize() {
        return internalBlockingPoolSize;
    }

    public void setInternalBlockingPoolSize(int internalBlockingPoolSize) {
        this.internalBlockingPoolSize = internalBlockingPoolSize;
    }

    public int getMaxEventLoopExecuteTime() {
        return maxEventLoopExecuteTime;
    }

    public void setMaxEventLoopExecuteTime(int maxEventLoopExecuteTime) {
        this.maxEventLoopExecuteTime = maxEventLoopExecuteTime;
    }

    public int getMaxWorkerExecuteTime() {
        return maxWorkerExecuteTime;
    }

    public void setMaxWorkerExecuteTime(int maxWorkerExecuteTime) {
        this.maxWorkerExecuteTime = maxWorkerExecuteTime;
    }

    public int getWarningExceptionTime() {
        return warningExceptionTime;
    }

    public void setWarningExceptionTime(int warningExceptionTime) {
        this.warningExceptionTime = warningExceptionTime;
    }
}

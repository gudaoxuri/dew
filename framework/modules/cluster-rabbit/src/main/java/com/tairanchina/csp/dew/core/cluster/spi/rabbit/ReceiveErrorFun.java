package com.tairanchina.csp.dew.core.cluster.spi.rabbit;

@FunctionalInterface
public interface ReceiveErrorFun {

    void invoke(Exception ex, Object beforeResult);

}

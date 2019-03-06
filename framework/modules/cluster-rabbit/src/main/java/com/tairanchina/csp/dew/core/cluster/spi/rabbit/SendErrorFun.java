package com.tairanchina.csp.dew.core.cluster.spi.rabbit;

@FunctionalInterface
public interface SendErrorFun {

    void invoke(Exception ex, Object beforeResult);

}

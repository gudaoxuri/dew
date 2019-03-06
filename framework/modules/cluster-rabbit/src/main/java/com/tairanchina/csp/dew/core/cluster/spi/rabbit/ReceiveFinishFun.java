package com.tairanchina.csp.dew.core.cluster.spi.rabbit;

@FunctionalInterface
public interface ReceiveFinishFun {

    void invoke(Object beforeResult);

}

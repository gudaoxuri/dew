package com.tairanchina.csp.dew.core.cluster.spi.rabbit;

@FunctionalInterface
public interface SendFinishFun {

    void invoke(Object beforeResult);

}

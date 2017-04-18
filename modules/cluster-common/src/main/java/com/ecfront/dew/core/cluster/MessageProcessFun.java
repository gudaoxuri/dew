package com.ecfront.dew.core.cluster;

@FunctionalInterface
public interface MessageProcessFun<M> {

    Object received(M dto) throws Exception;

}

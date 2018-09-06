package com.tairanchina.csp.dew.idempotent.strategy;


import com.tairanchina.csp.dew.Dew;

public class ItemProcessor implements IdempotentProcessor {

    private static final String CACHE_KEY = "dew:idempotent:item:";

    @Override
    public StatusEnum process(String optType, String optId, StatusEnum initStatus, long expireMs) {
        if (Dew.cluster.cache.setnx(CACHE_KEY + optType + ":" + optId, initStatus.toString(), expireMs / 1000)) {
            // 设置成功，表示之前不存在
            return StatusEnum.NOT_EXIST;
        } else {
            // 设置不成功，表示之前存在，返回存在的值
            String status = Dew.cluster.cache.get(CACHE_KEY + optType + ":" + optId);
            if (status == null || status.isEmpty()) {
                // 设置成功，表示之前不存在
                return StatusEnum.NOT_EXIST;
            } else {
                return StatusEnum.valueOf(status);
            }
        }
    }

    @Override
    public boolean confirm(String optType, String optId) {
        long ttl = Dew.cluster.cache.ttl(CACHE_KEY + optType + ":" + optId);
        if (ttl > 0) {
            Dew.cluster.cache.setex(CACHE_KEY + optType + ":" + optId, StatusEnum.CONFIRMED.toString(), ttl);
        }
        return true;
    }

    @Override
    public boolean cancel(String optType, String optId) {
        Dew.cluster.cache.del(CACHE_KEY + optType + ":" + optId);
        return true;
    }

}

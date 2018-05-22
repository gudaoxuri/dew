package com.tairanchina.csp.dew.core.metric;

import com.tairanchina.csp.dew.Dew;

import java.util.LinkedHashMap;
import java.util.Map;

public class RecordMap<K, V> extends LinkedHashMap<K, V> {

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > Dew.dewConfig.getMetric().getUrlSize();
    }
}

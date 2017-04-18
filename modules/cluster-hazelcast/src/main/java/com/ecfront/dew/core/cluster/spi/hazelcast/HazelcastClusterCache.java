package com.ecfront.dew.core.cluster.spi.hazelcast;

import com.ecfront.dew.core.cluster.ClusterCache;
import com.ecfront.dew.core.cluster.ClusterDist;
import com.ecfront.dew.core.cluster.ClusterDistLock;
import com.ecfront.dew.core.cluster.ClusterDistMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class HazelcastClusterCache implements ClusterCache {

    @Autowired
    private HazelcastAdapter hazelcastAdapter;

    @Override
    public boolean exists(String key) {
        return false;
    }

    @Override
    public String get(String key) {
        return null;
    }

    @Override
    public void set(String key, String value, int expireSec) {

    }

    @Override
    public void set(String key, String value) {

    }

    @Override
    public void del(String key) {

    }

    @Override
    public void lpush(String key, String value) {

    }

    @Override
    public void lmset(String key, List<String> values, int expireSec) {

    }

    @Override
    public void lmset(String key, List<String> values) {

    }

    @Override
    public String lpop(String key) {
        return null;
    }

    @Override
    public long llen(String key) {
        return 0;
    }

    @Override
    public List<String> lget(String key) {
        return null;
    }

    @Override
    public void hmset(String key, Map<String, String> values, int expireSec) {

    }

    @Override
    public void hmset(String key, Map<String, String> values) {

    }

    @Override
    public void hset(String key, String field, String value) {

    }

    @Override
    public String hget(String key, String field) {
        return null;
    }

    @Override
    public boolean hexists(String key, String field) {
        return false;
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        return null;
    }

    @Override
    public void hdel(String key, String field) {

    }

    @Override
    public long incrBy(String key, long incrValue) {
        return 0;
    }

    @Override
    public long decrBy(String key, long decrValue) {
        return 0;
    }

    @Override
    public void expire(String key, int expireSec) {

    }

    @Override
    public void flushdb() {

    }
}

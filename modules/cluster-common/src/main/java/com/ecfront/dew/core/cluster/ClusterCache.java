package com.ecfront.dew.core.cluster;

import java.util.List;
import java.util.Map;

public interface ClusterCache {

    /**
     * key是否存在
     *
     * @param key key
     * @return 是否存在
     */
    boolean exists(String key);

    /**
     * 获取字符串值
     *
     * @param key key
     * @return 值
     */
    String get(String key);

    /**
     * 设置字符串
     *
     * @param key       key
     * @param value     value
     * @param expireSec 过期时间(seconds)，0表示永不过期
     */
    void set(String key, String value, int expireSec);

    /**
     * 设置字符串
     *
     * @param key   key
     * @param value value
     */
    void set(String key, String value);

    /**
     * 删除key
     *
     * @param key key
     */
    void del(String key);

    /**
     * 添加列表值
     *
     * @param key   key
     * @param value value
     */
    void lpush(String key, String value);

    /**
     * 设置列表
     *
     * @param key       key
     * @param values    values
     * @param expireSec 过期时间(seconds)，0表示永不过期
     */
    void lmset(String key, List<String> values, int expireSec);

    /**
     * 设置列表
     *
     * @param key    key
     * @param values values
     */
    void lmset(String key, List<String> values);

    /**
     * 弹出栈顶的列表值
     * 注意，Redis的列表是栈结构，先进后出
     *
     * @param key key
     * @return 栈顶的列表值
     */
    String lpop(String key);

    /**
     * 获取列表值的长度
     *
     * @param key key
     * @return 长度
     */
    long llen(String key);

    /**
     * 获取列表中的所有值
     *
     * @param key key
     * @return 值列表
     */
    List<String> lget(String key);

    /**
     * 设置Hash集合
     *
     * @param key       key
     * @param values    values
     * @param expireSec 过期时间(seconds)，0表示永不过期
     */
    void hmset(String key, Map<String, String> values, int expireSec);

    /**
     * 设置Hash集合
     *
     * @param key    key
     * @param values values
     */
    void hmset(String key, Map<String, String> values);


    /**
     * 修改Hash集合field对应的值
     *
     * @param key   key
     * @param field field
     * @param value value
     */
    void hset(String key, String field, String value);

    /**
     * 获取Hash集合field对应的值
     *
     * @param key   key
     * @param field field
     * @return field对应的值
     */
    String hget(String key, String field);

    /**
     * 判断Hash集合field是否存在
     *
     * @param key   key
     * @param field field
     * @return 是否存在
     */
    boolean hexists(String key, String field);

    /**
     * 获取Hash集合的所有值
     *
     * @param key key
     * @return 所有值
     */
    Map<String, String> hgetAll(String key);

    /**
     * 删除Hash集合是对应的field
     *
     * @param key   key
     * @param field field
     */
    void hdel(String key, String field);

    /**
     * 原子加操作
     *
     * @param key       key，key不存在时会自动创建值为0的对象
     * @param incrValue 要增加的值，必须是Long Int Float 或 Double
     * @return 操作后的值
     */
    long incrBy(String key, long incrValue);

    /**
     * 原子减操作
     *
     * @param key       key不存在时会自动创建值为0的对象
     * @param decrValue 要减少的值，必须是Long  或 Int
     * @return 操作后的值
     */
    long decrBy(String key, long decrValue);

    /**
     * 设置过期时间
     *
     * @param key       key
     * @param expireSec 过期时间(seconds)，0表示永不过期
     */
    void expire(String key, int expireSec);

    void flushdb();

}

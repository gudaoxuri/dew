/*
 * Copyright 2022. the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package group.idealworld.dew.core.cluster;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 缓存服务.
 *
 * @author gudaoxuri
 */
public interface ClusterCache {

    /**
     * key是否存在.
     *
     * @param key key
     * @return 是否存在
     */
    boolean exists(String key);

    /**
     * 获取字符串值.
     *
     * @param key key
     * @return 值
     */
    String get(String key);

    /**
     * 设置字符串值.
     *
     * @param key   key
     * @param value value
     */
    void set(String key, String value);

    /**
     * 设置字符串值.
     *
     * @param key   key
     * @param value value
     */
    void setIfAbsent(String key, String value);

    /**
     * 设置字符串值，带过期时间.
     *
     * @param key       key
     * @param value     value
     * @param expireSec 过期时间(seconds)，0表示永不过期
     */
    void setex(String key, String value, long expireSec);

    /**
     * 字符串不存在时设置值，带过期时间.
     *
     * @param key       key
     * @param value     value
     * @param expireSec 过期时间(seconds)，0表示永不过期
     * @return true 设置成功 , false 设置失败（key已存在）
     */
    boolean setnx(String key, String value, long expireSec);


    /**
     * 设置字符串值，并返回其旧值，不存在时返回null.
     *
     * @param key   key
     * @param value value
     */
    String getSet(String key, String value);

    /**
     * 删除key.
     *
     * @param key key
     */
    void del(String key);

    /**
     * 添加列表值.
     *
     * @param key   key
     * @param value value
     */
    void lpush(String key, String value);

    /**
     * 添加列表.
     *
     * @param key       key
     * @param values    values
     * @param expireSec 过期时间(seconds)，0表示永不过期
     */
    void lmset(String key, List<String> values, long expireSec);

    /**
     * 添加列表.
     *
     * @param key    key
     * @param values values
     */
    void lmset(String key, List<String> values);

    /**
     * 弹出栈顶的列表值.
     * <p>
     * 注意，Redis的列表是栈结构，先进后出
     *
     * @param key key
     * @return 栈顶的列表值
     */
    String lpop(String key);

    /**
     * 获取列表值的长度.
     *
     * @param key key
     * @return 长度
     */
    long llen(String key);

    /**
     * 获取列表中的所有值.
     *
     * @param key key
     * @return 值列表
     */
    List<String> lget(String key);

    /**
     * 添加Set集合.
     *
     * @param key    key
     * @param values values
     */
    void smset(String key, List<String> values);

    /**
     * 添加Set集合.
     *
     * @param key       key
     * @param values    values
     * @param expireSec 过期时间(seconds)，0表示永不过期
     */
    void smset(String key, List<String> values, long expireSec);

    /**
     * 设置Set集合.
     *
     * @param key   key
     * @param value value
     */
    void sset(String key, String value);

    /**
     * 返回一个随机的成员值.
     *
     * @param key key
     * @return 返回值
     */
    String spop(String key);

    /**
     * 获取Set集合的长度.
     *
     * @param key key
     * @return 长度
     */
    long slen(String key);

    /**
     * 删除Set集合对应的values.
     *
     * @param key    key
     * @param values values
     * @return 影响的行数
     */
    long sdel(String key, String... values);

    /**
     * 返回set集合.
     *
     * @param key key
     * @return 值集合
     */
    Set<String> sget(String key);

    /**
     * 设置Hash集合.
     *
     * @param key       key
     * @param items     items
     * @param expireSec 过期时间(seconds)，0表示永不过期
     */
    void hmset(String key, Map<String, String> items, long expireSec);

    /**
     * 设置Hash集合.
     *
     * @param key   key
     * @param items items
     */
    void hmset(String key, Map<String, String> items);


    /**
     * 设置Hash集合field对应的value.
     *
     * @param key   key
     * @param field field
     * @param value value
     */
    void hset(String key, String field, String value);

    /**
     * 设置Hash集合field对应的value.
     *
     * @param key   key
     * @param field field
     * @param value value
     */
    void hsetIfAbsent(String key, String field, String value);


    /**
     * 获取Hash集合field对应的value.
     *
     * @param key   key
     * @param field field
     * @return field对应的value，不存在时返回null
     */
    String hget(String key, String field);

    /**
     * 获取Hash集合的所有items.
     *
     * @param key key
     * @return 所有items
     */
    Map<String, String> hgetAll(String key);

    /**
     * 判断Hash集合field是否存在.
     *
     * @param key   key
     * @param field field
     * @return 是否存在
     */
    boolean hexists(String key, String field);

    /**
     * 获取Hash集合的所有keys.
     *
     * @param key key
     * @return 所有keys
     */
    Set<String> hkeys(String key);

    /**
     * 获取Hash集合的所有values.
     *
     * @param key key
     * @return 所有values
     */
    Set<String> hvalues(String key);

    /**
     * 获取Hash集合的长度.
     *
     * @param key key
     * @return 长度
     */
    long hlen(String key);

    /**
     * 删除Hash集合是对应的field.
     *
     * @param key   key
     * @param field field
     */
    void hdel(String key, String field);

    /**
     * 原子加操作.
     *
     * @param key       key，key不存在时会自动创建值为0的对象
     * @param incrValue 要增加的值，必须是Long Int Float 或 Double
     * @return 操作后的值
     */
    long incrBy(String key, long incrValue);

    /**
     * Hash原子加操作.
     *
     * @param h         h
     * @param hk        hk
     * @param incrValue 要增加的值，必须是Long Int Float 或 Double
     * @return 操作后的值
     */
    long hashIncrBy(String h, String hk, long incrValue);


    /**
     * 原子减操作.
     *
     * @param key       key不存在时会自动创建值为0的对象
     * @param decrValue 要减少的值，必须是Long  或 Int
     * @return 操作后的值
     */
    long decrBy(String key, long decrValue);

    /**
     * 原子减操作.
     *
     * @param h         h
     * @param hk        hk
     * @param decrValue 要减少的值，必须是Long  或 Int
     * @return 操作后的值
     */
    long hashDecrBy(String h, String hk, long decrValue);

    /**
     * 设置过期时间.
     *
     * @param key       key
     * @param expireSec 过期时间(seconds)，0表示永不过期
     */
    void expire(String key, long expireSec);

    /**
     * 获取过期时间（秒）.
     *
     * @param key key
     * @return -2 key不存在，-1 对应的key永不过期，正数 过期时间(seconds)
     */
    long ttl(String key);

    /**
     * 删除当前数据库中的所有Key.
     */
    void flushdb();

    /**
     * 设置bit.
     *
     * @param key    key
     * @param offset offset
     * @param value  值
     * @return 原来的值
     */
    boolean setBit(String key, long offset, boolean value);

    /**
     * 获取指定偏移bit的值.
     *
     * @param key    key
     * @param offset offset
     * @return 指定偏移的值
     */
    boolean getBit(String key, long offset);

}

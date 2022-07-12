package com.iokfine.data.modules.cache;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author void
 * @date 2020/3/19 18:03
 * @desc
 */
@Component
public class FastRedisService {

    @Autowired
    private RedisTemplate fastRedisTemplate;

    private static final String FOLDER_STR = ":";

    /**
     * 删除缓存
     * @param key
     */
    public Boolean remove(String key) {
        if (exists(key)) {
            return fastRedisTemplate.delete(key);
        }
        return false;
    }

    /**
     * 删除缓存
     * @param folder
     * @param key
     * @return
     */
    public Boolean remove(String folder, String key) {
        String realKey = folder.concat(FOLDER_STR).concat(key);
        if (exists(realKey)) {
            fastRedisTemplate.delete(realKey);
            return true;
        }
        return false;
    }

    /**
     * 是否存在key
     * @param key
     * @return
     */
    public Boolean exists(String key){
        return fastRedisTemplate.hasKey(key);
    }

    /**
     * 是否存在key
     * @param key
     * @return
     */
    public Boolean exists(String folder, String key){
        return this.exists(folder.concat(FOLDER_STR).concat(key));
    }

    /**
     * 获取redis对象
     * @param key
     * @return
     */
    public Object get(String key){
        return fastRedisTemplate.opsForValue().get(key);
    }

    /**
     * 获取redis对象-带包名
     * @param folder
     * @param key
     * @return
     */
    public Object get(String folder, String key){
        return this.get(folder.concat(FOLDER_STR).concat(key));
    }

    /**
     * 获取redis对象
     * string-get
     * @param key
     * @param entityClass
     * @param <T>
     * @return
     */
    public <T> T get(String key, Class<T> entityClass){
        Object object = fastRedisTemplate.opsForValue().get(key);
        if(object instanceof Integer
                || object instanceof BigDecimal
                || object instanceof String
                || object instanceof Boolean){
            return (T) object;
        }
        JSONObject jsonObject = (JSONObject) object;
        if(jsonObject==null){
            return null;
        }
        return jsonObject.toJavaObject(entityClass);
    }

    /**
     * 获取redis对象-带包名
     * @param folder
     * @param key
     * @param entityClass
     * @param <T>
     * @return
     */
    public <T> T get(String folder, String key, Class<T> entityClass){
        return this.get(folder.concat(FOLDER_STR).concat(key), entityClass);
    }

    public <T> T getAndSet(String key, Object newVal, Class<T> entityClass){
        Object oldVal = fastRedisTemplate.opsForValue().getAndSet(key,newVal);
        if(oldVal instanceof Integer
                || oldVal instanceof BigDecimal
                || oldVal instanceof String
                || oldVal instanceof Boolean){
            return (T) oldVal;
        }
        JSONObject jsonObject = (JSONObject) oldVal;
        if(jsonObject==null){
            return null;
        }
        return jsonObject.toJavaObject(entityClass);
    }

    /**
     * 获取列表
     * string-get
     * @param key
     * @param entityClass
     * @param <T>
     * @return
     */
    public <T> List<T> list(String key, Class<T> entityClass){
        Object object = fastRedisTemplate.opsForValue().get(key);
        if(object==null){
            return null;
        }
        return ((JSONArray)object).toJavaList(entityClass);
    }

    /**
     * 获取列表-带包名
     * @param folder
     * @param key
     * @param entityClass
     * @param <T>
     * @return
     */
    public <T> List<T> list(String folder, String key, Class<T> entityClass){
        return this.list(folder.concat(FOLDER_STR).concat(key), entityClass);
    }

    /**
     * 设置缓存
     * @param key
     * @param object
     */
    public void set(String key, Object object){
        fastRedisTemplate.opsForValue().set(key, object);
    }

    /**
     * 设置缓存-带过期时间
     * @param key
     * @param object
     * @param timeout
     * @param unit
     */
    public void set(String key, Object object, long timeout, TimeUnit unit){
        fastRedisTemplate.opsForValue().set(key, object, timeout, unit);
    }

    /**
     * 原子操作检查redis值并且设置值,当前redis有则返回false,没有返回true
     * @param key
     * @param object
     * @param timeout
     * @param unit
     */
    public Boolean setIfAbsent(String key, Object object, long timeout, TimeUnit unit){
        Boolean result = fastRedisTemplate.opsForValue().setIfAbsent(key, object, timeout, unit);
        return result;
    }

    /**
     * 设置缓存-带文件夹名称和过期时间
     * @param folder
     * @param key
     * @param object
     * @param timeout
     * @param unit
     */
    public void set(String folder, String key, Object object, long timeout, TimeUnit unit){
        this.set(folder.concat(FOLDER_STR).concat(key), object, timeout, unit);
    }

    /**
     * 设置缓存多久过期
     * @param key
     */
    public void expire(String key, long timeout, TimeUnit unit){
        fastRedisTemplate.expire(key, timeout, unit);
    }
    
    /**
     * 设置缓存到某个时间点过期
     * @param key
     * @param expireDate
     */
    public void expireAt(String key, Date expireDate){
        fastRedisTemplate.expireAt(key, expireDate);
    }

    /**
     * 设置缓存
     * @param key
     * @param object
     */
    public Long increment(String key, long object){
        Long inc = fastRedisTemplate.opsForValue().increment(key, object);
        return inc;
    }

    /**
     * 设置缓存-带过期时间点
     * @param key
     * @param object
     */
    public Long increment(String key, long object, long timeout, TimeUnit unit){
        Long inc = fastRedisTemplate.opsForValue().increment(key, object);
        if(fastRedisTemplate.getExpire(key)==-1){
            fastRedisTemplate.expire(key, timeout, unit);
        }
        return inc;
    }

    /**
     * 设置缓存-带过期时间点
     * @param key
     * @param object
     * @param date
     */
    public Long increment(String key, long object, Date date){
        Long inc = fastRedisTemplate.opsForValue().increment(key, object);
        fastRedisTemplate.expireAt(key, date);
        return inc;
    }

    /**
     * 批量添加元素到set
     * @param key
     * @param value
     */
    public Long sAdd(String key, String value){
        return fastRedisTemplate.opsForSet().add(key, value);
    }
    
    /**
     * 批量添加元素到set
     * @param key
     * @param collection
     */
    public Long sAdd(String key, Collection<String> collection){
        return fastRedisTemplate.opsForSet().add(key, collection.toArray(new String[collection.size()]));
    }

    /**
     * 带包名的批量添加元素到set
     * @param folder
     * @param key
     * @param collection
     * @return
     */
    public Long sAdd(String folder, String key, Collection<String> collection){
        return this.sAdd(folder.concat(FOLDER_STR).concat(key), collection);
    }

    /**
     * 删除某个set值
     * @param key
     * @param value
     * @return
     */
    public Long sRemove(String key, String value){
        return fastRedisTemplate.opsForSet().remove(key, value);
    }
    
    /**
     * 获取set集合
     * @param key
     * @param entityClass
     * @param <T>
     * @return
     */
    public <T> Set<T> sMembers(String key, Class<T> entityClass){
        SetOperations<String, T> setOperations = fastRedisTemplate.opsForSet();
        return setOperations.members(key);
    }

    /**
     * 哈希添加元素
     * @param key
     * @param hashKey
     * @param value
     */
    public void hPut(String key, String hashKey, Object value){
        fastRedisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 带文件夹哈希添加元素
     * @param folder
     * @param key
     * @param hashKey
     * @param value
     */
    public void hPut(String folder, String key, String hashKey, Object value){
        this.hPut(folder.concat(FOLDER_STR).concat(key), hashKey, value);
    }

    /**
     * 哈希批量添加元素
     * @param key
     * @param map
     */
    public void hPutAll(String key, Map map){
        fastRedisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 带文件夹哈希批量添加元素
     * @param folder
     * @param key
     * @param map
     */
    public void hPutAll(String folder, String key, Map map){
        this.hPutAll(folder.concat(FOLDER_STR).concat(key), map);
    }

    public <T> T hGet(String key, String hashKey, Class<T> entityClass){
        Object object = fastRedisTemplate.opsForHash().get(key, hashKey);
        JSONObject jsonObject = (JSONObject) object;
        if(jsonObject==null){
            return null;
        }
        return jsonObject.toJavaObject(entityClass);
    }

    public <T> T hGet(String folder, String key, String hashKey, Class<T> entityClass){
        return this.hGet(folder.concat(FOLDER_STR).concat(key), hashKey, entityClass);
    }

    public Map<String, Object> hEntries(String key){
        Map<String, Object> map = fastRedisTemplate.opsForHash().entries(key);
        return map;
    }

    public void zAdd(String key, String value, double score){
        fastRedisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * zset添加元素，只有第一次新建key设置过期时间
     * @param key
     * @param value
     * @param score
     * @param time
     * @param timeUnit
     */
    public void zAdd(String key, String value, double score, long time, TimeUnit timeUnit){
        fastRedisTemplate.opsForZSet().add(key, value, score);
        if(fastRedisTemplate.getExpire(key)==-1){
            fastRedisTemplate.expire(key, time, timeUnit);
        }
    }

    /**
     * 批量添加元素
     * @param key
     * @param tuples
     * @param time
     * @param timeUnit
     */
    public void zAdd(String key, Set<ZSetOperations.TypedTuple<String>> tuples, long time, TimeUnit timeUnit){
        fastRedisTemplate.opsForZSet().add(key, tuples);
        if(fastRedisTemplate.getExpire(key)==-1){
            fastRedisTemplate.expire(key, time, timeUnit);
        }
    }

    /**
     * 统计zset key大小
     * @param key
     * @return
     */
    public Long zCard(String key){
        return fastRedisTemplate.opsForZSet().zCard(key);
    }

    /**
     * 统计zset区间中数量
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Long zCount(String key, double min, double max){
        return fastRedisTemplate.opsForZSet().count(key, min, max);
    }

    public Set zRangeByScore(String key, double min, double max){
        return fastRedisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * 根据score区间移除
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Long zRemoveRangeByScore(String key, double min, double max){
        return fastRedisTemplate.opsForZSet().removeRangeByScore(key, min, max);
    }
}

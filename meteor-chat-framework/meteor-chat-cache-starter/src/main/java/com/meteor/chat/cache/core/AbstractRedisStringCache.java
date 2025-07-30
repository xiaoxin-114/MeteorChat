package com.meteor.chat.cache.core;

import com.meteor.chat.redis.core.util.RedisUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractRedisStringCache<K, V> implements BatchCache<K, V> {

    private final Class<V> vClass;

    protected AbstractRedisStringCache(){
        ParameterizedType superclass =(ParameterizedType) this.getClass().getGenericSuperclass();
        this.vClass = (Class<V>) superclass.getActualTypeArguments()[1];
    }

    @Override
    public Map<K, V> getBatch(List<K> list) {
        //防御性编码
        if (CollectionUtils.isEmpty(list)){
            return new HashMap<>();
        }
        // 先去重
        list = list.stream().distinct().collect(Collectors.toList());
        // 将list转换成redis里的key列表
        List<String> keys = list.stream()
                .map(this::getKey)
                .collect(Collectors.toList());
        // 从redis中加载数据，返回的结果是以key顺序得到的值，如果redis没有那么对应位置是null
        List<V> vList = RedisUtils.mget(keys, vClass);
        Map<K, V> result = new HashMap<>();
        List<K> needLoadKeys = new ArrayList<>();
        for (int i = 0; i < vList.size(); i++) {
            // 如果是null，说明该key在redis中不存在
            if (vList.get(i) == null){
                needLoadKeys.add(list.get(i));
            }
        }
        Map<K, V> vs = new HashMap<>();
        // 过滤那些redis中不存在，还需要重新加载的数据
        if (!CollectionUtils.isEmpty(needLoadKeys)){
            // 加载数据
            vs = load(needLoadKeys);
            // 转换并存回redis中
            Map<String, V> redisMap = vs.entrySet().stream().collect(Collectors.toMap(e -> getKey(e.getKey()), e -> e.getValue()));
            RedisUtils.mset(redisMap, getExpireTime());
        }
        // 整合数据，key对应的value可能存在vList和vs中
        for (int i = 0; i < list.size(); i++) {
            K key = list.get(i);
            V value = Optional.ofNullable(vList.get(i)).orElse(vs.get(key));
            result.put(key, value);
        }
        return result;
    }

    @Override
    public Collection<V> getList(List<K> list) {
        return getBatch(list).values();
    }

    @Override
    public V get(K k) {
        return getBatch(Collections.singletonList(k)).get(k);
    }

    @Override
    public void deleteBatch(List<K> list) {
        if (CollectionUtils.isEmpty(list)){
            return;
        }
        RedisUtils.del(list.stream()
                .filter(Objects::nonNull)
                .map(k -> getKey(k))
                .collect(Collectors.toList()));
    }

    @Override
    public void delete(K k) {
        deleteBatch(Collections.singletonList(k));
    }

    protected abstract Long getExpireTime();

    public abstract String getKey(K k);

    public abstract Map<K, V> load(List<K> list);
}

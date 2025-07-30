package com.meteor.chat.cache.core;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 批量缓存模型
 * @param <K>
 * @param <V>
 */
public interface BatchCache<K, V> {

    /**
     * 使用旁路缓存获取单个值
     * @param k
     * @return
     */
    V get(K k);
    /**
     * 使用旁路缓存批量获取多个值
     * @param list
     * @return
     */
    Map<K, V> getBatch(List<K> list);

    Collection<V> getList(List<K> list);
    /**
     * 删除缓存中单个值
     * @param k
     * @return
     */
    void delete(K k);
    /**
     * 批量删除缓存中的值
     * @param list
     * @return
     */
    void deleteBatch(List<K> list);

}

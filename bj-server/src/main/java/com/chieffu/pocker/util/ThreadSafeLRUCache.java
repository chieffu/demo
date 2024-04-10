package com.chieffu.pocker.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadSafeLRUCache<K, V> {
    private final int CACHE_SIZE;
    private final ConcurrentHashMap<K, V> cache;
    private final LinkedBlockingQueue<K> queue;

    public ThreadSafeLRUCache(int cacheSize) {
        CACHE_SIZE = cacheSize;
        cache = new ConcurrentHashMap<>(cacheSize);
        queue = new LinkedBlockingQueue<>(cacheSize);
    }

    public V get(K key) {
        V value = cache.get(key);
        if (value != null) {
            // 移除已有元素，重新添加到队列末尾，保持最近访问
            queue.remove(key);
            queue.offer(key);
            return value;
        }
        return null;
    }

    public void put(K key, V value) {
        V existingValue = cache.putIfAbsent(key, value);
        if (existingValue == null) {
            // 新增元素，添加到队列末尾
            if (!queue.offer(key)) {
                // 如果队列已满，移除队首元素（最久未访问）
                K eldestKey = queue.poll();
                cache.remove(eldestKey);
                queue.offer(key);
            }
        } else {
            // 更新现有元素，移除已有元素，重新添加到队列末尾
            queue.remove(key);
            queue.offer(key);
        }
    }

    public void remove(K key) {
        cache.remove(key);
        queue.remove(key);
    }

    public int size() {
        return cache.size();
    }

    public static void main(String[] args) {
        ThreadSafeLRUCache<Integer, String> cache = new ThreadSafeLRUCache<>(2000000); // 缓存最多200M种数据

        // 模拟缓存操作
        for (int i = 0; i < 10000000; i++) { // 添加1000万个数据
            cache.put(i, "Value" + i);
        }

        System.out.println("Cache size: " + cache.size());

        // 访问一些数据，以观察LRU算法的替换效果
        for (int i = 0; i < 5000000; i++) { // 访问500万个数据
            cache.get(i);
        }

        System.out.println("Cache size after access: " + cache.size());

        // 添加更多数据，观察缓存是否达到上限
        for (int i = 10000000; i < 20000000; i++) { // 再添加1000万个数据
            cache.put(i, "Value" + i);
        }

        System.out.println("Cache size after addition: " + cache.size());
    }
}


package com.chieffu.pocker.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int CACHE_SIZE;

    public LRUCache(int cacheSize) {
        // true表示让LinkedHashMap按照访问顺序来进行排序,最近访问的放在头，最老访问的放在尾
        super((int) Math.ceil(cacheSize / 0.75) + 1, 0.75f, true);
        CACHE_SIZE = cacheSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        // 当map中的数据量大于指定的缓存个数的时候，就自动删除最老的数据
        return size() > CACHE_SIZE;
    }

    public static void main(String[] args) {
        LRUCache<Integer, String> cache = new LRUCache<>(2000000); // 缓存最多200M种数据

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

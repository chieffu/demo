package com.chieffu.pocker;

import java.util.*;
import java.util.stream.Collectors;

public class Ma {
    /**
     * 计算n的m阶排列数。
     *
     * @param n 总数，表示排列中的元素个数。
     * @param m 选取数，表示需要排列的元素个数。
     * @return 返回n的m阶排列数。如果m大于n或m小于0，则返回0。
     */
    public static long p(int n, int m) {
        if (m > n || m < 0) return 0;
        long r = 1;
        for (int i = 0; i < m; i++) {
            r *= (n - i);
        }
        return r;
    }

    /**
     * 计算组合数C(n, m)
     *
     * @param n 总数，表示从n个元素中选择
     * @param m 选择的元素个数
     * @return 返回组合数C(n, m)的值。如果m大于n或m小于0，返回0。
     */
    public static long c(int n, int m) {
        if (m > n || m < 0) return 0;
        int k = Math.min((n - m), m);
        return Ma.p(n, k) / Ma.p(k, k);
    }

    public static boolean isStraight(List<Pocker> pockers) {
        pockers.sort(Comparator.comparing(Pocker::getNum));
        boolean straight = true;
        for (int i = 1; i < pockers.size(); i++) {
            if (i == 1 && pockers.get(0).getNum() == 1 && pockers.get(pockers.size() - 1).getNum() == 13) continue;
            if (pockers.get(i).getNum() != pockers.get(i - 1).getNum() + 1) {
                straight = false;
                break;
            }
        }
        return straight;
    }

    public static boolean isFlush(List<Pocker> pockers) {
        pockers.sort(Comparator.comparing(Pocker::getSuit));
        boolean flush = true;
        for (int i = 1; i < pockers.size(); i++) {
            if (pockers.get(i).getSuit() != pockers.get(i - 1).getSuit()) {
                flush = false;
                break;
            }
        }
        return flush;
    }

    public static boolean isOneOfKind(List<Pocker> pockers) {
        pockers.sort(Comparator.comparing(Pocker::getNum));
        boolean oneOfKind = true;
        for (int i = 1; i < pockers.size(); i++) {
            if (pockers.get(i).getNum() != pockers.get(i - 1).getNum()) {
                oneOfKind = false;
                break;
            }
        }
        return oneOfKind;
    }

    /**
     * 返回背包中各个元素出现的次数。
     *
     * @param bag 包含整数元素的列表。
     * @return 一个映射，其中键是列表中的唯一元素，值是该元素在列表中出现的次数。
     */
    public static Map<Integer, Integer> groups(List<Integer> bag) {
        Map<Integer, List<Integer>> map = bag.stream().collect(Collectors.groupingBy(Integer::new));
        Map<Integer, Integer> result = new LinkedHashMap<>();
        for (Integer key : map.keySet()) {
            result.put(key, map.get(key).size());
        }
        return result;
    }

    public static Map<Integer, Integer> groupOfPockerNum(List<Pocker> bag) {
        Map<Integer, List<Pocker>> map = bag.stream().collect(Collectors.groupingBy(Pocker::getNum));
        Map<Integer, Integer> result = new LinkedHashMap<>();
        for (Integer key : map.keySet()) {
            result.put(key, map.get(key).size());
        }
        return result;
    }

    public static Map<SuitEnum, Integer> groupOfSuit(List<Pocker> bag) {
        Map<SuitEnum, List<Pocker>> map = bag.stream().collect(Collectors.groupingBy(Pocker::getSuit));
        Map<SuitEnum, Integer> result = new LinkedHashMap<>();
        for (SuitEnum key : map.keySet()) {
            result.put(key, map.get(key).size());
        }
        return result;
    }


}

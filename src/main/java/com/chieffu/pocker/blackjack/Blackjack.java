package com.chieffu.pocker.blackjack;

import com.chieffu.pocker.Pocker;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Data
public class Blackjack {
    static List<List<List<Integer>>> bags = bags(21);
    private  int[] pai;

    private int[][] pk;

    private List<Pocker> decks;

    public Blackjack(int n) {
        init(n);
        decks = Pocker.randomPocker(n);
    }

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
        return p(n, k) / p(k, k);
    }

    /**
     * 递归生成和为 1 到 n 的所有集合，集合的元素只能取 1-11的数字。
     * 给定一个正整数n，该函数将返回一个列表，其中每个元素也是一个列表，代表了不同袋子的容量组合。
     * 每个袋子的容量由一个整数列表表示，且列表中的整数按照升序排列。
     *
     * @param n 指定的递归迭代的最终点数。
     * @return
     */
    public static List<List<List<Integer>>> bags(int n) {
        List<List<List<Integer>>> results = new ArrayList<>();
        List<List<Integer>> pre = Collections.singletonList(Collections.singletonList(1));
        results.add(pre);
        for (int i = 2; i <= n; i++) {
            List<List<Integer>> next = new ArrayList<>();
            for (List<Integer> list : pre) {
                List<Integer> addOne = new ArrayList<>(list);
                addOne.add(1);
                Collections.sort(addOne);
                next.add(addOne);
            }
            for (List<Integer> list : pre) {
                Map<Integer, Integer> groups = groups(list);
                for (Integer k : groups.keySet()) {
                    if (k >= 11) continue;
                    List<Integer> plusOne = new ArrayList<>(list);
                    plusOne.remove(k);
                    plusOne.add(k + 1);
                    Collections.sort(plusOne);
                    if (!next.contains(plusOne)) {
                        next.add(plusOne);
                    }
                }
            }
            results.add(next);
            pre = next;
            next.sort(Comparator.comparingInt(List::size));
        }
        return results;
    }


    private void init(int n) {
        pai = new int[11];
        for (int i = 1; i <= 10; i++) {
            pai[i] = n * 4;
        }
        pai[10] = n * 4 * 4;
        pk = new int[4][13];
        for (int j = 0; j < 4; j++) {
            for (int k = 0; k < 13; k++) {
                pk[j][k] = n;
            }
        }
    }

    /**
     * 计算剩余牌数
     */
    private int countPai() {
        return Arrays.stream(pai).sum();
    }

    /**
     * 计算点数为dot的牌的张牌
     */
    public int countPai(int dot) {
        if (dot <= 0 || dot > 11) return 0;
        return pai[dot == 11 ? 1 : dot];
    }

    public int countPai(int huaSe, int dot) {
        assert (huaSe >= 0 && huaSe < 4);
        if (dot <= 0 || dot > 11) return 0;
        if (dot == 10)
            return pk[huaSe][9] + pk[huaSe][10] + pk[huaSe][11] + pk[huaSe][12];// 10 J Q K
        else if (dot == 11)
            return pk[huaSe][0]; //A
        else
            return pk[huaSe][dot - 1];
    }

    /**
     * 计算1张牌点数为 dot 的概率
     *
     * @param dot 指定点数
     * @return 事件发生的概率
     */
    public double p1(int dot) {
        return 1.0 * countPai(dot) / countPai();
    }

    /**
     * 计算两张牌点数和为 dot 的概率。
     * 对于给定的正整数n，该函数计算并返回一个与排列组合相关的概率之和。
     * 若n小于等于1或大于等于22，则认为无法计算，直接返回0。
     *
     * @param dot 正整数，条件牌点数。
     * @return 双精度浮点数，表示计算出的概率之和。
     */
    public double p2(int dot) {
        if (dot <= 1 || dot >= 22) return 0;
        double sum = 0;
        long countPai2 = c(countPai(), 2);
        for (int i = 1; i <= dot / 2; i++) {
            double rate;
            if (i == dot - i || i == 1 && dot == 12) {
                rate = 1.0 * c(countPai(i), 2) / countPai2;
            } else {
                rate = 1.0 * countPai(i) * countPai(dot - i) / countPai2;
            }
            sum += rate;
            //if(rate>0) log.info("{} = {} + {}    --  {}",n,i,n-i,rate);
        }
        return sum;
    }

    /**
     * 计算对子的数学期望
     *
     * @param purePairOdds 纯对子（同花色对子）的赔率
     * @param pairOdds     对子（不限花色）的赔率
     * @return 对子的数学期望值
     */
    public double pairExpectation(double purePairOdds, double pairOdds) {
        double purePairRate = checkPurePair();
        return (checkPair() - purePairRate) * (1 + pairOdds) + purePairRate * (1 + purePairOdds);
    }

    /**
     * 计算对子的概率
     *
     * @return 对子的概率
     */
    public double checkPair() {
        long pair = 0;
        for (int i = 0; i < 13; i++) {
            int numI = 0;
            for (int j = 0; j < 4; j++) {
                numI += pk[j][i];
            }
            pair += c(numI, 2);
        }
        return pair * 1.0 / c(countPai(), 2);
    }

    /**
     * 计算纯对子的概率
     *
     * @return 纯对子的概率
     */
    public double checkPurePair() {
        long purePair = 0;
        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 4; j++) {
                purePair += c(pk[j][i], 2);
            }
        }
        return purePair * 1.0 / c(countPai(), 2);
    }

    /**
     * 计算幸运3数学期望
     *
     * @param pureThreeOdds     纯三张（同花色三张）的赔率
     * @param straightFlushOdds 同花顺的赔率
     * @param threeOdds         三张（不限花色）的赔率
     * @param straightOdds      顺子的赔率
     * @param flushOdds         同花的赔率
     * @return 三张牌的数学期望值
     */
    public double luckThreeExpectation(double pureThreeOdds, double straightFlushOdds, double threeOdds, double straightOdds, double flushOdds) {
        // 假设我们有一个函数 checkPattern() 来检测特定组合的存在与否，并返回概率
        // 这里仅作为示例，实际情况请替换为实际的检测方法

        double pureThreeProb = checkPureThree(); // 同色三条的概率
        double straightFlushProb = checkStraightFlush3(); // 同花顺的概率
        double threeProb = checkThreeOfKind(); // 任意三条的概率
        double straightProb = checkStraight3(); // 顺子的概率
        double flushProb = checkFlush3(); // 同花的概率

        // 根据各种情况的赔率和概率计算期望值
        double expectation = pureThreeProb * (1 + pureThreeOdds) +
                straightFlushProb * (1 + straightFlushOdds) +
                (threeProb - pureThreeProb) * (1 + threeOdds) +
                (straightProb - straightFlushProb) * (1 + straightOdds) +
                (flushProb - pureThreeProb - straightFlushProb) * (1 + flushOdds);

        return expectation;
    }

    /**
     * 计算三条同色的概率
     *
     * @return
     */
    public double checkPureThree() {
        long pureThree = 0;
        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 4; j++) {
                pureThree += c(pk[j][i], 3);
            }
        }
        return pureThree * 1.0 / c(countPai(), 3);
    }

    /**
     * 计算3张同花顺的概率
     *
     * @return
     */
    public double checkStraightFlush3() {
        long straightFlush = 0;
        for (int j = 0; j < 4; j++) {
            for (int i = 1; i < 13; i++) {
                straightFlush += pk[j][i - 1] * pk[j][i] * pk[j][i == 12 ? 0 : i + 1];
            }
        }
        return straightFlush * 1.0 / c(countPai(), 3);
    }

    /**
     * 计算三条的概率
     *
     * @return
     */
    public double checkThreeOfKind() {
        long three = 0;
        for (int i = 0; i < 13; i++) {
            int numI = 0;
            for (int j = 0; j < 4; j++) {
                numI += pk[j][i];
            }
            three += c(numI, 3);
        }
        return three * 1.0 / c(countPai(), 3);
    }

    /**
     * 计算顺子的概率
     *
     * @return
     */
    public double checkStraight3() {
        long straight = 0;
        int[] nums = new int[13];
        for (int i = 0; i < 13; i++) {
            nums[i] = 0;
            for (int j = 0; j < 4; j++) {
                nums[i] += pk[j][i];
            }
        }
        for (int k = 1; k < 13; k++) {
            straight += nums[k - 1] * nums[k] * nums[k == 12 ? 0 : k + 1];
        }
        return straight * 1.0 / c(countPai(), 3);
    }

    /**
     * 计算同花的概率
     *
     * @return
     */
    public double checkFlush3() {
        long flush = 0;
        for (int i = 0; i < 4; i++) {
            flush += c(Arrays.stream(pk[i]).sum(), 3);
        }
        return flush * 1.0 / c(countPai(), 3);
    }

    /**
     * 计算 烫三手 数学期望
     *
     * @param threeSevenOdds 三张都为7的 赔率
     * @param flushOdds21    同花21点 赔率
     * @param odds21         非同花21点 赔率
     * @param odds20         20点 赔率
     * @param odds19         19点 赔率
     * @return
     */
    public double hotThreeExpectation(double threeSevenOdds, double flushOdds21, double odds21, double odds20, double odds19) {
        double threeSevenRate = c(countPai(7), 3) * 1.0 / c(countPai(), 3);
        double flush21Rate = checkFlush21Of3Rate();
        double p3_21 = p3(21);
        double p3_20 = p3(20);
        double p3_19 = p3(19);
        return threeSevenRate * (threeSevenOdds + 1) + flush21Rate * (flushOdds21 + 1) + (p3_21 - flush21Rate) * (odds21 + 1) + p3_20 * (odds20 + 1) + p3_19 * (odds19 + 1);

    }

    /**
     * 幸运女皇函数
     * 这个函数用于计算某种游戏或赌博中的赢利概率，考虑了多种不同的情况。
     *
     * @param luckyQueenWithBjOdds 与黑杰克一起出现的幸运女皇的概率。
     * @param luckyQueenOdds       出现幸运女皇 红桃Q对的概率。
     * @param purePairOdds         出现同花色对的概率。
     * @param OddsPure20           出现同花色20点的概率。
     * @param odds20               出现任意20点的概率。
     * @return 返回计算出的赢利概率，当前实现中总是返回0。
     */
    public double luckyQueenExpectation(double luckyQueenWithBjOdds, double luckyQueenOdds, double purePairOdds, double OddsPure20, double odds20) {
        long purePairQueue = c(pk[1][11],2);
        int countPai = countPai();
        double luckyQueenWithBj = 1.0 * c(countPai(1),1) * c(countPai(10)-2,1) / 2 * purePairQueue / c(countPai, 4);
        double luckyQueen = 1.0*purePairQueue/c(countPai,2);
        double purePair = checkPurePair();
        double pureP2_20 = checkFlush20Of2Rate();
        double p2_20 = p2(20);

        return (luckyQueenWithBjOdds+1) * luckyQueenWithBj  + (luckyQueenOdds+1) * (luckyQueen -luckyQueenWithBj) + (purePairOdds+1) * (purePair-luckyQueen) + (OddsPure20+1) * pureP2_20 + (odds20+1) * (p2_20 - pureP2_20);
    }

    /**
     * 计算3张同花 21 点的概率
     *
     * @return
     */
    public double checkFlush21Of3Rate() {
        List<List<Integer>> bag21 = bags.get(21 - 1).stream().filter(l -> l.size() == 3).collect(Collectors.toList());
        long flush21 = 0;
        for (int i = 0; i < 4; i++) {
            for (List<Integer> bag : bag21) {
                Map<Integer, Integer> groups = groups(bag);
                long r = 1;
                for (Integer k : groups.keySet()) {
                    int countK = countPai(i, k);
                    if (k == 11 && groups.containsKey(1) && r != 0)
                        r = r / c(countK, groups.get(1)) * c(countK, groups.get(k) + groups.get(1));
                    else
                        r *= c(countK, groups.get(k));
                }
                flush21 += r;
            }
        }
        return flush21 * 1.0 / c(countPai(), 3);
    }

    /**
     * 计算2张同花 20 点的概率
     *
     * @return
     */
    public double checkFlush20Of2Rate() {
        List<List<Integer>> bag21 = bags.get(20 - 1).stream().filter(l -> l.size() == 2).collect(Collectors.toList());
        long flush21 = 0;
        for (int i = 0; i < 4; i++) {
            for (List<Integer> bag : bag21) {
                Map<Integer, Integer> groups = groups(bag);
                long r = 1;
                for (Integer k : groups.keySet()) {
                    int countK = countPai(i, k);
                    if (k == 11 && groups.containsKey(1) && r != 0)
                        r = r / c(countK, groups.get(1)) * c(countK, groups.get(k) + groups.get(1));
                    else
                        r *= c(countK, groups.get(k));
                }
                flush21 += r;
            }
        }
        return flush21 * 1.0 / c(countPai(), 2);
    }

    /**
     * 计算3张点数为dot的概率
     *
     * @return
     */
    public double p3(int dot) {
        List<List<Integer>> bag21 = bags.get(dot - 1).stream().filter(l -> l.size() == 3).collect(Collectors.toList());
        long rate21 = 0;

        for (List<Integer> bag : bag21) {
            Map<Integer, Integer> groups = groups(bag);
            long r = 1;
            for (Integer k : groups.keySet()) {
                int countK = countPai(k);
                if (k == 11 && groups.containsKey(1) && r != 0)
                    r = r / c(countK, groups.get(1)) * c(countK, groups.get(k) + groups.get(1));
                else
                    r *= c(countK, groups.get(k));
            }
            rate21 += r;
        }

        return rate21 * 1.0 / c(countPai(), 3);
    }


    public double zPp(int current, int n) {
        List<List<Integer>> bagN = bags.get(n - 1);
        double sum = 0;
        for (List<Integer> bag : bagN) {
            sum += zRate(bag, current);
        }
        return sum;
    }


    public double zPp(int n) {
        List<List<Integer>> bagN = bags.get(n - 1);
        double sum = 0;
        for (List<Integer> bag : bagN) {
            sum += zRate(bag, 0);
        }
        return sum;
    }

    public double xPp(int threshold, int n) {
        List<List<Integer>> bagN = bags.get(n - 1);
        double sum = 0;
        for (List<Integer> bag : bagN) {
            sum += rate(bag, 0, threshold);
        }
        return sum;
    }

    public double xPp(int current, int threshold, int n) {
        List<List<Integer>> bagN = bags.get(n - 1);
        double sum = 0;
        for (List<Integer> bag : bagN) {
            sum += rate(bag, current, threshold);
        }
        return sum;
    }


    /**
     * 表示庄家只有2张牌且不爆牌的概率
     *
     * @return 双精度浮点数，表示庄家只有2张牌且不爆牌的概率。
     */
    public double z2() {
        double sum = 0;
        for (int i = 17; i <= 21; i++) {
            sum += p2(i);
        }
        return sum;
    }

    public double rZNotBloom() {
        double sum = 0;
        for (int i = 17; i <= 21; i++) {
            sum += zPp(i);
        }
        return sum;
    }

    public double rZNotBloom(int current) {
        if (current >= 17 && current <= 21) return 1;
        double sum = 0;
        for (int i = 17 - current; i <= 21 - current; i++) {
            sum += zPp(current, i);
        }
        return sum;
    }

    public double rXNotBloom(int current, int threshold) {
        if (current >= threshold && current <= 21) return 1;
        double sum = 0;
        for (int i = threshold - current; i <= 21 - current; i++) {
            sum += xPp(current, threshold, i);
        }
        return sum;
    }

    public double rXBloom(int threshold) {
        int bloomCard = 22 - threshold;
        List<List<Integer>> bagI = bags.get(threshold - 1);
        double rates = 0.0;
        for (List<Integer> bag : bagI) {
            Map<Integer, Integer> groups = groups(bag);
            //TODO
        }
        return rates;
    }

    public double rBjWin() {
        double bj = p2(21);
        double bjbj = 1.0 * c(countPai(1), 2) * c(countPai(10), 2) * 16 / p(countPai(), 4);
        return bj - bjbj;
    }


    /**
     * 计算 庄家当前牌面为current时，出现 指定ks列表中牌的概率
     *
     * @param ks      指定牌面列表，代表某种特定的状态集合。
     * @param current 当前的点数值。
     * @return 返回满足条件的概率值，若不满足条件则返回0。
     */
    private double zRate(List<Integer> ks, int current) {
        Map<Integer, Integer> groups = groups(ks);
        int size = ks.size();
        // 如果列表长度大于等于8 (最小点数为2，8张2为16点，只要出现过1张A（A可作为11点，则早就出现了超过16点的情况，这种场景不会出现
        // 长度大于等于5且包含1但不包含11，包含1时，A可以做11点计算，这时至少超过16点了，要么爆牌要么超16点，概率为0
        if (size >= 8 || size >= 5 && groups.containsKey(1) && !groups.containsKey(11)) return 0;

        // 计算最终庄家点数 sum0
        int sum0 = ks.stream().mapToInt(Integer::intValue).sum() + current;
        double count = 1;

        for (Integer k : groups.keySet()) {
            if (k == 1 && groups.containsKey(11)) {
                int m = groups.get(k) + groups.get(11);
                count *= c(countPai(k), m);
            } else if (!(k == 11 && groups.containsKey(1))) {
                int m = groups.get(k);
                count *= c(countPai(k), m);
            }
        }
        if (sum0 >= 17) {
            // 计算超过16的部分
            int theLast = sum0 - 16;
            int theLastCount = 0; //最后一张牌出来之前，所有点数之和不能超过16点
            for (int i = size - 1; i >= 0; i--) {
                if (ks.get(i) >= theLast) {
                    theLastCount++;
                }
            }
            if (!groups.containsKey(1)) {
                count = count * theLastCount / size;
                return count / c(countPai(), size);
            }
        }

        return count / c(countPai(), size);
    }

    /**
     * 返回背包中各个元素出现的次数。
     *
     * @param bag 包含整数元素的列表。
     * @return 一个映射，其中键是列表中的唯一元素，值是该元素在列表中出现的次数。
     */
    private static Map<Integer, Integer> groups(List<Integer> bag) {
        Map<Integer, List<Integer>> map = bag.stream().collect(Collectors.groupingBy(Integer::new));
        Map<Integer, Integer> result = new LinkedHashMap<>();
        for (Integer key : map.keySet()) {
            result.put(key, map.get(key).size());
        }
        return result;
    }

    private double rate(List<Integer> ks, int current, int threshold) {
        Map<Integer, Integer> groups = groups(ks);
        int size = ks.size();
        if (size >= 8 || size >= 5 && groups.containsKey(1) && !groups.containsKey(11)) return 0;
        int sum0 = ks.stream().mapToInt(Integer::intValue).sum() + current;
        double count = 1;
        if (groups.containsKey(1) && groups.containsKey(10) && !groups.containsKey(11) && sum0 > threshold) {
            return 0;
        }
        for (Integer k : groups.keySet()) {
            if (k == 1 && groups.containsKey(11)) {
                int m = groups.get(k) + groups.get(11);
                count *= c(countPai(k), m);
            } else if (!(k == 11 && groups.containsKey(1))) {
                int m = groups.get(k);
                count *= c(countPai(k), m);
            }
        }
        if (sum0 >= threshold) {
            int theLast = sum0 - threshold + 1;
            int theLastCount = 0;
            for (int i = size - 1; i >= 0; i--) {
                if (ks.get(i) >= theLast) {
                    theLastCount++;
                }
            }
            if (groups.containsKey(1) && groups.containsKey(11) && sum0 > threshold) {
                theLastCount -= groups.get(11);
            }
            count = count * theLastCount / size;
        }
        return count / c(countPai(), size);
    }

    public double rXWin() {
        double bjHe = 1.0 * c(countPai(1), 2) * c(countPai(10), 2) * 16 / p(countPai(), 4);

        double[] zPps = new double[22];
        for (int i = 17; i <= 21; i++) {
            zPps[i] = zPp(i);
        }
        double[][] xPps = new double[18][22];
        double maxRate = 0;
        for (int threshold = 12; threshold <= 17; threshold++) {
            xPps[threshold] = new double[22];
            for (int i = threshold; i <= 21; i++) {
                xPps[threshold][i] = xPp(threshold, i);
            }
            double zNotBloom = Arrays.stream(zPps).sum();
            double zBloom = 1 - zNotBloom;
            double x17Ping = zPps[17] * xPps[threshold][17];
            double x18Ping = zPps[18] * xPps[threshold][18];
            double x19Ping = zPps[19] * xPps[threshold][19];
            double x20Ping = zPps[20] * xPps[threshold][20];
            double x21Ping = (zPps[21] - p2(21)) * (xPps[threshold][21] - p2(21)) + bjHe;
            double z17Lose = zPps[17] * (xPps[threshold][18] + xPps[threshold][19] + xPps[threshold][20] + xPps[threshold][21] - p2(21));
            double z18Lose = zPps[18] * (xPps[threshold][19] + xPps[threshold][20] + xPps[threshold][21] - p2(21));
            double z19Lose = zPps[19] * (xPps[threshold][20] + xPps[threshold][21] - p2(21));
            double z20Lose = zPps[20] * (xPps[threshold][21] - p2(21));
            double ping = x17Ping + x18Ping + x19Ping + x20Ping + x21Ping + bjHe;
            double zLose = (zBloom + z17Lose + z18Lose + z19Lose + z20Lose) * 2;
            double bjWin = (rBjWin() - bjHe) * 2.5;
            double rate = ping + zLose + bjWin;
            if (maxRate < rate) {
                maxRate = rate;
            }
        }
        return maxRate;
    }


    public void removePocker(List<Pocker> pks) {
        for (Pocker p : pks) {
            removePocker(p);
        }
    }

    public void removePocker(Pocker p) {
        if (p != null) {
            int dot = dot(p);
            if (pai[dot] <= 0) {
                throw new RuntimeException("扑克牌" + p.toString() + "多了");
            }
            pai[dot]--;
            if (pk[p.getSuit().getHuaSe()][p.getNum()-1] <= 0) {
                throw new RuntimeException("扑克牌" + p.toString() + "多了");
            }
            pk[p.getSuit().getHuaSe()][p.getNum()-1]--;
        }
    }

    public static int dot(Pocker pocker) {
        return pocker.getNum() >= 10 ? 10 : pocker.getNum();
    }

    public static int[] dots(List<Pocker> pockers) {
        return dots(pockers.toArray(new Pocker[0]));
    }

    public static int[] dots(Pocker... pockers) {
        int sum = Arrays.stream(pockers).mapToInt(Blackjack::dot).sum();
        if (sum <= 11 && Arrays.stream(pockers).anyMatch(p -> p.getNum() == 1)) {
            return new int[]{sum, sum + 10};
        }
        return new int[]{sum};
    }



    private static void printBag() {
        for (int i = 1; i <= 21; i++) {
            List<List<Integer>> bagI = bags.get(i - 1);
            log.info("bag {} ---------------------------- count {} ", i, bagI.size());
            for (List<Integer> bag : bagI) {
                log.info("bag {} --{}", i, bag.stream().map(String::valueOf).collect(Collectors.joining(",")));
            }
        }
    }

    private static void test0() {
        Blackjack blackjack = new Blackjack(8);
        printBag();
        double sum2 = 0;
        int threshold = 13;
        for (int i = 1; i <= 21; i++) {
            double p1 = blackjack.p1(i);
            double p2 = blackjack.p2(i);
            double zPp = blackjack.zPp(i);
            double xPp = blackjack.xPp(threshold, i);
            if (i >= threshold) {
                sum2 += xPp;
            }
            log.info("{} -----p1={}  ----- p2={} ----- zPp={} ---- xPp={}", i, p1, p2, zPp, xPp);
        }
        log.info(" sum2 = {}", sum2);
        log.info(" rBjWin = {}", blackjack.rBjWin());
        log.info(" z2 = {}", blackjack.z2());
        log.info(" zNotBloom = {}", blackjack.rZNotBloom());
//        for (int i = 0; i <= 21; i++) {
//            log.info("{} - zNotBloom {}", i, blackjack.rZNotBloom(i));
//        }
//        for (int th = 12; th <= 17; th++) {
//            for (int i = 0; i <= 21; i++) {
//                log.info("{} {} - XNotBloom {}", th, i, blackjack.rXNotBloom(i, th));
//            }
//        }
        log.info("对子 ： {} ", blackjack.pairExpectation(25, 8));
        log.info("幸运3 ： {} ", blackjack.luckThreeExpectation(100, 40, 30, 10, 5));
        log.info("烫三手 ： {} ", blackjack.hotThreeExpectation(100, 20, 4, 2, 1));
        log.info("幸运女皇 ： {} ", blackjack.luckyQueenExpectation(1000, 125, 19, 9,4));
    }

    public static void main(String[] args) {
         test0();

    }
}

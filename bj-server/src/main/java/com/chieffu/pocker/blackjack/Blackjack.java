package com.chieffu.pocker.blackjack;

import com.chieffu.pocker.Ma;
import com.chieffu.pocker.Pocker;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Data
public class Blackjack extends Ma {
    static List<List<List<Integer>>> bags = bags(21);
    private int[] pai;

    private int[][] pk;

    public Blackjack() {
        this(8);
    }

    public Blackjack(int n) {
        init(n);
    }

    /**
     * 计算点数。
     *
     * @param pocker 扑克牌对象。
     * @return 扑克牌的点数。
     */
    public static int dot(Pocker pocker) {
        return pocker.getNum() >= 10 ? 10 : pocker.getNum();
    }

    public static int[] dotsOfPocker(List<Pocker> pockers) {
        return dots(pockers.toArray(new Pocker[0]));
    }

    /**
     * 递归生成和为 1 到 n 的所有集合，集合的元素只能取 1-11的数字。
     * 给定一个正整数n，该函数将返回一个列表，其中每个元素也是一个列表，代表了不同袋子的容量组合。
     * 每个袋子的容量由一个整数列表表示，且列表中的整数按照升序排列。
     *
     * @param n 指定的递归迭代的最终点数。
     * @return
     */
    protected static List<List<List<Integer>>> bags(int n) {
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
                Map<Integer, Integer> groups = Ma.groups(list);
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

    public static int[] dots(Pocker... pockers) {
        int sum = Arrays.stream(pockers).mapToInt(Blackjack::dot).sum();
        if (sum <= 11 && Arrays.stream(pockers).anyMatch(p -> p.getNum() == 1)) {
            return new int[]{sum, sum + 10};
        }
        return new int[]{sum};
    }

    public static int[] dots(List<Integer> dots) {
        int sum = dots.stream().mapToInt(Integer::intValue).sum();
        if (sum <= 11 && dots.stream().anyMatch(p -> p == 1)) {
            return new int[]{sum, sum + 10};
        }
        return new int[]{sum};
    }


    public static boolean isBlackjack(List<Integer> cards) {
        return cards.size() == 2 && cards.contains(1) && cards.contains(10);
    }

    public static int countPai(int[] pai) {
        return Arrays.stream(pai).sum();
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
    public int countPai() {
        return countPai(pai);
    }

    /**
     * 计算点数为dot的牌的张牌
     */
    public int countPai(int dot) {
        if (dot <= 0 || dot > 11) return 0;
        return pai[dot == 11 ? 1 : dot];
    }

    /**
     * 计算花色为huaSe，点数为dot的牌的张数
     *
     * @param huaSe 花色
     * @param dot   点数
     * @return 张数
     */
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
        double countPai2 = c(countPai(), 2);
        for (int i = 1; i <= dot / 2; i++) {
            double rate;
            if (i == dot - i || i == 1 && dot == 12) {
                rate = c(countPai(i), 2) / countPai2;
            } else {
                rate = countPai(i) * countPai(dot - i) / countPai2;
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
    public double expPair(double purePairOdds, double pairOdds) {
        double purePairRate = rPurePair();
        return (rPair() - purePairRate) * (1 + pairOdds) + purePairRate * (1 + purePairOdds);
    }

    /**
     * 计算对子的概率
     *
     * @return 对子的概率
     */
    public double rPair() {
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
    public double rPurePair() {
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
    public double expLuckThree(double pureThreeOdds, double straightFlushOdds, double threeOdds, double straightOdds, double flushOdds) {
        // 假设我们有一个函数 rPattern() 来检测特定组合的存在与否，并返回概率
        // 这里仅作为示例，实际情况请替换为实际的检测方法

        double pureThreeProb = rPureThree(); // 同色三条的概率
        double straightFlushProb = rStraightFlush3(); // 同花顺的概率
        double threeProb = rThreeOfKind(); // 任意三条的概率
        double straightProb = rStraight3(); // 顺子的概率
        double flushProb = rFlush3(); // 同花的概率

        // 根据各种情况的赔率和概率计算期望值
        double exp = pureThreeProb * (1 + pureThreeOdds) +
                straightFlushProb * (1 + straightFlushOdds) +
                (threeProb - pureThreeProb) * (1 + threeOdds) +
                (straightProb - straightFlushProb) * (1 + straightOdds) +
                (flushProb - pureThreeProb - straightFlushProb) * (1 + flushOdds);

        return exp;
    }

    /**
     * 计算三条同色的概率
     *
     * @return
     */
    public double rPureThree() {
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
    public double rStraightFlush3() {
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
    public double rThreeOfKind() {
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
    public double rStraight3() {
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
    public double rFlush3() {
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
    public double expHotThree(double threeSevenOdds, double flushOdds21, double odds21, double odds20, double odds19) {
        double threeSevenRate = c(countPai(7), 3) * 1.0 / c(countPai(), 3);
        double flush21Rate = rFlush21Of3Rate();
        double p3_21 = p3(21);
        double p3_20 = p3(20);
        double p3_19 = p3(19);
        return threeSevenRate * (threeSevenOdds + 1) + flush21Rate * (flushOdds21 + 1) + (p3_21 - flush21Rate) * (odds21 + 1) + p3_20 * (odds20 + 1) + p3_19 * (odds19 + 1);

    }

    /**
     * 幸运女皇
     * 这个函数用于计算某种游戏或赌博中的赢利概率，考虑了多种不同的情况。
     *
     * @param luckyQueenWithBjOdds 与黑杰克一起出现的幸运女皇的概率。
     * @param luckyQueenOdds       出现幸运女皇 红桃Q对的概率。
     * @param purePairOdds         出现同花色对的概率。
     * @param OddsPure20           出现同花色20点的概率。
     * @param odds20               出现任意20点的概率。
     * @return 返回计算出的赢利概率，当前实现中总是返回0。
     */
    public double expLuckyQueen(double luckyQueenWithBjOdds, double luckyQueenOdds, double purePairOdds, double OddsPure20, double odds20) {
        long purePairQueue = c(pk[1][11], 2);
        int countPai = countPai();
        double luckyQueenWithBj = 1.0 * c(countPai(1), 1) * c(countPai(10) - 2, 1) / 2 * purePairQueue / c(countPai, 4);
        double luckyQueen = 1.0 * purePairQueue / c(countPai, 2);
        double purePair = rPurePair();
        double pureP2_20 = rFlush20Of2Rate();
        double p2_20 = p2(20);

        return (luckyQueenWithBjOdds + 1) * luckyQueenWithBj + (luckyQueenOdds + 1) * (luckyQueen - luckyQueenWithBj) + (purePairOdds + 1) * (purePair - luckyQueen) + (OddsPure20 + 1) * (pureP2_20 - luckyQueen) + (odds20 + 1) * (p2_20 - pureP2_20 - luckyQueen);
    }

    /**
     * 计算 庄家爆破的 数学期望
     *
     * @return
     */
    public double expBloom(double bloom3Odds, double bloom4Odds, double bloom5Odds, double bloom6Odds, double bloom7Odds, double bloom8Odds) {

        List<Stage> stages = Stage.getZEndStage(0);
        Map<Integer, List<Stage>> groups = stages.stream().filter(s -> s.largeDot() > 21).collect(Collectors.groupingBy(s -> s.getCards().size()));
        double rate3 = Optional.ofNullable(groups.get(3)).orElse(Collections.emptyList()).stream().mapToDouble(s -> s.zRate(pai)).sum();
        double rate4 = Optional.ofNullable(groups.get(4)).orElse(Collections.emptyList()).stream().mapToDouble(s -> s.zRate(pai)).sum();
        double rate5 = Optional.ofNullable(groups.get(5)).orElse(Collections.emptyList()).stream().mapToDouble(s -> s.zRate(pai)).sum();
        double rate6 = Optional.ofNullable(groups.get(6)).orElse(Collections.emptyList()).stream().mapToDouble(s -> s.zRate(pai)).sum();
        double rate7 = Optional.ofNullable(groups.get(7)).orElse(Collections.emptyList()).stream().mapToDouble(s -> s.zRate(pai)).sum();
        double rate8 = Optional.ofNullable(groups.get(8)).orElse(Collections.emptyList()).stream().mapToDouble(s -> s.zRate(pai)).sum();
        double rate9 = Optional.ofNullable(groups.get(9)).orElse(Collections.emptyList()).stream().mapToDouble(s -> s.zRate(pai)).sum();
        double rate10 = Optional.ofNullable(groups.get(10)).orElse(Collections.emptyList()).stream().mapToDouble(s -> s.zRate(pai)).sum();

        return rate3 * bloom3Odds + rate4 * bloom4Odds + rate5 * bloom5Odds + rate6 * bloom6Odds + rate7 * bloom7Odds + (rate8 + rate9 + rate10) * bloom8Odds;

    }

    /**
     * 计算3张同花 21 点的概率
     *
     * @return
     */
    public double rFlush21Of3Rate() {
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
        return flush21 / (double) c(countPai(), 3);
    }

    /**
     * 计算2张同花 20 点的概率
     *
     * @return
     */
    public double rFlush20Of2Rate() {
        List<List<Integer>> bag20 = bags.get(20 - 1).stream().filter(l -> l.size() == 2).collect(Collectors.toList());
        long flush20 = 0;
        for (int i = 0; i < 4; i++) {
            for (List<Integer> bag : bag20) {
                Map<Integer, Integer> groups = groups(bag);
                long r = 1;
                for (Integer k : groups.keySet()) {
                    int countK = countPai(i, k);
                    r *= c(countK, groups.get(k));
                }
                flush20 += r;
            }
        }
        return flush20 / (double) c(countPai(), 2);
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

    /**
     * 表示庄家不爆牌的概率
     *
     * @return 双精度浮点数，表示庄家不爆牌的概率。
     */
    public double rZNotBloom(int currentDot) {
        Map<Integer, Double> rates = Stage.zRate(getPai(), currentDot);
        Double sum = 0.0;
        for (int i = 17; i <= 21; i++) {
            sum += rates.get(i);
        }
        return sum;
    }

    /**
     * 闲家赢的期望 BJ 1.5赔率 ，其他按 1 陪，和 0 陪。
     *
     * @return
     */
    public double expXWin() {
        return Stage.xWinRate(pai, new ArrayList<>(), 0) * 2 + rBjWin() * 1.25;
//         Map<Integer,Double> zRates = Stage.zRate(pai,0);
//         return Stage.xWinRate(zRates,xRates) * 2 + rBjWin() * 1.25 ;
//         double rate = getXEndStage(0).stream().map(s->s.xRate(pai)).reduce((a, b) -> a + b).get();
//         return rate * 2 +  rBjWin() * 1.25 ;
    }

    public double expXWin(List<Integer> xCards, int zCard) {
        double zNotBj = (1 - (zCard == 1 ? countPai(10) / (double) countPai() : zCard == 10 ? countPai() : 0));
        if (isBlackjack(xCards))
            return zNotBj * 2.5 + (1 - zNotBj);
        return Stage.xWinRate(pai, xCards, zCard) * 2;
//         Map<Integer,Double> zRates = Stage.zRate(pai,zCard);
//         return Stage.xWinRate(zRates,xRates) * 2  ;
////         double rate = getXEndStage(xCurrent).stream().map(s->s.xRate(pai)).reduce((a, b) -> a + b).get();
    }

    public double rBjWin() {
        double bj = p2(21);
        double xbjZbj = c(countPai(1), 1) * 4 * c(countPai(1) - 1, 1) * 2 * c(countPai(10), 1) * 2 * c(countPai(10) - 1, 1) / p(countPai(), 4);

        return bj - xbjZbj;
    }

    /**
     * 移除牌
     *
     * @param pks
     */
    public void removePocker(List<Pocker> pks) throws NotFoundException {
        for (Pocker p : pks) {
            removePocker(p);
        }
    }

    /**
     * 移除牌
     *
     * @param p
     */
    public void removePocker(Pocker p) throws NotFoundException {
        if (p != null) {
            int dot = dot(p);
            if (pai[dot] <= 0) {
                throw new NotFoundException("扑克牌" + p + "多了");
            }
            pai[dot]--;

            if (pk[p.getSuit().getHuaSe() - 1][p.getNum() - 1] <= 0) {
                throw new NotFoundException("扑克牌" + p + "多了");
            }
            pk[p.getSuit().getHuaSe() - 1][p.getNum() - 1]--;
        }
    }

    public Map<Pocker, Integer> getRemainingPocker() {
        Map<Pocker, Integer> remaining = new LinkedHashMap<>();
        for (Pocker p : Pocker.getStandardPork()) {
            remaining.put(p, pk[p.getSuit().getHuaSe() - 1][p.getNum() - 1]);
        }
        return remaining;
    }

    private static void test0() {
        Blackjack blackjack = new Blackjack(8);
        double sum2 = 0;
        int threshold = 13;
        for (int i = 1; i <= 21; i++) {
            double p1 = blackjack.p1(i);
            double p2 = blackjack.p2(i);
            sum2 += p2;
            log.info("{} -----p1={}  ----- p2={} ", i, p1, p2);
        }
        log.info(" sum2 = {}", sum2);
        log.info(" rBjWin = {}", blackjack.rBjWin());
        log.info(" z2 = {}", blackjack.z2());
        for (int i = 0; i <= 16; i++) {
            log.info(" zNotBloom current:{}  rate:{}", i, blackjack.rZNotBloom(i));
        }
//        for (int i = 0; i <= 21; i++) {
//            log.info("{} - zNotBloom {}", i, blackjack.rZNotBloom(i));
//        }
//        for (int th = 12; th <= 17; th++) {
//            for (int i = 0; i <= 21; i++) {
//                log.info("{} {} - XNotBloom {}", th, i, blackjack.rXNotBloom(i, th));
//            }
//        }
        log.info("对子 ： {} ", blackjack.expPair(25, 8));
        log.info("幸运3 ： {} ", blackjack.expLuckThree(100, 40, 30, 10, 5));
        log.info("烫三手 ： {} ", blackjack.expHotThree(100, 20, 4, 2, 1));
        log.info("幸运女皇 ： {} ", blackjack.expLuckyQueen(1000, 125, 19, 9, 4));
        log.info(" 庄爆： {} ", blackjack.expBloom(1, 2, 9, 50, 100, 250));
        log.info(" 闲赢： {} ", blackjack.expXWin());
    }

    public static void main(String[] args)throws Exception {
        long start = System.currentTimeMillis();
        // Stage xStage = Blackjack.getXStage(0);
        Stage zStage = Stage.getZStage(0);
//        log.info("count of all XStage:{} ",xStage.getStageCount());
        log.info("count of all zStage:{} ", zStage.getStageCount());
        log.info("spend time:{}", System.currentTimeMillis() - start);
        test0();

    }


}

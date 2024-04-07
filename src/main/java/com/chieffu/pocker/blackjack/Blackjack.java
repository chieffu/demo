package com.chieffu.pocker.blackjack;

import com.chieffu.pocker.Pocker;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Data
public class Blackjack {
    static List<List<List<Integer>>> bags = bags(21);
    private int[] pai;

    private int[][] pk;


    private static List<Stage> zStageList = initZStages();

    private static Map<Integer,Stage> xStageMap = initXStages();

    public Blackjack(int n) {
        init(n);

    }

    private static Map<Integer,Stage> initXStages() {
        Map<Integer,Stage> xStageList = new HashMap();
        for(int i=0;i<=21;i++){
            xStageList.put(i,initXStage(i));
        }
        return xStageList;
    }

    private static List<Stage>  initZStages() {
        List<Stage>  zStageList = new ArrayList<>();
        for(int i =0;i<=21;i++){
           zStageList.add(initZStage(i));
        }
        return zStageList;
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
     * 计算点数为dot的牌的张牌
     */
    public static int countPai(int[] pai, int dot) {
        if (dot <= 0 || dot > 11) return 0;
        return pai[dot == 11 ? 1 : dot];
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

    public static int dot(Pocker pocker) {
        return pocker.getNum() >= 10 ? 10 : pocker.getNum();
    }

    public static int[] dotsOfPocker(List<Pocker> pockers) {
        return dots(pockers.toArray(new Pocker[0]));
    }

    public static int[] dots(Pocker... pockers) {
        int sum = Arrays.stream(pockers).mapToInt(Blackjack::dot).sum();
        if (sum <= 11 && Arrays.stream(pockers).anyMatch(p -> p.getNum() == 1)) {
            return new int[]{sum, sum + 10};
        }
        return new int[]{sum};
    }
    public static boolean isFinalZStage(List<Integer> zCards){
        Stage zStage = zStageList.get(0);
        for(Integer card:zCards){
            if(zStage.getNext()==null)return true;
            zStage = zStage.getNext().stream().filter(s->s.getPai().equals(card)).findFirst().orElse(null);
            if(zStage==null)return true;
        }
        if(zStage.getNext()!=null){
            return false;
        }
        return true;
    }
    public static int[] dots(List<Integer> cards){
        int sum = cards.stream().mapToInt(Integer::intValue).sum();
        if(sum<=11 && cards.stream().anyMatch(p -> p == 1)){
            return new int[]{sum,sum+10};
        }
        return new int[]{sum};
    }

    public static double getCurrentWinRate(int xCurrent,Map<Integer, Double> zRates) {
        if(xCurrent>21)return 0;
        double zBloom = zRates.entrySet().stream().filter(e->e.getKey()>21).map(e->e.getValue()).reduce((a,b)->a+b).get();
        return zBloom + zRates.entrySet().stream().filter(e->e.getKey()<xCurrent).map(e->e.getValue()).reduce((a,b)->a+b).get()
                + Optional.ofNullable(zRates.get(xCurrent)).orElse(0.0).doubleValue()*0.5;
    }


    @Getter
    static class Stage {
        Stage parent;
        List<Stage> next;
        Integer pai;
        int[] dot;

        public Stage addZStage(Integer card) {
            if (next == null) return null;
            Stage stage = new Stage();
            stage.pai = card;
            stage.parent = this;
            stage.dot = stage.dot();
            next.add(stage);
            if (stage.largeDot()> 16 && stage.largeDot() <= 21 || stage.largeDot() > 16) {
                stage.next = null;
            } else {
                stage.next = new LinkedList<>();
            }
            return stage;
        }


        public Stage addXStage(Integer card) {
            if (next == null) return null;
            Stage stage = new Stage();
            stage.pai = card;
            stage.parent = this;
            stage.dot = stage.dot();
            next.add(stage);
            stage.next = null;
            return stage;
        }
        public Stage addXStage0(Integer card) {
            if (next == null) return null;
            Stage stage = new Stage();
            stage.pai = card;
            stage.parent = this;
            stage.dot = stage.dot();
            next.add(stage);
            if (stage.largeDot()<=11){
                stage.next = new LinkedList<>();
            } else {
                stage.next = null;
            }
            return stage;
        }

        public List<Integer> getCards() {
            List<Integer> list = new LinkedList<>();
            Stage st = this;
            while (st != null && st.pai != null) {
                list.add(0, st.pai);
                st = st.parent;
            }
            return list;
        }

        public int getRootDot(){
            Stage stage = this;
            while(stage.parent!=null){
                stage=stage.parent;
            }
            return stage.getDot()==null?0:stage.largeDot();
        }
        private int[] dot() {
            List<Integer> cards = getCards();
            int sum = cards.stream().mapToInt(Integer::intValue).sum();
            int current = getRootDot();
            sum+=current;
            if (sum <= 11 && cards.contains(1)) {
                return new int[]{sum, sum + 10};
            } else {
                return new int[]{sum};
            }
        }

        public int largeDot() {
            return dot[dot.length - 1];
        }

        public int smallDot() {
            return dot[0];
        }

        public String toString() {
            return pai == null ? "--" : largeDot() + " \t " + getCards().stream().map(String::valueOf).collect(Collectors.joining(","));
        }

        public double zRate(int[] pai) {
            double result = 1.0;
            List<Integer> cards = getCards();
            int total = Arrays.stream(pai).sum();
            for (int card : cards) {
                result *= pai[card]-- / (double) total--;
            }
            for (int card : cards) {
                pai[card]++;
            }
            if (result < 0) result = 0;
            return result;
        }

        public double xRate(int[] pai) {
            int total = Arrays.stream(pai).sum();
            if(getRootDot()>11){
                double result=0.0;
                for(int zCard=1;zCard<=10;zCard++){
                    double rz = pai[zCard]--/(double)total--;
                    Map<Integer,Double> zRates = Blackjack.zRate(pai, zCard);
                    Map<Integer,Double> xRates = Blackjack.getRateMap(pai,getNext());
                    double xWin = Blackjack.xWinRate(zRates,xRates);
                    result+=xWin*rz;
                    pai[zCard]++;
                }
                return result;
            }else{
                double result = 1.0;
                List<Integer> cards = getCards();
                for (int card : cards) {
                    result *= pai[card]-- / (double) total--;
                }
                double nextWin = Blackjack.getXStage(largeDot()).xRate(pai);
                for (int card : cards) {
                    pai[card]++;
                }
                if (result < 0) result = 0;
                return result*nextWin;
            }
        }


    }


    public static boolean isBlackjack( List<Integer> cards ) {
        return cards.size()==2 && cards.contains(1) && cards.contains(10);
    }
    private static Stage initZStage( int currentDot) {
        Stage root = new Stage();
        root.next = new LinkedList<>();
        root.dot=new int[]{currentDot};
        Queue<Stage> stages = new LinkedList<>();
        stages.add(root);
        addZSubStage(stages);
        return root;
    }
    private static Stage initXStage( int currentDot) {
        Stage root = new Stage();
        root.next = new LinkedList<>();
        root.dot=new int[]{currentDot};
        Queue<Stage> stages = new LinkedList<>();
        stages.add(root);
        if(currentDot<=11){
            addXStage(stages);
        }else {
            addXSubStage(stages);
        }
        return root;
    }

    private static void addZSubStage(Queue<Stage> stages) {
        while (!stages.isEmpty()) {
            Stage root = stages.poll();
            for (int i = 1; i <= 10; i++) {
                Stage stage = root.addZStage(i);
                if (stage != null) {
                    stages.add(stage);
                }
            }
        }
    }

    private static void addXSubStage(Queue<Stage> stages) {
        while (!stages.isEmpty()) {
            Stage root = stages.poll();
            for (int i = 1; i <= 10; i++) {
                Stage stage = root.addXStage(i);
                if (stage != null) {
                    stages.add(stage);
                }
            }
        }
    }

    private static void addXStage(Queue<Stage> stages) {
        while (!stages.isEmpty()) {
            Stage root = stages.poll();
            for (int i = 1; i <= 10; i++) {
                Stage stage = root.addXStage0(i);
                if (stage != null) {
                    stages.add(stage);
                }
            }
        }
    }

    private static void addFilterEndStage(Queue<Stage> notEndNode, List<Stage> result) {
        while (!notEndNode.isEmpty()) {
            Stage root = notEndNode.poll();
            if (isEndStage(root)) {
                result.add(root);
                continue;
            }
            for (Stage stage : root.getNext()) {
                notEndNode.add(stage);
            }
        }
    }

    private static boolean isEndStage(Stage root) {
        return root.getNext() == null;
    }

    public static List<Stage> getZEndStage(int current) {
        return getEndStage(zStageList.get(current));
    }

    private static List<Stage> getEndStage(Stage stage) {
        LinkedList<Stage> stages = new LinkedList<>();
        stages.add(stage);
        List<Stage> result = new LinkedList<>();
        addFilterEndStage(stages, result);
        return result;
    }

    public static List<Stage> getXEndStage(int current) {
        return getEndStage(xStageMap.get(current));
    }

    public static Stage getZStage(int currentDot){
        return zStageList.get(currentDot);
    }
    public static Stage getXStage(int currentDot){
        return xStageMap.get(currentDot);
    }
    public static Map<Integer, Double> zRate(int[] pai,Integer currentDot) {

        List<Stage> stages = getZEndStage(currentDot);
        return getRateMap(pai, stages);

    }

    private static Map<Integer, Double> getRateMap(int[] pai, List<Stage> stages) {
        Map<Integer, Double> rates = new HashMap<>();
        Map<Integer, List<Stage>> groups = stages.stream().collect(Collectors.groupingBy(stage -> stage.largeDot()));
        for (Integer i :groups.keySet()) {
            List<Stage> stages1 = groups.get(i);
            double rate = 0.0;
            for (Stage s : stages1) {
                double r = s.zRate(pai);
                // System.out.println(s + " \t " + r);
                rate += r;
            }
            rates.put(i, rate);
        }
        return rates;
    }

    public static Map<Integer, Double> xRate(int[] pai,int currentDot) {
        Map<Integer, Double> rates = new HashMap<>();
        List<Stage> nextStages = new LinkedList<>();
        Queue<Stage> queue = new LinkedList<>();
        queue.add(xStageMap.get(currentDot));
        while(!queue.isEmpty()){
            Stage stage = queue.poll();
            if(stage.getNext()==null)continue;
            for (Stage n:stage.getNext()){
                if(n.largeDot()<12){
                    queue.add(n);
                }else if(n.largeDot()<=21){
                    nextStages.add(n);
                }
            }
        }

        Map<Integer, List<Stage>> groups = nextStages.stream().collect(Collectors.groupingBy(stage -> stage.largeDot()));
        for (int i = 12; i <= 21; i++) {
            List<Stage> stages1 = groups.get(i);
            double rate = 0.0;
            for (Stage s : stages1) {
                double r = s.zRate(pai);
                // System.out.println(s + " \t " + r);
                rate += r;
            }
          //  System.out.println("current:"+currentDot+" ---------- totalDot:" + i + " --------end stage size:" + stages1.size() + "  rate: " + rate);
            rates.put(i, rate);
        }
        return rates;

    }



    public static double getNextWinRate(List<Integer> xCards, int[] pai, Map<Integer, Double> zRates) {

        int[] dots = dots(xCards);
        int dot = dots[dots.length - 1];
        Map<Integer, Double> xRates = xRate(pai, dot);
        return xWinRate(zRates, xRates);
    }

    private static double xWinRate(Map<Integer, Double> zRates, Map<Integer, Double> xRates) {
        double nextWinRate = 0;
        double zBloom = zRates.entrySet().stream().filter(e -> e.getKey()>21).map(Map.Entry::getValue).reduce(0.0, (a, b) -> a + b);
        double xBloom = xRates.entrySet().stream().filter(e -> e.getKey()>21).map(Map.Entry::getValue).reduce(0.0, (a, b) -> a + b);

        for (int k = 17; k <= 21; k++) {
            Double xRate = xRates.get(k);
            if(xRate==null){
                continue;
            }
            for (int j = 17; j <= k; j++) {
                double zRate = zRates.get(j);
                if (k == j) {
                    nextWinRate += xRate * zRate / 2;//和了算赢了一半
                } else {
                    nextWinRate += xRate * zRate;
                }
            }

        }
        return (1-xBloom)*zBloom+nextWinRate;
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
        return countPai(pai);
    }

    /**
     * 计算点数为dot的牌的张牌
     */
    public int countPai(int dot) {
        if (dot <= 0 || dot > 11) return 0;
        return pai[dot == 11 ? 1 : dot];
    }
    public static int countPai(int[] pai) {
        return Arrays.stream(pai).sum();
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
        long purePairQueue = c(pk[1][11], 2);
        int countPai = countPai();
        double luckyQueenWithBj = 1.0 * c(countPai(1), 1) * c(countPai(10) - 2, 1) / 2 * purePairQueue / c(countPai, 4);
        double luckyQueen = 1.0 * purePairQueue / c(countPai, 2);
        double purePair = checkPurePair();
        double pureP2_20 = checkFlush20Of2Rate();
        double p2_20 = p2(20);

        return (luckyQueenWithBjOdds + 1) * luckyQueenWithBj + (luckyQueenOdds + 1) * (luckyQueen - luckyQueenWithBj) + (purePairOdds + 1) * (purePair - luckyQueen) + (OddsPure20 + 1) * pureP2_20 + (odds20 + 1) * (p2_20 - pureP2_20);
    }


    /**
     * 计算 庄家爆破的 数学期望
     *
     * @return
     */
    public double bloomExpectation(double bloom3Odds, double bloom4Odds, double bloom5Odds, double bloom6Odds, double bloom7Odds, double bloom8Odds) {

        List<Stage> stages = getZEndStage(0);
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
        Map<Integer, Double> rates = zRate(getPai(),currentDot);
        Double sum = 0.0;
        for (int i = 17; i <= 21; i++) {
            sum += rates.get(i);
        }
        return sum;
    }

    /**
     * 闲家赢的期望 BJ 1.5赔率 ，其他按 1 陪，和 0 陪。
     * @return
     */
     public double xWinExpectation(){
         double rate = getXEndStage(0).stream().map(s->s.xRate(pai)).reduce((a, b) -> a + b).get();
         return rate * 2 +  rBjWin() * 1.25 ;
     }

    public static double xWinRate(int current,int[] pai) {
        double rate = getXEndStage(current).stream().map(s->s.xRate(pai)).reduce((a, b) -> a + b).get();
        return rate * 2 ;
    }

    public double rBjWin() {
        double bj = p2(21);
        double bjbj = 1.0 * c(countPai(1), 2) * c(countPai(10), 2) * c(2, 1) * c(2, 1) * c(2, 1) / p(countPai(), 4);
        return bj - bjbj;
    }


    /**
     * 移除牌
     *
     * @param pks
     */
    public void removePocker(List<Pocker> pks) {
        for (Pocker p : pks) {
            removePocker(p);
        }
    }

    /**
     * 移除牌
     *
     * @param p
     */
    public void removePocker(Pocker p) {
        if (p != null) {
            int dot = dot(p);
            if (pai[dot] <= 0) {
                throw new RuntimeException("扑克牌" + p + "多了");
            }
            pai[dot]--;
            if (pk[p.getSuit().getHuaSe()][p.getNum() - 1] <= 0) {
                throw new RuntimeException("扑克牌" + p + "多了");
            }
            pk[p.getSuit().getHuaSe()][p.getNum() - 1]--;
        }
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
        for(int i=0;i<=16;i++) {
            log.info(" zNotBloom current:{}  rate:{}",i, blackjack.rZNotBloom(i));
        }
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
        log.info("幸运女皇 ： {} ", blackjack.luckyQueenExpectation(1000, 125, 19, 9, 4));
        log.info(" 庄爆： {} ", blackjack.bloomExpectation(1, 2, 9, 50, 100,250));
        log.info(" 闲赢： {} ", blackjack.xWinExpectation());
    }

    public static void main(String[] args) {
        test0();

    }

}

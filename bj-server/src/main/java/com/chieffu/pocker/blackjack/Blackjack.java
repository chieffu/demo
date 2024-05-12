package com.chieffu.pocker.blackjack;

import com.chieffu.pocker.Ma;
import com.chieffu.pocker.Pocker;
import com.chieffu.pocker.util.StringUtils;
import com.chieffu.pocker.util.ThreadSafeLRUCache;
import lombok.Data;
import lombok.Getter;
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


    @Getter
    public static class Stage {
        private static final ThreadSafeLRUCache<String, Map<Integer, Double>> zRateCache = new ThreadSafeLRUCache<String, Map<Integer, Double>>(2000000);
        private static final Stage X_ROOT = initXStage(0);
        private static final Stage Z_ROOT = initZStage(0);
        /**
         * 闲12-21点的所有 中间状态
         */
        private static final List<Stage> X_MIDDLE_STAGES = X_ROOT.getXMiddleStage();
        private static final Map<Integer, List<Stage>> X_MIDDLE_STAGE_MAP = X_MIDDLE_STAGES.stream().collect(Collectors.groupingBy(stage -> stage.largeDot()));
        private static final List<Stage> Z_STAGE_LIST = initZStages();

        /**
         * 庄17-26点的所有终态
         */
        private static final Map<Integer, List<Stage>> Z_END_STAGE_MAP = Stage.getZEndStage(0).stream().collect(Collectors.groupingBy(Stage::largeDot));

        private Stage parent;
        private List<Stage> next;
        private Integer pai;
        private int[] dot;

        public int getRootDot(){
            Stage stage = this;
            while(stage.parent!=null){
                stage=stage.parent;
            }
            return stage.getDot()==null?0:stage.largeDot();
        }

        public double rate(Stage zStage, int[] pai) {
            double result = 1.0;
            double countPai = countPai(pai);
            List<Integer> cards = getCards();
            for (Integer card : cards)
                result *= (pai[card]--) / (countPai--);

            List<Integer> nextCards = zStage.getCards();
            for (Integer card : nextCards)
                result *= (pai[card]--) / (countPai--);

            for (Integer card : cards)
                pai[card]++;
            for (Integer card : nextCards)
                pai[card]++;

            return result;
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

        public boolean isEndStage() {
            return next == null;
        }

        public boolean isBj(){
            List<Integer> cards = getCards();
            return cards.size() == 2 && cards.contains(1) && cards.contains(10);
        }
        public Stage addZStage(Integer card) {
            if (next == null) return null;
            Stage stage = new Stage();
            stage.pai = card;
            stage.parent = this;
            stage.dot = stage.dot();
            next.add(stage);
            if (stage.largeDot() > 16) {
                stage.next = null;
            } else {
                stage.next = new LinkedList<>();
            }
            return stage;
        }


        public Stage addXStage0(Integer card) {
            if (next == null) return null;
            Stage stage = new Stage();
            stage.pai = card;
            stage.parent = this;
            stage.dot = stage.dot();
            if (stage.largeDot() >= 21 ) return null;
            next.add(stage);
            if (stage.largeDot() < 21) {
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

        public int largeDot() {
            return dot[dot.length - 1];
        }

        public int smallDot() {
            return dot[0];
        }

        public String toString() {
            return pai == null ? "--" : largeDot() + " \t " + getCards().stream().map(String::valueOf).collect(Collectors.joining(","));
        }

        public int getStageCount() {
            if (isEndStage()) {
                return 1;
            }
            int result = 1;
            for (Stage s : next) {
                result += s.getStageCount();
            }
            return result;
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

        public double getXCurrentWinRate( Stage zStage, int[] pai) {
            Integer xCurrentDot = this.largeDot();
            if(xCurrentDot>21)return 0.0;
            List<Stage> zEndStages = zStage.getEndStage();
            List<Stage> xWinZEndStages = zEndStages.stream().filter(s->s.largeDot()< xCurrentDot ||s.largeDot()>21).collect(Collectors.toList());
            double currentWinRate1 = 0.0;
            for(Stage s:xWinZEndStages){
                currentWinRate1+=s.rateToP(pai, zStage);
            }
            double evenRate = 0.0;
            List<Stage> xEqZEndStages = zEndStages.stream().filter(s->s.largeDot()== xCurrentDot &&!s.isBj()).collect(Collectors.toList());
            for(Stage s:xEqZEndStages){
                evenRate+=s.rateToP(pai, zStage);
            }
            return currentWinRate1+evenRate*0.5;
        }
        public double getOneMoreCardWinRate(Stage zStage, int[] pai) {
            double oneMoreCardWinRate1 = 0.0;
            List<Stage> xSubStages = this.getNext();
            if(xSubStages!=null) {
                double total = countPai(pai);
                for (Stage xSubStage : xSubStages) {
                    int xSubCard = xSubStage.pai;
                    double r0 = pai[xSubCard]-- / total--;
                    oneMoreCardWinRate1 += r0 * xSubStage.getXCurrentWinRate(zStage, pai);
                    pai[xSubCard]++;
                    total++;
                }
            }
            return oneMoreCardWinRate1;
        }

        public Map<Integer, Double> oneMoreCardRateMap(int[] pai) {
            Map<Integer, Double> xRates = new HashMap<>();
            if(next==null)return xRates;
            Map<Integer, List<Stage>> groups = next.stream().collect(Collectors.groupingBy(stage -> stage.largeDot()));
            double total = Arrays.stream(pai).sum();
            for (Integer dot : groups.keySet()) {
                List<Stage> dotStages = groups.get(dot);
                double rate = 0.0;
                for (Stage s : dotStages) {
                    rate += (pai[s.pai] / total);
                }
                xRates.put(dot, rate);
            }
            return xRates;
        }

        public List<Integer> getSortedCards(){
            List<Integer> cards= getCards();
            cards.sort(Comparator.comparingInt(o -> o));
            return cards;
        }

        private List<Stage> getXMiddleStage() {
            List<Stage> list = new LinkedList<>();
            if (largeDot() > 11) {
                list.add(this);
                return list;
            }
            if (next != null) {
                for (Stage s : next) {
                    list.addAll(s.getXMiddleStage());
                }
            }
            return list;
        }

        public static Stage getZStage(List<Integer> cards){
            Stage stage = Z_ROOT;
            for(int i=0;i<cards.size()&&stage!=null;i++){
                Integer card = cards.get(i);
                stage = stage.getNext().stream().filter(s->s.getPai()==card).findFirst().orElse(null);
            }
            return stage;
        }

        public static Stage getXStage(List<Integer> cards){
            Stage stage = X_ROOT;
            for(int i=0;i<cards.size()&&stage!=null;i++){
                Integer card = cards.get(i);
                stage = stage.getNext().stream().filter(s->s.getPai()==card).findFirst().orElse(null);
            }
            return stage;
        }

        public double rate(int[] pai) {
            double result = 1.0;
            double countPai = countPai(pai);
            List<Integer> cards = getCards();
            for (Integer card : cards)
                result *= pai[card]-- / countPai--;
            for (Integer card : cards)
                pai[card]++;
            return result;
        }
        public double rateToP(int[] pai,Stage parent){
            double total = countPai(pai);
            Stage s = this;
            double r = 1.0;
            while(s!=parent){
                r*=pai[s.pai]--/total--;
                s=s.parent;
            }
            s = this;
            while(s!=parent){
                pai[s.pai]++;
                s=s.parent;
            }
            return r;
        }
        public List<Stage> getEndStage(){
            return getEndStage(this);
        }

        public static List<Stage> getZEndStage(int current) {
            return getEndStage(Z_STAGE_LIST.get(current));
        }

        private static List<Stage> initZStages() {
            List<Stage> zStageList = new ArrayList<>();
            for (int i = 0; i <= 21; i++) {
                zStageList.add(initZStage(i));
            }
            return zStageList;
        }

        public static boolean isFinalZStage(List<Integer> zCards) {
            Stage zStage = Z_STAGE_LIST.get(0);
            for (Integer card : zCards) {
                if (zStage.getNext() == null) return true;
                zStage = zStage.getNext().stream().filter(s -> s.getPai().equals(card)).findFirst().orElse(null);
                if (zStage == null) return true;
            }
            return zStage.getNext() == null;
        }

        public static Stage getX_ROOT(List<Integer> cards) {
            int[] dot = dots(cards);
            if (dot[0] > 21) return null;
            Stage stage = X_ROOT;
            for (Integer card : cards) {
                stage = stage.getNext().stream().filter(s -> card.equals(s.pai)).findFirst().orElse(null);
            }
            return stage;
        }

        public static Stage getZStage(int currentDot) {
            return Z_STAGE_LIST.get(currentDot);
        }

        private static Stage initXStage(int currentDot) {
            Stage root = new Stage();
            root.next = new LinkedList<>();
            root.dot = new int[]{currentDot};
            Queue<Stage> stages = new LinkedList<>();
            stages.add(root);
            if (currentDot <= 21) {
                addXStage(stages);
            }
            return root;
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
                if (root.isEndStage()) {
                    result.add(root);
                    continue;
                }
                for (Stage stage : root.getNext()) {
                    notEndNode.add(stage);
                }
            }
        }

        private static List<Stage> getEndStage(Stage stage) {
            LinkedList<Stage> stages = new LinkedList<>();
            stages.add(stage);
            List<Stage> result = new LinkedList<>();
            addFilterEndStage(stages, result);
            return result;
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

        private static Stage initZStage(int currentDot) {
            Stage root = new Stage();
            root.next = new LinkedList<>();
            root.dot = new int[]{currentDot};
            Queue<Stage> stages = new LinkedList<>();
            stages.add(root);
            addZSubStage(stages);
            return root;
        }
        public static double xWinRate(int[] pai, List<Integer> cards, int zCard) {
            Stage root = getXStage(cards);
            if (root == null) return 0.0;
            Map<Integer, Double> zRates = zRate(pai, zCard);

            List<Stage> middleStages = root.getXMiddleStage();//12-21点的中间点
            Map<Integer, List<Stage>> groups = middleStages.stream().collect(Collectors.groupingBy(stage -> stage.largeDot()));
            double result = 0.0;
            for (Integer i : groups.keySet()) {
                double currentWinRate = getCurrentWinRate(i, zRates);
                List<Stage> stages1 = groups.get(i);
                for (Stage s : stages1) {
                    double rateS = (s.zRate(pai)/root.zRate(pai));
                    Map<Integer, Double> xRates1 = s.oneMoreCardRateMap(pai);
                    double oneMoreCardWinRate = Blackjack.xWinRate(zRates, xRates1);
                    result += rateS * Math.max(currentWinRate, oneMoreCardWinRate);
                }
            }
            return result;
        }
        public static Map<Integer, Double> zRate(int[] pai, Integer currentDot) {
            String key = Arrays.toString(pai) + currentDot;
            Map<Integer, Double> cache = zRateCache.get(key);
            if (cache!=null) {
                return cache;
            }
            List<Stage> stages = getZEndStage(currentDot);
            Map<Integer, Double> rates = new HashMap<>();
            if (stages != null) {
                Map<Integer, List<Stage>> groups = stages.stream().collect(Collectors.groupingBy(stage -> stage.largeDot()));
                for (Integer i : groups.keySet()) {
                    List<Stage> stages1 = groups.get(i);
                    double rate = 0.0;
                    for (Stage s : stages1) {
                        double r = s.zRate(pai);
                        rate += r;
                    }
                    rates.put(i, rate);
                }
            }
            zRateCache.put(key, rates);
            return rates;

        }
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
     *
     * 每个袋子的容量由一个整数列表表示，且列表中的整数按照升序排列。
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

    public static double getCurrentWinRate(int xCurrent, Map<Integer, Double> zRates) {
        if (xCurrent > 21) return 0;
        double zBloom = zRates.entrySet().stream().filter(e -> e.getKey() > 21).map(e -> e.getValue()).reduce((a, b) -> a + b).orElse(0.0);
        double xGtZ = zRates.entrySet().stream().filter(e -> e.getKey() < xCurrent&&e.getKey()!=0).map(e -> e.getValue()).reduce((a, b) -> a + b).orElse(0.0);
        double xEqZ =  Optional.ofNullable(zRates.get(xCurrent)).orElse(0.0).doubleValue() ; //持平的话回本
        return zBloom+xGtZ+xEqZ*0.5;
    }

    public static double xWinRate(Map<Integer, Double> zRates, Map<Integer, Double> xRates) {
        double nextWinRate = 0;
        double zBloom = zRates.entrySet().stream().filter(e -> e.getKey() > 21).map(Map.Entry::getValue).reduce(0.0, (a, b) -> a + b);
        double xBloom =1- xRates.entrySet().stream().filter(e -> e.getKey() < 22).map(Map.Entry::getValue).reduce(0.0, (a, b) -> a + b);

        for (int k = 17; k <= 21; k++) {
            Double xRate = xRates.get(k);
            if (xRate == null) {
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
        return (1 - xBloom) * zBloom + nextWinRate;
    }

    public static Map<Integer, Double> zRate(int[] pai, Integer currentDot) {
        return zRate(pai, currentDot,false);
    }
    public static Map<Integer, Double> zRate(int[] pai, Integer currentDot,boolean possibleZbj) {
        String key = Arrays.toString(pai) + currentDot+possibleZbj;
        Map<Integer, Double> cache = Stage.zRateCache.get(key);
        if (cache!=null) {
            return cache;
        }
        List<Stage> stages = Stage.getZEndStage(currentDot);
        double bjRate = 0;
        Map<Integer, Double> rates = new HashMap<>();
        if (stages != null) {
            Map<Integer, List<Stage>> groups = stages.stream().collect(Collectors.groupingBy(stage -> stage.largeDot()));
            for (Integer i : groups.keySet()) {
                List<Stage> stages1 = groups.get(i);
                double rate = 0.0;
                for (Stage s : stages1) {

                    double r = s.zRate(pai);
                    if(possibleZbj&&currentDot==0&&s.isBj()||possibleZbj && s.largeDot()==21&&s.getCards().size()==1) {
                        bjRate+=r;
                    }else{
                        rate += r;
                    }
                }
                rates.put(i, rate);
            }
            rates.put(0,bjRate);
        }
        Stage.zRateCache.put(key, rates);
        return rates;
    }

    public double expXWin() {
        int[] pai = this.pai;
        Map<Integer, List<Stage>> xMiddleStageMap = Stage.X_MIDDLE_STAGE_MAP;
        double xNotBjWin = 0.0;
        for (Integer dotI : xMiddleStageMap.keySet()) {
            if(dotI>21)continue;
            List<Stage> xStages = xMiddleStageMap.get(dotI);
            for (Stage xStage : xStages) {
                if(xStage.isBj())continue;
                if(xStage.isBj())continue;
                double xCurrentRate = 1.0;
                List<Integer> xcards = xStage.getCards();
                double total = Arrays.stream(pai).sum();
                for (int card : xcards) {
                    xCurrentRate *= pai[card]-- /  total--;
                }
                double rateK = 0.0;
                for(int zcard=1;zcard<=10;zcard++){
                    double zCurrentRate = pai[zcard]--/total--;
                    Stage zStage = Stage.getZStage(Collections.singletonList(zcard));
                    double currentWinRate1 = xStage.getXCurrentWinRate( zStage,pai);
                    double oneMoreCardWinRate1 = xStage.getOneMoreCardWinRate(zStage, pai);
                    rateK += zCurrentRate*Math.max(currentWinRate1,oneMoreCardWinRate1);
                    pai[zcard]++;
                }
                for (int card : xcards) {
                    pai[card]++;
                }
                xNotBjWin += xCurrentRate * rateK;
            }
        }
        return xNotBjWin*2+rBjWin()*2.5+rBjHe();
    }




    public static double xWinRateWithoutBJ(int[] pai, List<Integer> cards, int zCard) {
        Stage root = Stage.getX_ROOT(cards);
        if (root == null) return 0.0;
        Map<Integer, Double> zRates = zRate(pai, zCard);

        List<Stage> middleStages = root.getXMiddleStage();//12-21点的中间点
        Map<Integer, List<Stage>> groups = middleStages.stream().filter(p->!p.isBj()).collect(Collectors.groupingBy(stage -> stage.largeDot()));
        double result = 0.0;
        for (Integer i : groups.keySet()) {
            double currentWinRate = getCurrentWinRate(i, zRates);
            List<Stage> stages1 = groups.get(i);
            for (Stage s : stages1) {
                double rateS = (s.zRate(pai)/root.zRate(pai));
                Map<Integer, Double> xRates1 = s.oneMoreCardRateMap(pai);
                double oneMoreCardWinRate = xWinRate(zRates, xRates1);
                result += rateS * Math.max(currentWinRate, oneMoreCardWinRate);
            }
        }
        return result;
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
     * 计算对子的数学期望
     *
     * @param purePairOdds 纯对子（同花色对子）的赔率
     * @param sameColorPairOdds  对子（同顏色）的赔率
     * @param diffColorPairOdds  对子（不同顏色）的赔率
     * @return 对子的数学期望值
     */
    public double expPair(double purePairOdds, double sameColorPairOdds,double diffColorPairOdds ) {
        double purePairRate = rPurePair();
        double sameColorPate = rSameColorPair();
        double diffColorPate = rDiffColorPair();
        return  purePairRate * (1 + purePairOdds) + sameColorPate *(1+sameColorPairOdds) +diffColorPate*(1+diffColorPairOdds);
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
     * 计算同色同花的对子的概率
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
    public double rSameColorPair() {
        long sameColorPair = 0;
        for (int i = 0; i < 13; i++) {
                sameColorPair += c(pk[0][i]+pk[2][i], 2);
                sameColorPair += c(pk[1][i]+pk[3][i], 2);
        }
        return sameColorPair * 1.0 / c(countPai(), 2);
    }
    public double rDiffColorPair() {
        long diffColorPair = 0;
        for (int i = 0; i < 13; i++) {
            diffColorPair += c(pk[0][i]+pk[1][i], 2);
            diffColorPair += c(pk[1][i]+pk[2][i], 2);
            diffColorPair += c(pk[2][i]+pk[3][i], 2);
            diffColorPair += c(pk[0][i]+pk[3][i], 2);
        }
        return diffColorPair * 1.0 / c(countPai(), 2);
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
        Map<Integer, Double> rates = zRate(getPai(), currentDot);
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
    public double expXWin0() {
        return xWinRateWithoutBJ(pai, new ArrayList<>(), 0) * 2 + rBjWin() * 2.5 +rBjHe();
//         Map<Integer,Double> zRates = Stage.zRate(pai,0);
//         return Stage.xWinRate(zRates,xRates) * 2 + rBjWin() * 1.25 ;
//         double rate = getXEndStage(0).stream().map(s->s.xRate(pai)).reduce((a, b) -> a + b).get();
//         return rate * 2 +  rBjWin() * 1.25 ;
//        return 0.0;
    }

    public double expXWin(List<Integer> xCards, int zCard) {
        double zNotBj = (1 - (zCard == 1 ? countPai(10) / (double) countPai() : zCard == 10 ? countPai() : 0));
        if (isBlackjack(xCards))
            return zNotBj * 2.5 + (1 - zNotBj);
        return Stage.xWinRate(pai, xCards, zCard) * 2;
    }

    public double rBjWin() {
        double bj = p2(21);
        return bj - rBjHe();
    }
    public double rBjHe(){
        return c(countPai(1), 1) * 2 * c(countPai(1) - 1, 1) * c(countPai(10), 1) * 2 * c(countPai(10) - 1, 1) / (double)p(countPai(), 4);
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
    public void addPocker(List<Pocker> pks) throws NotFoundException {
        for (Pocker p : pks) {
            addPocker(p);
        }
    }
    public void addPocker(Pocker p)  {
        if (p != null) {
            int dot = dot(p);
            pai[dot]++;
            pk[p.getSuit().getHuaSe() - 1][p.getNum() - 1]++;
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

    public double expectation(){
        double highLowCardExpectation = highLowCardCounting();
        if(highLowCardExpectation>1){

        }
        return omegaIICardCounting();
    }

    public double highLowCardCounting() {
       return (pai[10] + pai[1] - pai[2] - pai[3] - pai[4] - pai[5] - pai[6]) * 52/ (double) countPai();
    }

    public double myCardCounting(){
        return (pai[1]+pai[2]+pai[3]+pai[4]+pai[5]+pai[6]-pai[8]-pai[9]-pai[10])*52/(double)countPai();
    }
    public double omegaIICardCounting() {
        double big = pai[10]*2;
        double small = (pai[4]+pai[5]+pai[6])*2;
        double middle = (pai[2]+pai[3]+pai[7]);
        double equal = pai[9];
        return (big + equal - small - middle) * 52 / (countPai()) ;
    }

    private static void test0() {
        Blackjack blackjack = new Blackjack(8);
        double sum2 = 0;
        for (int i = 1; i <= 21; i++) {
            double p1 = blackjack.p1(i);
            double p2 = blackjack.p2(i);
            sum2 += p2;
            log.info("{} -----p1={}  ----- p2={} ", i, p1, p2);
        }
        log.info(" pure pair = {}  --  预期赔率：{}",blackjack.rPurePair(),1/ blackjack.rPurePair()-1);
        log.info(" sum2 = {}", sum2);
        log.info(" rBjWin = {}", blackjack.rBjWin());
        log.info(" expXWin = {}", blackjack.expXWin());
        log.info(" expXWin0 = {}", blackjack.expXWin0());
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
        log.info("对子 3分 ： {} ", blackjack.expPair(25, 12,6));
        log.info("幸运3 ： {} ", blackjack.expLuckThree(100, 40, 30, 10, 5));
        log.info("烫三手 ： {} ", blackjack.expHotThree(100, 20, 4, 2, 1));
        log.info("幸运女皇 ： {} ", blackjack.expLuckyQueen(1000, 125, 19, 9, 4));
        log.info(" 庄爆： {} ", blackjack.expBloom(1, 2, 9, 50, 100, 250));
        log.info(" 闲赢： {} ", blackjack.expXWin());
    }

    public static void test1() {
        Blackjack blackjack = new Blackjack(7);
        long start = System.currentTimeMillis();
     //   log.info(" expXWin = {}", blackjack.expXWin());
       // log.info("expXWin spend time:{}",System.currentTimeMillis()-start);
        int t = 0;
        for(int i=2;i<=6;i++){
            int n = StringUtils.newRandomInt(4,6);
//            n=2;
            blackjack.pai[i]-=n;
            t+=blackjack.pai[i];
        }
       log.info("真數：{}",blackjack.expectation());
        log.info("剩余牌偏離：{}",blackjack.countPai()/(double)(t));
        log.info(" expXWin = {}", blackjack.expXWin());
        log.info("expXWin spend time:{}",System.currentTimeMillis()-start);
    }

    public static void main(String[] args)throws Exception {
        long start = System.currentTimeMillis();

        // Stage xStage = Blackjack.getXStage(0);
        Stage zStage = Stage.getZStage(0);
        log.info("count of all XStage:{} ",Stage.getX_ROOT(new ArrayList<>()).getStageCount());
        log.info("count of all zStage:{} ", zStage.getStageCount());
        log.info("spend time:{}", System.currentTimeMillis() - start);
//        test0();
//        test1();

    }

}
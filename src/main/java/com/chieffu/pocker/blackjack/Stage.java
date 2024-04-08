package com.chieffu.pocker.blackjack;

import com.chieffu.pocker.util.LRUCache;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class Stage {
    private static final LRUCache<String, Map<Integer, Double>> zRateCache = new LRUCache<String, Map<Integer, Double>>(2000000);
    private static final Stage xStage = initXStage(0);
    private static final List<Stage> zStageList = initZStages();
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
    public static List<Stage> getZEndStage(int current) {
        return getEndStage(zStageList.get(current));
    }

    private static List<Stage> initZStages() {
        List<Stage> zStageList = new ArrayList<>();
        for (int i = 0; i <= 21; i++) {
            zStageList.add(initZStage(i));
        }
        return zStageList;
    }

    public static boolean isFinalZStage(List<Integer> zCards) {
        Stage zStage = zStageList.get(0);
        for (Integer card : zCards) {
            if (zStage.getNext() == null) return true;
            zStage = zStage.getNext().stream().filter(s -> s.getPai().equals(card)).findFirst().orElse(null);
            if (zStage == null) return true;
        }
        return zStage.getNext() == null;
    }

    public static Stage getXStage(List<Integer> cards) {
        int[] dot = Blackjack.dots(cards);
        if (dot[0] > 21) return null;
        Stage stage = xStage;
        for (Integer card : cards) {
            stage = stage.getNext().stream().filter(s -> card.equals(s.pai)).findFirst().orElse(null);
        }
        return stage;
    }

    public static Stage getZStage(int currentDot) {
        return zStageList.get(currentDot);
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

    public static double getCurrentWinRate(int xCurrent, Map<Integer, Double> zRates) {
        if (xCurrent > 21) return 0;
        double zBloom = zRates.entrySet().stream().filter(e -> e.getKey() > 21).map(e -> e.getValue()).reduce((a, b) -> a + b).orElse(0.0);
        return zBloom + zRates.entrySet().stream().filter(e -> e.getKey() < xCurrent).map(e -> e.getValue()).reduce((a, b) -> a + b).orElse(0.0)
                + Optional.ofNullable(zRates.get(xCurrent)).orElse(0.0).doubleValue() * 0.5; //持平的话回本
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
        String key = Arrays.toString(pai) + currentDot;
        if (zRateCache.containsKey(key)) {
            return zRateCache.get(key);
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

    public boolean isEndStage() {
        return next == null;
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
        if (stage.largeDot() > 21 || stage.getCards().size() > 7) return null;
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
                double oneMoreCardWinRate = xWinRate(zRates, xRates1);
                result += rateS * Math.max(currentWinRate, oneMoreCardWinRate);
            }
        }
        return result;
    }

    public Map<Integer, Double> oneMoreCardRateMap(int[] pai) {
        Map<Integer, Double> xRates = new HashMap<>();
        if(next==null)return xRates;
        Map<Integer, List<Stage>> groups = next.stream().collect(Collectors.groupingBy(stage -> stage.largeDot()));
        for (Integer i : groups.keySet()) {
            List<Stage> stages1 = groups.get(i);
            double rate = 0.0;
            for (Stage s : stages1) {
                int total = Arrays.stream(pai).sum();
                double middleR = 1.0;
                Stage theStage = s;
                while (theStage != this) {
                    middleR *= (pai[theStage.pai]-- / (double) total--);
                    theStage=theStage.parent;
                }
                rate += middleR;
                theStage = s;
                while (theStage != this) {
                    pai[theStage.pai]++;
                    theStage=theStage.parent;
                }
            }
            xRates.put(i, rate);
        }
        return xRates;
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


}

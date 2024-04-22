package com.chieffu.pocker.blackjack;


import com.chieffu.pocker.Pocker;
import com.chieffu.pocker.util.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 21点百家乐  //https://3l3b0um9.com/game/rule?gametype=3043&lang=cn
 *
 * 游戏使用8副扑克牌。
 * 依照闲1、庄1、闲2、庄2顺序派牌，下注结束即开牌。
 * 任一家首2张牌点数总和小于15以下，需补1张牌。若首2张牌点数总和大于16以上，不需补牌。
 * 补牌顺序由闲家先补，若闲家补牌后爆牌，则庄家不必补牌直接胜出。
 * 补牌后无论是否超过15点，都不再补牌。
 * 当任一家首2张牌为BlackJack时，另一家无须补牌。
 * 在无爆牌、无BlackJack时，以点数计算最靠近21点的一家胜出。
 */
@Data
@Slf4j
public class BjBaccarat extends Blackjack {
    public BjBaccarat() {
        this(8);
    }

    public BjBaccarat(int n) {
        super(n);
    }

    @Data
    public static class Node {
        public final static Node root = initAllNodes();
        public final static List<Node> END_NODES = root.getEndNodes();
        public final static Map<Integer, List<Node>> DOT_END_NODES = END_NODES.stream().collect(Collectors.groupingBy(n -> n.getDot()));

        private Node parent;
        private List<Node> next = new ArrayList<>();
        private Integer pai;//1 - 10

        private Integer dot;

        public int getDot() {
            if (dot == null) {
                int[] dots = dots(getCards());
                dot = dots[dots.length - 1];
            }
            return dot;
        }

        public List<Integer> getCards() {
            List<Integer> list = new LinkedList<>();
            Node st = this;
            while (st != null && st.pai != null) {
                list.add(0, st.pai);
                st = st.parent;
            }
            return list;
        }

        private Node addNode(Integer card) {
            List<Integer> cards = this.getCards();
            if (cards.size() == 3) return null;
            if (getDot() >= 16) return null;
            Node stage = new Node();
            stage.pai = card;
            stage.parent = this;
            this.getNext().add(stage);
            return stage;
        }

        public static Node initAllNodes() {
            Node root = new Node();
            Queue<Node> nodes = new LinkedList<>();
            nodes.add(root);
            addSubNode(nodes);
            return root;
        }

        private static void addSubNode(Queue<Node> nodes) {
            while (!nodes.isEmpty()) {
                Node root = nodes.poll();
                for (int i = 1; i <= 10; i++) {
                    Node stage = root.addNode(i);
                    if (stage != null) {
                        nodes.add(stage);
                    }
                }
            }
        }

        public List<Node> getEndNodes() {
            List<Node> nodes = new ArrayList<>();
            if (next == null || next.isEmpty()) {
                nodes.add(this);
                return nodes;
            }
            for (BjBaccarat.Node node : next) {
                nodes.addAll(node.getEndNodes());
            }
            return nodes;
        }

        public List<Node> getMiddleNode() {
            List<Node> nodes = new ArrayList<>();
            if (getCards().size() == 2 && getDot() < 16) {
                nodes.add(this);
                return nodes;
            }
            for (BjBaccarat.Node node : next) {
                nodes.addAll(node.getMiddleNode());
            }
            return nodes;
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

        public double rate(Node nextNode, int[] pai) {
            double result = 1.0;
            double countPai = countPai(pai);
            List<Integer> cards = getCards();
            for (Integer card : cards)
                result *= (pai[card]--) / (countPai--);

            List<Integer> nextCards = nextNode.getCards();
            for (Integer card : nextCards)
                result *= (pai[card]--) / (countPai--);

            for (Integer card : cards)
                pai[card]++;
            for (Integer card : nextCards)
                pai[card]++;

            return result;
        }

        public boolean isBlackjack() {
            List<Integer> cards = getCards();
            if (cards.size() != 2) return false;
            return getDot() == 21;
        }

        public String toString() {
            return getCards().toString();
        }

    }

    /**
     * blackjack 出现的概率
     * @return
     */
    public double rBj() {
        return c(countPai(1), 1) * c(countPai(10), 1) / (double) c(countPai(), 2);
    }
    /**
     * 以blackjack 赢的概率
     * @return
     */
    public double rBjWin() {
        double bj = rBj();
        return bj - bj*rBjHe();
    }

    /**
     * 闲家 爆牌的概率
     * @return
     */
    public double rXBloom() {
        double sum = 0;
        List<Node> bloomNode = Node.END_NODES.stream().filter(i -> i.getDot() > 21).collect(Collectors.toList());
        for (Node node : bloomNode) {
            sum += node.rate(getPai());
        }
        return sum;
    }

    /**
     * 庄家爆牌的概率
     * @return
     */
    public double rZBloom() {
        List<Node> bloomNode = Node.END_NODES.stream().filter(i -> i.getDot() > 21).collect(Collectors.toList());
        double sum = 0;
        for (int i = 21; i > 2; i--) {
            List<Node> list = Node.DOT_END_NODES.getOrDefault(i,new ArrayList<>());
            sum+=sumRate(i,list, bloomNode);
        }
//        double xb = rXBloom();
//        double r0 = (1 - xb) * xb;
//        System.out.println(sum==r0);
        return sum;
    }

    /**
     * 闲家赢的概率
     * @return
     */
    public double rXWin() {
        double sum = rWinNotBloom()+rBjWin();
        return sum  + rZBloom();
    }

    /**
     * 赢牌但都没爆牌的概率 （不包括blackjack 不包括和牌）
     * @return
     */
    public double rWinNotBloom() {
        double sum = 0;
        for (int i = 21; i > 2; i--) {
            for (int j = i - 1; j > 0; j--) {
                List<Node> list = Node.DOT_END_NODES.getOrDefault(i,new ArrayList<>());
                List<Node> next = Node.DOT_END_NODES.getOrDefault(j,new ArrayList<>());
                if (list == null || list.isEmpty() || next == null || next.isEmpty()) continue;
                sum+=sumRate(i,list, next);
            }
        }
        return sum ;
    }

    /**
     * 和牌的概率
     * @return
     */
    public double rHe() {
        double sum = 0;
        Map<Integer, List<Node>> groups = Node.DOT_END_NODES;
        for (int i = 21; i > 2; i--) {
            List<Node> list = groups.get(i);
            if (list == null || list.isEmpty()) continue;
            for (Node n : list) {
                if (n.isBlackjack()) continue;
                for (Node m : list) {
                    if (m.isBlackjack()) continue;
                    sum += n.rate(m, getPai());
                }
            }
        }
        return sum + rBjHe();
    }

    /**
     * 任意对子
     * @return
     */
    public double rPair(){
       return super.rPair();
    }
    private double rBjHe() {
        return c(countPai(1), 1) * c(countPai(10), 1) / (double) c(countPai(), 2) * c(countPai(1) - 1, 1) * c(countPai(10) - 1, 1) / (double) c(countPai() - 2, 2);
    }

    public double p2(int n){
        double sum = 0.0;
        double total = c(countPai(), 2);
        for(int i=2;i<=11;i++){
            int j=(n-i);
            if(i!=j)
                sum+=c(countPai(i),1)*c(countPai(j),1)/total;
            else
                sum+=c(countPai(i),2)/total;
        }
        return sum;
    }

    /**
     * 庄家赢的概率
     * @return
     */
    public double rZWin() {
        return rWinNotBloom() +rBjWin()+ rXBloom() - rXBloomZbj();
    }

    public double rXBloomZbj() {
        List<Node> bloomNodes = Node.END_NODES.stream().filter(i->i.getDot()>21).collect(Collectors.toList());
        List<Node> bjs = Node.DOT_END_NODES.getOrDefault(21,new ArrayList<>()).stream().filter(i->i.isBlackjack()).collect(Collectors.toList());
        return sumRate(0,bloomNodes,bjs);
    }
    /**
     *
     * 单 不爆牌
     * @return
     */
    public double rOddNotBloom(){
        double sum = 0.0;
        for(int j=21;j>2;j--){
            if(j%2==1){
                List<Node> list = Node.DOT_END_NODES.getOrDefault(j,new ArrayList<>());
                for(Node n:list){
                    sum+=n.rate(getPai());
                }
            }
        }
        return sum;
    }

    /**
     * 闲单	1:1	闲家手牌(含补牌)，点数加总为奇数(包含BJ)且不爆牌。如果闲爆牌将退还本金	(15局后不能下单）
     * @return
     */
    public double expXOdd(){
        return 2.0*rOddNotBloom() + rXBloom();
    }

    /**
     *  庄家赢的期望    赔率1:0.92 和了退回本金
     * @return
     */
    public double expZWin(){
        return 1.92*rZWin()+rHe();
    }

    /**
     *  闲家赢的期望    赔率1:1 和了退回本金
     * @return
     */
    public double expXWin(){
        return 2*rXWin()+rHe();
    }

    /**
     *   和牌的期望 赔率 1:8.5 双方BJ时退回本届
     * @return
     */
    public double expHe(){
        return 9.5*rHe() - 8.5*rBjHe();
    }

    /**
     *  庄/闲 前2张牌 组成20点的期望    赔率1:6 (30局后不得下注）
     * @return
     */
    public double expP2_20(){
        return p2(20)*7;
    }

    /**
     * 庄/闲  BJ的期望  赔率1:14  (25局后不得下注）
     * @return
     */
    public double expBJ(){
        return rBj()*15;
    }

    /**
     * 超级21点	庄家以BJ或三张牌点数加总为21点赢闲家	（30局后不得下注）
     * 庄以两张牌21点(BJ)获胜	1:6
     * 庄以三张牌21点获胜	1:13
     * @return
     */
    public double expZ21Win(){
       double z21_2= rBjWin() * 7;
       double z21_3 = Node.DOT_END_NODES.get(21).stream().filter(node->node.getCards().size()==3).map(n->n.rate(getPai())).reduce((a, b) -> a + b).orElse(0.0);
       return z21_2 + (1-rXBloom())*z21_3 * 14;
    }


    /**
     * 龙宝  (40 局后不得下注)
     * 1.龙宝有庄闲玩法，指以非BJ赢过对家，且赢家减输家牌面点数为1-15点；或以BJ胜出
     * •BJ定义：首两张牌的组合为一张A牌及一张10点的牌所组成的21点
     * •非BJ定义：BJ之外所有情况
     * 2.双方以非BJ和局，会员输掉本金
     * 3.任一方开牌结果为爆牌，会员输掉本金
     *
     * 庄/闲 龙宝
     * 胜方以非BJ赢，且双方点数相减为13,14,15点	1:30
     * 胜方以非BJ赢，且双方点数相减为10,11,12点	1:10
     * 胜方以非BJ赢，且双方点数相减为7,8,9点	1:3
     * 胜方以非BJ赢，且双方点数相减为4,5,6点	1:2
     * 胜方以非BJ赢，且双方点数相减为1,2,3点	1:1
     * 胜方以BJ胜出	1:1
     * 双方皆为BJ且和局	退还本金
     *
     * @return
     */
    public double expLongBao() {
        double sum = 0;
        Map<Integer, List<Node>> groups = Node.DOT_END_NODES;
        for (int i = 21; i > 6; i--) {
             List<Node> list = groups.getOrDefault(i,new ArrayList<>());
             if(i==21){
                 list.removeIf(n->n.isBlackjack());
             }
            List<Node> next13_15 = new ArrayList<>();
            next13_15.addAll(groups.getOrDefault(i-13,new ArrayList<>()));
            next13_15.addAll(groups.getOrDefault(i-14,new ArrayList<>()));
            next13_15.addAll(groups.getOrDefault(i-15,new ArrayList<>()));
            sum+= sumRate(i,list, next13_15) * 31;

            List<Node> next10_12 = new ArrayList<>();
            next10_12.addAll(groups.getOrDefault(i-10,new ArrayList<>()));
            next10_12.addAll(groups.getOrDefault(i-11,new ArrayList<>()));
            next10_12.addAll(groups.getOrDefault(i-12,new ArrayList<>()));
            sum+= sumRate(i,list, next10_12) * 11;

            List<Node> next7_9 = new ArrayList<>();
            next7_9.addAll(groups.getOrDefault(i-7,new ArrayList<>()));
            next7_9.addAll(groups.getOrDefault(i-8,new ArrayList<>()));
            next7_9.addAll(groups.getOrDefault(i-9,new ArrayList<>()));
            sum+= sumRate(i,list, next7_9) * 4;

            List<Node> next4_6 = new ArrayList<>();
            next4_6.addAll(groups.getOrDefault(i-4,new ArrayList<>()));
            next4_6.addAll(groups.getOrDefault(i-5,new ArrayList<>()));
            next4_6.addAll(groups.getOrDefault(i-6,new ArrayList<>()));
            sum+= sumRate(i,list, next4_6) * 3;

            List<Node> next1_3 = new ArrayList<>();
            next1_3.addAll(groups.getOrDefault(i-1,new ArrayList<>()));
            next1_3.addAll(groups.getOrDefault(i-2,new ArrayList<>()));
            next1_3.addAll(groups.getOrDefault(i-3,new ArrayList<>()));
            sum+= sumRate(i,list, next1_3) * 2;

        }
        return sum + rBjWin()*2 + rBjHe();
    }

    /**
     * 庄爆牌	1:6.5	庄家手牌(含补牌)，点数加总超过21点	30局后不得下注
     * @return
     */
    public double expZBloom(){
        return rZBloom()*7.5;
    }
    /**
     * 闲爆牌	1:5.3	闲家手牌(含补牌)，点数加总超过21点	30
     * @return
     */
    public double expXBloom(){
        return rZBloom()*6.3;
    }


    public double t0(){
        double sum = 0;
        List<Node> nodes = Node.root.getEndNodes();
        for(Node node: nodes){
            for(Node node1:nodes){
                sum+=node.rate(node1,getPai());
            }

        }
        return sum;
    }

    private double sumRate(int i,List<Node> list, List<Node> next) {
        double d0 = 0;
        if ( list.isEmpty() ||  next.isEmpty()) return d0;
        for (Node n : list) {
            if (i==21&&n.isBlackjack()) continue;
             for (Node m : next) {
                d0 += n.rate(m, getPai());
            }
        }
        return d0;
    }

    public static MockContext mock(int i,double gate) throws NotFoundException {
        List<Pocker> pks = Pocker.randomPocker(8);
        BjBaccarat bj = new BjBaccarat(pks.size() / 52);
        int round = 0;
        List<Pocker> pz = new ArrayList<>();
        List<Pocker> px = new ArrayList<>();
        MockContext total = new MockContext("总");
        MockContext longBaoContext = new MockContext("龙宝");
        MockContext xContext = new MockContext("闲");
        MockContext zContext = new MockContext("庄");
        int cut =  StringUtils.newRandomInt(416-240, 416-256);
        while (pks.size()>cut) {
            round++;

            px.add(pks.remove(pks.size() - 1));
            px.add(pks.remove(pks.size() - 1));
            pz.add(pks.remove(pks.size() - 1));
            pz.add(pks.remove(pks.size() - 1));
            int[] xDot = Blackjack.dotsOfPocker(px);
            int[] zDot = Blackjack.dotsOfPocker(pz);
            if(xDot[xDot.length-1]<=15){
                px.add(pks.remove(pks.size()-1));
                xDot = Blackjack.dotsOfPocker(px);
            }
            if(xDot[xDot.length-1]<=21&&zDot[zDot.length-1]<=15){
                pz.add(pks.remove(pks.size()-1));
                zDot = Blackjack.dotsOfPocker(pz);
            }

            //mockLongBao(i, bj, round, pz, px, longBaoContext, xDot, zDot);
            mockX(i, bj, round, pz, px, xContext, xDot, zDot,gate);
//            if(!mockZ){
//                boolean mockZ = mockZ(i, bj, round, pz, px, zContext, xDot, zDot,gate);
//            }
            bj.removePocker(px);
            bj.removePocker(pz);
            px.clear();
            pz.clear();
        }
        total.merge(longBaoContext);
        total.merge(xContext);
        total.merge(zContext);
        return total;
    }

    private static void mockLongBao(int i, BjBaccarat bj, int round, List<Pocker> pz, List<Pocker> px, MockContext longBaoContext, int[] xDot, int[] zDot) {
        double rlongbao = bj.expLongBao();
        if(rlongbao>1.0) {
            double result = -1;
            if(bj.isBj(px)&& bj.isBj(pz)){
                result = 0;
            }else if (xDot[xDot.length - 1] <=21 && zDot[zDot.length - 1] <= 21 ) {
               int d = xDot[xDot.length - 1] - zDot[zDot.length - 1];
               if(d==13||d==14||d==15){
                   result = 30;
               }else if(d==10||d==11||d==12){
                   result = 10;
               }else if(d==7||d==8||d==9){
                   result = 3;
               }else if(d==4||d==5||d==6){
                   result = 2;
               }else if(d==1||d==2||d==3){
                   result = 1;
               }else{
                   result = -1;
               }
            }
            longBaoContext.addCount();
            longBaoContext.addResult(result);
            log.info("第{}靴第{}局压{}:{} ---- cnt: {} min: {} max: {}  result: {}   结果 {}  {}{} - {}{} "
                    , i, round,longBaoContext.getName(),String.format("%.4f",rlongbao), longBaoContext.getCount()
                    , longBaoContext.getMinWin(), longBaoContext.getMaxWin(), longBaoContext.getResult(),result, px, xDot[xDot.length-1], pz, zDot[zDot.length-1]);
        }
    }
    private static boolean mockX(int i, BjBaccarat bj, int round, List<Pocker> pz, List<Pocker> px, MockContext xContext, int[] xDot, int[] zDot,double gate) {
        double expXWin = bj.expXWin();
        if(expXWin>=gate) {
            double result = -1;
            if(bj.isBj(px)&& bj.isBj(pz)){
                result = 0;
            }else if(bj.isBj(px)||zDot[zDot.length - 1]>21){
                result = 1;
            } else if (xDot[xDot.length - 1] <= 21 && xDot[xDot.length - 1] > zDot[zDot.length - 1]) {
                result = 1;
            }else if( xDot[xDot.length - 1] == zDot[zDot.length - 1]){
                result = 0;
            }
            xContext.addCount();
            xContext.addResult(result);
            log.info("第{}靴第{}局压{}:{} ---- cnt: {} min: {} max: {}  result: {}   结果 {}  {}{} - {}{} "
                    , i, round,xContext.getName(),String.format("%.4f",expXWin), xContext.getCount()
                    , String.format("%.2f",xContext.getMinWin()), String.format("%.2f",xContext.getMaxWin()), String.format("%.2f",xContext.getResult()),String.format("%.2f",result), px, xDot[xDot.length-1], pz, zDot[zDot.length-1]);
            return true;
        }
        return false;
    }
    private static boolean mockZ(int i, BjBaccarat bj, int round, List<Pocker> pz, List<Pocker> px, MockContext zContext, int[] xDot, int[] zDot,double gate) {
        double expZWin = bj.expZWin();
        if(expZWin>=gate) {
            double result = -1;
            if(bj.isBj(px)&& bj.isBj(pz)){
                result = 0;
            }else if(bj.isBj(pz)||xDot[xDot.length - 1]>21){
                result = 0.92;
            } else if (zDot[zDot.length - 1] <= 21 && xDot[xDot.length - 1] < zDot[zDot.length - 1]) {
                result = 0.92;
            }else if( xDot[xDot.length - 1] == zDot[zDot.length - 1]){
                result = 0;
            }
            zContext.addCount();
            zContext.addResult(result);
            log.info("第{}靴第{}局压{}:{} ---- cnt: {} min: {} max: {}  result: {}   结果 {}  {}{} - {}{} "
                    , i, round,zContext.getName(),String.format("%.4f",expZWin), zContext.getCount()
                    , String.format("%.2f",zContext.getMinWin()), String.format("%.2f",zContext.getMaxWin()), String.format("%.2f",zContext.getResult()),String.format("%.2f",result), px, xDot[xDot.length-1], pz, zDot[zDot.length-1]);
            return true;
        }
        return false;
    }

    public Boolean isBj(List<Pocker> cards) {
       return Blackjack.isBlackjack(cards.stream().map(Pocker::getBlackjackDot).collect(Collectors.toList()));
    }
    public static void main(String[] args) throws Exception {
//        test();
        MockContext c0 = new MockContext("total");
        for (int i = 1; i <= 1000; i++) {
            MockContext c = mock(i,0.99);
            log.info("第{}靴---次数 = {} -----max={} ----- min={}----结果 = {}",i, c.getCount(), String.format("%.2f",c.getMaxWin()), String.format("%.2f",c.getMinWin()),String.format("%.2f", c.getResult()));
            c0.merge(c);
            log.info("total---次数 = {} -----max={} ----- min={}----结果 = {}", c0.getCount(), String.format("%.2f",c0.getMaxWin()), String.format("%.2f",c0.getMinWin()), String.format("%.2f",c0.getResult()));
        }

    }

    private static void test() {
        BjBaccarat bjB = new BjBaccarat();
        Node root = Node.initAllNodes();
        List<Node> nodes = root.getEndNodes();
        for (Node node : nodes) {
            log.info("点数{}  牌{}", node.getDot(), node.getCards());
        }
        log.info("终态节点总计 {}", nodes.size());
        log.info("------------------------");
        List<Node> middleNode = root.getMiddleNode();
        for (Node node : middleNode) {
            log.info("中间节点点数{}  牌{}", node.getDot(), node.getCards());
        }
        log.info("中间态节点总计 {}", middleNode.size());
        Blackjack bj = new Blackjack();
        log.info("BJ 赢的概率：{}", bj.rBjWin());

        log.info("sum all node  概率：{}", bjB.t0());
        log.info("sum zxh {}", bjB.rHe()+ bjB.rXWin()+bjB.rZWin());


        log.info("bjWin 概率：{}", bjB.rBjWin());
        log.info("expBJ 期望：{}", bjB.expBJ());

        log.info("xBloom 概率：{}", bjB.rXBloom());
        log.info("expXBloom 期望：{}", bjB.expXBloom());

        log.info("zBloom 概率：{}", bjB.rZBloom());
        log.info("expZBloom 期望：{}", bjB.expZBloom());

        log.info("he 概率：{}", bjB.rHe());
        log.info("expHe 期望：{}", bjB.expHe());

        log.info("xWin 概率：{}", bjB.rXWin());
        log.info("expXWin 期望：{}", bjB.expXWin());

        log.info("zWin 概率：{}", bjB.rZWin());
        log.info("expZWin 期望：{}", bjB.expZWin());

        log.info("rOddNotBloom 概率： {}",bjB.rOddNotBloom());
        log.info("expXOdd 概率： {}",bjB.expXOdd());


        log.info("expP2_20 期望：{}", bjB.expP2_20());
        log.info("expZ21Win 期望：{}", bjB.expZ21Win());
        log.info("expLongBao 期望：{}", bjB.expLongBao());
    }

}

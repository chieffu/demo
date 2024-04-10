package com.chieffu.pocker.blackjack;

import com.chieffu.pocker.Pocker;
import com.chieffu.pocker.SuitEnum;
import com.chieffu.pocker.util.ConfigUtil;
import com.chieffu.pocker.util.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class MockBlackjack {

    private static boolean printLog=false;
    private static int times = 20;
    private static double commonQ = 1.05;
    private static double luckyQueueQ = 1.1;
    private static double pairQ = 1.1;
    private static double luckyThreeQ=1.1;
    private static double hotThreeQ = 1.1;
    @Data
    static class Context {
        String name;

        Context(String name) {
            this.name = name;
        }

        double count = 0;
        double result = 0;
        double maxWin = 0.0;
        double minWin = 0.0;

        void addCount() {
            count++;
        }

        void addCount(double count) {
            this.count += count;
        }

        void addResult(double d) {
            result += d;
            if (maxWin < result) {
                maxWin = result;
            }
            if (minWin > result) {
                minWin = result;
            }
        }
        public void merge(Context c){
            this.addResult(c.getResult());
            this.addCount(c.getCount());
        }
        public void merge(Context c,int times){
            this.addResult(c.getResult()*times);
            this.addCount(c.getCount()*times);
        }
    }


    private static void mockCommon(int shift, int round, Blackjack blackjack, List<Pocker> pz, List<Pocker> px, Context commonContext, List<Pocker> pks) throws NotFoundException {
        double xWin = blackjack.xWinExpectation();
        double luckyQueue = blackjack.luckyQueenExpectation(1000, 125, 19, 9, 4);
        double hotThree = blackjack.hotThreeExpectation(100, 20, 4, 2, 1);
        double luckyThree = blackjack.luckThreeExpectation(100, 40, 30, 10, 5);
        double pair = blackjack.pairExpectation(25, 8);
        if (xWin > commonQ||luckyQueue>luckyQueueQ|hotThree>hotThreeQ||luckyThree>luckyThreeQ||pair>pairQ) {
           //log.info("shift {}  round {} pai:{}  rate:{}",shift,round, Arrays.toString(blackjack.getPai()), xWin);
           blackjack.removePocker(pz.get(0));
            blackjack.removePocker(px);
            List<Integer> xCards = px.stream().map(p->Blackjack.dot(p)).collect(Collectors.toList());
            int[] dot = Blackjack.dots(xCards);

            while (dot[dot.length - 1] <= 11) {
                Pocker remove = pks.remove(pks.size() - 1);
                px.add(remove);
                blackjack.removePocker(remove);
                xCards = px.stream().map(p->Blackjack.dot(p)).collect(Collectors.toList());
                dot = Blackjack.dots(xCards);
            }
            Map<Integer, Double> zRates = Stage.zRate(blackjack.getPai(), pz.get(0).getNum());

            double currentWinRate = Stage.getCurrentWinRate(dot[dot.length - 1], zRates);
           if(printLog) log.info("当前 【 {} 】,赢面为{}",dot[dot.length-1],String.format("%.3f",currentWinRate));
            Stage xStage = Stage.getXStage(xCards);
            double oneMoreCardWinRate = xStage==null?0:Stage.xWinRate(zRates, xStage.oneMoreCardRateMap(blackjack.getPai()));
            if(printLog) log.info("加一张牌赢面为{}",String.format("%.3f",oneMoreCardWinRate));
            int doubleBet = 1;
            if(oneMoreCardWinRate>0.5 && oneMoreCardWinRate>currentWinRate){
                log.info("加倍下注");
                doubleBet = 2;
            }
            while (currentWinRate < oneMoreCardWinRate) {
                Pocker remove = pks.remove(pks.size() - 1);
                px.add(remove);
                blackjack.removePocker(remove);
                xCards = px.stream().map(p->Blackjack.dot(p)).collect(Collectors.toList());
                dot = Blackjack.dots(xCards);
                zRates = Stage.zRate(blackjack.getPai(), pz.get(0).getNum());
                currentWinRate = Stage.getCurrentWinRate(dot[dot.length - 1], zRates);
                xStage = Stage.getXStage(xCards);
                oneMoreCardWinRate = xStage == null ? 0 : Stage.xWinRate(zRates, xStage.oneMoreCardRateMap(blackjack.getPai()));
                if(printLog) log.info("当前 【 {} 】,赢面为{}",dot[dot.length-1],String.format("%.3f",currentWinRate));
                if(doubleBet==2)break;
                if(printLog) log.info("加一张牌赢面为{}",String.format("%.3f",oneMoreCardWinRate));

           }

            List<Integer> zCards = pz.stream().map(p->Blackjack.dot(p)).collect(Collectors.toList());
            blackjack.removePocker(pz.get(1));
            while (!Stage.isFinalZStage(zCards)) {
                Pocker remove = pks.remove(pks.size() - 1);
                pz.add(remove);
                blackjack.removePocker(remove);
                zCards = pz.stream().map(p->Blackjack.dot(p)).collect(Collectors.toList());
            }
           double r = 0;
            if (Blackjack.isBlackjack(zCards) && Blackjack.isBlackjack(xCards)) {

            } else if (Blackjack.isBlackjack(zCards)) {
                r=-1;
            } else if (Blackjack.isBlackjack(xCards)) {
                r=1.5;
            }else {
                int[] xdots = Blackjack.dots(xCards);
                int[] zdots = Blackjack.dots(zCards);
                if (xdots[xdots.length - 1] >= 22) {
                    r = -1;
                } else if (zdots[zdots.length - 1] >= 22) {
                    r = 1;
                } else if (xdots[xdots.length - 1] > zdots[zdots.length - 1]) {
                    r = 1;
                } else if (xdots[xdots.length - 1] < zdots[zdots.length - 1]) {
                    r = -1;
                }
            }
            r = r*doubleBet;
            commonContext.addResult(r);
            commonContext.addCount();
            if(printLog) log.info("{}靴{}把压{} 期望：{}  结果 {}  当前 max:{}  min:{}  result:{}  庄：{} - 闲：{} ", shift, round, commonContext.getName(), String.format("%.3f",xWin), r,commonContext.getMaxWin(),commonContext.getMinWin(),r, pz, px);

        }else{
            blackjack.removePocker(px);
            blackjack.removePocker(pz);

        }
    }

    private static void mockHotThree(int shift, int round, Blackjack blackjack, List<Pocker> pz, List<Pocker> px, Context hotThreeContext) {
        double hotThree = blackjack.hotThreeExpectation(100, 20, 4, 2, 1);
        if (hotThree > hotThreeQ) {
            List<Pocker> cards = Arrays.asList(px.get(0), px.get(1), pz.get(0));
            int[] dots = Blackjack.dotsOfPocker(cards);
            double r=-1;
            if (dots[dots.length - 1] == 21) {

                if (Blackjack.isFlush(cards)) {
                   r=20;
                } else {
                    r=4;
                }
            } else if (dots[dots.length - 1] == 20) {
               r=2;
            } else if (dots[dots.length - 1] == 19) {
               r=1;
            } else {
                r=-1;
            }
            hotThreeContext.addCount();
            hotThreeContext.addResult(r);
            if(printLog)log.info("{}靴{}把压{} 期望：{}  结果：{}", shift, round, hotThreeContext.getName(), String.format("%.3f",hotThree),r);
        }
    }

    private static void mockLuckyThree(int shift, int round, Blackjack blackjack, List<Pocker> pz, List<Pocker> px, Context luckyThreeContext) {
        double luckyThree = blackjack.luckThreeExpectation(100, 40, 30, 10, 5);
        if (luckyThree >luckyThreeQ) {
            List<Pocker> cards = Arrays.asList(px.get(0), px.get(1), pz.get(0));
            double r=-1;
            if (Blackjack.isOneOfKind(cards)) {
                if (Blackjack.isFlush(cards))
                   r=100;
                else
                   r=30;
            } else if (Blackjack.isFlush(cards)) {
                if (Blackjack.isStraight(cards)) {
                    r=40;
                } else {
                  r=5;
                }
            } else if (Blackjack.isStraight(cards)) {
               r=10;
            } else {
               r=-1;
            }
            luckyThreeContext.addCount();
            luckyThreeContext.addResult(r);
            if(printLog)log.info("{}靴{}把压{} 期望：{}  结果：{}", shift, round, luckyThreeContext.getName(), String.format("%.3f",luckyThree), r);
        }
    }

    private static void mockPair(int shift, int round, Blackjack blackjack, List<Pocker> pz, List<Pocker> px, Context pairContext) {
        double pair = blackjack.pairExpectation(25, 8);
        if (pair > pairQ) {
            pairContext.addCount();
            double  r= -1;
            if (px.get(0).equals(px.get(1))) {
                r=25;
            } else if (px.get(0).getNum() == px.get(1).getNum()) {
                r=8;
            } else {
                r=-1;
            }
            pairContext.addResult(r);
            if(printLog)log.info("{}靴{}把 压对子 期望：{}  结果：{}", shift, round, String.format("%.3f",pair), r);
        }
    }
    private static void mockBloom(int shift, int round, Blackjack blackjack, List<Pocker> pz, List<Pocker> px, Context pairContext) {
        double pair =  blackjack.bloomExpectation(1, 2, 9, 50, 100,250);
        if (pair > 1.05) {
            double r = -1;
            pairContext.addCount();
            int[] zz = Blackjack.dotsOfPocker(pz);
            if (zz[zz.length-1]>21) {
                if(zz.length==3){
                    r=1;
                } else if (zz.length == 4) {
                    r=2;
                } else if(zz.length==5){
                    r=9;
                }else if(zz.length==6){
                    r=50;
                }else if(zz.length==7){
                    r=100;
                }else if(zz.length>=8){
                    r=250;
                }
            }
            pairContext.addResult(r);
            if(printLog)log.info("{}靴{}把 压庄爆 期望：{}  结果：{}", shift, round, String.format("%.3f",pair), pairContext.getResult());
        }
    }

    private static void mockLuckyQueue(int shift, int round, Blackjack blackjack, List<Pocker> pz, List<Pocker> px, Context luckyQueueContext) {
        double luckyQueue = blackjack.luckyQueenExpectation(1000, 125, 19, 9, 4);
        if (luckyQueue > luckyQueueQ) {
            luckyQueueContext.addCount();
            double r=-1;
            if (px.get(0).equals(px.get(1))) {
                Pocker pocker = px.get(0);
                if (pocker.getSuit().equals(SuitEnum.HEART) && pocker.getNum() == 12) {
                    int[] zz = Blackjack.dotsOfPocker(pz);
                    if (zz[zz.length - 1] == 21) {
                        r=1000;
                    } else {
                        r=125;
                    }
                } else {
                    r=19;
                }
            } else {
                int[] xx = Blackjack.dotsOfPocker(px);
                if (xx[xx.length - 1] == 20) {
                    if (px.get(0).getSuit() == px.get(1).getSuit()) {
                        r=9;
                    } else {
                       r=4;
                    }
                } else {
                    r=-1;
                }
            }
            luckyQueueContext.addResult(r);
            if(printLog)log.info("{}靴{}把压{} 期望：{}  结果：{} 庄：{} - 闲：{} ", shift, round, luckyQueueContext.getName(), String.format("%.3f",luckyQueue), r,pz,px);
        }
    }


    private static Context mock(int shift, Blackjack bj) throws NotFoundException {
        List<Pocker> pks = Pocker.randomPocker(8);
        Blackjack blackjack = new Blackjack(pks.size() / 52);
        int round = 0;
        List<Pocker> pz = new ArrayList<>();
        List<Pocker> px = new ArrayList<>();
        Context luckyQueueContext = new Context("幸运女皇");
        Context pairContext = new Context("对子");
        Context luckyThreeContext = new Context("幸运三");
        Context hotThreeContext = new Context("烫三手");
        Context commonContext = new Context("底注");
        Context bloomContext = new Context("庄爆");
        while (pks.size() > StringUtils.newRandomInt(10, 20)) {
            round++;

            px.add(pks.remove(pks.size() - 1));
            px.add(pks.remove(pks.size() - 1));
            pz.add(pks.remove(pks.size() - 1));
            pz.add(pks.remove(pks.size() - 1));

//            mockLuckyQueue(shift, round, blackjack, pz, px, luckyQueueContext);
//            mockPair(shift, round, blackjack, px, px, pairContext);
//            mockLuckyThree(shift, round, blackjack, pz, px, luckyThreeContext);
//            mockHotThree(shift, round, blackjack, pz, px, hotThreeContext);

            mockCommon(shift, round, blackjack, pz, px, commonContext, pks);
//            mockBloom(shift, round, blackjack, pz, px, bloomContext);

//            blackjack.removePocker(px);
//            blackjack.removePocker(pz);
//

//            log.info("庄：{} 闲家：{}", pz, px);
            px.clear();
            pz.clear();
        }

        Context sum = new Context("sum");
        sum.merge(luckyQueueContext, times);
        sum.merge(pairContext , times);
        sum.merge(luckyThreeContext, times);
        sum.merge(hotThreeContext,times);
        sum.merge(commonContext);
        sum.merge(bloomContext,times);
        return sum;

    }

    public static void main(String[] args) {
        try {
            printLog = Boolean.parseBoolean(ConfigUtil.getSetting("mock.log.detail","false"));
            commonQ = Double.parseDouble(ConfigUtil.getSetting("mock.common.q", "1.05"));
            luckyThreeQ = Double.parseDouble(ConfigUtil.getSetting("mock.luckyThree.q", "1.1"));
            hotThreeQ = Double.parseDouble(ConfigUtil.getSetting("mock.hotThree.q", "1.1"));
            pairQ= Double.parseDouble(ConfigUtil.getSetting("mock.pair.q", "1.1"));
            luckyQueueQ = Double.parseDouble(ConfigUtil.getSetting("mock.luckyQueue.q", "1.1"));
            times = Integer.parseInt(ConfigUtil.getSetting("mock.times", "20"));
            Context c0 = new Context("total");
            for (int i = 1; i <= 100; i++) {
                Blackjack bj = new Blackjack(8);
                Context c = mock(i, bj);
                log.info("第{}靴---次数 = {} -----max={} ----- min={}----结果 = {}",i, c.getCount(), c.getMaxWin(), c.getMinWin(), c.getResult());
                c0.merge(c);
                log.info("total---次数 = {} -----max={} ----- min={}----结果 = {}", c0.getCount(), c0.getMaxWin(), c0.getMinWin(), c0.getResult());
            }
           //bjhui24 huihui2403   2.tt
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }


    }
}
package com.chieffu.pocker.blackjack.mock;

import com.chieffu.pocker.Ma;
import com.chieffu.pocker.Pocker;
import com.chieffu.pocker.SuitEnum;
import com.chieffu.pocker.blackjack.Blackjack;
import com.chieffu.pocker.blackjack.MockContext;
import com.chieffu.pocker.blackjack.NotFoundException;
import com.chieffu.pocker.util.ConfigUtil;
import com.chieffu.pocker.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class MockBlackjack {

    private static boolean printLog=false;
    private static int times = 1;
    private static double commonQ = 1.05;
    private static double luckyQueueQ = 1.1;
    private static double pairQ = 1.1;
    private static double luckyThreeQ=1.1;
    private static double hotThreeQ = 1.1;


    private static void mockCommon(int shift, int round, Blackjack blackjack, List<Pocker> pz, List<Pocker> px, MockContext commonContext, List<Pocker> pks) throws NotFoundException {
        double expectation = blackjack.highLowCardCounting();
//        double zBloom = 1-blackjack.rZNotBloom(0);
//        double r9_10 = blackjack.countPai()/(double)(blackjack.countPai(9)+blackjack.countPai(10));
        double luckyQueue = blackjack.expLuckyQueen(1000, 125, 19, 9, 4);
        double hotThree = blackjack.expHotThree(100, 20, 4, 2, 1);
        double luckyThree = blackjack.expLuckThree(100, 40, 30, 10, 5);
        double pair = blackjack.expPair(25, 8);
        if (luckyQueue>luckyQueueQ|hotThree>hotThreeQ||luckyThree>luckyThreeQ||pair>pairQ) {
           //log.info("shift {}  round {} pai:{}  rate:{}",shift,round, Arrays.toString(blackjack.getPai()), xWin);
            List<Integer> xCards = px.stream().map(p->Blackjack.dot(p)).collect(Collectors.toList());
            int[] dot = Blackjack.dots(xCards);
            blackjack.removePocker(px);
            blackjack.removePocker(pz.get(0));
            int zcard = pz.get(0).getBlackjackDot();
            List<Pocker> px1 = new ArrayList<>();
           if(px.get(0).getNum()==px.get(1).getNum()){
             double xwin0 = blackjack.expXWin(xCards,zcard);
             double splitWin0 = blackjack.expXWin(Arrays.asList(px.get(0).getBlackjackDot()),zcard);
             if(splitWin0>1){
                //操作分牌 TODO
                 log.info("拆牌...");
                 px1.add(px.remove(0));
             }
            }
            while (dot[dot.length - 1] <= 11) {
                Pocker remove = pks.remove(pks.size() - 1);
                px.add(remove);
                blackjack.removePocker(remove);
                xCards = px.stream().map(p->Blackjack.dot(p)).collect(Collectors.toList());
                dot = Blackjack.dots(xCards);
            }
            Blackjack.Stage xStage = Blackjack.Stage.getXStage(xCards);
            Blackjack.Stage zStage = Blackjack.Stage.getZStage(Collections.singletonList(zcard));

            double currentWinRate = xStage.getXCurrentWinRate(zStage,blackjack.getPai());
            double oneMoreCardWinRate = xStage.getOneMoreCardWinRate(zStage,blackjack.getPai());
            if(printLog) log.info("{}靴{}把压{} 真数：{}  闲{}{} 庄[{}]  当前胜率为{}，加一张胜率为{}  ", shift, round, commonContext.getName(), String.format("%.3f",expectation), px,dot[dot.length-1],pz.get(0),String.format("%.4f",currentWinRate), String.format("%.4f",oneMoreCardWinRate));
            int doubleBet = 1;
            boolean touXiang = xCards.size()==2&&Math.max(currentWinRate,oneMoreCardWinRate)<0.05;
            if(touXiang && px1.isEmpty()){
                if(printLog) log.info("投降输一半");
            }else  if(oneMoreCardWinRate>0.5 && px.size()==2 && px1.isEmpty()){
                if(printLog)  log.info("加倍下注");
                doubleBet = 2;
            }
            while (!touXiang && currentWinRate < oneMoreCardWinRate) {
                Pocker remove = pks.remove(pks.size() - 1);
                px.add(remove);
                blackjack.removePocker(remove);
                xCards = px.stream().map(p -> Blackjack.dot(p)).collect(Collectors.toList());
                dot = Blackjack.dots(xCards);
                xStage = Blackjack.Stage.getXStage(xCards);
                if(xStage==null){
                    break;
                }
                currentWinRate = xStage.getXCurrentWinRate(zStage, blackjack.getPai());
                oneMoreCardWinRate = xStage.getOneMoreCardWinRate(zStage, blackjack.getPai());
                if(printLog) log.info("{}靴{}把压{} 真数：{}  闲{}{} 庄[{}]  当前胜率为{}，加一张胜率为{}  ", shift, round, commonContext.getName(), String.format("%.3f",expectation), px,dot[dot.length-1],pz.get(0),String.format("%.4f",currentWinRate), String.format("%.4f",oneMoreCardWinRate));
                if (doubleBet == 2) break;
            }
            if(!px1.isEmpty()){
                Pocker remove = pks.remove(pks.size() - 1);
                px.add(remove);
                blackjack.removePocker(remove);
                xCards = px.stream().map(p->Blackjack.dot(p)).collect(Collectors.toList());
                dot = Blackjack.dots(xCards);
            }
            List<Integer> zCards = pz.stream().map(p->Blackjack.dot(p)).collect(Collectors.toList());
            blackjack.removePocker(pz.get(1));
            while (!Blackjack.Stage.isFinalZStage(zCards)) {
                Pocker remove = pks.remove(pks.size() - 1);
                pz.add(remove);
                blackjack.removePocker(remove);
                zCards = pz.stream().map(p->Blackjack.dot(p)).collect(Collectors.toList());
            }
            int[] zdots = Blackjack.dots(zCards);
            double r = 0;
            if(touXiang){
                r = -0.5;
            }else if (Blackjack.isBlackjack(zCards) && Blackjack.isBlackjack(xCards)) {

            } else if (Blackjack.isBlackjack(zCards)) {
                r=-1;
            } else if (Blackjack.isBlackjack(xCards)) {
                r=1.5;
            }else {
                if (dot[dot.length - 1] >= 22) {
                    r = -1;
                } else if (zdots[zdots.length - 1] >= 22) {
                    r = 1;
                } else if (dot[dot.length - 1] > zdots[zdots.length - 1]) {
                    r = 1;
                } else if (dot[dot.length - 1] < zdots[zdots.length - 1]) {
                    r = -1;
                }
            }
            r = r*doubleBet * times;
            commonContext.addResult(r);
            commonContext.addCount(times);
            if(printLog) log.info("{}靴{}把压{} 真数：{}  结果 {}  当前 max:{}  min:{}  result:{}  闲：{}{} - 庄：{}{} ", shift, round, commonContext.getName(), String.format("%.3f",expectation), r,commonContext.getMaxWin(),commonContext.getMinWin(),commonContext.getResult(),  dot[dot.length-1],px,zdots[zdots.length - 1],pz);
            blackjack.addPocker(px);
            blackjack.addPocker(pz);
//            log.info(" {} ",blackjack.expXWin0());
        }
    }

    private static void mockHotThree(int shift, int round, Blackjack blackjack, List<Pocker> pz, List<Pocker> px, MockContext hotThreeContext) {
        double hotThree = blackjack.expHotThree(100, 20, 4, 2, 1);
        if (hotThree > hotThreeQ) {
            List<Pocker> cards = Arrays.asList(px.get(0), px.get(1), pz.get(0));
            int[] dots = Blackjack.dotsOfPocker(cards);
            double r=-1;
            if (dots[dots.length - 1] == 21) {

                if (Ma.isFlush(cards)) {
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
            if(printLog)log.info("{}靴{}把压{} 期望：{}  结果：{} 庄：{} - 闲：{}", shift, round, hotThreeContext.getName(), String.format("%.3f",hotThree),r,pz,px);
        }
    }

    private static void mockLuckyThree(int shift, int round, Blackjack blackjack, List<Pocker> pz, List<Pocker> px, MockContext luckyThreeContext) {
        double luckyThree = blackjack.expLuckThree(100, 40, 30, 10, 5);
        if (luckyThree >luckyThreeQ) {
            List<Pocker> cards = Arrays.asList(px.get(0), px.get(1), pz.get(0));
            double r=-1;
            if (Ma.isOneOfKind(cards)) {
                if (Ma.isFlush(cards))
                   r=100;
                else
                   r=30;
            } else if (Ma.isFlush(cards)) {
                if (Ma.isStraight(cards)) {
                    r=40;
                } else {
                  r=5;
                }
            } else if (Ma.isStraight(cards)) {
               r=10;
            } else {
               r=-1;
            }
            luckyThreeContext.addCount();
            luckyThreeContext.addResult(r);
            if(printLog)log.info("{}靴{}把压{} 期望：{}  结果：{} 庄：{} - 闲：{}", shift, round, luckyThreeContext.getName(), String.format("%.3f",luckyThree), r,pz,px);
        }
    }

    private static void mockPair(int shift, int round, Blackjack blackjack, List<Pocker> pz, List<Pocker> px, MockContext pairContext) {
        double pair = blackjack.expPair(25, 8);
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
    private static void mockBloom(int shift, int round, Blackjack blackjack, List<Pocker> pz, List<Pocker> px, MockContext pairContext) {
        double pair =  blackjack.expBloom(1, 2, 9, 50, 100,250);
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

    private static void mockLuckyQueue(int shift, int round, Blackjack blackjack, List<Pocker> pz, List<Pocker> px, MockContext luckyQueueContext) {
        double luckyQueue = blackjack.expLuckyQueen(1000, 125, 19, 9, 4);
        if (luckyQueue > luckyQueueQ) {
            luckyQueueContext.addCount();
            double r=-1;
            if (px.get(0).equals(px.get(1))) {
                Pocker pocker = px.get(0);
                if (pocker.getSuit().equals(SuitEnum.HEART) && pocker.getNum() == 12) {
                    int[] zz = Blackjack.dots(pz.get(0),pz.get(1));
                    if (zz[zz.length - 1] == 21 ) {
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


    private static MockContext mock(int shift) throws NotFoundException {
        List<Pocker> pks = Pocker.randomPocker(8);
        Blackjack blackjack = new Blackjack(pks.size() / 52);
        int round = 0;
        List<Pocker> pz = new ArrayList<>();
        List<Pocker> px = new ArrayList<>();
        MockContext luckyQueueContext = new MockContext("幸运女皇");
        MockContext pairContext = new MockContext("对子");
        MockContext luckyThreeContext = new MockContext("幸运三");
        MockContext hotThreeContext = new MockContext("烫三手");
        MockContext commonContext = new MockContext("底注");
        MockContext bloomContext = new MockContext("庄爆");
        int cut =  StringUtils.newRandomInt(140, 170);
        while (pks.size() >cut) {
            round++;

            px.add(pks.remove(pks.size() - 1));
            px.add(pks.remove(pks.size() - 1));
            pz.add(pks.remove(pks.size() - 1));
            pz.add(pks.remove(pks.size() - 1));

            mockLuckyQueue(shift, round, blackjack, pz, px, luckyQueueContext);
            mockPair(shift, round, blackjack, px, px, pairContext);
            mockLuckyThree(shift, round, blackjack, pz, px, luckyThreeContext);
            mockHotThree(shift, round, blackjack, pz, px, hotThreeContext);

//            mockCommon(shift, round, blackjack, pz, px, commonContext, pks);
//            mockBloom(shift, round, blackjack, pz, px, bloomContext);

            blackjack.removePocker(px);
            blackjack.removePocker(pz);


//            log.info("庄：{} 闲家：{}", pz, px);
            px.clear();
            pz.clear();
        }

        MockContext sum = new MockContext("sum");
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
            printLog = true;
            MockContext c0 = new MockContext("total");
            for (int i = 1; i <= 10000; i++) {
                MockContext c = mock(i);
                if(c.getCount()>0) {
                    log.info("第{}靴---次数 = {} -----max={} ----- min={}----结果 = {}", i, String.format("%.3f",c.getCount()), String.format("%.3f",c.getMaxWin()),  String.format("%.3f",c.getMinWin()), String.format("%.3f", c.getResult()));
                    c0.merge(c);
                    log.info("total---次数 = {} -----max={} ----- min={}----结果 = {}  win:{}  lose:{} even:{}   win/all:{}  win/win+lose:{}", String.format("%.3f",c0.getCount()), String.format("%.3f",c0.getMaxWin()), String.format("%.3f",c0.getMinWin()), String.format("%.3f",c0.getResult()),c0.getWin(),c0.getLose(),c0.getEven(),c0.getWin()/(c0.getEven()+c0.getWin()+c0.getLose()),c0.getWin()/(c0.getWin()+c0.getLose()));
                }
            }
           //bjhui24 huihui2403   2.tt
            //laoxiao qq8896946  ybtyjt.com
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }


    }
}
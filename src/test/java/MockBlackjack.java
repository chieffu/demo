import com.chieffu.pocker.Pocker;
import com.chieffu.pocker.SuitEnum;
import com.chieffu.pocker.blackjack.Blackjack;
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

    @Data
   static class Context{
        String name;
        Context(String name){
            this.name = name;
        }
        double count = 0;
        double result = 0;
        double maxWin = 0.0;
        double minWin = 0.0;
        void addCount(){
            count++;
        }
        void addCount(double count){
            this.count+=count;
        }
        void addResult(double d){
            result+=d;
            if(maxWin<result){
                maxWin = result;
            }
            if(minWin>result){
                minWin=result;
            }
        }
    }


    private static void mockCommon(int shift, int round, Blackjack blackjack, List<Pocker> pz, List<Pocker> px, Context commonContext, List<Pocker> pks) {
        double xWin = blackjack.xWinExpectation();
        if (xWin > 1) {
            List<Integer> xCards = px.stream().map(Pocker::getNum).collect(Collectors.toList());
            int[] dot = Blackjack.dots(xCards);
            while(dot[dot.length-1]<=11){
                Pocker remove = pks.remove(pks.size() - 1);
                px.add(remove);
                blackjack.removePocker(remove);
                xCards = px.stream().map(Pocker::getNum).collect(Collectors.toList());
                dot = Blackjack.dots(xCards);
            }
            Map<Integer, Double> zRates = Blackjack.zRate(blackjack.getPai(), pz.get(0).getNum());

            double currentWinRate = Blackjack.getCurrentWinRate( dot[dot.length-1],  zRates);
            if(dot.length>1){
                currentWinRate+=Blackjack.xWinRate(dot[0],blackjack.getPai());
            }
            double nextWinRate = Blackjack.xWinRate(dot[dot.length-1],blackjack.getPai());

            while (currentWinRate < nextWinRate) {
                Pocker remove = pks.remove(pks.size() - 1);
                px.add(remove);
                blackjack.removePocker(remove);
                xCards = px.stream().map(Pocker::getNum).collect(Collectors.toList());
                dot = Blackjack.dots(xCards);
                currentWinRate =  Blackjack.getCurrentWinRate( dot[dot.length-1],  zRates);
                nextWinRate = Blackjack.xWinRate(dot[dot.length-1],blackjack.getPai());
            }

            List<Integer> zCards = pz.stream().map(Pocker::getNum).collect(Collectors.toList());
            while(!Blackjack.isFinalZStage(zCards)){
                Pocker remove = pks.remove(pks.size() - 1);
                pz.add(remove);
                blackjack.removePocker(remove);
                zCards = pz.stream().map(Pocker::getNum).collect(Collectors.toList());
            }

            if(Blackjack.isBlackjack(zCards) && Blackjack.isBlackjack(xCards)){

            }else if(Blackjack.isBlackjack(zCards)){
                commonContext.addResult(-1);
            }else if( Blackjack.isBlackjack(xCards)){
                commonContext.addResult(1.5);
            }
            int[] xdots = Blackjack.dots(xCards);
            int[] zdots = Blackjack.dots(zCards);
            if(xdots[xdots.length-1]>=22){
                commonContext.addResult(-1);
            }else if(zdots[zdots.length-1]>=22){
                commonContext.addResult(1);
            }else if(xdots[xdots.length-1]>zdots[zdots.length-1]){
                commonContext.addResult(1);
            }else if(xdots[xdots.length-1]<zdots[zdots.length-1]){
                commonContext.addResult(-1);
            }
            commonContext.addCount();
            log.info("{}靴{}把压{} 期望：{}  结果：{}", shift, round, commonContext.getName(),xWin, commonContext.getResult());

        }
    }

    private static void mockHotThree(int shift, int round, Blackjack blackjack, List<Pocker> pz, List<Pocker> px, Context hotThreeContext) {
        double hotThree = blackjack.hotThreeExpectation(100, 20, 4, 2, 1);
        if(hotThree>1.05){
            List<Pocker> cards = Arrays.asList(px.get(0), px.get(1), pz.get(0));
            int[] dots = Blackjack.dotsOfPocker(cards);
            if(dots[dots.length-1]==21){

                if(Blackjack.isFlush(cards)){
                    hotThreeContext.addResult(20);
                }else {
                    hotThreeContext.addResult(4);
                }
            }else if(dots[dots.length-1]==20){
                hotThreeContext.addResult(2);
            }else if(dots[dots.length-1]==19){
                hotThreeContext.addResult(1);
            }else{
                hotThreeContext.addResult(-1);
            }
            hotThreeContext.addCount();
            log.info("{}靴{}把压{} 期望：{}  结果：{}", shift, round, hotThreeContext.getName(),hotThree, hotThreeContext.getResult());
        }
    }

    private static void mockLuckyThree(int shift, int round, Blackjack blackjack, List<Pocker> pz, List<Pocker> px, Context luckyThreeContext) {
        double luckyThree = blackjack.luckThreeExpectation(100, 40, 30, 10, 5);
        if(luckyThree>1.05){
            List<Pocker> cards = Arrays.asList(px.get(0), px.get(1), pz.get(0));
            if(Blackjack.isOneOfKind(cards)){
                if(Blackjack.isFlush(cards))
                    luckyThreeContext.addResult(100);
                else
                    luckyThreeContext.addResult(30);
            }else if(Blackjack.isFlush(cards)) {
                if(Blackjack.isStraight(cards)){
                    luckyThreeContext.addResult(40);
                }else {
                    luckyThreeContext.addResult(5);
                }
            }else if(Blackjack.isStraight(cards)){
                luckyThreeContext.addResult(10);
            }else{
                luckyThreeContext.addResult(-1);
            }
            luckyThreeContext.addCount();
            log.info("{}靴{}把压{} 期望：{}  结果：{}", shift, round, luckyThreeContext.getName(),luckyThree, luckyThreeContext.getResult());
        }
    }

    private static void mockPair(int shift, int round, Blackjack blackjack, List<Pocker> pz, List<Pocker> px, Context pairContext) {
        double pair = blackjack.pairExpectation(25, 8);
        if (pair > 1) {
            pairContext.addCount();
            if(px.get(0).equals(px.get(1))) {
                pairContext.addResult(25);
            }else if(px.get(0).getNum() == px.get(1).getNum()){
                pairContext.addResult(8);
            }else{
                pairContext.addResult(-1);
            }
            log.info("{}靴{}把 压对子 期望：{}  结果：{}", shift, round, pair, pairContext.getResult());
        }
    }

    private static void mockLuckyQueue(int shift, int round, Blackjack blackjack, List<Pocker> pz, List<Pocker> px, Context luckyQueueContext) {
        double luckyQueue = blackjack.luckyQueenExpectation(1000, 125, 19, 9, 4);
        if (luckyQueue > 1.05) {
             luckyQueueContext.addCount();
            if (px.get(0).equals(px.get(1))) {
                Pocker pocker = px.get(0);
                if (pocker.getSuit().equals(SuitEnum.HEART) && pocker.getNum() == 12) {
                    int[] zz = Blackjack.dotsOfPocker(pz);
                    if (zz[zz.length - 1] == 21) {
                        luckyQueueContext.addResult(1000);
                    } else {
                        luckyQueueContext.addResult(125);
                    }
                } else {
                    luckyQueueContext.addResult(19);
                }
            } else {
                int[] xx = Blackjack.dotsOfPocker(px);
                if (xx[xx.length - 1] == 20) {
                    if (px.get(0).getSuit() == px.get(1).getSuit()) {
                        luckyQueueContext.addResult(9);
                    } else {
                        luckyQueueContext.addResult(4);
                    }
                } else {
                    luckyQueueContext.addResult(-1);
                }
            }
            log.info("{}靴{}把压{} 期望：{}  结果：{}", shift, round, luckyQueueContext.getName(),luckyQueue, luckyQueueContext.getResult());
        }
    }



    private static double[] mock(int shift, Blackjack bj) {
        List<Pocker> pks = Pocker.randomPocker(8);
        Blackjack blackjack = new Blackjack(pks.size() / 52);
        int round = 0;
        List<Pocker> pz = new ArrayList<>();
        List<Pocker> px = new ArrayList<>();
        Context luckyQueueContext = new Context("luckyQueue");
        Context pairContext = new Context("pair");
        Context luckyThreeContext = new Context("luckyThree");
        Context hotThreeContext = new Context("hotThree");
        Context commonContext= new Context("common");
        List<Context> contexts = Arrays.asList(luckyQueueContext, pairContext, luckyThreeContext, hotThreeContext,commonContext);
        while (pks.size() > StringUtils.newRandomInt(80,120)) {
            round++;

            px.add(pks.remove(pks.size() - 1));
            px.add(pks.remove(pks.size() - 1));
            pz.add(pks.remove(pks.size() - 1));
            pz.add(pks.remove(pks.size() - 1));
//            mockLuckyQueue(shift, round, blackjack, pz, px, luckyQueueContext);
//            mockPair(shift, round, blackjack, px, px, pairContext);
//            mockLuckyThree(shift, round, blackjack, pz, px, luckyThreeContext);
//            mockHotThree(shift, round, blackjack, pz, px, hotThreeContext);

            blackjack.removePocker(px);
            blackjack.removePocker(pz);

             mockCommon(shift, round, blackjack, pz, px, commonContext,pks);

            px.clear();
            pz.clear();
        }

        Context sum = new Context("sum");
        for(Context context:contexts) {
            sum.addCount(context.getCount());
            sum.addResult(context.getResult());
            log.info("名称{}---第{}靴-----次数 {}----max={} ----- min={}----结果 = {}", context.getName(), shift, context.getCount(), context.getMaxWin(), context.getMinWin(), context.getResult());
        }
        return new double[]{sum.getCount(), sum.getResult()};

    }
    public static void main(String[] args) {
        double[] cr = new double[2];
        double maxWin=0;
        double minWin=0;
        for (int i = 1; i <= 1000; i++) {
            Blackjack bj = new Blackjack(8);
            double[] cri = mock(i, bj);
            for (int j = 0; j < cri.length; j++) {
                cr[j] += cri[j];
            }
            if(cr[1]>maxWin) maxWin=cr[1];
            if(cr[1]<minWin) minWin=cr[1];
        }
        log.info("-次数 = {} -------max={} ----- min={}----结果 = {}", cr[0],maxWin,minWin,cr[1]);
        //bjhui24 huihui2403   2.tt


    }

}

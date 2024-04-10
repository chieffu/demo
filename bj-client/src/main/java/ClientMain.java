//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Slf4j
//public class ClientMain {
//
//
//        private static boolean printLog=false;
//        private static int times = 20;
//        private static double baseQ = 1.05;
//        private static double luckyQueenQ = 1.1;
//        private static double pairQ = 1.1;
//        private static double luckyThreeQ=1.1;
//        private static double hotThreeQ = 1.1;
//
//        private static double bloomQ = 1.1;
//        @Data
//        static class Context {
//            String name;
//
//            Context(String name) {
//                this.name = name;
//            }
//
//            double count = 0;
//            double result = 0;
//            double maxWin = 0.0;
//            double minWin = 0.0;
//
//            void addCount(String tableId,String roundId) {
//                count++;
//                //TODO去下注 并记录
//            }
//
//            void addCount(String tableId,String roundId, double count) {
//
//                this.count += count;
//            }
//
//            void addResult(String tableId,String roundId ,double d) {
//                result += d;
//                if (maxWin < result) {
//                    maxWin = result;
//                }
//                if (minWin > result) {
//                    minWin = result;
//                }
//            }
//            public void merge(Context c,String tableId,String roundId){
//                this.addResult(tableId,roundId,c.getResult());
//                this.addCount(tableId,roundId,c.getCount());
//            }
//            public void merge(Context c,int times,String tableId,String roundId){
//                this.addResult(tableId,roundId,c.getResult()*times);
//                this.addCount(tableId,roundId,c.getCount()*times);
//            }
//        }
//
//        public static Double getLuckyQueenResult(List<Pocker> pz,List<Pocker> px) {
//            double r=-1;
//            if (px.get(0).equals(px.get(1))) {
//                Pocker pocker = px.get(0);
//                if (pocker.getSuit().equals(SuitEnum.HEART) && pocker.getNum() == 12) {
//                    int[] zz = Blackjack.dotsOfPocker(pz);
//                    if (zz[zz.length - 1] == 21) {
//                        r=1000;
//                    } else {
//                        r=125;
//                    }
//                } else {
//                    r=19;
//                }
//            } else {
//                int[] xx = Blackjack.dotsOfPocker(px);
//                if (xx[xx.length - 1] == 20) {
//                    if (px.get(0).getSuit() == px.get(1).getSuit()) {
//                        r=9;
//                    } else {
//                        r=4;
//                    }
//                } else {
//                    r=-1;
//                }
//            }
//            return r;
//        }
//        private static Context mock(int shift,BlackjackServer server)  {
//            List<Pocker> pks = Pocker.randomPocker(8);
//            int round = 0;
//            List<Pocker> pz = new ArrayList<>();
//            List<Pocker> px = new ArrayList<>();
//            String tableId = shift+"";
//            int cutPai = StringUtils.newRandomInt(100, 200);
//            Context baseContext = new Context(Type.BASE_PLAY.getName());
//            Context luckyQueenContext = new Context(Type.LUCKY_QUEEN.getName());
//            Context hotThreeContext = new Context(Type.HOT_THREE.getName());
//            Context luckyThreeContext = new Context(Type.LUCKY_THREE.getName());
//            Context pairContext = new Context(Type.PAIR.getName());
//            Context bloomContext = new Context(Type.BLOOM.getName());
//            Round theRound;
//            while (pks.size() > cutPai) {
//                round++;
//                if(printLog)log.info("开始第" + tableId + "桌第" + round + "局 : " + theRound);
//                theRound = server.getRound(tableId, round+"");
//                if(printLog)log.info("查询第"+tableId+"桌第"+round+"局期望...");
//                Map<String, Double> expections =  server.getExpectations("shift");
//                if(printLog)log.info("查到期望为：{}",expections);
//                boolean needBase = expections.get(Type.LUCKY_QUEEN)> luckyQueenQ
//                        ||expections.get(Type.LUCKY_THREE)>luckyThreeQ
//                        ||expections.get(Type.HOT_THREE)>hotThreeQ
//                        ||expections.get(Type.PAIR)>pairQ
//                        ||expections.get(Type.BLOOM)>bloomQ;
//                if(needBase) {
//                    if(printLog)log.info("有期望值，先下一注base");
//                    baseContext.addCount(theRound.getTableId(),theRound.getRoundId());//下1注
//                }
//
//                if(expections.get(Type.LUCKY_QUEEN)> luckyQueenQ) {
//                    luckyQueenContext.addCount(theRound.getTableId(),theRound.getRoundId());
//                    //下注 luckyQueen
//                }
//                if(expections.get(Type.LUCKY_THREE)>luckyThreeQ) {
//                    luckyThreeContext.addCount(theRound.getTableId(),theRound.getRoundId());
//                    //下注 luckyThree
//                }
//                if(expections.get(Type.HOT_THREE)>hotThreeQ) {
//                    hotThreeContext.addCount(theRound.getTableId(),theRound.getRoundId());
//                    //下注 hotThree
//                }
//                if(expections.get(Type.PAIR)>pairQ){
//                    pairContext.addCount(theRound.getTableId(),theRound.getRoundId());
//                    //下注 pair
//                }
//                if(expections.get(Type.BLOOM)>bloomQ) {
//                    bloomContext.addCount(theRound.getTableId(),theRound.getRoundId());
//                    //下注 bloom
//                }
//                //统计结果
//                if(expections.get(Type.LUCKY_QUEEN)> luckyQueenQ) {
//                    luckyQueenContext.addResult(theRound.getTableId(),theRound.getRoundId(),luckyQueenResult);
//
//                }
//                if(expections.get(Type.LUCKY_THREE)>luckyThreeQ) {
//                    luckyThreeContext.addResult(theRound.getTableId(),theRound.getRoundId(),luckyThreeResult);
//                    //下注 luckyThree
//                }
//                if(expections.get(Type.HOT_THREE)>hotThreeQ) {
//                    hotThreeContext.addResult(theRound.getTableId(),theRound.getRoundId(),hotThreeResult);
//                    //下注 hotThree
//                }
//                if(expections.get(Type.PAIR)>pairQ){
//                    pairContext.addResult(theRound.getTableId(),theRound.getRoundId(),pairResult);
//                    //下注 pair
//                }
//                if(expections.get(Type.BLOOM)>bloomQ) {
//                    bloomContext.addResult(theRound.getTableId(),theRound.getRoundId(),bloomResult);
//                    //下注 bloom
//                }
//                //等待发牌
//                px.add(pks.remove(pks.size() - 1));
//                pz.add(pks.remove(pks.size() - 1));
//                px.add(pks.remove(pks.size() - 1));
//
//                if(needBase) {
//                    boolean needMore ;//需要决策是否要牌
//                    do {
//                        server.updateCards(tableId, round + "", pz.stream().map(Pocker::ofCard).collect(Collectors.toList()), Collections.singletonList(px.stream().map(Pocker::ofCard).collect(Collectors.toList())));
//                        double currentWinExpect = server.getCurrentWinRate(tableId, px.stream().map(Pocker::ofCard).collect(Collectors.toList()), pz.get(0).ofCard());
//                        double oneMoreCardWinExpect = server.getOneMoreWinRate(tableId, px.stream().map(Pocker::ofCard).collect(Collectors.toList()), pz.get(0).ofCard());
//                        needMore = oneMoreCardWinExpect > currentWinExpect;
//                        if(needMore){
//                            if(oneMoreCardWinExpect>0.7) {
//                                log.info("赢的概率超过7成，加倍");
//                                //TODO doubleBase 加倍
//                                needMore = false;//加倍后就不能再要牌了
//                            }
//                            px.add(pks.remove(pks.size() - 1));
//                            //hit 要牌
//                        }
//                    }while(needMore);
//                }
//
//                //TODO 其他player 决策，根据明牌情况 更新牌或者移除牌
//
//
//                //庄家 明第二张牌
//                pz.add(pks.remove(pks.size() - 1));
//
//                //更新牌堆
//                server.updateCards(tableId, round + "", pz.stream().map(Pocker::ofCard).collect(Collectors.toList()), Collections.singletonList(px.stream().map(Pocker::ofCard).collect(Collectors.toList())));
//
//                while(!isFinalStage(pz)) { //如果庄家没到终态，庄家就会要牌
//                    //庄家
//                    pz.add(pks.remove(pks.size() - 1));
//
//                    //更新牌堆
//                    server.updateCards(tableId, round + "", pz.stream().map(Pocker::ofCard).collect(Collectors.toList()), Collections.singletonList(px.stream().map(Pocker::ofCard).collect(Collectors.toList())));
//
//                }
//                //更新牌堆
//                server.updateCards(tableId,round+"",pz.stream().map(Pocker::ofCard).collect(Collectors.toList()), Collections.singletonList(px.stream().map(Pocker::ofCard).collect(Collectors.toList())));
//
//                log.info("");
//                updateContext(pz,px,luckyQueenContext,luckyThreeQ);
//
//             }
//
//            Context sum = new Context("sum");
//            sum.merge(luckyQueenContext, times);
//            sum.merge(pairContext , times);
//            sum.merge(luckyThreeContext, times);
//            sum.merge(hotThreeContext,times);
//            sum.merge(commonContext);
//            sum.merge(bloomContext,times);
//            return sum;
//        }
//    }
//
//    private static boolean isFinalStage(List<Pocker> pz) {
//            List<Integer> cards = pz.stream().map(Pocker::getBlackjackDot).collect(Collectors.toList());
//            int sum = cards.stream().reduce(0, Integer::sum);
//            if(sum<=10&&cards.contains(1)) {
//                sum = sum+10;
//            }
//            return sum>16;
//    }
//
//    public static void main(String[] args) {
//        try {
//            printLog = Boolean.parseBoolean(ConfigUtil.getSetting("mock.log.detail","false"));
//            baseQ = Double.parseDouble(ConfigUtil.getSetting("mock.common.q", "1.05"));
//            luckyThreeQ = Double.parseDouble(ConfigUtil.getSetting("mock.luckyThree.q", "1.1"));
//            hotThreeQ = Double.parseDouble(ConfigUtil.getSetting("mock.hotThree.q", "1.1"));
//            pairQ= Double.parseDouble(ConfigUtil.getSetting("mock.pair.q", "1.1"));
//            luckyQueenQ = Double.parseDouble(ConfigUtil.getSetting("mock.luckyQueue.q", "1.1"));
//            bloomQ = Double.parseDouble(ConfigUtil.getSetting("mock.bloom.q", "1.1"));
//            times = Integer.parseInt(ConfigUtil.getSetting("mock.times", "20"));
//
//            Context c0 = new Context("total");
//            for (int i = 1; i <= 100; i++) {
//                Blackjack bj = new Blackjack(8);
//                Context c = mock(i, bj);
//                log.info("第{}靴---次数 = {} -----max={} ----- min={}----结果 = {}",i, c.getCount(), c.getMaxWin(), c.getMinWin(), c.getResult());
//                c0.merge(c);
//                log.info("total---次数 = {} -----max={} ----- min={}----结果 = {}", c0.getCount(), c0.getMaxWin(), c0.getMinWin(), c0.getResult());
//            }
//            //bjhui24 huihui2403   2.tt
//        } catch (Exception e) {
//            log.error(e.getLocalizedMessage(), e);
//        }
//
//
//    }
//    }
//}

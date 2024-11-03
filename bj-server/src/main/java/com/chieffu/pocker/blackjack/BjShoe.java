package com.chieffu.pocker.blackjack;

import com.chieffu.pocker.Pocker;
import lombok.Data;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Data
public class BjShoe {
    String tableId;
    Date startTime = new Date();

    Date endTime ;
    public BjShoe(String tableId){
        this.tableId=tableId;
    }
    Map<String, BjRound> roundMap = new ConcurrentHashMap<>();

    public List<BjRound> getRoundList() {
        return roundMap.values().stream().sorted(Comparator.comparing(BjRound::getRoundId)).collect(Collectors.toList());
    }

    public BjRound getCurrentRound(){
        List<BjRound> rounds = getRoundList();
        if(rounds.size()==0)return null;
        BjRound latest = rounds.get(rounds.size()-1);
        if(latest.isOver())
            return null;
        return latest;
    }
    public void addRound(BjRound round) {
        if (round.equals(roundMap.get(round.getRoundId()))) return;
        roundMap.put(round.getRoundId(), round);
    }

    public BjRound getBjRound(String roundId) {
        if(roundId==null||roundId.length()==0)return new BjRound(roundId);
         BjRound bjRound = roundMap.get(roundId);
         if(bjRound==null){
             bjRound = new BjRound(roundId);
             bjRound.setTableId(tableId);
            roundMap.put(roundId,bjRound);
         }
         return bjRound;
    }

    public void updateCards(String roundId, List<Pocker> bankCards, List<List<Pocker>> playsCards) throws NotFoundException {
        BjRound bjRound = getBjRound(roundId);
        if (!bankCards.containsAll(bjRound.getBanker())) {
            throw new NotFoundException("第" + roundId + "的庄家牌" + bankCards + "与上报的牌" + bjRound.getBanker() + "不一致");
        }
        bjRound.setBanker(bankCards);
        List<Pocker> playerCards = playsCards.stream().flatMap(Collection::stream).collect(Collectors.toList());
        if (!playerCards.containsAll(bjRound.getPlayers().stream().flatMap(Collection::stream).collect(Collectors.toList()))) {
            throw new NotFoundException("第" + roundId + "的玩家牌" + playerCards + "与上报的牌" + bjRound.getPlayers().stream().flatMap(Collection::stream).collect(Collectors.toList()) + "不一致");
        }
        bjRound.setPlayers(playsCards);
    }

    public Map<BjTypeEnum, Double> odds() throws NotFoundException {
        Blackjack bj = getBlackjack();
        double luckyQueen = bj.expLuckyQueen(1000, 125, 19, 9, 4);
        double hotThree = bj.expHotThree(100, 20, 4, 2, 1);
        double luckyThree = bj.expLuckThree(100, 40, 30, 10, 5);
        double pair = bj.expPair(25, 8);
        double base = bj.expXWin();
        double bloom = bj.expBloom(1, 2, 9, 50, 100, 250);
        Map<BjTypeEnum, Double> result = new HashMap<>();

        result.put(BjTypeEnum.LUCKY_QUEEN, roundToFourDecimalPlaces(luckyQueen));
        result.put(BjTypeEnum.HOT_THREE, roundToFourDecimalPlaces(hotThree));
        result.put(BjTypeEnum.LUCKY_THREE, roundToFourDecimalPlaces( luckyThree));
        result.put(BjTypeEnum.PAIR, roundToFourDecimalPlaces( pair));
        result.put(BjTypeEnum.BASE_PLAY, roundToFourDecimalPlaces( base));
        result.put(BjTypeEnum.BLOOM, roundToFourDecimalPlaces(bloom));
        return result;
    }
    private static double roundToFourDecimalPlaces(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(4, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }
    private Blackjack getBlackjack() throws NotFoundException {
        Blackjack bj = new Blackjack(8);
        ArrayList<BjRound> rounds = new ArrayList<BjRound>(getRoundList());
        for (BjRound round : rounds) {
            bj.removePocker(round.getBanker());
            bj.removePocker(round.getPlayers().stream().flatMap(Collection::stream).collect(Collectors.toList()));
        }
        return bj;
    }

    public Double oneMoreCardWinRate() throws NotFoundException {
        BjRound last = this.getCurrentRound();
        if(last==null||last.isOver())return 0.0;
        List<Pocker> zpk = last.getBanker();
        if (zpk.size() != 1) return 0.0;
        Blackjack bj = getBlackjack();
        Integer zCard = Blackjack.dot(Pocker.fromCard(zpk.get(0).getBlackjackDot()));
        Map<Integer, Double> zRateMap = Blackjack.zRate(bj.getPai(), zCard);
        List<List<Pocker>> players = last.getPlayers();
        List<Integer> xCards = players.get(0).stream().map(Pocker::getBlackjackDot).collect(Collectors.toList());
        Blackjack.Stage xStage = Blackjack.Stage.getXStage(xCards);
        return xStage == null ? 0 : Blackjack.xWinRate(zRateMap, xStage.oneMoreCardRateMap(bj.getPai()));
    }

    public Double getCurrentWinRate() throws NotFoundException {
        BjRound last = this.getCurrentRound();
        if(last==null||last.isOver())return 0.0;
        Blackjack bj = getBlackjack();
        List<Pocker> zpk = last.getBanker();
        if (zpk.size() == 0) return bj.xWinExpectation();
        List<Integer> xCards = last.getPlayers().get(0).stream().map(c -> c.getBlackjackDot()).collect(Collectors.toList());
        int[] dot = Blackjack.dots(xCards);
        int[] zdot = Blackjack.dots(zpk.stream().map(c -> c.getBlackjackDot()).collect(Collectors.toList()));
        Map<Integer, Double> zRateMap = Blackjack.zRate(bj.getPai(), zdot[zdot.length - 1]);
        double currentWinRate = Blackjack.getCurrentWinRate(dot[dot.length - 1], zRateMap);
        return currentWinRate;
    }

    public Boolean shouldSplit() throws NotFoundException {
        BjRound last = this.getCurrentRound();
        if(last==null||last.isOver())return false;
        Blackjack bj = getBlackjack();
        List<Pocker> zpk = last.getBanker();
        if (zpk.size() != 1) return false;
        List<Pocker> player = last.getPlayers().get(0);
        if (player.size() == 2 && player.get(0).getNum() == player.get(1).getNum()) {
            double splitWin0 = bj.expXWin(Arrays.asList(player.get(0).getBlackjackDot()), zpk.get(0).getBlackjackDot());
            if (splitWin0 >= 1.0) {
                // log.info("闲{} 庄 {}  对子拆分后胜率 {}", player.getCards(),dealer.getCards(),splitWin0);
                return true;
            }
        }
        return false;
    }

    public Boolean shouldDouble() throws NotFoundException {
        List<BjRound> rounds = getRoundList();
        if (rounds.isEmpty()) return false;
        BjRound last = rounds.get(rounds.size() - 1);
        return last.getBanker().size()==1 && last.getPlayers().get(0).size() == 2 && oneMoreCardWinRate() >= 0.5;
    }
}

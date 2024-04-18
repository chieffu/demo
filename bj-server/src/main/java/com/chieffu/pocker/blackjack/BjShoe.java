package com.chieffu.pocker.blackjack;

import com.chieffu.pocker.Pocker;
import lombok.Data;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Data
public class
BjShoe {
    Blackjack bj = new Blackjack(8);
    String tableId;
    Map<String, BjRound> roundMap = new ConcurrentHashMap<>();

    public List<BjRound> getRoundList() {
        return roundMap.values().stream().sorted(Comparator.comparing(BjRound::getRoundId)).collect(Collectors.toList());
    }

    public void addRound(BjRound round) {
        roundMap.put(round.getRoundId(), round);
    }

    public BjRound getBjRound(String roundId) {
         BjRound round = roundMap.get(roundId);
        if(round==null){
            round = new BjRound(roundId);
            roundMap.put(roundId, round);
        }
        return round;
    }

    public void updateCards(String roundId, List<Pocker> bankCards, List<List<Pocker>> playsCards) throws NotFoundException {
        BjRound bjRound = getBjRound(roundId);
        if(!bankCards.containsAll(bjRound.getBanker())){
            throw new NotFoundException("第" + roundId + "的庄家牌"+bankCards+"与上报的牌" + bjRound.getBanker() + "不一致");
        }
        bjRound.setBanker(bankCards);
        List<Pocker> playerCards = playsCards.stream().flatMap(Collection::stream).collect(Collectors.toList());
        if(!playerCards.containsAll(bjRound.getPlayers().stream().flatMap(Collection::stream).collect(Collectors.toList()))){
            throw new NotFoundException("第" + roundId + "的玩家牌"+playerCards+"与上报的牌" + bjRound.getPlayers().stream().flatMap(Collection::stream).collect(Collectors.toList()) + "不一致");
        }
        bjRound.setPlayers(playsCards);
    }
}

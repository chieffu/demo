package com.chieffu.pocker.blackjack;

import com.chieffu.pocker.Pocker;
import lombok.Getter;

import java.util.*;

@Getter
public class BjTable {

    private String tableId ;
    private Blackjack blackjack = new Blackjack(8);

    private Map<String,BjRound> rounds = new LinkedHashMap<>();

    public BjTable(String tableId)  {
        this.tableId = tableId;
        this.blackjack = new Blackjack(8);
    }

    /**
     * 获取牌局
     * @param roundId
     * @return
     */
    public BjRound getBjRound(String roundId) {
        BjRound bjRound = rounds.get(roundId);
        if(bjRound==null){
             bjRound = new BjRound(roundId);
            rounds.put(roundId,bjRound);
            return bjRound;
        }
        return bjRound;
    }

    /**
     * 更新牌局
     * @param roundId
     * @param bankCards
     * @param playsCards
     * @throws NotFoundException
     */
    public synchronized void updateCards(String roundId,List<Pocker> bankCards,List<List<Pocker>> playsCards) throws NotFoundException {
        BjRound bjRound = rounds.get(roundId);
        for(int i=0;i<bankCards.size();i++){
            Pocker pocker = bankCards.get(i);
            if(bjRound.getBanker().size()<=i){
                bjRound.getBanker().add(pocker);
                blackjack.removePocker(pocker);
            }else if(!bjRound.getBanker().get(i).equals(pocker)){
                throw new NotFoundException("第"+roundId+"的庄家第"+(i+1)+"张牌"+bjRound.getBanker().get(i)+"已存在，且与上报的牌"+pocker+"不一致");
            }
        }
        for(int i=0;i<playsCards.size();i++){
            List<Pocker> pockerList = playsCards.get(i);
            List<Pocker> player ;
            if(bjRound.getPlayers().size()<=i){
                player = new ArrayList<>();
                bjRound.getPlayers().add(player);
            }else{
                player = bjRound.getPlayers().get(i);
            }
            for(int j=0;j<pockerList.size();j++){
                Pocker playerCard = playsCards.get(i).get(j);
                if(player.size()<=j){
                   player.add(playerCard);
                   blackjack.removePocker(playerCard);
               }else if(!playerCard.equals(player.get(j))){
                    throw new NotFoundException("第"+roundId+"的庄家第"+(i+1)+"位玩家的第"+(j+1)+"张牌"+player.get(j)+"已存在，且与上报的牌"+playerCard+"不一致");
                }
            }
        }

    }
}

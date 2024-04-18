package com.chieffu.pocker.blackjack;

import com.chieffu.pocker.Pocker;
import com.chieffu.pocker.util.ThreadSafeLRUCache;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class BjTable {

    private String tableId ;
    /**
     * 当前牌局
     */
    private BjShoe currentShoe;

    private AtomicInteger currentRoundNum = new AtomicInteger(1);

    private Integer status;// 101 结束下注

    ThreadSafeLRUCache<String, BjShoe> shoes  = new ThreadSafeLRUCache<>(100000);

    public BjTable(String tableId)  {
        this.tableId = tableId;
    }

    public Blackjack getBlackjack() {
        return getCurrentShoe().getBj();
    }
    /**
     * 获取牌局
     * @param roundId
     * @return
     */
    public BjRound getBjRound(String roundId) {
        BjRound bjRound = (shoes.get(roundId)!=null?shoes.get(roundId):getCurrentShoe()).getBjRound(roundId);
        if(bjRound==null){
            bjRound = new BjRound(roundId);
            bjRound.setShoeNum(currentRoundNum.get());
            getCurrentShoe().addRound(bjRound);
            return bjRound;
        }
        return bjRound;
    }

    public BjShoe getCurrentShoe() {
        if(currentShoe==null){
            currentShoe = new BjShoe();
            currentRoundNum.set(1) ;
            currentShoe.setTableId(tableId);
        }
        return currentShoe;
    }

    /**
     * 更新牌局
     * @param roundId
     * @param bankCards
     * @param playsCards
     * @throws NotFoundException
     */
    public synchronized void updateCards(String roundId,List<Pocker> bankCards,List<List<Pocker>> playsCards) throws NotFoundException {
        BjShoe currentShoe = getCurrentShoe();
        BjShoe his = shoes.get(roundId);
        if(his==null ){
            shoes.put(roundId,currentShoe);
        }
        currentShoe.updateCards(roundId, bankCards, playsCards);

    }

    public synchronized void updateStatus(Integer roundNum, String roundId, Integer status) {
        if(roundNum<currentRoundNum.get()||roundNum==1){
            currentShoe=null;
        }
        currentRoundNum.set(roundNum);
        getCurrentShoe().addRound( getBjRound(roundId));

    }
}

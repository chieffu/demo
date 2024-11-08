package com.chieffu.pocker.blackjack;

import com.chieffu.pocker.Pocker;
import com.chieffu.pocker.util.ThreadSafeLRUCache;
import lombok.Data;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class BjTable {

    private final String tableId ;

    private String name;

    private String type;
    /**
     * 当前牌局
     */
    private BjShoe currentShoe ;

    private final AtomicInteger currentRoundNum = new AtomicInteger(1);

    private Integer status;// 101 结束下注

    ThreadSafeLRUCache<Integer, BjShoe> shoes  = new ThreadSafeLRUCache<>(1000);

    public BjTable(String tableId)  {
        this.tableId = tableId;
        this.currentShoe = new BjShoe(tableId);
    }


    /**
     * 获取牌局
     * @param roundId
     * @return
     */
    public BjRound getBjRound(String roundId) {
       return getCurrentShoe().getBjRound(roundId);
    }

    public BjShoe getCurrentShoe() {
        return currentShoe==null?currentShoe=new BjShoe(tableId):currentShoe;
    }

    /**
     * 更新牌局
     * @param roundId
     * @param bankCards
     * @param playsCards
     * @throws NotFoundException
     */
    public synchronized void updateCards(String roundId,List<Pocker> bankCards,List<List<Pocker>> playsCards) throws NotFoundException {
        getCurrentShoe().updateCards(roundId, bankCards, playsCards);

    }

    public synchronized boolean updateStatus(Integer roundNum, String roundId, Integer status) {
        this.status=status;
        if (roundNum>0 && roundNum < currentRoundNum.get() || roundNum == 1) {
            if(currentShoe!=null&&currentShoe.getRoundList().size()>1) {
                currentShoe.setEndTime(new Date());
                shoes.put(shoes.size(), currentShoe);
                currentShoe = new BjShoe(tableId);
            }
        }
        if(status == 109){
            return false;
        }
        if (status == 0) {
            BjRound round = new BjRound(roundId);
            currentRoundNum.set(roundNum);
            round.setShoeNum(roundNum);
            round.setStatus(status);
            this.status = status;
            getCurrentShoe().addRound(round);
            return true;
        }

        BjRound round = getBjRound(roundId);
        currentRoundNum.set(roundNum);
        round.setShoeNum(roundNum);
        Integer oldStatus = round.getStatus();
        round.setStatus(status);
        this.status = status;
        getCurrentShoe().addRound(round);
        return status.equals(oldStatus);
    }
}

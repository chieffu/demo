package com.chieffu.pocker.blackjack;

import com.chieffu.pocker.Pocker;
import com.chieffu.pocker.util.ThreadSafeLRUCache;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BjService {
    ThreadSafeLRUCache<String, BjTable> tables = new ThreadSafeLRUCache<>(10000);

    public BjTable getTable(String tableId) {
        BjTable table=tables.get(tableId);
       return table!=null?table:createTable(tableId);
    }

    private BjTable createTable(String tableId) {
        BjTable table = new BjTable(tableId);
        tables.put(tableId, table);
        return table;
    }

    public BjRound getBjRound(String tableId, String roundId)  {
        BjTable table = tables.get(tableId);
        if(table==null)return null;
        //throw new NotFoundException("桌号"+tableId+"找不到,roundId="+roundId);
        return table.getBjRound(roundId);
    }

    /**
     * 更新牌
     * @param tableId
     * @param roundId
     * @param bankCards
     * @param playsCards
     * @throws NotFoundException
     */
    public void updateCards(String tableId, String roundId, List<Integer> bankCards, List<List<Integer>> playsCards) throws NotFoundException {
        List<Pocker> bankPockerList = bankCards.stream().map(n->Pocker.fromCard(n)).collect(Collectors.toList());
        List<List<Pocker>> playerPockerLists = playsCards.stream().map(l->l.stream().map(n-> Pocker.fromCard(n)).collect(Collectors.toList())).collect(Collectors.toList());
        getTable(tableId).updateCards(roundId, bankPockerList, playerPockerLists);
    }



}

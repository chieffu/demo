package com.chieffu.pocker.blackjack;

import com.chieffu.pocker.Pocker;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BjRound {

    private String tableId;

    private Integer shoeNum;
    private String roundId;

    private Integer status; // 0:开始， 100:开始下注，101:结束， 103:等待发牌， 113:等待决策 hit、miss、split、double，109:等待洗牌

    private List<List<Pocker>> players = new ArrayList<>();

    private List<Pocker> banker = new ArrayList<>();

    public BjRound(String roundId) {
        this.roundId = roundId;
    }

    public String toString(){
        return String.format("%s -- 庄:{}  闲:{}",roundId,banker,players);
    }

}

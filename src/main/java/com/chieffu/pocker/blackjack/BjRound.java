package com.chieffu.pocker.blackjack;

import com.chieffu.pocker.Pocker;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BjRound {

    private String roundId;

    private List<List<Pocker>> players = new ArrayList<>();

    private List<Pocker> banker = new ArrayList<>();

    public BjRound(String roundId) {
        this.roundId = roundId;
    }

    public String toString(){
        return String.format("%s -- 庄:{}  闲:{}",roundId,banker,players);
    }

}

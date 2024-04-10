package com.chieffu.pocker.blackjack;

import lombok.Data;

import java.util.List;

@Data
public class BjRoundDTO {
    String roundId;
    List<Integer> bankCards;
    List<List<Integer>> playsCards;
}

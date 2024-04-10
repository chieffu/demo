package com.chieffu.pocker.blackjack;

import lombok.Data;

import java.util.List;

@Data
public class BjRoundDTO {
    String roundId;
    List<Integer> banker;
    List<List<Integer>> players;
}

package com.chieffu.pocker.blackjack;

import lombok.Data;

import java.util.List;

@Data
public class Play {
    String roundId;
    List<Integer> banker;
    List<Integer> player;
}

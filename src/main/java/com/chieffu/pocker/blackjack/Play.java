package com.chieffu.pocker.blackjack;

import lombok.Data;

import java.util.List;

@Data
public class Play {
    Integer banker;
    List<Integer> player;
}

package com.chieffu.pocker.blackjack;

import lombok.Data;

import java.util.List;

@Data
public class BjRoundVO {

    String roundId;
    String tableId;
    List<String> banker;
    List<List<String>> players;
}

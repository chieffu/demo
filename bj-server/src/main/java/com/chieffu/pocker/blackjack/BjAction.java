package com.chieffu.pocker.blackjack;

import lombok.Data;

import java.util.Map;

@Data
public class BjAction {
    String tableId;
    String roundId;
    String command;
    Map<BjTypeEnum, Double> odds;
    Double winRate;
    Double oneMoreCardWinRate;
    String errorMessage;
}

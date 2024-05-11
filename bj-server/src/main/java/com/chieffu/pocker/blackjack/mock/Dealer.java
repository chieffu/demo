package com.chieffu.pocker.blackjack.mock;

import com.chieffu.pocker.Pocker;

public class Dealer extends Player{

    public Pocker getFirstCard(){
        return cards.get(0);
    }

    public boolean shouldHit() {
        return getHandValue() < 17;
    }

}

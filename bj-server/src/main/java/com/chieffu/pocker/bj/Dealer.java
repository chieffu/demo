package com.chieffu.pocker.bj;

/**
 * Created by scottjones on 11/26/17.
 */
public class Dealer extends Player{


    public int getFirstCardValue(){
        return hand.size()==0?0:hand.get(0).getValue();
    }

    public boolean shouldHit() {
        return getHandValue() < 17;
    }

    public boolean isDown(){
        return getHandValue() >= 17;
    }

}

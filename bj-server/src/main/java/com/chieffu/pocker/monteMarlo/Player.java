package com.chieffu.pocker.monteMarlo;
/**
 * Player class
 */
public class Player extends AbstractPlayer{
    
    /**
     * @param deck type Deck
     */
    public Player(Deck deck){
        super(deck);
        name = "Generic Player";
    }

    /**
     * @return type int
     * temp generic player hand
     */
    @Override
    public int playHand(int DealerFaceUpTotal, int handNum){
        return currentTotal(handNum);
    }
}

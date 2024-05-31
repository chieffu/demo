package com.chieffu.pocker.monteMarlo;

/**
 * Represents a Playing Card
 */
public class Card {
    private Value val;
    private Suit suit;

    /**
     * Card constructor
     * @param suit type Suit
     * @param val type Value
     */
    public Card(Suit suit, Value val){
        this.suit = suit;
        this.val = val;
    }

    /**
     * @return type Value
     */
    public Value getValue(){
        return this.val;
    }

    /**
     * @return type Suit
     */
    public Suit getSuit(){
        return this.suit;
    }

    /**
     * the first 9 are their orders plus 2. Ex. Seven is position 5 in the enum, 5 + 2 = 7
     * if the enum is in position 12 it is an ace (worth 11)...
     * @return type int
     */
    public int getNum() {
        int ordinal = this.val.ordinal();
        if(ordinal > 8 && ordinal < 12) return 10;
        else if(ordinal == 12) return 11;
        else return ordinal + 2;
    }
}

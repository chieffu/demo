package com.chieffu.pocker.monteMarlo;
/**
 * Dealer class
 */
public class Dealer extends AbstractPlayer{

    /**
     * @param deck type Deck
     */
    public Dealer(Deck deck){
        super(deck);
        name = "Dealer";
    }

    
    /**
     * @param handNum type int
     * @return type boolean
     */
    public boolean isSeventeenMin(int handNum){
        return currentTotal(handNum) >= 17;
    }

    /**
     * Gets the dealer to 17 minimum
     * 
     * @return type int
     */
    @Override
    public int playHand(int DealerFaceUpTotal, int handNum){
        System.out.println("-------");
        System.out.println("Dealers's hand:");
        printHand(handNum);
        System.out.println("-------");
        System.out.println("Dealer's current total is " + currentTotal(handNum));
        System.out.println("-------");
        if(isSeventeenMin(handNum)) return currentTotal(handNum);
        while(!isSeventeenMin(handNum)){
            delay();
            if(isBust(handNum)) {
                return currentTotal(handNum);
            }
            else{
                takeCard(handNum);
                printLastCard(handNum);
                System.out.println("-------");
                System.out.println("Dealer's current total is " + currentTotal(handNum));
                System.out.println("-------");
            }
        }
        return currentTotal(handNum);
    }

}

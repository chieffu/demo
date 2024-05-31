package com.chieffu.pocker.monteMarlo;

public class RandomPlayer extends AbstractPlayer{
    private static int botNumber = 0;

    /**
     * @param deck type Deck
     */
    public RandomPlayer(Deck deck){
        super(deck);
        botNumber++;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Random Player #");
        stringBuilder.append(botNumber);
        name = stringBuilder.toString();
    }

    /**
     * Picks a random move
     * 
     * @return type int
     */
    @Override
    public int playHand(int DealerFaceUpTotal, int handNum){
        int move;
        do{
            if(currentTotal(handNum) > 21) return currentTotal(handNum);
            move = (int) Math.round(Math.random());
            if(move == 1){
                System.out.println(getName() + " hits!");
                takeCard(handNum);
                printLastCard(handNum);
                System.out.println("-------");
            }
            else{
                System.out.println(getName() + " is standing with " + currentTotal(handNum) + "!");
                return currentTotal(handNum);
            }


        } while(true);
    }

}

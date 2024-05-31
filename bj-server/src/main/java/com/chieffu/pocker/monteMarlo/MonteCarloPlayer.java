package com.chieffu.pocker.monteMarlo;

/**
 * MonteCarloPlayer class
 */
public class MonteCarloPlayer extends AbstractPlayer{
    private MonteCarlo monteCarlo;
    private static int botNumber = 0;

    /**
     * MonteCarloPlayer Constructor
     * 
     * @param deck type Deck
     * @param num type int
     */
    public MonteCarloPlayer(Deck deck){
        super(deck);
        botNumber++;
        monteCarlo = new MonteCarlo();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Monte Carlo Bot #");
        stringBuilder.append(botNumber);
        name = stringBuilder.toString();
    }

    /**
     * @param currentTotal type int
     * @param dealerFaceUp type int
     * @return type char
     */
    public String getBestMove(int currentTotal, int dealerFaceUp, int handNum, boolean hasAnAce){
        return monteCarlo.bestMove(currentTotal, dealerFaceUp, handNum, hasAnAce);
    }

    /**
     * Generates a table of all the best possible moves
     */
    public void getAllMoves(){
        monteCarlo.generateMonteCarloTableHardTotal();
    }

    /**
     * plays the hand according to the generated best possile move
     * @param DealerFaceUpTotal type int
     * @return type int
     */
    @Override
    public int playHand(int DealerFaceUpTotal, int handNum){
        String c;
        do{
            if(currentTotal(handNum) > 21) return currentTotal(handNum);
            if(isSoftStart(handNum)){
                c = getBestMove(currentTotal(handNum), DealerFaceUpTotal, handNum, true);
            }
            c = getBestMove(currentTotal(handNum), DealerFaceUpTotal, handNum, false);
            if(c == "H"){
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

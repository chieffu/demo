package com.chieffu.pocker.monteMarlo;

// this file is a mess from a lot of testing



/**
 * MonteCarlo class
 */
public class MonteCarlo{
    private final int TESTS = 100_00;    
    

    /**
     * Runs the monte carlo best move method for every possible combination of player total and dealer face up card
     * formats it nicely in 2D array
     */
    public void generateMonteCarloTableHardTotal(){
        String[][] table = new String[11][11];
        int row = 17;
        table[0][0] = " ";
        for(int i = 1; i < table.length; i++){
            table[0][i] = i == 10 ? table[0][i] = "A" : String.valueOf(i + 1);
        }
        for(int j = 1; j < table.length; j++){
            table[j][0] = String.valueOf(row - j + 1);
        }
        for(int i = 1; i < table.length; i++){
            for(int j = 1; j < table[0].length; j++){
                table[i][j] = bestMove(row - i + 1, j + 1, 0, false);
            }
        }
        printMonteCarloTable(table);
        
    }

    /**
     * Generates the monte carlo table for soft totals
     */
    public void generateMonteCarloTableSoftTotal(){
        String[][] table = new String[9][11];
        int row = 20;
        table[0][0] = " ";
        for(int j = 1; j < table[0].length; j++){
            table[0][j] = j == 10 ? table[0][j] = "A" : String.valueOf(j + 1);
        }
        for(int i = 1; i < table.length; i++){
            table[i][0] = "A," + (9 - i + 1);
        }
        for(int i = 1; i < table.length; i++){
            for(int j = 1; j < table[0].length; j++){
                table[i][j] = bestMove(row - i + 1, j + 1, 0, true);
            }
        }
        printMonteCarloTable(table);
        
    }

    /**
     * prints out the formatted 2d array
     * @param table type String[][]
     */
    private void printMonteCarloTable(String[][] table){
        for(int i = 0; i < table.length; i++){
            for(int j = 0; j < table[0].length; j++){
                System.out.printf("%-4s", table[i][j]);
            }
            System.out.println();
        }
    }

    
    /**
     * Find the best move given the players current total and the dealers face up card value
     * 
     * @param playerTotal type int
     * @param dealerFaceUpTotal type int
     * @param handNum type int
     * @param hasAnAce type boolean
     * @return type String
     */
    public String bestMove(int playerTotal, int dealerFaceUpTotal, int handNum, boolean hasAnAce){
        // shuffling is costly
        int count = 0;
        int hitWins = 0;
        int standWins = 0;
        int hitLose = 0;
        int standLose = 0;
        int standTie = 0;
        int hitTie = 0;
        

        int num1;
        int num2;
        Deck trialDeck = new Deck(Main.AMOUNTOFDECKS);
        Player tempPlayer = new Player(trialDeck);
        Dealer tempDealer = new Dealer(trialDeck);
        if(playerTotal > 17) return "S";
        else if(playerTotal < 8) return "H";
        while(count < TESTS){
            if(trialDeck.hasCutCardBeenHit()){
                trialDeck.resetCards();
            }
            
            if(hasAnAce){
                num1 = 11;
                num2 = playerTotal - 11;
            }
            else{
                num1 = playerTotal / 2;
                num2 = playerTotal - num1;
            }
            tempPlayer.predeterminedCard(num1, handNum);
            tempPlayer.predeterminedCard(num2, handNum);
            tempDealer.predeterminedCard(dealerFaceUpTotal, handNum);
            tempDealer.takeCard(handNum);
            
            
            // hitting
            int dealerAmount;
            tempPlayer.takeCard(handNum);
            int current = tempPlayer.currentTotal(handNum);
            
            if(current <= 21){
                dealerAmount = getDealerToMin(tempDealer, handNum);
                if((current >= dealerAmount && current <= 21) || (current <= 21 && dealerAmount > 21)){
                    hitWins++;
                }
                else if((dealerAmount >= current && dealerAmount <= 21) || (dealerAmount <= 21 && current > 21)){
                    hitLose++;
                }
            }
            else{
                hitLose++;
            }
            tempDealer.clearHand(handNum);
            tempPlayer.clearHand(handNum);


            if(trialDeck.hasCutCardBeenHit()){
                trialDeck.resetCards();
            }
            
            if(hasAnAce){
                num1 = 11;
                num2 = playerTotal - 11;
            }
            else{
                num1 = playerTotal / 2;
                num2 = playerTotal - num1;
            }
            tempPlayer.predeterminedCard(num1, handNum);
            tempPlayer.predeterminedCard(num2, handNum);
            tempDealer.predeterminedCard(dealerFaceUpTotal, handNum);
            tempDealer.takeCard(handNum);
            
           

            current = tempPlayer.currentTotal(handNum);
            
            // standing
            dealerAmount = getDealerToMin(tempDealer, handNum);
            if((playerTotal >= dealerAmount && playerTotal <= 21) || (playerTotal <= 21 && dealerAmount > 21)){
                standWins++;
            }
            else if((dealerAmount >= playerTotal && dealerAmount <= 21) || (dealerAmount <= 21 && playerTotal > 21)){
                standLose++;
            }
            
            tempDealer.clearHand(handNum);
            tempPlayer.clearHand(handNum);
            count++;
        }
        String c;
        if(standWins >= hitWins){
            c = "S";
        }
        else{
            c = "H";
        }
        count = 0;
        hitWins = 0;
        standWins = 0;
        hitLose = 0;
        standLose = 0;    
        return c;

    }
    
    /**
     * Gets the dealer to the minimum for testing
     * 
     * @param dealer type Dealer
     * @param handNum type int
     * @return type int
     */
    public int getDealerToMin(Dealer dealer, int handNum){
        if(dealer.isSeventeenMin(handNum)) return dealer.currentTotal(handNum);
        while(!dealer.isSeventeenMin(handNum)){
            dealer.takeCard(handNum);
            if(dealer.isBust(handNum)) {
                return dealer.currentTotal(handNum);
            }
        }
        return dealer.currentTotal(handNum);
    }

}

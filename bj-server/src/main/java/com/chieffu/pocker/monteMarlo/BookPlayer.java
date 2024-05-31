package com.chieffu.pocker.monteMarlo;

public class BookPlayer extends AbstractPlayer{

    // solved states
    private String[][] hardTotals = {
        {"",   "2","3","4","5","6","7","8","9","10","A"},
        {"17", "S","S","S","S","S","S","S","S","S","S"},
        {"16", "S","S","S","S","S","H","H","H","H","H"},
        {"15", "S","S","S","S","S","H","H","H","H","H"},
        {"14", "S","S","S","S","S","H","H","H","H","H"},
        {"13", "S","S","S","S","S","H","H","H","H","H"},
        {"12", "H","H","S","S","S","H","H","H","H","H"},
        {"11", "H","H","H","H","H","H","H","H","H","H"},
        {"10", "H","H","H","H","H","H","H","H","H","H"},
        {"9",  "H","H","H","H","H","H","H","H","H","H"},
        {"8",  "H","H","H","H","H","H","H","H","H","H"}
    };

    private String[][] softTotals = {
        {"",    "2","3","4","5","6","7","8","9","10","A"},
        {"A,9", "S","S","S","S","S","S","S","S","S","S"},
        {"A,8", "S","S","S","S","S","S","S","S","S","S"},
        {"A,7", "S","S","S","S","S","S","S","H","H","H"},
        {"A,6", "H","H","H","H","H","H","H","H","H","H"},
        {"A,5", "H","H","H","H","H","H","H","H","H","H"},
        {"A,4", "H","H","H","H","H","H","H","H","H","H"},
        {"A,3", "H","H","H","H","H","H","H","H","H","H"},
        {"A,2", "H","H","H","H","H","H","H","H","H","H"}
    };

    private String[][] pairSplitting = {
        {"",    "2","3","4","5","6","7","8","9","10","A"},
        {"A,A", "Y","Y","Y","Y","Y","Y","Y","Y","Y","Y"},
        {"T,T", "N","N","N","N","N","N","N","N","N","N"},
        {"9,9", "Y","Y","Y","Y","Y","N","Y","Y","N","N"},
        {"8,8", "Y","Y","Y","Y","Y","Y","Y","Y","Y","Y"},
        {"7,7", "Y","Y","Y","Y","Y","Y","N","N","N","N"},
        {"6,6", "N","Y","Y","Y","Y","N","N","N","N","N"},
        {"5,5", "N","N","N","N","N","N","N","N","N","N"},
        {"4,4", "N","N","N","N","N","N","N","N","N","N"},
        {"3,3", "N","N","Y","Y","Y","Y","N","N","N","N"},
        {"2,2", "N","N","Y","Y","Y","Y","N","N","N","N"}
    };

    private static int botNumber = 0;




    
    /**
     * BookPlayer constructor
     * 
     * @param deck type deck
     */
    public BookPlayer(Deck deck){
        super(deck);
        botNumber++;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Book Player #");
        stringBuilder.append(botNumber);
        name = stringBuilder.toString();
    }

    /**
     * Plays the hand by using the book strategy
     * All possible actions are stored in a 2d array
     * then retrieved based on the current hand
     * 
     * @return type int
     */
    @Override
    public int playHand(int DealerFaceUpTotal, int handNum){
        String c = "";
        do{
            if(handNum > 0) System.out.println(getName() + " Hand " + (handNum+1));
            System.out.println(getName() + "'s total is " + currentTotal(handNum));
            if(currentTotal(handNum) > 21) return currentTotal(handNum);
            if(canSplit(handNum)){
                int row = firstCardTotal(handNum);
                if(pairSplitting[12 - row][DealerFaceUpTotal - 1] == "Y"){
                    System.out.println("Splitting hands!");
                    int addedHand = addHand();
                    addCard(removeLastCard(handNum), addedHand);
                    takeCard(handNum);
                    takeCard(addedHand);
                    continue;
                }
            }
            if(isSoftStart(handNum)){
                
                c = softTotals[21 - currentTotal(handNum)][DealerFaceUpTotal - 1];
            }
            else{
                int row = 17;
                // int temp1 = currentTotal(handNum);
                // int temp2 = row - currentTotal(handNum) + 1;
                if(currentTotal(handNum) > 17) c = "S";
                else if(currentTotal(handNum) < 8) c = "H";
                else{
                    c = hardTotals[row - currentTotal(handNum) + 1][DealerFaceUpTotal - 1];
                }
            }

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

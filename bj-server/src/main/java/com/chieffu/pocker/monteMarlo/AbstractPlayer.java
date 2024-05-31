package com.chieffu.pocker.monteMarlo;
import java.util.ArrayList;

/**
 * Represents an AbstractPlayer
 */
public abstract class AbstractPlayer{
    protected ArrayList<ArrayList<Card>> hands;
    protected int amountOfHands = 0;
    protected String name;
    private Deck deck;
    private int wins = 0;
    private int losses = 0;
    private int ties = 0;

    /**
     * constructor, creates an arraylist of the player hand
     * 
     * @param deck type Deck
     */
    public AbstractPlayer(Deck deck){
        hands = new ArrayList<ArrayList<Card>>(2);
        hands.add(new ArrayList<Card>());
        amountOfHands++;
        this.deck = deck;
    }

    /**
     * @param DealerFaceUpTotal type int
     * @param handNum type int
     * @return type int
     */
    abstract int playHand(int DealerFaceUpTotal, int handNum);

    
    /**
     * Takes the top cards off of the deck and adds it to the player hand
     * 
     * @param handNum type int
     */
    public void takeCard(int handNum){
        hands.get(handNum).add(deck.takeCard());
    }

    /**
     * Amount of cards in hand
     * 
     * @param handNum type int
     * @return type int
     */
    public int handSize(int handNum){
        return hands.get(handNum).size();
    }

    
    /**
     * @param handNum type int
     * @return type boolean
     */
    public boolean isBust(int handNum){
        return currentTotal(handNum) > 21;
    }

    
    /**
     * @param handNum type int
     * @return type boolean
     */
    public boolean isTwentyOne(int handNum){
        return currentTotal(handNum) == 21;
    }

    /**
     * @return type int
     */
    public int addHand(){
        hands.add(new ArrayList<Card>());
        return amountOfHands++;
    }

    /**
     * If a pplayer has more than one hand remove it.
     */
    public void removeAllHandsButOne(){
        for(int i = 1; i < hands.size(); i++){
            hands.remove(i);
        }
        amountOfHands = 1;
    }

    /**
     * @param handNum type int
     * @return type boolean
     */
    public boolean canSplit(int handNum){
        return sameCards(hands.get(handNum));
    }

    /**
     * Check if the player has the two same cards
     * 
     * @param hand type ArrayList<Card>
     * @return type boolean
     */
    public boolean sameCards(ArrayList<Card> hand){
        if(hand.size() == 2){
            if(hand.get(0).getValue().ordinal() == hand.get(1).getValue().ordinal()) return true;
        }

        return false;
    }

    /**
     * @param handNum type int
     * @return type boolean
     */
    public Card removeLastCard(int handNum){
        return hands.get(handNum).remove(hands.get(handNum).size()-1);
    }

    /**
     * @param card type Card
     * @param handNum type int
     */
    public void addCard(Card card, int handNum){
        hands.get(handNum).add(card);
    }

    
    /**
     * @param handNum type int
     */
    public void clearHand(int handNum){
        hands.get(handNum).clear();
    }

    
    /**
     * Gets the players current total
     * 
     * @param handNum type int
     * @return type int
     */
    public int currentTotal(int handNum){
        int currentCount = 0;
        int aceCount = 0;
        for(int i = 0; i < hands.get(handNum).size(); i++){
            currentCount += hands.get(handNum).get(i).getNum();
            if(hands.get(handNum).get(i).getNum() == 11) aceCount++;
        }
        // an ace can be 11 or 1
        // if we go over 21 and have an ace make the ace worth 1
        if(currentCount > 21 && aceCount > 0){
            for(int i = 0; i < aceCount; i++){
                if(currentCount > 21){
                    currentCount -= 10;
                }
            }
        }
        return currentCount;
    }

    
    /**
     * Prints the hand
     * 
     * @param handNum type int
     */
    public void printHand(int handNum){
        for(int i = 0; i < hands.get(handNum).size(); i++){
            delay();
            // if we have an aces print it out nicely instead of printing 11
            if(hands.get(handNum).get(i).getNum() == 11){
                System.out.println("Ace of " + hands.get(handNum).get(i).getSuit());
            }
            else{
                System.out.println(hands.get(handNum).get(i).getNum() + " of " + hands.get(handNum).get(i).getSuit());
            }
        }
    }

    
    /**
     * Prints the hand of the dealer
     * 
     * @param handNum type int
     */
    public void printDealerHand(int handNum){
        System.out.println("Face Down");
        for(int i = 1; i < hands.get(handNum).size(); i++){
            delay();
            if(hands.get(handNum).get(i).getNum() == 11){
                System.out.println("Ace of " + hands.get(handNum).get(i).getSuit());
            }
            else{
                System.out.println(hands.get(handNum).get(i).getNum() + " of " + hands.get(handNum).get(i).getSuit());
            }
        }
    }

    
    /**
     * Prints the last card in the hand
     * @param handNum type int
     */
    public void printLastCard(int handNum){
        try{Thread.sleep(1000);}
        catch (Exception e) {System.out.println(e);}
        if(hands.get(handNum).get(hands.get(handNum).size()-1).getNum() == 11){
            System.out.println("Ace of " + hands.get(handNum).get(hands.get(handNum).size()-1).getSuit());
        }
        else{
            System.out.println(hands.get(handNum).get(hands.get(handNum).size()-1).getNum() + " of " + hands.get(handNum).get(hands.get(handNum).size()-1).getSuit());
        }
    }



    
    /**
     * Gives the dealer a predetermined card
     * 
     * @param value type int
     * @param handNum type int
     */
    public void predeterminedCard(int value, int handNum){
        int num;
        Suit s = Suit.Clubs;
        if(value == 11) num = 12;
        else{
            num = value - 2;
        }
        Value v = Value.values()[num];
        Card c = new Card(s, v);
       
        hands.get(handNum).add(c);
        
    }


    
    /**
     * Returns the value of the first card in the hand
     * 
     * @param handNum type int
     * @return type int
     */
    public int firstCardTotal(int handNum){
        return hands.get(handNum).get(hands.get(handNum).size()-1).getNum();
    }

    /**
     * Checks if there is an ace in the dealt hand
     * @param handNum type int
     * @return type boolean
     */
    public boolean isSoftStart(int handNum){
        int count = 0;
        for(int i = 0; i < hands.get(handNum).size(); i++){
            if(hands.get(handNum).get(i).getNum() == 11){
                count++;
            }
        }
        return count == 1 && hands.get(handNum).size() == 2;
    }

    /**
     * Adds one to losses
     */
    public void addToLosses(){
        losses++;
    }

    /**
     * Adds one to wins
     */
    public void addToWins(){
        wins++;
    }

    /**
     * @return int wins
     */
    public int getWins(){
        return wins;
    }

    /**
     * @return int losses
     */
    public int getLosses(){
        return losses;
    }

    /**
     * @return int ties
     */
    public int getTies(){
        return ties;
    }

    /**
     * adds one to ties
     */
    public void addToTies(){
        ties++;
    }

    /**
     * Delay so that the output is not all at once
     */
    public void delay(){
        Main main = new Main();
        try{Thread.sleep(main.getDelay());}
        catch (Exception e) {System.out.println(e);}
    }

    /**
     * @return String
     */
    public String getName(){
        return name;
    }


}

package com.chieffu.pocker.monteMarlo;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Represents a Deck of Cards
 */
public class Deck{
    private ArrayList<Card> cards;
    private ArrayList<Card> cardsUsed;
    private int cutCard;
    private boolean cutCardHit = false;
    private int amountOfDecks;

    /**
     * deck constructor, loops through the amount of decks passed in
     * adds that amount of decks to the arraylist cards
     * @param amountOfDecks type int
     */
    public Deck(int amountOfDecks){
        this.amountOfDecks = amountOfDecks;
        cardsUsed = new ArrayList<Card>();
        cards = new ArrayList<Card>(Suit.values().length * Value.values().length);
        putCutCard();
        for(int i = 0; i < amountOfDecks; i++){
            addDeck();
        }
        shuffle();
    }

    /**
     * loops through every possible card of both enums
     * then adds them to the arraylist
     */
    private void addDeck(){
        for (Suit s : Suit.values()) {
            for (Value v : Value.values()) {
                Card c = new Card(s, v);
                cards.add(c);
            }
        }
    }

    /**
     * shuffles the arraylist of cards
     */
    public void shuffle() {
        Collections.shuffle(cards); 
    }

    /**
     * returns the card in the cards arraylist in the 0th position
     * also add this cards to the cardsUsed arraylist and remove it from the cards arraylist
     * @return type Card
     */
    public Card takeCard(){
        if(isEmpty()) return null;
        cardsUsed.add(cards.get(0));
        Card top = cards.get(0);
        cards.remove(0);
        if(cardsUsed.size() == cutCard) cutCardHit = true;
        return top;
    }


    /**
     * checks if the deck is empty or out of cards
     * @return type boolean
     */
    public boolean isEmpty(){
        return cards.isEmpty(); 
    }

    /**
     * adds all of the cards that have been previously used back to the cards arraylist
     */
    public void resetCards(){
        cards.addAll(cardsUsed);
        cardsUsed.clear();
        shuffle();
        putCutCard();
    }

    /**
     * checks if the cut card is true or false
     * @return type boolean
     */
    public boolean hasCutCardBeenHit(){
        return cutCardHit;
    }

    /**
     * places the cut card
     */
    public void putCutCard(){
        cutCardHit = false;
        // the 5 * 2 should proabaly be 5 * the amount of players
        int max = ((amountOfDecks*52) - (5 * 2));
        int min = 25 * amountOfDecks;
        cutCard = (int)(Math.random() * (max - min)) + min;
        
    }

    /**
     * prints out the deck for debugging purposes
     */
    public void printDeck(){
        for(int i = 0; i < cards.size(); i++){
            System.out.println(cards.get(i).getValue() + " of " + cards.get(i).getSuit());
        }
        System.out.println("-----------------------");
    }

    /**
     * prints the amount of decks
     * @return type int
     */
    public int amountOfDecks(){
        return amountOfDecks;
    }

    

}
package com.chieffu.pocker.project;

import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Zosit on 11/22/2017.
 * This
 *
 */
 @Getter
public class Shoe {

  private List<Card> shoe;

  private int[] cardCount;

  int cut;

  public Shoe() {
    reset();
  }
  public void reset() {
    List<Card> newShoe = new LinkedList<>();
    int deckSize =8;
    // creates 6 decks and shuffles them together before returning the shoe
    for (int i = 0; i < deckSize; i++) {
      for (Card.Suit s : Card.Suit.values()) {
        for (Card.Rank r : Card.Rank.values()) {
          newShoe.add(new Card(s, r));
        }
      }
    }
    Collections.shuffle(newShoe);
    this.shoe = newShoe;
    cut = (int)(Math.random()*(180-60))+60;
    cardCount = new int[13];
    Arrays.fill(cardCount, 4*deckSize);
  }

  // remove a card from index 0 of the shoe (since it's randomized) and return it
  public Card drawCard() {
    Card card= shoe.remove(0);
    cardCount[card.getRank().getNum()-1]--;
    return card;
  }

  public double highLowCardCounting() {
    return (cardCount[9]+cardCount[10]+cardCount[11]+cardCount[12] + cardCount[0] - cardCount[1] - cardCount[2] - cardCount[3] - cardCount[4] - cardCount[5]) * 52/ (double)shoe.size();
  }

  public double omegaIICardCounting() {
    double big = (cardCount[9]+cardCount[10]+cardCount[11]+cardCount[12])*2;
    double small = (cardCount[3]+cardCount[4]+cardCount[5])*2;
    double middle = (cardCount[1]+cardCount[2]+cardCount[6]);
    double equal = cardCount[8];
    return (big + equal - small - middle) * 52 / (double)shoe.size() ;
  }
  public double myCardCounting(){
    return (cardCount[1]+cardCount[2]+cardCount[3]+cardCount[4]+cardCount[5]+cardCount[0]-cardCount[7]-cardCount[8]-cardCount[9]-cardCount[10]-cardCount[11]-cardCount[12])*52/(double)shoe.size();
  }
  public boolean isOver(){
    return shoe.size()<cut;
  }

  public int getShoeSize() {
    return this.shoe.size();
  }
}

package com.chieffu.pocker.bj;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zosit on 11/22/2017.
 * This class acts as a player in a game of BlackJack (I think the dealer will also be here?).
 * They should be able to use a Shoe class to draw and discard cards from their hand.
 */
@Getter
public class Player {


    List<Card> hand ;
    double bet = 1;
    Player split;

    boolean stand;

    public Player() {
        hand = new ArrayList<>();
    }

    public List<Card> getHand() {
        return hand;
    }

    public int getHandValue() {
        int total = 0;
        for (Card c : hand) {
            total += c.getValue();
        }
        if (hasAce() && total <= 11) {
            total += 10;
        }
        return total;
    }

    public int getHandMinValue() {
        int total = 0;
        for (Card c : hand) {
            total += c.getValue();
        }
        return total;
    }

    public boolean hasAce() {
        for (Card c : hand) {
            if (c.isAce()) {
                return true;
            }
        }
        return false;
    }

    public void reset() {
        hand.clear();
        bet = 1;
        split = null;
        stand = false;
    }

    public void hit(Card card) {
        if(split!=null&&!split.isDown()){
            split.hit(card);
        }else {
            hand.add(card);
        }
    }

    public void doubleDown(Card card) {
        bet *= 2;
        hit(card);
        stand();
    }
    public void bet(double bet){
        this.bet = bet;
    }

    public void stand(){
       if(split!=null&&split.stand==false) {
           split.stand = true;
       }else{
           stand=true;
       }
    }

    public boolean split(){
        if(hand.size()==2 && hand.get(0).getNum()==hand.get(1).getNum()&&split==null){
            split=new Player();
            Card card = hand.remove(1);
            split.hit(card);
            split.bet=bet;
            return true;
        }
        return false;
    }
    public boolean shouldHit() {
        return getHandValue() < 12;
    }

    public boolean isDown(){
//        if(split==null||split.isDown()){
            return getHandValue()>=21||stand;
//        }
//        return false;
    }

    public boolean isBlackjack() {
        return split==null&&hand.size() == 2 &&
                ((hand.get(0).isFace() && hand.get(1).isAce()) ||
                        (hand.get(0).isAce() && hand.get(1).isFace()));
    }

    public boolean isBusted() {
        return getHandValue()>21;
    }
    public boolean isSplit(){
        return split!=null;
    }

    public String toString(){
        if(hand.isEmpty())return hand.toString();
        if(split!=null){
            return String.format("%s-%s,%s",getHandValue(),hand.toString(),split.toString());
        }
        return String.format("%s-%s",getHandValue(),hand.toString());
    }
}

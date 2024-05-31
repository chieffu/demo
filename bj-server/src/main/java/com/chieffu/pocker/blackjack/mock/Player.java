package com.chieffu.pocker.blackjack.mock;

import com.chieffu.pocker.Pocker;
import com.chieffu.pocker.blackjack.Blackjack;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class Player {
    protected List<Pocker> cards = new ArrayList<>();
    List<Player> splits;

    double bet;
    public List<Player> split(){
        Player p0 = new Player();
        p0.hit(cards.get(0));
        Player p1 = new Player();
        p1.hit(cards.get(1));
        splits = new ArrayList<>();
        splits.add(p0);
        splits.add(p1);
        return splits;
    }

    public void bet(double bet){
        this.bet = bet;
    }
    public boolean shouldHit(){
        return getHandValue()<12;
    }
    public int getHandValue(){
       int[] dot= Blackjack.dots( cards.stream().map(p -> Blackjack.dot(p)).collect(Collectors.toList()));
       return dot[dot.length-1];
    }
    public int getHandMinValue(){
        int[] dot= Blackjack.dots( cards.stream().map(p -> Blackjack.dot(p)).collect(Collectors.toList()));
        return dot[0];
    }
    public boolean hasAce(){
        int[] dot= Blackjack.dots( cards.stream().map(p -> Blackjack.dot(p)).collect(Collectors.toList()));
        return dot.length>1;
    }

    public boolean isBlackjack(){
        return Blackjack.isBlackjack(cards.stream().map(p -> Blackjack.dot(p)).collect(Collectors.toList()));
    }
    public void hit(Pocker pocker){
        cards.add(pocker);
    }

    public List<Integer> getCardNums() {
        return getCards().stream().map(p -> Blackjack.dot(p)).collect(Collectors.toList());
    }

    public void reset(){
        cards.clear();
        splits=null;
        bet=0;
    }

    public String toString(){
        if(splits!=null){
            return splits.toString();
        }
        return getCards().toString() + getHandValue();
    }

}

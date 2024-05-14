package com.chieffu.pocker.bj;

import lombok.Getter;

import java.util.Arrays;

@Getter
public class Blackjack {

    //   public static final Action[] BET_ACTIONS = new Action[]{Action.BET_BIG, Action.BET_MEDIUM, Action.BET_SMALL};
//   public static final Action[] STAND_ACTION = {Action.STAND};
//   public static final Action[] FIRST_ROUND_ACTIONS  = new Action[]{Action.STAND, Action.HIT, Action.DOUBLE_DOWN};
    public static final Action[] COMMON_ACTIONS = new Action[]{Action.STAND, Action.HIT};
    Shoe shoe;
    Player player;
    Dealer dealer;
    int win;
    int lose;
    int even;

    public Blackjack() {
        this.shoe = new Shoe();
        this.player = new Player();
        this.dealer = new Dealer();
    }

    public Action[] getAvailableActions() {
//      if(isFirstRound()){
//         return FIRST_ROUND_ACTIONS;
//      }else{
        return COMMON_ACTIONS;
//      }
    }

    private boolean isFirstRound() {
        return player.getHand().size() == 2 && player.getSplit() == null;
    }

    public boolean isNewRound() {
        return player.getHand().isEmpty();
    }

    public void nextRound(double score) {
        if (score > 0) win++;
        if (score < 0) lose++;
        if (score == 0) even++;
        player.reset();
        dealer.reset();
        player.hit(shoe.drawCard());
        player.hit(shoe.drawCard());
        dealer.hit(shoe.drawCard());
        while (player.shouldHit()) {
            player.hit(shoe.drawCard());
        }
    }

    public boolean isOver() {
        return shoe.isOver();
    }

    public void reset() {
        shoe.reset();
        player.reset();
        dealer.reset();
    }

    public double getScore() {
        if (player.isBlackjack()) {
            if (dealer.isBlackjack()) return 0;
            return 1.5 * player.getBet();
        } else if (dealer.isBlackjack()) {
            return -player.getBet();
        }
        return calculateBenefit(player, dealer);
    }

    private double calculateBenefit(Player player, Dealer dealer) {
        double bets = player.getBet();
        if (player.isBusted()) return -bets;
        if (dealer.isBusted() || player.getHandValue() > dealer.getHandValue()) return bets;
        else if (player.getHandValue() < dealer.getHandValue()) return -bets;
        return 0;
    }

    public double takeAction(Action action) {
//      Action[] availableActions = getAvailableActions();
//      if(Arrays.stream(availableActions).allMatch(a->a!=action)){
//         return -1;
//      }
        double score = 0;
        switch (action) {
//         case BET_BIG:
//            player.bet(10);
//            player.hit(shoe.drawCard());
//            player.hit(shoe.drawCard());
//            dealer.hit(shoe.drawCard());
//            break;
//         case BET_MEDIUM:
//            player.bet(1);
//            player.hit(shoe.drawCard());
//            player.hit(shoe.drawCard());
//            dealer.hit(shoe.drawCard());
//            break;
//         case BET_SMALL:
//            player.bet(0.1);
//            player.hit(shoe.drawCard());
//            player.hit(shoe.drawCard());
//            dealer.hit(shoe.drawCard());
//            break;
//         case SPLIT:
//            boolean s = player.split();
//            if(s){
//               player.getSplit().hit(shoe.drawCard());
//               if(player.getSplit().isDown()){
//                  player.getSplit().stand();
//               }
//               player.hit(shoe.drawCard());
//               if(player.isDown()){
//                  while (dealer.shouldHit()) {
//                     dealer.hit(shoe.drawCard());
//                  }
//                  score = getScore();
//                  nextRound();
//               }
//            }
//            break;
//         case DOUBLE_DOWN:
//            player.doubleDown(shoe.drawCard());
//            while (dealer.shouldHit()) {
//               dealer.hit(shoe.drawCard());
//            }
//            score = getScore();
//            nextRound();
//            break;
            case HIT:
                player.hit(shoe.drawCard());
                if (player.isDown()) {
                    while (dealer.shouldHit()) {
                        dealer.hit(shoe.drawCard());
                    }
                    score = getScore();
                    nextRound(score);
                }
                break;
            case STAND:
                player.stand();
                if (player.isDown()) {
                    while (dealer.shouldHit()) {
                        dealer.hit(shoe.drawCard());
                    }
                    score = getScore();
                    nextRound(score);
                }
                break;

        }
        return score;
    }

    public double[] getState() {
        double[] state = new double[4];
        int i = 0;
        state[i++] = dealer.getFirstCardValue();
        int handValue = player.getHandValue();
        state[i++] = handValue;
        state[i++] = (player.getHandMinValue() != handValue) ? 1 : 0;
        int ome = (int) (shoe.myCardCounting());
        state[i++] = ome;
        return state;
    }

    public void removeRandomCards() {
        int n = (int) (Math.random() * 100);
        for (int i = 0; i < n; i++) {
            shoe.drawCard();
        }
    }

    public void start() {
        player.hit(shoe.drawCard());
        player.hit(shoe.drawCard());
        dealer.hit(shoe.drawCard());
        while (player.shouldHit()) {
            player.hit(shoe.drawCard());
        }
    }
}

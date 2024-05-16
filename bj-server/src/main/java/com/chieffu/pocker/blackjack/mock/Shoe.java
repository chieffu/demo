package com.chieffu.pocker.blackjack.mock;

import com.chieffu.pocker.Pocker;
import com.chieffu.pocker.bj.Qlearning;
import com.chieffu.pocker.blackjack.Blackjack;
import com.chieffu.pocker.blackjack.NotFoundException;
import com.chieffu.pocker.util.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Data
public class Shoe extends Blackjack {
    List<Pocker> cards ;
    static Qlearning algorithm = initQlearning();
    static com.chieffu.pocker.project.Qlearning projectAlgorithm = initQlearning2();

    int cut;

    public Shoe(int n) {
        super(n);
        cards = Pocker.randomPocker(n);
        cut = (int)(Math.random()*(180-130))+130;
    }

    private static Qlearning initQlearning() {
        Qlearning algorithm = new Qlearning();
        algorithm.loadQ("q-1.0.0.0");
//        algorithm.loadQ("q-3.0.0");
//        algorithm.loadQ("q-4.0.0");
        algorithm.printQ();
        double[] winningQ = algorithm.test(100000);

        System.out.println("----- Wins -----");
        System.out.printf("%5.2f%%    %5.2f%% (win-loss)%n", winningQ[0]*100, winningQ[0]*100);
        return algorithm;
    }


    private static com.chieffu.pocker.project.Qlearning initQlearning2() {
        com.chieffu.pocker.project.Qlearning algorithm = new com.chieffu.pocker.project.Qlearning();
        algorithm.loadQ("q-1.0.0");
        algorithm.prettyPrintQ(false);
        return algorithm;
    }

    public void cut(){
        this.cut =  StringUtils.newRandomInt(140, 170);
    }

    public Pocker drawCard() throws NotFoundException {
        Pocker remove = cards.remove(cards.size() - 1);
        super.removePocker(remove);
        return remove;
    }

    public double getPlayerCurrentWinRate(Player player,Dealer dealer){
        if(player.getHandValue()>21)return 0;
        Blackjack.Stage xStage = Blackjack.Stage.getXStage(player.getCardNums());
        Blackjack.Stage zStage = Blackjack.Stage.getZStage(Collections.singletonList(Blackjack.dot(dealer.getFirstCard())));
        return xStage.getXCurrentWinRate(zStage,getPai());
    }

    public boolean shouldPlayerHit(Player player,Dealer dealer){
        if(player.getHandValue()>=21)return false;

        Blackjack.Stage xStage = Blackjack.Stage.getXStage(player.getCardNums());
        if(xStage==null){
            return false;
        }
        Blackjack.Stage zStage = Blackjack.Stage.getZStage(Collections.singletonList(Blackjack.dot(dealer.getFirstCard())));
        double currentWinRate = xStage.getXCurrentWinRate(zStage,getPai());
        double oneMoreCardWinRate = xStage.getOneMoreCardWinRate(zStage,getPai());
        if(Game.printLog)log.info("闲{} 庄 {}  当前胜率为{}，加一张胜率为{}  ", player.getCards(),dealer.getCards(),String.format("%.4f",currentWinRate), String.format("%.4f",oneMoreCardWinRate));
        boolean shouldHit = oneMoreCardWinRate>currentWinRate;
        return shouldHit|| shouldHighLowHit(player,dealer);
    }

    private boolean shouldHighLowHit(Player player, Dealer dealer) {

        int state = dealer.getFirstCard().getBlackjackDot();
        state += (player.getHandMinValue() << 5);
        if (player.hasAce()) {
            state += 16;
        }
        double[] q = projectAlgorithm.getQ()[state];
        double hit = q[1];
        double stand = q[0];
        return hit > stand;
    }
    private Boolean shouldMyCountingHit(Player player, Dealer dealer) {
        double[] state = new double[4];
        int i = 0;
        state[i++] = dealer.getFirstCard().getBlackjackDot();
        int handValue = player.getHandValue();
        state[i++] = handValue;
        state[i++] = (player.getHandMinValue() != handValue) ? 1 : 0;
        int percentage = (int) (myCardCounting());
        state[i++] = percentage;
        StringBuffer s = new StringBuffer();
        for (i = 0; i < state.length; i++) {
            if(i!=0)s.append(" ");
            s.append((int)state[i]);
        }
        String name = s.toString();
        double[] q = algorithm.getQMap().get(name);
        double hit = q[0];
        double stand = q[1];
        return hit==stand?null:hit>stand;
    }
    private Boolean shouldStrategyHit(Player player, Dealer dealer) {
        double[] state = new double[3];
        int i = 0;
        state[i++] = dealer.getFirstCard().getBlackjackDot();
        int handValue = player.getHandValue();
        state[i++] = handValue;
        state[i++] = (player.getHandMinValue() != handValue) ? 1 : 0;

        StringBuffer s = new StringBuffer();
        for (i = 0; i < state.length; i++) {
            if(i!=0)s.append(" ");
            s.append((int)state[i]);
        }
        String name = s.toString();
        double[] q = algorithm.getQMap().get(name);
        double hit = q[0];
        double stand = q[1];
        return hit>stand;
    }

    private boolean shouldOmegaHit(Player player, Dealer dealer) {
        int[] state = new int[4];
        int i = 0;
        state[i++] = dealer.getFirstCard().getBlackjackDot();
        int handValue = player.getHandValue();
        state[i++] = handValue;
        state[i++] = (player.getHandMinValue() != handValue) ? 1 : 0;

//      int h_l =  (int)(shoe.highLowCardCounting());
//      if(h_l>7)h_l=7;
//      else if(h_l<-7)h_l=-7;
        int ome = (int)(this.omegaIICardCounting());
        if(ome>15)ome=15;
        else if(ome<-15){
            ome=-15;
        }
        state[i++]=ome;
        StringBuffer s = new StringBuffer();
        for ( i = 0; i < state.length; i++) {
            if(i!=0)s.append(" ");
            s.append(state[i]);
        }
        String name = s.toString();
        double[] q = this.algorithm.getQMap().getOrDefault(name,new double[2]);
        double hit = q[0];
        double stand = q[1];
        boolean result = hit>stand;
        return result;
    }

    public double getPlayerOneMoreCardWinRate(Player player,Dealer dealer){
        Blackjack.Stage xStage = Blackjack.Stage.getXStage(player.getCardNums());
        if(xStage==null)return 0;
        Blackjack.Stage zStage = Blackjack.Stage.getZStage(Collections.singletonList(Blackjack.dot(dealer.getFirstCard())));
        return xStage.getOneMoreCardWinRate(zStage,getPai());
    }

    public boolean ShouldPlayerSplit(Player player,Dealer dealer){
        if(player.getCards().size()==2&&player.getCards().get(0).getNum()==player.getCards().get(1).getNum()){
            double splitWin0 = expXWin(Arrays.asList(player.getCards().get(0).getBlackjackDot()),Blackjack.dot(dealer.getFirstCard()));
            if(splitWin0>1.0){
               // log.info("闲{} 庄 {}  对子拆分后胜率 {}", player.getCards(),dealer.getCards(),splitWin0);
               return true;
            }
        }
        return false;
    }

    public boolean shouldPlayerSurrender(Player player,Dealer dealer){
        if(player.getCards().size()==2){
           double currentWinRate = getPlayerCurrentWinRate(player, dealer);
           double oneMoreCardWinRate = getPlayerOneMoreCardWinRate(player, dealer);
           return Math.max(currentWinRate,oneMoreCardWinRate)<0.25;
        }
       return false;
   }

   public boolean shouldPlayerDouble(Player player,Dealer dealer){
       if(player.getCards().size()==2){
           return getPlayerOneMoreCardWinRate(player,dealer)>0.50;
       }
       return false;
   }


   public double play(Player player,Dealer dealer) throws NotFoundException {
       player.hit(drawCard());
       player.hit(drawCard());
       dealer.hit(drawCard());
        /*if(shouldPlayerSurrender(player,dealer)){
            return -0.5;
        }else */
       if(shouldPlayerDouble(player,dealer)){
            player.hit(drawCard());
            dealerRound(dealer);
            return calculateResult(player, dealer)*2;
        }else if(ShouldPlayerSplit(player,dealer)){
            List<Player> players = player.split();
            for(Player p:players){
                while(p.shouldHit()){
                    p.hit(drawCard());
                }
                while(shouldPlayerHit(p,dealer)){
                    p.hit(drawCard());
                }
            }
            dealerRound(dealer);
            return players.stream().map(p->calculateResult(p,dealer)).reduce((a,b)->a+b).orElse(0.0);
        }else{
            while(player.shouldHit()) {
                player.hit(drawCard());
            }
            while(shouldPlayerHit(player,dealer)){
                player.hit(drawCard());
            }
            dealerRound(dealer);
            if(player.isBlackjack() && !dealer.isBlackjack()){
                return 1.5;
            }else if(dealer.isBlackjack()){
                return -1;
            }
            return calculateResult(player,dealer);
        }
   }

    private double calculateResult(Player player, Dealer dealer) {
        int playerHandValue = player.getHandValue();
        if(playerHandValue >21) return -1;
        int dealerHandValue = dealer.getHandValue();
        if(dealerHandValue >21) return 1;
        if(dealer.isBlackjack())return -1;
        if(playerHandValue > dealerHandValue) return 1;
        if(playerHandValue < dealerHandValue) return -1;
        return 0;
    }

    private void dealerRound(Dealer dealer) throws NotFoundException {
        while(dealer.shouldHit()){
            dealer.hit(drawCard());
        }
    }

    public boolean isOver() {
        return this.cards.size()<=cut;
    }
}

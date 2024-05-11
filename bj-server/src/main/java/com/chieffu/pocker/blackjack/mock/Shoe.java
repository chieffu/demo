package com.chieffu.pocker.blackjack.mock;

import com.chieffu.pocker.Pocker;
import com.chieffu.pocker.blackjack.Blackjack;
import com.chieffu.pocker.blackjack.NotFoundException;
import com.chieffu.pocker.project.MyQlearning;
import com.chieffu.pocker.project.Qlearning;
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
    public Shoe(int n) {
        super(n);
        cards = Pocker.randomPocker(n);

    }

    private static Qlearning initQlearning() {
        Qlearning algorithm = new MyQlearning();

//        int episodes = 1500000;
//        double eta = 0.01;
//        double gamma = 0.01;
//        double epsilonStart = 1.0;
//        double epsilonMin   = 0.1;
//        double epsilonDelta = 0.01;
//        int epsilonEvery = (int)(episodes * epsilonDelta);
//
//        algorithm.train(episodes, eta, gamma, epsilonStart, epsilonMin, epsilonDelta, epsilonEvery);

//        algorithm.loadQ("q-1.4");
        algorithm.loadQ("q-2.9");
        int  n = 0;
        int t = 0;
        for(int i=0;i<algorithm.getQ().length;i++){
            for(int j=0;j<algorithm.getQ()[i].length;j++) {
                if(algorithm.getQ()[i][j]==0){
                    n++;
                }
                t++;
            }
        }
        log.info("{} / {}  {}",n,t,n*100.0/t);
        double[] winningQ = algorithm.test(400000);
        double[] winningR = algorithm.randomTest(10000);

        System.out.println("----- Wins -----");
        System.out.println("Qlearn    Random");
        System.out.printf("%5.2f%%    %5.2f%% (win-loss)%n", winningQ[0]*100, winningR[0]*100);
        System.out.printf("%5.2f%%    %5.2f%% (win-push-loss)%n%n", winningQ[1]*100, winningR[1]*100);

        return algorithm;
    }

    int cut;

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
        int state = Blackjack.dot(dealer.getFirstCard());
        state += (player.getHandMinValue() << 5);
        if (player.hasAce()) {
            state += 16;
        }
        if(algorithm instanceof MyQlearning){
            int count  = (int)this.highLowCardCounting();
            if(count<-7)count=-7;
            if(count>7)count=7;
           state = MyQlearning.encode(dealer.getFirstCard().getBaccaratDot(),player.getHandMinValue(),player.hasAce(),count);
        }
        double stand = this.algorithm.getQ()[state][0];
        double hit = this.algorithm.getQ()[state][1];
        boolean result = hit>stand;
        if(result)return true;
//        //if(player.getHandValue()>21)return false;
        Blackjack.Stage xStage = Blackjack.Stage.getXStage(player.getCardNums());
        if(xStage==null){
            return false;
        }
        Blackjack.Stage zStage = Blackjack.Stage.getZStage(Collections.singletonList(Blackjack.dot(dealer.getFirstCard())));
        double currentWinRate = xStage.getXCurrentWinRate(zStage,getPai());
        double oneMoreCardWinRate = xStage.getOneMoreCardWinRate(zStage,getPai());
        if(Game.printLog)log.info("闲{} 庄 {}  当前胜率为{}，加一张胜率为{}  ", player.getCards(),dealer.getCards(),String.format("%.4f",currentWinRate), String.format("%.4f",oneMoreCardWinRate));
        boolean shouldHit = oneMoreCardWinRate>currentWinRate;
        return shouldHit;
    }
    public double getPlayerOneMoreCardWinRate(Player player,Dealer dealer){
        Blackjack.Stage xStage = Blackjack.Stage.getXStage(player.getCardNums());
        if(xStage==null)return 0;
        Blackjack.Stage zStage = Blackjack.Stage.getZStage(Collections.singletonList(Blackjack.dot(dealer.getFirstCard())));
        return xStage.getOneMoreCardWinRate(zStage,getPai());
    }

    public boolean ShouldPlayerSplit(Player player,Dealer dealer){
        if(player.getCards().get(0).getNum()==player.getCards().get(1).getNum()){
            double splitWin0 = expXWin(Arrays.asList(player.getCards().get(0).getBlackjackDot()),Blackjack.dot(dealer.getFirstCard()));
            if(splitWin0>1.02){
                log.info("闲{} 庄 {}  对子拆分后胜率 {}", player.getCards(),dealer.getCards(),splitWin0);
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
           return getPlayerOneMoreCardWinRate(player,dealer)>0.51;
       }
       return false;
   }


   public double play(Player player,Dealer dealer) throws NotFoundException {
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
        if(dealerHandValue==21&&dealer.getCards().size()==2)return -1;
        if(playerHandValue > dealerHandValue) return 1;
        if(playerHandValue < dealerHandValue) return -1;
        return 0;
    }

    private void dealerRound(Dealer dealer) throws NotFoundException {
        while(dealer.shouldHit()){
            dealer.hit(drawCard());
        }
    }
}

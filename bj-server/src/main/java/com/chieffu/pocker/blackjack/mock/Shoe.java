package com.chieffu.pocker.blackjack.mock;

import com.chieffu.pocker.Ma;
import com.chieffu.pocker.Pocker;
import com.chieffu.pocker.SuitEnum;
import com.chieffu.pocker.bj.Qlearning;
import com.chieffu.pocker.blackjack.Blackjack;
import com.chieffu.pocker.blackjack.NotFoundException;
import com.chieffu.pocker.util.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.Delayed;

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
        cut = 416-180;
    }

    private static Qlearning initQlearning() {
        Qlearning algorithm = new Qlearning();
        algorithm.loadQ("q-1.0.0.0");
//        algorithm.loadQ("q-3.0.0");
//        algorithm.loadQ("q-4.0.0");
        algorithm.printQ();
//        double[] winningQ = algorithm.test(100000);
//        System.out.println("----- Wins -----");
//        System.out.printf("%5.2f%%    %5.2f%% (win-loss)%n", winningQ[0]*100, winningQ[0]*100);
        return algorithm;
    }


    private static com.chieffu.pocker.project.Qlearning initQlearning2() {
        com.chieffu.pocker.project.Qlearning algorithm = new com.chieffu.pocker.project.Qlearning();
        algorithm.loadQ("q-1.0.0");
        adjustQ(algorithm);
//        algorithm.loadQ("q.q");
        algorithm.prettyPrintQ(false);
//        double[] winningQ = algorithm.test(100000);
//        System.out.printf("%5.2f%%    %5.2f%% (win-loss)%n", winningQ[0]*100, winningQ[1]*100);

        return algorithm;
    }

    public void cut(){
        this.cut =  StringUtils.newRandomInt(230, 240);
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
        boolean shouldHit = oneMoreCardWinRate > currentWinRate;
        return shouldHit || shouldHighLowHit(player,dealer);
    }

    private boolean shouldHighLowHit(Player player, Dealer dealer) {
        int state = dealer.getFirstCard().getBlackjackDot();
        int p = player.getHandValue();
        state += (p << 5);
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

    public boolean shouldBet(){
        int[] pai = getPai();
        int winCount=0;
        int lossCount=0;
        int even=0;
        List<Integer> player = new ArrayList<>();
        List<Integer> dealer = new ArrayList<>();
        boolean playerHasAce;
        int[] xDots;
        int[] zDots;
        int cnt = 40000;
        while(cnt-->0){
           player.add(randomCard(pai));
           player.add(randomCard(pai));
           dealer.add(randomCard(pai));
           xDots = Blackjack.dots(player);
           playerHasAce = xDots.length==2;
           while(xDots[xDots.length-1]<12){
               player.add(randomCard(pai));
               xDots = Blackjack.dots(player);
               playerHasAce = xDots.length==2;
           }
           while(xDots[xDots.length-1]<21 && projectAlgorithm.shouldHit(dealer.get(0),xDots[xDots.length-1],playerHasAce)){
               player.add(randomCard(pai));
               xDots = Blackjack.dots(player);
               playerHasAce = xDots.length==2;
           }
           dealer.add(randomCard(pai));
            zDots = Blackjack.dots(dealer);
            while(zDots[zDots.length-1]<17){
                dealer.add(randomCard(pai));
                zDots = Blackjack.dots(dealer);
            }
            if(xDots[xDots.length-1]>21){
                lossCount++;
            }else if(Blackjack.isBlackjack(player)){
                if(Blackjack.isBlackjack(dealer)){
                    even++;
                }else{
                    winCount++;
                }
            }else if(Blackjack.isBlackjack(dealer)){
                lossCount++;
            }else if(zDots[zDots.length-1]>21){
                winCount++;
            }else if(xDots[xDots.length-1]>zDots[zDots.length-1]){
                winCount++;
            }else if(xDots[xDots.length-1]<zDots[zDots.length-1]){
                lossCount++;
            }else{
                even++;
            }
            for(int i=player.size()-1;i>=0;i--){
               pai[player.remove(i)]++;
            }
            for(int i=dealer.size()-1;i>=0;i--){
                pai[dealer.remove(i)]++;
            }
        }
        double rate =  winCount/(double)(winCount+lossCount);
        return rate > 0.473;
    }
    public Integer randomCard( int[] pai){
        int countPai = countPai(pai);
        int random = StringUtils.newRandomInt(0, countPai);
        for(int i=1;i<pai.length;i++){
            if(random>=pai[i]){
                random-=pai[i];
            }else{
                pai[i]--;
                return i;
            }
        }
        return null;
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
            if(splitWin0>=1.0){
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
           return getPlayerOneMoreCardWinRate(player,dealer)>=0.5;
//           Blackjack.Stage xStage = Blackjack.Stage.getXStage(player.getCardNums());
//           int zcard = Blackjack.dot(dealer.getFirstCard());
//           Blackjack.Stage zStage = Blackjack.Stage.getZStage(Collections.singletonList(zcard));
//           Map<Integer, Double> zRates = zRate(getPai(), zcard);
//           double currentRate = xStage.getXCurrentWinRate(zStage,getPai());
//           Map<Integer, Double> xRates1 = xStage.oneMoreCardRateMap(getPai());
//           double oneMoreCardWinRate = xWinRate( zRates, xRates1);
//           if(currentRate<oneMoreCardWinRate && oneMoreCardWinRate>0.5){
//               return true;
//           }
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
                if(shouldPlayerHit(p,dealer)){
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
//
//    public static void main(String[] args)throws Exception {
//        long start = System.currentTimeMillis();
//        adjustQ1(projectAlgorithm);
//        projectAlgorithm.prettyPrintQ(false);
////        projectAlgorithm.saveQ("q.q");
//    }

    public double luckyQueenResult(Dealer dealer,Player player,double luckyQueenWithBjOdds, double luckyQueenOdds, double purePairOdds, double OddsPure20, double odds20){
        List<Pocker> px = player.getSplits()==null?player.getCards():Arrays.asList(player.getSplits().get(0).getCards().get(0),player.getSplits().get(1).getCards().get(0));
        int[] xx = Blackjack.dots(px.get(0),px.get(1));
        double r=-1;
        if(xx[xx.length - 1] == 20) {
            if (px.get(0).equals(px.get(1))) {
                Pocker pocker = px.get(0);
                if (pocker.getSuit().equals(SuitEnum.HEART) && pocker.getNum() == 12) {
                    if (dealer.isBlackjack()) {
                        r = luckyQueenWithBjOdds;
                    } else {
                        r = luckyQueenOdds;
                    }
                } else {
                    r = purePairOdds;
                }
            } else {
                if (px.get(0).getSuit() == px.get(1).getSuit()) {
                    r = OddsPure20;
                } else {
                    r = odds20;
                }
            }
        }
        return r;
    }
    public double luckyThreeResult(Player player,Dealer dealer,double pureThreeOdds, double straightFlushOdds, double threeOdds, double straightOdds, double flushOdds){
        List<Pocker> cards = new ArrayList<>();
        cards.add(dealer.getFirstCard());
        if(player.getSplits()==null) {
            cards.add(player.getCards().get(0));
            cards.add(player.getCards().get(1));
        }else{
            cards.add(player.getSplits().get(0).getCards().get(0));
            cards.add(player.getSplits().get(1).getCards().get(0));
        }
        double r=-1;
        if (Ma.isOneOfKind(cards)) {
            if (Ma.isFlush(cards))
                r=pureThreeOdds;
            else
                r=threeOdds;
        } else if (Ma.isFlush(cards)) {
            if (Ma.isStraight(cards)) {
                r=straightFlushOdds;
            } else {
                r=flushOdds;
            }
        } else if (Ma.isStraight(cards)) {
            r=straightOdds;
        } else {
            r=-1;
        }
        return r;
    }

    public double pairResult(Player player, Dealer dealer, double purePairOdds, double sameColorPairOdds,double diffColorPairOdds) {
        Pocker p1,p2;
        if(player.getSplits()==null) {
            p1 = player.getCards().get(0);
            p2 = player.getCards().get(1);
        }else{
            p1 = player.getSplits().get(0).getCards().get(0);
            p2 = player.getSplits().get(1).getCards().get(0);
        }
        if (p1.getNum() == p2.getNum()) {
            if (p1.getSuit() == p2.getSuit()) {
                return purePairOdds;
            } else if(p1.getSuit()==SuitEnum.CLUB && p2.getSuit()==SuitEnum.HEART||
                    p2.getSuit()==SuitEnum.CLUB && p1.getSuit()==SuitEnum.HEART||
                    p1.getSuit()==SuitEnum.SPADE && p2.getSuit()==SuitEnum.DIAMOND||
                    p2.getSuit()==SuitEnum.SPADE && p1.getSuit()==SuitEnum.DIAMOND){
                return sameColorPairOdds;
            }else{
                return diffColorPairOdds;
            }
        } else {
            return -1;
        }
    }
    private static void adjustQ(com.chieffu.pocker.project.Qlearning projectAlgorithm) {
        int state = 8;
        state += (16 << 5);
        state += 16;

        double[] q = projectAlgorithm.getQ()[state];
        double t = q[0];
        q[0]=q[1];
        q[1]=t;


        state = 8;
        state += (15 << 5);
        q = projectAlgorithm.getQ()[state];
        t = q[0];
        q[0]=q[1];
        q[1]=t;

        state = 8;
        state += (15 << 5);
        state += 16;
        q = projectAlgorithm.getQ()[state];
        t = q[0];
        q[0]=q[1];
        q[1]=t;

        state = 7;
        state += (15 << 5);
        q = projectAlgorithm.getQ()[state];
        t = q[0];
        q[0]=q[1];
        q[1]=t;


        state = 8;
        state += (14 << 5);
        q = projectAlgorithm.getQ()[state];
        t = q[0];
        q[0]=q[1];
        q[1]=t;

        state = 9;
        state += (14 << 5);
        q = projectAlgorithm.getQ()[state];
        t = q[0];
        q[0]=q[1];
        q[1]=t;

        state = 2;
        state += (13 << 5);
        q = projectAlgorithm.getQ()[state];
        t = q[0];
        q[0]=q[1];
        q[1]=t;

        state = 5;
        state += (12 << 5);
        state += 16;
        q = projectAlgorithm.getQ()[state];
        t = q[0];
        q[0]=q[1];
        q[1]=t;


        state = 10;
        state += (8 << 5);
        state += 16;
        q = projectAlgorithm.getQ()[state];
        t = q[0];
        q[0]=q[1];
        q[1]=t;

        state = 9;
        state += (8 << 5);
        state += 16;
        q = projectAlgorithm.getQ()[state];
        t = q[0];
        q[0]=q[1];
        q[1]=t;

        state = 2;
        state += (8 << 5);
        state += 16;
        q = projectAlgorithm.getQ()[state];
        t = q[0];
        q[0]=q[1];
        q[1]=t;

        state = 1;
        state += (8 << 5);
        state += 16;
        q = projectAlgorithm.getQ()[state];
        t = q[0];
        q[0]=q[1];

        state = 10;
        state += (9 << 5);
        state += 16;
        q = projectAlgorithm.getQ()[state];
        t = q[0];
        q[0]=q[1];
        q[1]=t;
    }
    private static void adjustQ1(com.chieffu.pocker.project.Qlearning projectAlgorithm) {
        int state;
        double[] q;
        double t ;

        state = 10;
        state += (16 << 5);
        q = projectAlgorithm.getQ()[state];
        t = q[0];
        q[0]=q[1];
        q[1]=t;

        state = 10;
        state += (16 << 5);
        state +=16;
        q = projectAlgorithm.getQ()[state];
        t = q[0];
        q[0]=q[1];
        q[1]=t;

    }

}

package com.chieffu.pocker.blackjack.mock;

import com.chieffu.pocker.blackjack.MockContext;
import com.chieffu.pocker.blackjack.NotFoundException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class Game {
    public static boolean printLog = true;

    public static void main(String[] args) throws NotFoundException {
        Game game = new Game();
        game.printLog=false;
        game.mockRound(1000);

    }
    public MockContext mockRound(int n) throws NotFoundException {
        MockContext c0 = new MockContext("total");
        for (int i = 1; i <= n; i++) {
            MockContext c = mock(i);
            if(c.getCount()>0){
                c0.merge(c);
                log.info("{} - total---次数 = {} -----max={} ----- min={}----结果 = {}  win:{}  lose:{} even:{}   win/all:{}  win/win+lose:{}",i, String.format("%.3f",c0.getCount()), String.format("%.3f",c0.getMaxWin()), String.format("%.3f",c0.getMinWin()), String.format("%.3f",c0.getResult()),c0.getWin(),c0.getLose(),c0.getEven(),String.format("%.3f",c0.getWin()/(c0.getEven()+c0.getWin()+c0.getLose())),String.format("%.3f",c0.getWin()/(c0.getWin()+c0.getLose())));
            }
        }
        return c0;
    }
    public MockContext mock(int shift) throws NotFoundException {
        MockContext commonContext = new MockContext("底注");
        Shoe shoe = new Shoe(8);
        Player player = new Player();
        Dealer dealer = new Dealer();
        shoe.cut();
        int round=0;
        while(!shoe.isOver()){
            round++;
            player.reset();
            dealer.reset();

            double highLowCardCounting = shoe.highLowCardCounting();
            double omegaIICardCounting = shoe.omegaIICardCounting();
//            double mycount = shoe.myCardCounting();
//            double zBloom = 1-shoe.rZNotBloom(0);
//            double x = shoe.xWinExpectation();
//            double doubleRate = shoe.rDoubleRate();
//            double luckyQueen = shoe.expLuckyQueen(1000, 125, 19, 9, 4);
//            double luckyThree = shoe.expLuckThree(100, 40, 30, 10, 5);
//            double pair = shoe.expPair(25, 12,6);
            double bet = /*highLowCardCounting>4&&highLowCardCounting>7?2:*/1;
            if(highLowCardCounting>2 && shoe.shouldBet()) {

                player.bet(bet);
                double result = shoe.play(player,dealer);
//            if(luckyQueen>1.05) {
//                commonContext.addCount(player.getBet());
//                commonContext.addResult(result);
//                commonContext.addCount(bet*8);
//                commonContext.addResult(shoe.luckyQueenResult(dealer, player,1000, 125, 19, 9, 4) * bet*8);
//            }

//                commonContext.addCount(player.getBet());
                commonContext.addResult(result);
                commonContext.addCount(bet);
//                commonContext.addResult(shoe.pairResult(player, dealer,25,12,6) * bet);
            }else{
                shoe.drawCard();
                shoe.drawCard();
                shoe.drawCard();
                shoe.drawCard();
                double random = Math.random();
                if(random >0.2)shoe.drawCard();
                if(random >0.7)shoe.drawCard();
                if(random >0.9)shoe.drawCard();
            }
        }

        return commonContext;
    }
}

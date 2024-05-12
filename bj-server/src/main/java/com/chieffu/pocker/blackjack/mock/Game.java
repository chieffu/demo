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
        game.mockRound(2000);

    }
    public MockContext mockRound(int n) throws NotFoundException {
        MockContext c0 = new MockContext("total");
        for (int i = 1; i <= n; i++) {
            MockContext c = mock(i);
            if(c.getCount()>0) {
                log.info("第{}靴---次数 = {} -----max={} ----- min={}----结果 = {}", i, String.format("%.3f",c.getCount()), String.format("%.3f",c.getMaxWin()),  String.format("%.3f",c.getMinWin()), String.format("%.3f", c.getResult()));
                c0.merge(c);
                log.info("total---次数 = {} -----max={} ----- min={}----结果 = {}  win:{}  lose:{} even:{}   win/all:{}  win/win+lose:{}", String.format("%.3f",c0.getCount()), String.format("%.3f",c0.getMaxWin()), String.format("%.3f",c0.getMinWin()), String.format("%.3f",c0.getResult()),c0.getWin(),c0.getLose(),c0.getEven(),String.format("%.3f",c0.getWin()/(c0.getEven()+c0.getWin()+c0.getLose())),String.format("%.3f",c0.getWin()/(c0.getWin()+c0.getLose())));
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
        while(shoe.getCards().size()>shoe.getCut()){
            round++;
            player.reset();
            dealer.reset();
            player.hit(shoe.drawCard());
            player.hit(shoe.drawCard());
            dealer.hit(shoe.drawCard());
            double highLowCardCounting = shoe.highLowCardCounting();
            double omegaIICardCounting = shoe.myCardCounting();
            if(highLowCardCounting<-3){
                double result = shoe.play(player,dealer);
                commonContext.addCount();
                commonContext.addResult(result);
                if(printLog) log.info("{}靴{}把压{} 真数：{}  结果 {}  当前 max:{}  min:{}  result:{}  闲：{} - 庄：{} ", shift, round, commonContext.getName(), String.format("%.3f",highLowCardCounting), result,commonContext.getMaxWin(),commonContext.getMinWin(),commonContext.getResult(), player,dealer);
            }else{
                dealer.hit(shoe.drawCard());
            }
        }

        return commonContext;
    }
}

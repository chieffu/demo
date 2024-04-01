import com.chieffu.pocker.Pocker;
import com.chieffu.pocker.SuitEnum;
import com.chieffu.pocker.blackjack.Blackjack;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Slf4j
public class MockBlackjack {

    private static double[] mock(int shift, Blackjack bj) {
        List<Pocker> pks = bj.getDecks();
        Blackjack blackjack = new Blackjack(pks.size() / 52);
        int round = 0;
        List<Pocker> pz = new ArrayList<>();
        List<Pocker> px = new ArrayList<>();
        double count = 0;
        double result = 0;
        double maxWin = 0.0;
        double minWin = 0.0;
        while (pks.size() > 100) {
            round++;

            px.add(pks.remove(pks.size() - 1));
            px.add(pks.remove(pks.size() - 1));
            pz.add(pks.remove(pks.size() - 1));
            pz.add(pks.remove(pks.size() - 1));
            double luckyQueue = blackjack.luckyQueenExpectation(1000, 125, 19, 9, 4);
            if (luckyQueue > 1.05) {
                count += 1;
                if (px.get(0).equals(px.get(1))) {
                    Pocker pocker = px.get(0);
                    if (pocker.getSuit().equals(SuitEnum.HEART) && pocker.getNum() == 12) {
                        int[] zz = Blackjack.dots(pz);
                        if (zz[zz.length - 1] == 21) {
                            result += 1000;
                        } else {
                            result += 125;
                        }
                    } else {
                        result += 19;
                    }
                } else {
                    int[] xx = Blackjack.dots(px);
                    if (xx[xx.length - 1] == 20) {
                        if (px.get(0).getSuit() == px.get(1).getSuit()) {
                            result += 9;
                        } else {
                            result += 4;
                        }
                    } else {
                        result -= 1;
                    }
                }
                if (maxWin < result) {
                    maxWin = result;
                }
                if (minWin > result) {
                    minWin = result;
                }
                //log.info("{}靴{}把 压幸运女皇 期望：{}  结果：{}", shift, round, luckyQueue, result);
            }
//            double pair = blackjack.pairExpectation(25, 8);
//            if (pair > 1) {
//                count += 1;
//                if(px.get(0).equals(px.get(1))) {
//                    result += 25;
//                }else if(px.get(0).getNum() == px.get(1).getNum()){
//                    result +=8;
//                }else{
//                    result-=1;
//                }
//                if (maxWin < result) {
//                    maxWin = result;
//                }
//                if (minWin > result) {
//                    minWin = result;
//                }
//                log.info("{}靴{}把 压对子 期望：{}  结果：{}", shift, round, pair, result);
//            }
            double luckyThree = blackjack.luckThreeExpectation(100, 40, 30, 10, 5);
            if(luckyThree>1.05){
                List<Pocker> cards = Arrays.asList(px.get(0),px.get(1),pz.get(0));
                if(isOneOfKind(cards)){
                    if(isFlush(cards))
                        result+=100;
                    else
                        result+=30;
                }else if(isFlush(cards)) {
                    if(isStraight(cards)){
                        result+=40;
                    }else {
                        result += 5;
                    }
                }else if(isStraight(cards)){
                    result+=10;
                }else{
                    result-=1;
                }
                count+=1;
                if (maxWin < result) {
                    maxWin = result;
                }
                if (minWin > result) {
                    minWin = result;
                }
                log.info("{}靴{}把 压幸运三 期望：{}  结果：{}", shift, round, luckyThree, result);

            }
//            double hotThree = blackjack.hotThreeExpectation(100, 20, 4, 2, 1);

            blackjack.removePocker(px);
            blackjack.removePocker(pz);
            px.clear();
            pz.clear();
        }


        log.info("-第{}靴-----次数 = {} -------max={} ----- min={}----结果 = {}", shift, count, maxWin, minWin, result);
        return new double[]{count, result};

    }

    private static boolean isOneOfKind(List<Pocker> pockers){
        pockers.sort(Comparator.comparing(Pocker::getNum));
        boolean oneOfKind = true;
        for(int i=1;i<pockers.size();i++){
            if(pockers.get(i).getNum()!=pockers.get(i-1).getNum()){
                oneOfKind = false;
                break;
            }
        }
        return oneOfKind;
    }
    private static boolean isFlush(List<Pocker> pockers){
        pockers.sort(Comparator.comparing(Pocker::getSuit));
        boolean flush = true;
        for(int i=1;i<pockers.size();i++){
            if(pockers.get(i).getSuit()!=pockers.get(i-1).getSuit()){
                flush = false;
                break;
            }
        }
        return flush;
    }
    private static boolean isStraight(List<Pocker> pockers) {
        pockers.sort(Comparator.comparing(Pocker::getNum));
        boolean straight = true;
        for(int i=1;i<pockers.size();i++){
            if(i==1&&pockers.get(0).getNum()==1 && pockers.get(pockers.size()-1).getNum()==13)continue;
            if(pockers.get(i).getNum()!=pockers.get(i-1).getNum()+1){
                straight = false;
                break;
            }
        }
        return straight;
    }

    public static void main(String[] args) {
        double[] cr = new double[2];
        double maxWin=0;
        double minWin=0;
        for (int i = 1; i <= 1000; i++) {
            Blackjack bj = new Blackjack(8);
            double[] cri = mock(i, bj);
            for (int j = 0; j < cri.length; j++) {
                cr[j] += cri[j];
            }
            if(cr[1]>maxWin) maxWin=cr[1];
            if(cr[1]<minWin) minWin=cr[1];
        }
        log.info("-次数 = {} -------max={} ----- min={}----结果 = {}", cr[0],maxWin,minWin,cr[1]);

    }

}

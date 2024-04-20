package com.chieffu.pocker.blackjack;

import com.chieffu.pocker.Ma;
import com.chieffu.pocker.Pocker;
import com.chieffu.pocker.SuitEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class IndiaSofa extends Blackjack{

    public IndiaSofa(int n) {
        super(n);
    }

    public static void main(String[] args) {

        System.out.println("Hello India");
        double result = yindu6he();
        System.out.println(result);
        System.out.println(duizi());
        mock(10);
    }
    private static MockContext mock( int n)  {
        MockContext mockContext = new MockContext("印度炸金花六合彩");
        for(int i=0;i<n;i++) {
            List<Pocker> pks = Pocker.randomPocker(1);
            List<Pocker> pockers = new ArrayList<>();
            for (int j = 0; j < 6; j++) {
                pockers.add(pks.get(j));
            }
            pockers.sort((o1, o2) -> o1.getNum() == o2.getNum()?o1.getSuit().compareTo(o2.getSuit()):o1.getNum()-o2.getNum());
            Map<Integer,Integer> numbs = Ma.groupOfPockerNum(pockers);
            boolean isFourOfKind = numbs.values().stream().max(Integer::compareTo).orElse(0)==4;
            boolean isThreeOfKind = numbs.values().stream().max(Integer::compareTo).orElse(0)==3;
            boolean isFullHouse = isThreeOfKind && numbs.keySet().size()<=3;
            Map<SuitEnum,Integer> suites = Ma.groupOfSuit(pockers);
            boolean isFlush = suites.values().stream().max(Integer::compareTo).orElse(0)==5;
            boolean isStrait = numbs.size()==6 && (Ma.isStraight(pockers.subList(0,5))|| Ma.isStraight(pockers.subList(1,6)));
            if(numbs.size()==5) {
                List<Pocker> list  = new ArrayList<>();
                Pocker pair = pockers.stream().filter(p -> numbs.get(p.getNum()) == 2).findFirst().orElse(null);
                for (Pocker p : pockers) {
                    if(p!=pair){
                        list.add(p);
                    }
                }
                isStrait = Ma.isStraight(list);
            }

            double result = -1;
            if(isFlush && isStrait ){
                SuitEnum suite = suites.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey).orElse(null);
                boolean isKingStrait = pockers.stream().filter(p->p.getSuit()==suite).map(en->en.getNum()).collect(Collectors.toSet()).containsAll(Arrays.asList(10,11,12,13,1));
                if(isKingStrait){
                   result += 1001;
                }else{
                    result += 201;
                }
            }else if(isFourOfKind) {
                result += 101;
            }else if(isFullHouse) {
                result += 21;
            }else if(isFlush) {
                result += 16;
            }else if(isStrait) {
                result+=11;
            }else if(isThreeOfKind){
                result+=8;
            }
            mockContext.addCount();
            mockContext.addResult(result);

            log.info("第{}次模拟结果:{}  r:{}   cont:{} min:{}  max:{}  result:{}",i,pockers,result,mockContext.getCount(),mockContext.getMinWin(),mockContext.getMaxWin(),mockContext.getResult() );
        }
       return mockContext;
    }
    private static double yindu6he() {
        double all = c(52,6);
        double kFlush = 4*c(52-5,1)/all ;
        double flushStraight = 10*4*c(52-5,1)/all ;
        double fourOfAKind = 13*c(52-4,2)/all ;
        double fourOfPair = 13*c(12,1)*c(4,2)/all ;
        double fullHouse = 13*c(4,3)*c(12,1)*c(4,2)*c(52-5,1)/all ;
        double flush = 4*c(13,5)*c(52-5,1)/all ;
        double straight = 10*4*4*4*4*4*c(52-5,1)/all ;
        double threeOfAKind = 13*c(4,3)*c(12,3)*4*4*4/all ;
        double result = kFlush* 1001 + (flushStraight-kFlush) *  201 + fourOfAKind * 101 + (fullHouse-fourOfPair) * 21 + (flush-flushStraight) * 16 + (straight-flushStraight) * 11 + (threeOfAKind-flush) * 8;

        return result;
    }

    private static double duizi() {
        double all = c(52,3);

        double threeA = c(4,3)/all ;
        double threeOfKind = 13*c(4,3)/all ;
        double flushStraight = 12*4/all ;
        double straight = 12*4*4*4/all;
        double flush = 4*c(13,3)/all ;
        double pair = c(13,1)*c(4,2)*c(50,1)/all;

        double result = threeA*51 + (threeOfKind-threeA) *  41 + flushStraight * 31  + (straight-flushStraight) * 7 + (flush-flushStraight) * 4 + pair * 2;
        return result;
    }
}

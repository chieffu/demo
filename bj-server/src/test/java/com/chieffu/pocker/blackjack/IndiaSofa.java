
package com.chieffu.pocker.blackjack;

import com.chieffu.pocker.Ma;
import com.chieffu.pocker.Pocker;
import com.chieffu.pocker.SuitEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class IndiaSofa extends Blackjack {

    public IndiaSofa(int n) {
        super(n);
    }

    public static double rB(int n, int m) {
        double r = 0.0;
        for (int i = n; i <= m; i++) {
            r += f(i);
        }
        return r;
    }

    public static double f(int k) {
        return c(3, 1) * c(48, k - 1) / (double) c(50, k - 1) / 51;
    }

    private static MockContext mock7(int n) {
        MockContext mockContext = new MockContext("边注城市7张");
        for (int i = 0; i < n; i++) {
            List<Pocker> pks = Pocker.randomPocker(1);
            List<Pocker> pockers = new ArrayList<>();
            for (int j = 0; j < 7; j++) {
                pockers.add(pks.get(j));
            }
            pockers.sort((o1, o2) -> o1.getNum() == o2.getNum() ? o1.getSuit().compareTo(o2.getSuit()) : o1.getNum() - o2.getNum());
            Map<Integer, Integer> numbs = Ma.groupOfPockerNum(pockers);
            boolean isFourOfKind = numbs.values().stream().max(Integer::compareTo).orElse(0) == 4;
            boolean isThreeOfKind = numbs.values().stream().max(Integer::compareTo).orElse(0) == 3;
            boolean isFullHouse = isThreeOfKind && numbs.keySet().size() <= 4;
            Map<SuitEnum, Integer> suites = Ma.groupOfSuit(pockers);
            boolean isFlush = suites.values().stream().max(Integer::compareTo).orElse(0) == 5;
            List<Pocker> temp = new ArrayList<>();
            for (Pocker p : pockers) {
                if (temp.isEmpty() || temp.get(temp.size() - 1).getNum() != p.getNum()) {
                    temp.add(p);
                }
            }
            boolean isStrait = temp.size() == 5 && Ma.isStraight(temp)
                    || temp.size() == 6 && (Ma.isStraight(temp.subList(0, 5)) || Ma.isStraight(temp.subList(1, 6))
                    || temp.size() == 7 && (Ma.isStraight(temp.subList(0, 5)) || Ma.isStraight(temp.subList(1, 6)) || Ma.isStraight(temp.subList(2, 7))));

            double result = -1;
            if (isFlush && isStrait) {
                SuitEnum suite = suites.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey).orElse(null);
                List<Pocker> sameSuit = pockers.stream().filter(p -> p.getSuit() == suite).sorted(Comparator.comparingInt(Pocker::getNum)).collect(Collectors.toList());

                boolean isFlushStrait = sameSuit.size() == 5 && Ma.isStraight(sameSuit)
                        || sameSuit.size() == 6 && (Ma.isStraight(sameSuit.subList(0, 5)) || Ma.isStraight(sameSuit.subList(1, 6))
                        || sameSuit.size() == 7 && (Ma.isStraight(sameSuit.subList(0, 5)) || Ma.isStraight(sameSuit.subList(1, 6)) || Ma.isStraight(sameSuit.subList(2, 7))));

                boolean isKingStrait = sameSuit.stream().map(en -> en.getNum()).collect(Collectors.toSet()).containsAll(Arrays.asList(10, 11, 12, 13, 1));
                if (isKingStrait) {
                    result += 501;
                } else if (isFlushStrait) {
                    result += 101;
                }
            } else if (isFourOfKind) {
                result += 51;
            } else if (isFullHouse) {
                result += 8;
            } else if (isFlush) {
                result += 6;
            } else if (isStrait) {
                result += 5;
            } else if (isThreeOfKind) {
                result += 4;
            }
            mockContext.addCount();
            mockContext.addResult(result);

            log.info("第{}次模拟结果:{}  r:{}   cont:{} min:{}  max:{}  result:{}", i, pockers, result, mockContext.getCount(), mockContext.getMinWin(), mockContext.getMaxWin(), mockContext.getResult());
        }
        return mockContext;
    }

    private static MockContext mock3(int n) {
        MockContext mockContext = new MockContext("炸金花3张");
        for (int i = 0; i < n; i++) {
            List<Pocker> pks = Pocker.randomPocker(1);
            List<Pocker> zhuang = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                zhuang.add(pks.remove(pks.size()-1));
            }
            List<Pocker> xian = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                xian.add(pks.remove(pks.size()-1));
            }
            xian.sort((o1, o2) -> o1.getNum() == o2.getNum() ? o1.getSuit().compareTo(o2.getSuit()) : o1.getNum() - o2.getNum());
            double result = -1;
            if(isFlush(zhuang)||isStraight(zhuang)||isOneOfKind(zhuang)){
                result = 2;
            }else {
                for(int j=1;j<zhuang.size();j++) {
                    int num = zhuang.get(j).getNum();
                    if(zhuang.get(j-1).getNum()==num){
                        if(num==1||num>=9){
                            result = 2;
                        }
                    }
                }
            }
            if(result==-1){
                if(isFlush(xian)||isStraight(xian)||isOneOfKind(xian)){
                    result = 2;
                }else {
                    for(int j=1;j<xian.size();j++) {
                        int num = xian.get(j).getNum();
                        if(xian.get(j-1).getNum()==num){
                            if(num==1||num>=9){
                                result = 2;
                            }
                        }
                    }
                }
            }
            mockContext.addCount();
            mockContext.addResult(result);
            log.info("第{}次模拟结果:{} ,{}  r:{}   cont:{} min:{}  max:{}  result:{}", i, zhuang,xian, result, mockContext.getCount(), mockContext.getMinWin(), mockContext.getMaxWin(), mockContext.getResult());
        }
        return mockContext;
    }


    private static double yindu6he() {
        double all = c(52, 6);
        double kFlush = 4 * c(52 - 5, 1) / all;
        double flushStraight = 10 * 4 * c(52 - 5, 1) / all;
        double fourOfAKind = 13 * c(52 - 4, 2) / all;
        double fourOfPair = 13 * c(12, 1) * c(4, 2) / all;
        double fullHouse = 13 * c(4, 3) * c(12, 1) * c(4, 2) * c(52 - 5, 1) / all;
        double flush = 4 * c(13, 5) * c(52 - 5, 1) / all;
        double straight = 10 * 4 * 4 * 4 * 4 * 4 * c(52 - 5, 1) / all;
        double threeOfAKind = 13 * c(4, 3) * c(12, 3) * 4 * 4 * 4 / all;
        double result = kFlush * 1001 + (flushStraight - kFlush) * 201 + fourOfAKind * 101 + (fullHouse - fourOfPair) * 21 + (flush - flushStraight) * 16 + (straight - flushStraight) * 11 + (threeOfAKind - flush) * 8;

        return result;
    }

    private static double yindu7he() {
        double all = c(52, 7);
        double kFlushStraight = 4 * c(52 - 5, 2) / all;
        double flushStraight = (4 * 10 * c(52 - 13, 2) +4 * 10 * c(13 - 5, 1) * c(52 - 13, 1) + c(4, 1) * 10 * c(13 - 5, 2)) / all;
        double fourOfAKind = 13 * c(12, 3) * 4 * 4 * 4  / all;
        double fourOfThree = 13 * c(12, 1) * c(4, 3) / all;
        double fourOfPair = 13 * c(12, 1) * c(4, 2) * c(52 - 8, 1) / all;

        double threeThreeOne = c(13, 2) * c(4, 3) * c(4, 3) * c(44, 1) / all;
        double threeTwoTwo = 13 * c(4, 3) * c(12, 2) * c(4, 2) * c(4, 2) / all;
        double threeTwoOneOne = 13 * c(4, 3) * c(12, 1) * c(4, 2) * c(11, 2) * 4 * 4 / all;
        double fullHouse = threeTwoOneOne + threeTwoTwo + threeThreeOne;
        double flush = (4 * c(13, 5) * c(52 - 13, 2) + 4 * c(13, 6) * c(52 - 13, 1) + 4 * c(13, 7)) / all;
        double straight = (10 * 4 * 4 * 4 * 4 * 4 * c(52 - 20, 2) + 10*4*4*4*4*c(4,2) * c(52 - 20, 1) + 10*4*4*4*4*c(4,3)+10*c(4,2)*c(4,2)*4*4*4) / all;
        double threeOfAKind = 13 * c(4, 3) * c(12, 4) * 4 * 4 * 4 * 4 / all;
        double threeOfStrait = 10 * 4 * 4 * 4 * 4 * 4 * c(5, 1) * c(3, 2) / all;
        double threeOfFlush = 4 * c(13, 5) * c(5, 1) * c(3, 2) / all;

        double result = kFlushStraight * 501 + (flushStraight - kFlushStraight) * 101 + (fourOfAKind + fourOfThree + fourOfPair) * 51 + (fullHouse - fourOfPair) * 8 + (flush - flushStraight) * 6 + (straight - flushStraight) * 5 + (threeOfAKind-threeOfFlush-threeOfStrait) * 4;

        return result;
    }


    private static double duizi() {
        double all = c(52, 3);

        double miniQueenFlushStrage = c(4, 1) / all;
        double threeOfKind = 13 * c(4, 3) / all;
        double flushStraight = 12 * 4 / all;
        double straight = 12 * 4 * 4 * 4 / all;
        double flush = 4 * c(13, 3) / all;
        double pair = 13 * c(4, 2) * c(50, 1) / all;

        double result = miniQueenFlushStrage * 101 + (flushStraight - miniQueenFlushStrage) * 41 + threeOfKind * 31 + (straight - flushStraight) * 6 + (flush - flushStraight) * 5 + (pair - threeOfKind) * 2;
        return result;
    }

    private static void test3() {
        double all = c(52,3);
        double threeOfKind = 13 * c(4, 3) / all;
        double flushStrait = 12 * 4 / all;
        double flush = 4 * c(13, 3) / all;
        double straight = 12 * 4 * 4 * 4 / all;
        double dui9OrLarge = c(6,1)*c(4,2)*c(52-4,1)/all+straight+flush-flushStrait+threeOfKind;
       log.info("threeOfKind {}   赔率必须大于{}  ",threeOfKind,  1/threeOfKind-1);
       log.info("flushStrait {}   赔率必须大于{}  ",flushStrait,  1/flushStrait-1);
       log.info("flush {}   赔率必须大于{}  ",flush,  1/flush-1);
       log.info("straight {}   赔率必须大于{}  ",straight,  1/straight-1);
       log.info("dui9OrLarge {}   赔率必须大于{}  ",dui9OrLarge,  1/dui9OrLarge-1);
        double anda = 0.0;
        double baha = 0.0;
        for(int i=1;i<=49;i++){
            if(i%2==1){
                anda+=f(i);
            }else{
                baha+=f(i);
            }
        }
        log.info("anda: {} {}   baha: {} {}",anda,1/anda,baha,1/baha);
        log.info("first {}   {}",f(1),1/f(1));
        double sum = 0.0;
        double[] peilv = new double[]{2, 3, 4, 5, 8, 12, 20, 40, 110, 800};
        int k = 0;
        for (int i = 1; i < 45; i += 5, k++) {
            double rate = rB(i, i + 4);
            sum += rate;
            log.info("{}_{}  - {}  --  {}  {}-{} ", i, i + 4, rate, rate * (peilv[k] + 1),peilv[k],1/rate);
        }

        double rate = rB(46, 49);
        sum += rate;
        log.info("{}_{}  - {}  --  {}  {}-{}", 46, 49, rate, rate * (peilv[peilv.length - 1] + 1),peilv[peilv.length - 1],1/rate);
        log.info("sum : {}", sum);

        System.out.println("Hello India");
        log.info("7 card bonus exp:{}", yindu7he());
        log.info("6 card bonus exp:{}", yindu6he());
        log.info("plus pair exp:{}", duizi());

        double gyHeOdds = c(5,1)*c(5,1)/(double)c(10,2);
        double gyHeEven = (c(5,2)+c(5,2))/(double)c(10,2);
        double gyHeSmall = ((10-2+1)+(9-3+1)+(8-4+1)+(7-5+1)+(6-6+1))/(double)c(10,2);
        double gyHeBig= 1-gyHeSmall;
        log.info("冠亚和单{}   赔率需要大于{}",gyHeOdds,1/gyHeOdds);
        log.info("冠亚和双{}   赔率需要大于{}",gyHeEven,1/gyHeEven);
        log.info("冠亚和小{}   赔率需要大于{}",gyHeSmall,1/gyHeSmall);
        log.info("冠亚和大{}   赔率需要大于{}",gyHeBig,1/gyHeBig);

        double heDan= (c(6,3)*c(5,2)+c(6,1)*c(5,4)+c(6,5))/(double)c(11,5);
        double heShuang= (c(6,0)*c(5,5)+c(6,2)*c(5,3)+c(6,4)*c(5,1))/(double)c(11,5);
        log.info("11选5和单{}   赔率需要大于{}",heDan,1/heDan);
        log.info("11选5和双{}   赔率需要大于{}",heShuang,1/heShuang);
    }
    public static void main(String[] args) {
        test3();
        mock3(1);

    }

}

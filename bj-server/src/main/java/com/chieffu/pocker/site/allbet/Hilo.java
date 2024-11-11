package com.chieffu.pocker.site.allbet;

import com.chieffu.pocker.Ma;
import com.chieffu.pocker.Pocker;
import com.chieffu.pocker.SuitEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class Hilo extends Ma {
    private int[][] pk;

    public Hilo() {
        init();
    }

    private void init() {
        pk = new int[5][14];
        for (int i = 1; i <= 4; i++) {
            pk[i] = new int[14];
            Arrays.fill(pk[i], 6 );
            pk[i][10] = 0;
            pk[i][0] = 0;
        }
    }

    public int countPai() {
        int count = 0;
        for (int i = 0; i < pk.length; i++) {
            int[] huaseI = pk[i];
            for (int j = 0; j < huaseI.length; j++) {
                count += huaseI[j];
            }
        }
        return count;
    }

    public int countNum(Integer num){
        int count = 0;
        if(num>13||num<1)return 0;
        for(int i=0;i<pk.length;i++){
            count+=pk[i][num];
        }
        return count;
    }
    public int countSuit(SuitEnum suit){
        return Arrays.stream(pk[suit.getHuaSe()]).sum();
    }
    public int countPocker(Pocker pocker){
        return pk[pocker.getSuit().getHuaSe()][pocker.getNum()];
    }

    public void removePocker(Pocker pocker){
        pk[pocker.getSuit().getHuaSe()][pocker.getNum()]--;
    }
    public double[] odds(int num){
        double[] r = new double[10];
        double count = countPai();
        int count2_5 = countNum(2)+countNum(3)+countNum(4)+countNum(5);
        int count6_9 = countNum(6)+countNum(7)+countNum(8)+countNum(9);
        int countJ_A = countNum(1)+countNum(11)+countNum(12)+countNum(13);
        int countSmall=0;
        int countBig=0;

        if(num==1){
            countSmall = (int) (count-countNum(1));
            countBig = 0;
        }else{
            for(int i=2;i<num;i++){
                countSmall+=countNum(i);
            }
            for(int i=num+1;i<14;i++){
                countBig+=countNum(i);
            }
            countBig+=countNum(1);
        }
        int countNum = countNum(num);
        int countRed = countSuit(SuitEnum.HEART) + countSuit(SuitEnum.DIAMOND);
        int countBlack = countSuit(SuitEnum.SPADE) + countSuit(SuitEnum.CLUB);
        int countOdd = countNum(1)+countNum(3)+countNum(5)+countNum(7)+countNum(9)+countNum(11)+countNum(13);
        int countPair = countNum(2)+countNum(4)+countNum(6)+countNum(8)+countNum(12);
        r[0] = count/(count2_5==0?0.0001:count2_5)-1;
        r[1] = count/(count6_9==0?0.0001:count6_9)-1;
        r[2] = count/(countJ_A==0?0.0001:countJ_A)-1;
        r[3] = count/(countSmall==0?0.001:countSmall)-1;
        r[4] = count/(countNum==0?0.0001:countNum)-1;
        r[5] = count/(countBig==0?0.0001:countBig)-1;
        r[6] = count/(countRed==0?0.0001:countRed)-1;
        r[7] = count/(countBlack==0?0.0001:countBlack)-1;
        r[8] = count/(countOdd==0?0.0001:countOdd)-1;
        r[9] = count/(countPair==0?0.0001:countPair)-1;
        return r;
    }

    public static void main(String[] args) {
        Hilo hilo  = new Hilo();
        double[] odds = hilo.odds(7);
        log.info( String.format("2/3/4/5:%.3f  6/7/8/9:%.3f  J/Q/K/A:%.3f \t|\t 小:%.3f 对:%.3f 大:%.3f \t|\t 红:%.3f  黑:%.3f  单:%.3f  双:%.3f",odds[0],odds[1],odds[2],odds[3],odds[4],odds[5],odds[6],odds[7],odds[8],odds[9]));
        List<Pocker> pks = new ArrayList<>();
        Arrays.asList(211,304,108,413,303,205,205,302,411,213,309,411,208).stream().forEach(i->pks.add(Pocker.fromCard(i)));
        pks.forEach(p->hilo.removePocker(p));
        log.info("{}",pks);
        odds = hilo.odds(pks.get(pks.size()-1).getNum());
        log.info( String.format("2/3/4/5:%.3f  6/7/8/9:%.3f  J/Q/K/A:%.3f \t|\t 小:%.3f 对:%.3f 大:%.3f \t|\t 红:%.3f  黑:%.3f  单:%.3f  双:%.3f",odds[0],odds[1],odds[2],odds[3],odds[4],odds[5],odds[6],odds[7],odds[8],odds[9]));

        hilo.init();
        pks.clear();
        Arrays.asList(209,104,304,212,404,305,406,107,111,102,307,201,211,106,309,104).stream().forEach(i->pks.add(Pocker.fromCard(i)));
        pks.forEach(p->hilo.removePocker(p));
        log.info("{}",pks);
        odds = hilo.odds(pks.get(pks.size()-1).getNum());
        log.info( String.format("2/3/4/5:%.3f  6/7/8/9:%.3f  J/Q/K/A:%.3f \t|\t 小:%.3f 对:%.3f 大:%.3f \t|\t 红:%.3f  黑:%.3f  单:%.3f  双:%.3f",odds[0],odds[1],odds[2],odds[3],odds[4],odds[5],odds[6],odds[7],odds[8],odds[9]));



    }
}

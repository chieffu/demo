

package com.chieffu.pocker;

import lombok.Data;

import java.io.Serializable;
import java.util.*;

@Data
public class Pocker implements Serializable {
    private static List<Pocker> standard = initPork();

    private SuitEnum suit;

    private int num;

    public static Pocker getPocker(int card) {
        return standard.get(card%standard.size());
    }


    public Pocker(int huaSe, int num) {
        this.suit = SuitEnum.getSuitEnum(huaSe);
        this.num = num;
    }

    /**
     * 例如 105 表示黑桃5  313 表示梅花K  401 表示 方块A
     * @param n 三位的数字，第一位表示花色 1:黑桃  2:红桃 3:梅花 4:方块
     * @return
     */
    public static Pocker fromCard(Integer n) {
        return new Pocker(n/100, n%13);
    }

    public int getBaccaratDot() {
        return (this.num >= 10) ? 0 : this.num;
    }

    public String toString() {
        String msg = suit.getDescription();
        switch (this.num) {
            case 1:
                msg = "A"+msg  ;
                return msg;
            case 11:
                msg = "J"+msg ;
                return msg;
            case 12:
                msg = "Q"+msg  ;
                return msg;
            case 13:
                msg = "K"+msg ;
                return msg;
            default:
                msg =  this.num+msg ;
                return msg;
        }

    }

    private static List<Pocker> initPork() {
        List<Pocker> list0 = new ArrayList<>(64);
        for (int i = 1; i <= 4; i++) {
            for (int j = 1; j <= 13; j++)
                list0.add(new Pocker(i, j));
        }
        return list0;
    }

    public static List<Pocker> getStandardPork() {
        return standard;
    }

    public static List<Pocker> randomPocker(int nFupai) {
        List<Pocker> list = new ArrayList<>(nFupai * 64);
        List<Pocker> list0 = getStandardPork();
        for (int i = 0; i < nFupai; i++)
            list.addAll(list0);
        Random rd = new Random();
        Collections.shuffle(list, rd);
        Collections.shuffle(list, rd);
        return new LinkedList<>(list);
    }
}

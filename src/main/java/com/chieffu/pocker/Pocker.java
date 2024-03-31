

package com.chieffu.pocker;

import lombok.Data;

import java.util.*;

@Data
public class Pocker {
    private static List<Pocker> standard = initPork();

    private SuitEnum suit;

    private int num;

    public static Pocker getPocker(int card) {
        if (card == 255)
            return null;
        return standard.get(card);
    }


    public Pocker(int huaSe, int num) {
        this.suit = SuitEnum.getSuitEnum(huaSe);
        this.num = num;
    }

    public int getBaccaratDot() {
        return (this.num >= 10) ? 0 : this.num;
    }

    public String toString() {
        String msg = suit.getDescription();
        switch (this.num) {
            case 1:
                msg = msg + "A";
                return msg;
            case 11:
                msg = msg + "J";
                return msg;
            case 12:
                msg = msg + "Q";
                return msg;
            case 13:
                msg = msg + "K";
                return msg;
        }
        msg = msg + this.num;
        return msg;
    }

    private static List<Pocker> initPork() {
        List<Pocker> list0 = new ArrayList<>(64);
        for (int i = 0; i < 4; i++) {
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
        return new LinkedList<>(list);
    }
}

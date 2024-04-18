package com.chieffu.pocker.blackjack;

public class IndiaSofa extends Blackjack{

    public IndiaSofa(int n) {
        super(n);
    }

    public static void main(String[] args) {

        System.out.println("Hello India");
        double all = c(52,6);
        double kFlush = 4*c(52-5,1)/all ;
        double flushStraight = 9*4*c(52-5,1)/all ;
        double fourOfAKind = 13*c(52-4,2)/all ;
        double fullHouse = 13*c(4,3)*c(12,1)*c(4,2)/all ;
        double flush = (4*c(13,5)*c(52-5,1)-9*4*c(52-5,1))/all ;
        double straight = 10*4*4*4*4*c(52-5,1)/all * 11;
        double result = kFlush* 1001 + flushStraight *  201 + fourOfAKind * 101 + fullHouse * 21 + flush * 16 + straight * 11;
        System.out.println(result);
    }
}

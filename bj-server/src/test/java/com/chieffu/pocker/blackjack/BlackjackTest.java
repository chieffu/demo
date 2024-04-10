package com.chieffu.pocker.blackjack;

import com.chieffu.pocker.Pocker;
import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class BlackjackTest extends TestCase {

    public void testP() {
        Assert.assertEquals(5*4*3,Blackjack.p(5,3));
    }

    public void testC() {
        Assert.assertEquals(10*9*8*7/(4*3*2*1), Blackjack.c(10,4));
    }

    public void testIsStraight() {
        List<Pocker> pockers = Arrays.asList(Pocker.getPocker(4), Pocker.getPocker(5), Pocker.getPocker(6));
        Assert.assertTrue(Blackjack.isStraight(pockers));
        log.info("{}",pockers);
        pockers=Arrays.asList(Pocker.getPocker(0),Pocker.getPocker(1),Pocker.getPocker(2));
        Assert.assertTrue(Blackjack.isStraight(pockers));
        log.info("{}",pockers);
        pockers=Arrays.asList(Pocker.getPocker(0),Pocker.getPocker(11),Pocker.getPocker(12));
        log.info("{}",pockers);
        Assert.assertTrue(Blackjack.isStraight(pockers));
        Assert.assertTrue(Blackjack.isFlush(pockers));

        pockers=Arrays.asList(Pocker.getPocker(0),Pocker.getPocker(14),Pocker.getPocker(28),Pocker.getPocker(3),Pocker.getPocker(4),Pocker.getPocker(5),Pocker.getPocker(6));
        Assert.assertTrue(Blackjack.isStraight(pockers));
        log.info("{}",pockers);
    }

    public void testIsFlush() {
        List<Pocker> pockers = Arrays.asList(Pocker.getPocker(0), Pocker.getPocker(5), Pocker.getPocker(9));

        Assert.assertTrue(Blackjack.isFlush(pockers));
    }

    public void testIsOneOfKind() {
    }

    public void testCountPai() {
    }

    public void testGroups() {
    }

    public void testDot() {
    }

    public void testDotsOfPocker() {
    }

    public void testDots() {
    }

    public void testTestDots() {
    }

    public void testIsBlackjack() {
    }

    public void testTestCountPai() {
    }

    public void testTestCountPai1() {
    }

    public void testTestCountPai2() {
    }

    public void testP1() {
    }

    public void testP2() {
    }

    public void testPairExpectation() {
    }

    public void testCheckPair() {
    }

    public void testCheckPurePair() {
    }

    public void testLuckThreeExpectation() {
    }

    public void testCheckPureThree() {
    }

    public void testCheckStraightFlush3() {
    }

    public void testCheckThreeOfKind() {
    }

    public void testCheckStraight3() {
    }

    public void testCheckFlush3() {
    }

    public void testBags() {
    }

    public void testHotThreeExpectation() {
    }

    public void testLuckyQueenExpectation() {
    }

    public void testBloomExpectation() {
    }

    public void testCheckFlush21Of3Rate() {
    }

    public void testCheckFlush20Of2Rate() {
    }

    public void testP3() {
    }

    public void testZ2() {
    }

    public void testRZNotBloom() {
    }

    public void testXWinExpectation() {
    }

    public void testXWinRate() {
    }

    public void testRBjWin() {
    }

    public void testRemovePocker() {
    }

    public void testTestRemovePocker() {
    }

    public void testGetPai() {
    }

    public void testGetPk() {
    }
}
package com.chieffu.pocker.blackjack;

import junit.framework.TestCase;
import org.junit.Assert;

import java.util.Arrays;
import java.util.List;

public class StageTest extends TestCase {

    public void testGetZEndStage() {
        List<Blackjack.Stage> stage = Blackjack.Stage.getZEndStage(0);
        Assert.assertNotEquals(0, stage.size());
    }

    public void testIsFinalZStage() {
        Assert.assertTrue(Blackjack.Stage.isFinalZStage(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)));
        Assert.assertTrue(Blackjack.Stage.isFinalZStage(Arrays.asList(1, 2, 9,7)));
    }

    public void testGetXStage() {
        Blackjack.Stage stage = Blackjack.Stage.getX_ROOT(Arrays.asList(1, 2, 3));
        System.out.println(stage.getCards());
        Assert.assertEquals(16,stage.largeDot());
    }

    public void testGetZStage() {
        Blackjack.Stage stage3 = Blackjack.Stage.getZStage(3);
        System.out.println(stage3.getCards());
        Assert.assertEquals(10,stage3.getNext().size());
        Blackjack.Stage stage = stage3.getNext().stream().filter(s->s.getPai()==10).findFirst().orElse(null);
        Assert.assertEquals(13,stage.largeDot());
    }

    public void testGetCurrentWinRate() {
        Blackjack.Stage stage = Blackjack.Stage.getX_ROOT(Arrays.asList(1, 10));

    }


    public void testZRate() {
    }

    public void testIsEndStage() {
    }

    public void testAddZStage() {
    }

    public void testAddXStage0() {
    }

    public void testGetCards() {
    }

    public void testLargeDot() {
    }

    public void testSmallDot() {
    }

    public void testTestToString() {
    }

    public void testGetStageCount() {
    }

    public void testTestZRate() {
    }

    public void testXRate() {
    }
}
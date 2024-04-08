package com.chieffu.pocker.blackjack;

import com.chieffu.pocker.Pocker;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class StageTest extends TestCase {

    public void testGetZEndStage() {
        List<Stage> stage = Stage.getZEndStage(0);
        Assert.assertNotEquals(0, stage.size());
    }

    public void testIsFinalZStage() {
        Assert.assertTrue(Stage.isFinalZStage(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)));
        Assert.assertTrue(Stage.isFinalZStage(Arrays.asList(1, 2, 9,7)));
    }

    public void testGetXStage() {
        Stage stage = Stage.getXStage(Arrays.asList(1, 2, 3));
        System.out.println(stage.getCards());
        Assert.assertEquals(16,stage.largeDot());
    }

    public void testGetZStage() {
        Stage stage3 = Stage.getZStage(3);
        System.out.println(stage3.getCards());
        Assert.assertEquals(10,stage3.getNext().size());
        Stage stage = stage3.getNext().stream().filter(s->s.getPai()==10).findFirst().orElse(null);
        Assert.assertEquals(13,stage.largeDot());
    }

    public void testGetCurrentWinRate() {
        Stage stage = Stage.getXStage(Arrays.asList(1, 10));

    }

    public void testXWinRate() {
        double d  = Stage.xWinRate(new Blackjack(8).getPai(), Arrays.asList(1, 10), 5);
        Assert.assertTrue(d>0.8);
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
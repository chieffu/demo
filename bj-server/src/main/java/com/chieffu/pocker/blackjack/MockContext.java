package com.chieffu.pocker.blackjack;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class MockContext {
    String name;

    public MockContext(String name) {
        this.name = name;
    }
    Map<String,Object> attribate = new HashMap<>();
    double count = 0;
    double result = 0;
    double maxWin = 0.0;
    double minWin = 0.0;

    double even;

    double win;

    double lose;

   public void addCount() {
        count++;
    }

   public void addCount(double count) {
        this.count += count;
    }

   public void addResult(double d) {
        result += d;
        if (maxWin < result) {
            maxWin = result;
        }
        if (minWin > result) {
            minWin = result;
        }
        if(d>0){
            win++;
        }else if(d<0){
            lose++;
        }else {
            even++;
        }
    }

    public void merge(MockContext c) {
        this.addResult(c.getResult());
        this.addCount(c.getCount());
        this.win+=c.win;
        this.lose+=c.lose;
        this.even+=c.even;
    }

    public void merge(MockContext c, int times) {
        this.addResult(c.getResult() * times);
        this.addCount(c.getCount() * times);
        this.win+=c.win;
        this.lose+=c.lose;
        this.even+=c.even;
    }
}

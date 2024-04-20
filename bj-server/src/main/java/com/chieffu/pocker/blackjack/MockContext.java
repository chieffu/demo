package com.chieffu.pocker.blackjack;

import lombok.Data;

@Data
class MockContext {
    String name;

    MockContext(String name) {
        this.name = name;
    }

    double count = 0;
    double result = 0;
    double maxWin = 0.0;
    double minWin = 0.0;

    void addCount() {
        count++;
    }

    void addCount(double count) {
        this.count += count;
    }

    void addResult(double d) {
        result += d;
        if (maxWin < result) {
            maxWin = result;
        }
        if (minWin > result) {
            minWin = result;
        }
    }

    public void merge(MockContext c) {
        this.addResult(c.getResult());
        this.addCount(c.getCount());
    }

    public void merge(MockContext c, int times) {
        this.addResult(c.getResult() * times);
        this.addCount(c.getCount() * times);
    }
}

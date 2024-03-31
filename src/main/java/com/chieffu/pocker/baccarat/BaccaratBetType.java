package com.chieffu.pocker.baccarat;

public enum BaccaratBetType {
    Banker(0, 0.95D, "庄"),
    Player(1, 1.0D, "闲"),
    Tie(2, 8.0D, "和"),
    BankerPair(3, 11.0D, "庄对"),
    PlayerPair(4, 11.0D, "闲对"),
    BankerBonus(5, 0.0D, "庄龙宝"),
    PlayerBonus(6, 0.0D, "闲龙宝"),
    Big(7, 0.5D, "大"),
    Small(8, 1.5D, "小"),
    Phoenix(9, 25.0D, "完美对子"),
    Turtle(10, 5.0D, "任意对子");
    public int categoryIdx;
    public double rate;
    public String name;

    BaccaratBetType(int categoryIdx, double rate, String name) {
        this.categoryIdx = categoryIdx;
        this.rate = rate;
        this.name = name;
    }

    public static BaccaratBetType from(String name) {
        BaccaratBetType[] values = values();
        for (BaccaratBetType t : values) {
            if (t.name.equalsIgnoreCase(name.trim()) || t.toString().equalsIgnoreCase(name.trim())) {
                return t;
            }
        }
        return null;
    }
}


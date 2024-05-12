package com.chieffu.pocker.bj;

public enum Action {
    HIT(0), // 要牌
    STAND(1), // 停牌
//    DOUBLE_DOWN(2),// 加倍
//    SPLIT(3), // 拆牌
//    SURRENDER, // 投降
//    BET_BIG(4),//大注
//    BET_MEDIUM(5),//中注
//    BET_SMALL(6)//小注
    ;
    int idx;
    Action(int idx){
        this.idx=idx;
    }
    public int getIndex(){
        return idx;
    }
}

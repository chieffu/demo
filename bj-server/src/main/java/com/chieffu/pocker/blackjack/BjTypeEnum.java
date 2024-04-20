package com.chieffu.pocker.blackjack;

public enum BjTypeEnum {
    BASE_PLAY("底注",0,"A+10/J/Q/K  赔2.5,其他赔1"),
    PAIR("对子",1,"纯对子赔25，其他对子赔8") ,
    LUCKY_THREE("幸运3",2,"同花色3条赔100，同花顺赔40，三条赔30，顺子赔10，同花赔5"),
    HOT_THREE("烫三手",3,"前三张3个7组成21点赔100，同花21点赔20,其他21点赔4,其他20点赔2，其他19点赔1"),
    BLOOM("爆牌",4,"前三张3张爆赔1,4张爆赔2,5张爆赔9,6张爆赔50,7张爆赔100,8张或以上爆赔250"),
    LUCKY_QUEEN("幸运女皇",5,"庄BJ闲红桃Q对1000,闲家前两张红桃Q对赔125,纯色对子赔19,同花20点赔9，任意20点赔4");

    private final String name;
    private final int code;

    private final String description;

    BjTypeEnum(String name,int code,String description){
       this.name = name;
       this.code = code;
       this.description = description;
    }

    public String getName(){
        return this.name;
    }

    public static BjTypeEnum fromCode(int code){
        for(BjTypeEnum bjTypeEnum : BjTypeEnum.values()){
            if(bjTypeEnum.code == code){
                return bjTypeEnum;
            }
        }
        return null;
    }
}

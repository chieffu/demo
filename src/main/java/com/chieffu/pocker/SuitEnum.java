package com.chieffu.pocker;

public enum SuitEnum {

        SPADE(0,"黑桃"),
        HEART(1,"红桃"),
        CLUB(2,"梅花"),
        DIAMOND(3,"方块");

        // 每个枚举常量对应一个字符串描述
        private final String description;
        private final int huaSe;

        // 构造器用于初始化每个枚举成员
        SuitEnum(int huaSe,String description) {
            this.huaSe = huaSe;
            this.description = description;
        }

        public int getHuaSe() {
            return huaSe;
        }
        // 提供获取枚举成员描述的方法
        public String getDescription() {
            return description;
        }

        public static SuitEnum getSuitEnum(int huaSe) {
            for (SuitEnum suitEnum : SuitEnum.values()) {
                if (suitEnum.getHuaSe() == huaSe) {
                    return suitEnum;
                }
            }
            return null;
        }

}

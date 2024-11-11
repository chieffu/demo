package com.chieffu.pocker.site.DB;

public class DBConstant {
    public static class ResponseId{
        // 添加常量
        public static final int INTER_GAME = 101;
        public static final int NEW_INTER_GAME = 401;
        public static final int OUT_GAME = 102;
        public static final int NEW_BOOT = 103;
        public static final int OPEN_CARD = 106;
        public static final int SETTLEMENT = 107;
        public static final int ROAD_PAPER = 116;
        public static final int SINGLE_ROAD = 161;
        public static final int GOOD_ROAD_CHANGE_BOOT = 216;
        public static final int INTER_MULTIPLE = 301;
        public static final int INTER_GOOD_ROAD = 302;
        public static final int GOOD_ROAD_SETTLEMENT = 303;
        public static final int TABLE_LIST_CHANGE = 304;
        public static final int GOOD_ROAD_LIST = 10021;
        public static final int TABLE_DATA_UPDATE = 10052;
        public static final int GOOD_ROAD_DATA_UPDATE = 10053;
        public static final int UPDATE_ONLINE_NUMBER = 10054;
        public static final int TABLE_BASE_DATA = 10070;
        public static final int TABLE_ROAD = 10071;
        public static final int TABLE_BOOT_REPORT = 10072;
        public static final int TABLE_BET_POINT_LIMIT = 10073;
        public static final int TABLE_BOOT_NUMBER_LIMIT = 10074;
        public static final int TABLE_VERSION = 10075;
        public static final int TABLE_BOOT_NUMBER_LIMIT_UPDATE = 10077;
        public static final int TABLE_LIST_NEW = 10078;
        public static final int TABLE_LIST_ALL = 10089;
        public static final int TABLE_CACHE_BET_POINT_LIMIT_ID = 10081;
        public static final int TABLE_BET_POINT_LIMIT_UPDATE = 10082;
        public static final int TABLE_CHANGE_INFO = 10083;
        public static final int TABLE_ITEM_INFO = 10084;
        public static final int TABLE_ROUND_LIST = 10087;
        public static final int TABLE_GET_ROUND_INFO = 10088;
        public static final int SEARCH = 10105;
        public static final int SEARCH_GET_HISTORY = 10097;
        public static final int SEARCH_CLEAR_HISTORY = 10098;
    }
    public static class GameType{
        // 常量定义
        public static final int BACCARAT_GOOD_ROAD = 0;
        public static final int HALL_ALL = 1;
        public static final int BACCARAT_GOOD_ROAD_F = 4;
        public static final int BACCARAT = 2001;
        public static final int BACCARAT_FAST = 2002;
        public static final int BACCARAT_BID = 2003;
        public static final int BACCARAT_VIP = 2004;
        public static final int BACCARAT_REVEAL = 2005;
        public static final int DRAGONTIGER = 2006;
        public static final int ROULETTE = 2007;
        public static final int SIC_BO = 2008;
        public static final int BULL_FIGHT = 2009;
        public static final int WIN_THREE_CARDS = 2010;
        public static final int THREE_TRUMPS = 2011;
        public static final int MULTIPLAY = 2013;
        public static final int BACCARAT_HIGH_STAKES = 2014;
        public static final int DOU_NIU = 2015;
        public static final int BACCARAT_INSURANCE = 2016;
        public static final int CRYPTO_CLASSIC_BACCARAT = 2017;
        public static final int BACCARAT_MATCH = 2018;
        public static final int TEXASPOKER = 2019;
        public static final int FAN_TAN = 2020;
        public static final int BLACKJACK = 2021;
        public static final int COLORDISC = 2022;
        public static final int PAIGOW = 2023;
        public static final int ANDARBAHAR = 2025;
        public static final int INDIA_THREECARDS = 2026;
        public static final int BACCARAT_JINWU = 2027;
        public static final int OBBALL = 2028;
        public static final int MARK_SIX = 2029;
        public static final int BACCARAT_ZHUBO = 2030;
        public static final int GAME_3D = 2031;
        public static final int GAME_5D = 2032;
        public static final int LIGHTNING_BACC = 2034;
        public static final int CAR = 2035;
        public static final int DOLLY = 2036;
        public static final int HALL_VIDEO = -121099;
        public static final int MATCH_LOBBY = -121090;
        public static final int LIVE_LOBBY = -121091;
        public static final int NONE = -1;

        // 反向映射常量
        public static final String BACCARAT_GOOD_ROAD_STR = "Baccarat_GOOD_ROAD";
        public static final String HALL_ALL_STR = "HALL_ALL";
        public static final String BACCARAT_GOOD_ROAD_F_STR = "Baccarat_GOOD_ROAD_F";
        public static final String BACCARAT_STR = "BACCARAT";
        public static final String BACCARAT_FAST_STR = "BACCARAT_FAST";
        public static final String BACCARAT_BID_STR = "BACCARAT_BID";
        public static final String BACCARAT_VIP_STR = "BACCARAT_VIP";
        public static final String BACCARAT_REVEAL_STR = "BACCARAT_REVEAL";
        public static final String DRAGONTIGER_STR = "DRAGONTIGER";
        public static final String ROULETTE_STR = "ROULETTE";
        public static final String SIC_BO_STR = "SIC_BO";
        public static final String BULL_FIGHT_STR = "BULL_FIGHT";
        public static final String WIN_THREE_CARDS_STR = "WIN_THREE_CARDS";
        public static final String THREE_TRUMPS_STR = "THREE_TRUMPS";
        public static final String MULTIPLAY_STR = "MULTIPLAY";
        public static final String BACCARAT_HIGH_STAKES_STR = "BACCARAT_HIGH_STAKES";
        public static final String DOU_NIU_STR = "DOU_NIU";
        public static final String BACCARAT_INSURANCE_STR = "BACCARAT_INSURANCE";
        public static final String CRYPTO_CLASSIC_BACCARAT_STR = "CRYPTO_CLASSIC_BACCARAT";
        public static final String BACCARAT_MATCH_STR = "BACCARAT_MATCH";
        public static final String TEXASPOKER_STR = "TEXASPOKER";
        public static final String FAN_TAN_STR = "FAN_TAN";
        public static final String BLACKJACK_STR = "BLACKJACK";
        public static final String COLORDISC_STR = "COLORDISC";
        public static final String PAIGOW_STR = "PAIGOW";
        public static final String ANDARBAHAR_STR = "ANDARBAHAR";
        public static final String INDIA_THREECARDS_STR = "INDIA_THREECARDS";
        public static final String BACCARAT_JINWU_STR = "BACCARAT_JINWU";
        public static final String OBBALL_STR = "OBBALL";
        public static final String MARK_SIX_STR = "MarkSix";
        public static final String BACCARAT_ZHUBO_STR = "BACCARAT_ZHUBO";
        public static final String GAME_3D_STR = "GAME_3D";
        public static final String GAME_5D_STR = "GAME_5D";
        public static final String LIGHTNING_BACC_STR = "LIGHTNING_BACC";
        public static final String CAR_STR = "CAR";
        public static final String DOLLY_STR = "DOLLY";
        public static final String HALL_VIDEO_STR = "HALL_VIDEO";
        public static final String MATCH_LOBBY_STR = "MATCH_LOBBY";
        public static final String LIVE_LOBBY_STR = "LIVE_LOBBY";
        public static final String NONE_STR = "NONE";
    }

    public static class GameStatus{
        // 常量定义
        public static final int READY = 0;
        public static final int SHUFFLE = 1;
        public static final int BET = 2;
        public static final int OPEN = 3;
        public static final int COUNT = 4;
        public static final int MANTAIN = 6;
        public static final int FLYFLAG = 11;

        // 反向映射常量
        public static final String READY_STR = "READY";
        public static final String SHUFFLE_STR = "SHUFFLE";
        public static final String BET_STR = "BET";
        public static final String OPEN_STR = "OPEN";
        public static final String COUNT_STR = "COUNT";
        public static final String MANTAIN_STR = "MANTAIN";
        public static final String FLYFLAG_STR = "FLYFLAG";
    }
}

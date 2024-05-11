package com.chieffu.pocker.project;

public class MyQlearning extends Qlearning{
    public MyQlearning(){
        STATES = 698 + 1; // 698 = largest legal state # possible
        //       21 Ace 10 [in decimal] = 10101 1 1010 [in binary]);
        STATES = encode(21, 10, true, -7)+1;
        this.q = new double[STATES][2];
    }

    /**
     * Get the current state.
     * State value = 5 bits for player hand value
     *             + 1 bit for player has Ace/no Ace
     *             + 4 bits for dealer exposed card value
     * @param game The current game.
     * @return The current state.
     */
    protected int getState(Game game) {
        int dealerCard = game.getDealerUpCardValue(); //取值范围 [1,10]
        int player = game.getPlayerHandMinValue();//取值范围 [2,21]
        boolean hasAce = game.playerHasAce();//取值范围 [0,1]
        int count = (int)game.getShoe().highLowCardCounting();//取值范围 [-7,7]
        if(count>7)count=7;
        if(count<-7)count=-7;

        return encode(dealerCard, player, hasAce, count);
    }
    public static int encode(int dealerCard, int player, boolean hasAce, int count) {
        // 将 count 转换为绝对值并映射到 14 位
        count = Math.abs(count) % 15;

        // 位移，确保每个数字占据独立的位
        int encoded =
                (dealerCard & 0x1F) << 10 |
                        (player & 0x1F) << 5 |
                        (hasAce ? 1 : 0) << 4 |
                        count;

        return encoded;
    }

    public static int[] decode(int encoded) {
        int[] decoded = new int[4];
        decoded[0] = (encoded >> 10) & 0x1F; // dealerCard
        decoded[1] = (encoded >> 5) & 0x1F; // player
        decoded[2] = encoded >> 4 & 1; // hasAce
        decoded[3] = encoded & 0xF; // count

        // 如果 count 的值大于7，表示它是负数
        decoded[3] = decoded[3] > 7 ? -(decoded[3] - 7) : decoded[3];

        return decoded;
    }


    /**
     * Get card information from a provided state.
     * @param state The state to unget.
     * @return the card info. available from the state.
     */
    protected String unGetState(int state) {
        int[] code =decode(state);

        return String.format("%3s-%2d%sAce %9s",code[3], code[1], code[2]==1? "+" : "-", code[0]);
    }

    public void prettyPrintQ(boolean printValues) {
        double stand;
        double hit;

        System.out.println("Player -- Dealer -- Stand -- Hit -");

        for (int i = 0; i < STATES; i++) {
            stand = this.q[i][0];
            hit = this.q[i][1];
            if (stand != 0.0 && hit != 0.0) {
                if (printValues) {
                    System.out.printf("%10s %8.3f %8.3f %n", unGetState(i), stand, hit);
                } else {
                    if (stand > hit) {
                        System.out.printf("%10s %6s %7s %n", unGetState(i), "X", "-");
                    } else {
                        System.out.printf("%10s %6s %7s %n", unGetState(i), "-", "X");
                    }
                }
            }
        }
        System.out.println();
    }
}

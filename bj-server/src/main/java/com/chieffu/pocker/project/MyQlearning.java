package com.chieffu.pocker.project;

public class MyQlearning extends Qlearning{
    public MyQlearning(){
        STATES = 698 + 1; // 698 = largest legal state # possible
        //       21 Ace 10 [in decimal] = 10101 1 1010 [in binary]);
        STATES = encode(10, 21, true, -1)+1;
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
        int player = game.getPlayerHandValue();//取值范围 [2,21]
        boolean hasAce = game.playerHasAce();//取值范围 [0,1]
        int count = (int)game.getShoe().myCardCounting();//取值范围 [-15,15]
        if(count>15)count=15;
        if(count<-15)count=-15;

        return encode(dealerCard, player, hasAce, count);
    }
    public static int encode(int dealerCard, int player, boolean hasAce, int count) {
        // 将 count 转换为绝对值并映射到 14 位
        int encoded = (dealerCard & 0xF) << 11 |
                (player & 0x1F) << 6 |
                (hasAce ? 1 : 0) << 5 |
                (count & 0x1F) ;

        return encoded;
    }

    public static int[] decode(int encoded) {
        int[] decoded = new int[4];
        decoded[0] = (encoded >> 11) & 0xF; // dealerCard  [1,10]
        decoded[1] = (encoded >> 6) & 0x1F; // player [2,21]
        decoded[2] = encoded >> 5 & 1; // hasAce [0,1]
        decoded[3] = encoded & 0x1F; // count [-15,15]

        // 将 count 转换回 -15 到 15 的范围
        decoded[3] = decoded[3]>15?(decoded[3]-32):decoded[3];
        return decoded;
    }

    /**
     * Train with Q Learning.
     * @param episodes Number of episodes (games) to play/train.
     * @param eta Learning rate
     * @param gamma Gamma value
     * @param epsilon Initial epsilon value
     * @param epsilonMin Minimum epsilon value
     * @param epsilonDelta Amount to change epsilon every epsilonEvery episodes
     * @param epsilonEvery Number of episodes to train before reducing epsilon by epsilonDelta
     */
    public void train(int episodes, double eta, double gamma, double epsilon, double epsilonMin, double epsilonDelta, int epsilonEvery) throws IllegalArgumentException {
        if (eta <= 0.0 || gamma < 0.0 || epsilon < 0.0 || epsilonMin < 0.0 || epsilonDelta < 0.0 ||
                eta > 1.0  || gamma > 1.0 || epsilon > 1.0 || epsilonMin > 1.0 || epsilonDelta > 1.0 ||
                epsilonEvery >= episodes || epsilonMin > epsilon || episodes <= 0 || epsilonEvery < 1) {
            throw new IllegalArgumentException("Illegal argument(s) passed to Qlearning.train()");
        }
        int plotEvery = episodes > 50 ? episodes / 50 : 1;
        int state;
        int stateNew;
        int action;
        double reward;

        System.out.printf("%nLearning Q-style    .    .    .    .    .%9d %n", episodes);

        for (int i = 0; i < episodes; i++) {
            Game game = new Game();
            state = getState(game);

            do {
                action = getAction(state, epsilon);
                reward = takeAction(action, game);
                stateNew = getState(game);
                q[state][action] += eta * (reward + gamma * getActionMaxValue(stateNew) - q[state][action]);
                state = stateNew;
            } while (!game.isOver());

            // Update epsilon value periodically
            if ((i + 1) % epsilonEvery == 0) {
                if (epsilon > epsilonMin) {
                    epsilon -= epsilonDelta;
                }
                if (epsilon < epsilonMin) {
                    epsilon = epsilonMin;
                }
            }

            // Update progress bar
            if ((i+1) % plotEvery == 0) {
                System.out.print('>');
            }
        }
        System.out.println("\n");
    }

    /**
     * Test for #episodes. Return win %'s.
     * @param episodes Number of episodes (games) to play
     * @return The win percentages as array of 2 doubles [w/o pushes, w/ pushes].
     */
    public double[] test(int episodes) {
        if (episodes < 1) {
            throw new IllegalArgumentException("Illegal argument passed to Qlearning.test()");
        }

        int state;
        int action;
        int wins = 0;
        int losses = 0;
        int evens = 0;

        for (int i = 0; i < episodes; i++) {
            Game game = new Game();
            state = getState(game);

            do {
                action = getActionMax(state);
                takeAction(action, game);
                state = getState(game);
            } while (!game.isOver());
            wins+=game.getWin();
            losses+=game.getLose();
            evens+=game.getEven();
        }

        return new double[] {wins / (double) (wins + losses), wins / (double) (wins+losses+evens)};
    }
    /**
     * Get card information from a provided state.
     * @param state The state to unget.
     * @return the card info. available from the state.
     */
    protected String unGetState(int state) {
        int[] code =decode(state);

        return String.format("%5s-%2d%sAce %9s",code[3], code[1], code[2]==1? "+" : "-", code[0]);
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

    public static void main(String[] args) {
        int s = encode(4,16,true,-15);
        int[] decoded = decode(s);
        System.out.println(s);
    }
}

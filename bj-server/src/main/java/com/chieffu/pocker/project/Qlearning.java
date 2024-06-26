package com.chieffu.pocker.project;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * The Qlearning class
 * 1. Create a Qlearning object.
 * 2. Call Qlearning.train(...).
 * 3. Call Qlearning.test(...).
 * 4. ???
 * 5. Profit.
 */
public class Qlearning {


    protected double[][] q;
    protected int ACTIONS = 2; // [0] = stand, [1] = hit
    protected int STATES  = 698 + 1; // 698 = largest legal state # possible
                                          //       21 Ace 10 [in decimal] = 10101 1 1010 [in binary]);

    /**
     * Qlearning constructor
     */
    public Qlearning() {
        this.q = new double[STATES][ACTIONS];
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
     * Randomly play for #episodes. Return win %'s.
     * @param episodes Number of episodes (games) to play.
     * @return The win percentages as array of 2 doubles [w/o pushes, w/ pushes].
     */
    public double[] randomTest(int episodes) {
        if (episodes < 1) {
            throw new IllegalArgumentException("Illegal argument passed to Qlearning.randomTest()");
        }

        int wins = 0;
        int losses = 0;
        int evens = 0;

        for (int i = 0; i < episodes; i++) {
            Game game = new Game();

            do {
                takeAction(getActionRandom(), game);
            } while (!game.isOver());

            wins+=game.getWin();
            losses+=game.getLose();
            evens+=game.getEven();
        }

        return new double[] {wins / (double) (wins + losses), wins / (double) (wins+losses+evens)};
    }

    /**
     * Get the Q table.
     * @return The Q table.
     */
    public double[][] getQ() {
        return q;
    }


    /**
     * Pretty Print the Q table.
     */
    public void prettyPrintQ(boolean printValues) {
        double stand;
        double hit;

        System.out.println("Player -- Dealer -- Stand -- Hit -");

        for (int i = 0; i < STATES; i++) {
            stand = this.q[i][0];
            hit = this.q[i][1];
            if (stand != 0.0 && hit != 0.0) {
                if (i % 16 == 1) {
                    System.out.println();
                }
                System.out.println(String.format("%10s %8.3f %8.3f | %6s %7s", unGetState(i), stand, hit, stand>hit?"X":"-", stand>hit?"-":"X"));
            }
        }
        System.out.println();
    }

    // Private /////////////////////////////////////////////////////////////////

    /**
     * Get the current state.
     * State value = 5 bits for player hand value
     *             + 1 bit for player has Ace/no Ace
     *             + 4 bits for dealer exposed card value
     * @param game The current game.
     * @return The current state.
     */
    protected int getState(Game game) {
        int state = game.getDealerUpCardValue();
        state += (game.getPlayerHandMinValue() << 5);
        if (game.playerHasAce()) {
            state += 16;
        }

        return state;
    }
    public boolean shouldHit(int zCard,int xMinHand,boolean hasAce) {
        int state = zCard;
        state += (xMinHand << 5);
        if (hasAce) {
            state += 16;
        }

        return q[state][1]>q[state][0];
    }


    /**
     * Get card information from a provided state.
     * @param state The state to unget.
     * @return the card info. available from the state.
     */
    protected String unGetState(int state) {
        int dealer = state % 16;
        state >>= 4;
        boolean hasAce = state % 2 == 1;
        int player = state >> 1;

        String dealerOut;
        if (dealer == 1) {
            dealerOut = "Ace";
        } else if (dealer == 10) {
            dealerOut = "Face";
        } else {
            dealerOut = Integer.toString(dealer);
        }

        return String.format("%2d%sAce %9s", player, hasAce? "+" : "-", dealerOut);
    }

    /**
     * Get an epsilon-greedy action to take.
     * @param state The current state.
     * @param epsilon The chance of selecting a random action.
     * @return The an action to take.
     */
    protected int getAction(int state, double epsilon) {
        if (Math.random() < epsilon) { // return random action
            return getActionRandom();
        } else { // return index of max action available
            return getActionMax(state);
        }
    }

    /**
     * Get the best action to take.
     * @param state The current state.
     * @return The best action to take (random if they're equal).
     */
    protected int getActionMax(int state) {
        double stand = q[state][0];
        double hit   = q[state][1];

        if (stand == hit) {
            return getActionRandom();
        } else {
            return stand > hit ? 0 : 1;
        }
    }

    /**
     * Get a random action to take.
     * @return The random action to take.
     */
    protected int getActionRandom() {
        return (int) (Math.random() * ACTIONS);
    }

    /**
     * Get the best value of the available actions at this state.
     * @param state The state to observe.
     * @return The best action value.
     */
    protected double getActionMaxValue(int state) {
        return state > STATES ? Game.GameState.LOSE.value() : this.q[state][getActionMax(state)];
    }

    /**
     * Take the supplied action, get the reward.
     * @param action The action to take.
     * @param game The game in which to take the action.
     * @return The reward value of the action taken.
     */
    protected int takeAction(int action, Game game) {
        if (action == 0) {
            game.playerStand();
        } else {
            game.playerHit();
        }
        return game.getScore();
    }

    /**
     * Designed for a single hit/stay action decision in the GUI. Return win or lose.
     * @return If the A.I. won the game or not.
     */
    public int testGUI(Game game) {
        int state;
        int action;

        state = getState(game);

        action = getActionMax(state);
        takeAction(action, game);

        return action;
    }

    public void saveQ(String path) {
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
            out.writeObject(this.q);
            out.flush();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void loadQ(String path) {
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(path))) {
            q = (double[][]) in.readObject();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}

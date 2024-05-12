package com.chieffu.pocker.project;

import java.util.Arrays;

/**
 * This class handles the Qlearning object and launches the GUI.
 */
public class Main {

    public static void main(String[] args) {
        Qlearning algorithm = new Qlearning();
//        algorithm.loadQ("q-4.0.0");

        for(int i=0;i<20;i++) {
            int episodes = 3000000;
            double eta = 0.01;
            double gamma = 0.01;
            double epsilonStart = 1.0;
            double epsilonMin = 0.1;
            double epsilonDelta = 0.01;
            int epsilonEvery = (int) (episodes * epsilonDelta);
            algorithm.train(episodes, eta, gamma, epsilonStart, epsilonMin, epsilonDelta, epsilonEvery);
            algorithm.saveQ("q-1."+i);
            double[] winningQ = algorithm.test(2000);
            double[] winningR = algorithm.randomTest(2000);

            System.out.println("----- Wins -----");
            System.out.println("Qlearn    Random");
            System.out.printf("%5.2f%%    %5.2f%% (win-loss)%n", winningQ[0] * 100, winningR[0] * 100);
            System.out.printf("%5.2f%%    %5.2f%% (win-push-loss)%n%n", winningQ[1] * 100, winningR[1] * 100);
            algorithm.prettyPrintQ(false);

        }
//        algorithm.prettyPrintQ(false);
//        System.out.println("------------ Q-values ------------\n");

//        BlackJackGUI blackJackGUI = new BlackJackGUI(algorithm);
//        blackJackGUI.display();
    }
}

package com.chieffu.pocker.bj;


import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
public class Train {

    public static void main(String[] args) {
        train();
    }
    private static void test(){
        Qlearning algorithm = new Qlearning();
        algorithm.loadQ("qq-1.0.0");

    }
    private static void train() {
        Qlearning algorithm = new Qlearning();
        for(int i=0;i<20;i++) {
            int episodes = 1500000;
            double eta = 0.01;
            double gamma = 0.01;
            double epsilonStart = 1.0;
            double epsilonMin = 0.1;
            double epsilonDelta = 0.01;
            int epsilonEvery = (int) (episodes * epsilonDelta);
            algorithm.train(episodes, eta, gamma, epsilonStart, epsilonMin, epsilonDelta, epsilonEvery);
            algorithm.saveQ("q-1."+i);
            double[] winningQ = algorithm.test(2000);

            System.out.println("----- Wins -----");
            System.out.printf("%5.2f(result)    %5.2f%%(win+loss)   %5.2f%% (win+push+loss)%n", winningQ[2],winningQ[0] * 100, winningQ[1] * 100);
            algorithm.printQ();
        }
    }
}

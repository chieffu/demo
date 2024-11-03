package com.chieffu.pocker.bj;

import lombok.Data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Data
public class Qlearning {
    private Map<String, double[]> qMap = new ConcurrentHashMap<>();

    public void train(int episodes, double eta, double gamma, double epsilon, double epsilonMin, double epsilonDelta, int epsilonEvery) throws IllegalArgumentException {
        if (eta <= 0.0 || gamma < 0.0 || epsilon < 0.0 || epsilonMin < 0.0 || epsilonDelta < 0.0 ||
                eta > 1.0  || gamma > 1.0 || epsilon > 1.0 || epsilonMin > 1.0 || epsilonDelta > 1.0 ||
                epsilonEvery >= episodes || epsilonMin > epsilon || episodes <= 0 || epsilonEvery < 1) {
            throw new IllegalArgumentException("Illegal argument(s) passed to Qlearning.train()");
        }
        int plotEvery = episodes > 50 ? episodes / 50 : 1;
        double[] stateQ;
        double[] stateQNew;
        Action action;
        double reward;
//        List<Experience> replayBuffer = new ArrayList<>();

        System.out.printf("%nLearning Q-style    .    .    .    .    .%9d %n", episodes);

        for (int i = 0; i < episodes; i++) {
            Blackjack game = new Blackjack();
//            game.removeRandomCards();
            game.start();
            stateQ = getStateQ(game);
            do {
                action = getAction(game, stateQ, epsilon);
                reward = game.takeAction(action);
                stateQNew = getStateQ(game);
                stateQ[action.idx] += eta * (reward + gamma * getActionMaxValue(stateQNew) - stateQ[action.idx]);
                stateQ = stateQNew;
            } while (!game.isOver());

            // Update epsilon value periodically
            if ((i + 1) % epsilonEvery == 0) {
                if (epsilon > epsilonMin) {
                    epsilon -= epsilonDelta;
                }
                if (epsilon < epsilonMin) {
                    epsilon = epsilonMin;
                }
//                List<Experience> batch = getRandomBatch(replayBuffer, epsilonEvery);
//                for (Experience e : batch) {
//                    e.getState()[e.getAction().idx] +=
//                            eta * (e.getReward() + gamma * getActionMaxValue(e.getNewState()) - e.getState()[e.getAction().idx]);
//                }
            }

            // Update progress bar
            if ((i+1) % plotEvery == 0) {
                System.out.print('>');
            }
        }
        System.out.println("\n");
    }
    public void printQ(){
        List<String> keys = getQMap().keySet().stream().sorted().collect(Collectors.toList());
        for(String key:keys){
            double[] q = getQMap().get(key);
            String s = q[0]>q[1]?" X       -  ":" -       X  ";
            System.out.println(String.format("%24s   %7.4f   %7.4f  |  %s  ",key,q[0],q[1],s));
        }
    }
    public double[] test(int episodes) {
        if (episodes < 1) {
            throw new IllegalArgumentException("Illegal argument passed to Qlearning.test()");
        }

        double[] stateQ;
        Action action;
        int wins = 0;
        int losses = 0;
        int evens = 0;
        double totalReword = 0.0;

        for (int i = 0; i < episodes; i++) {
            Blackjack game = new Blackjack();
            stateQ = getStateQ(game);

            do {
                action = getMaxQAction(stateQ,game);
                totalReword+= takeAction(action, game);
                stateQ = getStateQ(game);
            } while (!game.isOver());
            wins+=game.getWin();
            losses+=game.getLose();
            evens+=game.getEven();
        }

        return new double[] {wins / (double) (wins + losses), wins / (double) (wins+losses+evens),totalReword};
    }


    private double takeAction(Action action, Blackjack game) {
       return game.takeAction(action);
    }

    private Action getAction(Blackjack game, double[] stateQ, double epsilon) {
        Action[] actions = game.getAvailableActions();
        if (Math.random() < epsilon) {
            return actions[(int)(Math.random()*actions.length)];
        } else {
            return getMaxQAction(stateQ, game);
        }
    }

    private Action getMaxQAction(double[] stateQ, Blackjack game) {
        Action[] actions = game.getAvailableActions();
        Action maxValueAction = actions[0];
        for(Action action: actions){
            if(stateQ[action.idx]> stateQ[maxValueAction.idx]){
                maxValueAction=action;
            }
        }
        return maxValueAction;
    }

    private double getActionMaxValue(double[] stateNew) {
        double max = stateNew[0];
        for(int i=1;i<stateNew.length;i++){
            if(stateNew[i]>max){
                max = stateNew[i];
            }
        }
        return max;
    }

    private double[] getStateQ(Blackjack game) {
        double[] status = game.getState();
        StringBuffer s = new StringBuffer();
        for (int i = 0; i < status.length; i++) {
            if(i!=0)s.append(" ");
            s.append((int)status[i]);
        }
        String name = s.toString();
        if(qMap.containsKey(name))
            return qMap.get(name);
        double[] state = new double[Action.values().length];
        qMap.put(name, state);
        return state;
    }

    public void saveQ(String path) {
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
            out.writeObject(this.qMap);
            out.flush();
            System.out.println("size:"+qMap.size());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void loadQ(InputStream inputStream){
        try(ObjectInputStream in = new ObjectInputStream(inputStream)){
            qMap = (Map<String, double[]>) in.readObject();
            System.out.println("size:"+qMap.size());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public void loadQ(String path) {
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(path))) {
            qMap = (Map<String, double[]>) in.readObject();
            System.out.println("size:"+qMap.size());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

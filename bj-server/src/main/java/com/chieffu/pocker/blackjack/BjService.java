package com.chieffu.pocker.blackjack;

import com.chieffu.pocker.Pocker;
import com.chieffu.pocker.util.ThreadSafeLRUCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class BjService {
    ThreadSafeLRUCache<String, BjTable> tables = new ThreadSafeLRUCache<>(10000);
    com.chieffu.pocker.project.Qlearning projectAlgorithm = initQlearning2();

    private Set<String> bjTables = Arrays.asList("151","168").stream().collect(Collectors.toSet());
    private com.chieffu.pocker.project.Qlearning initQlearning2() {
        com.chieffu.pocker.project.Qlearning algorithm = new com.chieffu.pocker.project.Qlearning();
        algorithm.loadQ("q-1.0.0");
        adjustQ(algorithm);
//        algorithm.loadQ("q.q");
        algorithm.prettyPrintQ(false);
//        double[] winningQ = algorithm.test(100000);
//        System.out.printf("%5.2f%%    %5.2f%% (win-loss)%n", winningQ[0]*100, winningQ[1]*100);

        return algorithm;
    }
    private void adjustQ(com.chieffu.pocker.project.Qlearning projectAlgorithm) {
        int state = 8;
        state += (16 << 5);
        state += 16;

        double[] q = projectAlgorithm.getQ()[state];
        double t = q[0];
        q[0]=q[1];
        q[1]=t;


        state = 8;
        state += (15 << 5);
        q = projectAlgorithm.getQ()[state];
        t = q[0];
        q[0]=q[1];
        q[1]=t;

        state = 8;
        state += (15 << 5);
        state += 16;
        q = projectAlgorithm.getQ()[state];
        t = q[0];
        q[0]=q[1];
        q[1]=t;

        state = 7;
        state += (15 << 5);
        q = projectAlgorithm.getQ()[state];
        t = q[0];
        q[0]=q[1];
        q[1]=t;


        state = 8;
        state += (14 << 5);
        q = projectAlgorithm.getQ()[state];
        t = q[0];
        q[0]=q[1];
        q[1]=t;

        state = 9;
        state += (14 << 5);
        q = projectAlgorithm.getQ()[state];
        t = q[0];
        q[0]=q[1];
        q[1]=t;

        state = 2;
        state += (13 << 5);
        q = projectAlgorithm.getQ()[state];
        t = q[0];
        q[0]=q[1];
        q[1]=t;

        state = 5;
        state += (12 << 5);
        state += 16;
        q = projectAlgorithm.getQ()[state];
        t = q[0];
        q[0]=q[1];
        q[1]=t;


        state = 10;
        state += (8 << 5);
        state += 16;
        q = projectAlgorithm.getQ()[state];
        t = q[0];
        q[0]=q[1];
        q[1]=t;

        state = 9;
        state += (8 << 5);
        state += 16;
        q = projectAlgorithm.getQ()[state];
        t = q[0];
        q[0]=q[1];
        q[1]=t;

        state = 2;
        state += (8 << 5);
        state += 16;
        q = projectAlgorithm.getQ()[state];
        t = q[0];
        q[0]=q[1];
        q[1]=t;

        state = 1;
        state += (8 << 5);
        state += 16;
        q = projectAlgorithm.getQ()[state];
        t = q[0];
        q[0]=q[1];

        state = 10;
        state += (9 << 5);
        state += 16;
        q = projectAlgorithm.getQ()[state];
        t = q[0];
        q[0]=q[1];
        q[1]=t;
    }
     public BjTable getTable(String tableId) {
        BjTable table=tables.get(tableId);
       return table!=null?table:createTable(tableId);
    }

    private BjTable createTable(String tableId) {
        BjTable table = new BjTable(tableId);
        tables.put(tableId, table);
        return table;
    }

    public BjRound getBjRound(String tableId, String roundId)  {
        BjTable table = getTable(tableId);
        if(table==null)return null;
        //throw new NotFoundException("桌号"+tableId+"找不到,roundId="+roundId);
        return table.getBjRound(roundId);
    }

    public BjAction pushGameStatus(String tableId, Integer roundNum, String roundId,Integer status){
        BjTable table = getTable(tableId);
        boolean updateStatus = table.updateStatus(roundNum, roundId, status);
        if (!isBjTable(tableId)||!updateStatus) return null;
        BjAction action = new BjAction();
        action.setTableId(tableId);
        action.setRoundId(roundId);
        // 0:开始， 100:开始下注，101:结束， 103:等待发牌， 113:等待决策 hit、miss、split、double，109:等待洗牌
        try {
            switch (status) {
                case 0:
                    action.setCommand("startRound");
                    break;
                case 101:
                    action.setCommand("finishRound");
                    break;
                case 103:
                    action.setCommand("doNothing");
                    break;
                case 100:
                    action.setCommand("bet");
                    action.setOdds(table.getCurrentShoe().odds());
                    break;

                case 113:
                    //决策
                    BjRound bjRound = getBjRound(tableId, roundId);
                    List<Pocker> players = bjRound.getPlayers().isEmpty() ? new ArrayList() : bjRound.getPlayers().get(0);
                    int[] xDots = Blackjack.dotsOfPocker(players);
                    int[] zDots = Blackjack.dotsOfPocker(bjRound.getBanker());
                    if (xDots[xDots.length - 1] >= 21) {
                        action.setCommand("doNothing");
                    } else {
                        double oneMoreCardWinRate = table.getCurrentShoe().oneMoreCardWinRate();
                        double currentWinRate = table.getCurrentShoe().getCurrentWinRate();
                        action.setWinRate(currentWinRate);
                        action.setOneMoreCardWinRate(oneMoreCardWinRate);
                        if (bjRound.getBanker().size() == 1 && bjRound.getPlayers().size() == 1 && bjRound.getPlayers().get(0).size() == 2
                                && oneMoreCardWinRate > 0.5) {
                            action.setCommand("double");
                        } else if (table.getCurrentShoe().shouldSplit()) {
                            action.setCommand("split");
                        } else {
                            boolean playerHasAce = players.stream().anyMatch(p -> p.getNum() == 1);
                            boolean shouldHit = projectAlgorithm.shouldHit(zDots[0], xDots[xDots.length - 1], playerHasAce);
                            if (shouldHit) {
                                action.setCommand("hit");
                            } else {
                                if (oneMoreCardWinRate > currentWinRate) {
                                    action.setCommand("hit");
                                } else {
                                    action.setCommand("stand");
                                }
                            }
                        }
                    }
                    break;
                default:
                    action.setCommand("doNothing");
            }
        } catch (NotFoundException e) {
            log.error(e.getMessage(), e);
            action.setErrorMessage(e.getMessage());
        }
        return action;
    }

    private boolean isBjTable(String tableId) {
        return bjTables.contains(tableId);
    }

    public List<String> getBjTables(){
        return new ArrayList<>(bjTables);
    }

    public void setBjTables(List<String> tables){
        bjTables = new HashSet<>(tables);
    }

    /**
     * 更新牌
     * @param tableId
     * @param roundId
     * @param bankCards
     * @param playsCards
     * @throws NotFoundException
     */
    public void updateCards(String tableId, String roundId, List<Integer> bankCards, List<List<Integer>> playsCards) throws NotFoundException {
        List<Pocker> bankPockerList = bankCards.stream().filter(n->n>0).map(n->Pocker.fromCard(n)).collect(Collectors.toList());
        List<List<Pocker>> playerPockerLists = playsCards.stream().map(l->l.stream().filter(n->n>0).map(n-> Pocker.fromCard(n)).collect(Collectors.toList())).collect(Collectors.toList());
        if(bankCards.size()==0&&playerPockerLists.stream().flatMap(l->l.stream()).count()==0){
            return;
        }
        getTable(tableId).updateCards(roundId, bankPockerList, playerPockerLists);
    }

    public void updateTableName(String tableId, String name){
        BjTable table = getTable(tableId);
        table.setName(name);
    }

}

package com.chieffu.pocker.blackjack;

import com.chieffu.pocker.Pocker;
import com.chieffu.pocker.project.Qlearning;
import com.chieffu.pocker.util.ThreadSafeLRUCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class BjService {
    ThreadSafeLRUCache<String, BjTable> tables = new ThreadSafeLRUCache<>(10000);
    com.chieffu.pocker.project.Qlearning projectAlgorithm ;

    private Set<String> bjTables = Arrays.asList("151","168").stream().collect(Collectors.toSet());

    private Set<String> lunpanTables = Arrays.asList("165","169","121","123").stream().collect(Collectors.toSet());

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
                            boolean shouldHit = getProjectAlgorithm().shouldHit(zDots[0], xDots[xDots.length - 1], playerHasAce);
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

    private Qlearning getProjectAlgorithm() {
        if(projectAlgorithm==null){
            projectAlgorithm =  initQlearning2();
        }
        return projectAlgorithm;
    }

    public boolean isBjTable(String tableId) {
        return bjTables.contains(tableId);
    }

    public boolean isLunpanTable(String tableId) {
        return lunpanTables.contains(tableId)||"401".equals(getTable(tableId).getType());
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
        List<Pocker> bankPockerList = bankCards.stream().filter(n->n>=0).map(n->Pocker.fromCard(n)).collect(Collectors.toList());
        List<List<Pocker>> playerPockerLists = playsCards.stream().map(l->l.stream().filter(n->n>=0).map(n-> Pocker.fromCard(n)).collect(Collectors.toList())).collect(Collectors.toList());
        if(bankCards.size()==0&&playerPockerLists.stream().flatMap(l->l.stream()).count()==0){
            return;
        }
        getTable(tableId).updateCards(roundId, bankPockerList, playerPockerLists);
    }

    public void updateTableName(String tableId, String name,String type){
        BjTable table = getTable(tableId);
        table.setName(name);
        table.setType(type);
    }

    public void updateRoadData(String tableId, List<String> roadData)  {
        BjTable table = getTable(tableId);
        BjShoe shoe = table.getCurrentShoe();
        synchronized(shoe) {
            Map<String, Integer> map = new HashMap() {
                {
                    this.put("000", 0);
                    this.put("111", 1);
                    this.put("222", 2);
                    this.put("313", 3);
                    this.put("421", 4);
                    this.put("512", 5);
                    this.put("623", 6);
                    this.put("711", 7);
                    this.put("822", 8);
                    this.put("913", 9);
                    this.put("A21", 10);
                    this.put("B12", 11);
                    this.put("C23", 12);
                    this.put("D14", 13);
                    this.put("E25", 14);
                    this.put("F16", 15);
                    this.put("G24", 16);
                    this.put("H15", 17);
                    this.put("I26", 18);
                    this.put("J34", 19);
                    this.put("K45", 20);
                    this.put("L36", 21);
                    this.put("M44", 22);
                    this.put("N35", 23);
                    this.put("O46", 24);
                    this.put("P37", 25);
                    this.put("Q48", 26);
                    this.put("R39", 27);
                    this.put("S47", 28);
                    this.put("T38", 29);
                    this.put("U49", 30);
                    this.put("V37", 31);
                    this.put("W48", 32);
                    this.put("X39", 33);
                    this.put("Y47", 34);
                    this.put("Z38", 35);
                    this.put("a49", 36);
                }
            };
            int matches = 0;
            if (shoe.getRoundMap().size() == 0) {
                long roundId = 0;
                for (int i = 0; i < roadData.size(); i++) {
                    BjRound newRound = new BjRound("" + (++roundId));
                    newRound.setBanker(Arrays.asList(Pocker.fromCard(map.get(roadData.get(i)))));
                    shoe.addRound(newRound);
                }
            } else {
                while (matches < roadData.size()) {
                    // 获取最近的 50 个 round
                    List<BjRound> rounds = shoe.getRoundList();
                    BjRound round = rounds.get(rounds.size() - 1 - matches);
                    long roundId = Long.parseLong(round.getRoundId());
                    int cnt = 0;
                    int k = roadData.size() - 1 - matches;
                    Pocker pocker = Pocker.fromCard(map.get(roadData.get(k)));
                    while (!round.getBanker().isEmpty() && !round.getBanker().get(0).equals(pocker)) {
                        cnt++;
                        k--;
                        if (k < 0) break;
                        pocker = Pocker.fromCard(map.get(roadData.get(k)));
                    }
                    // 找到i 位置的值，后面的填充
                    while (cnt > 0) {
                        BjRound newRound = new BjRound("" + (++roundId));
                        newRound.setBanker(Arrays.asList(Pocker.fromCard(map.get(roadData.get(++k)))));
                        shoe.addRound(newRound);
                        cnt--;
                    }
                    matches += cnt + 1;
                }
                List<BjRound> rounds = shoe.getRoundList();
                for (int i = 0; i < roadData.size(); i++) {
                    BjRound round = rounds.get(rounds.size() - 1 - i);
                    if (round.getBanker().isEmpty()) {
                        round.setBanker(Arrays.asList(Pocker.fromCard(map.get(roadData.get(roadData.size() - 1 - i)))));
                    }
                }
            }
        }
    }

}

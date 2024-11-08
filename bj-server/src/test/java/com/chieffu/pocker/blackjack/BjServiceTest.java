
package com.chieffu.pocker.blackjack;

import com.chieffu.pocker.Pocker;
import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class BjServiceTest extends TestCase {

    BjService bjService = new BjService();
    String tableId = "12345";
    private Map<String, Integer> map = new HashMap() {
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
    @Test
    public void testUpdateRoadData_7Missing3Match() {
        // 准备测试数据

        List<String> roadData = Arrays.asList("000", "111", "222", "313", "421", "512", "623", "711", "822", "913");

        // 创建模拟的 BjTable 和 BjShoe
        BjTable table = createTableWithRounds(Arrays.asList(
                createRound("10", Pocker.fromCard(7)),
                createRound("20", Pocker.fromCard(8)),
                createRound("30", Pocker.fromCard(9)),
                createRound("40", Pocker.fromCard(10)),
                createRound("50", Pocker.fromCard(11)),
                createRound("60", Pocker.fromCard(12)),
                createRound("70", Pocker.fromCard(13)),
                createRound("80", Pocker.fromCard(7)),
                createRound("90", Pocker.fromCard(8)),
                createRound("100", Pocker.fromCard(9))
        ));

        // 调用方法
        checkResults(tableId, roadData, table);
    }

    @Test
    public void testUpdateRoadData_3Match3Missing3Match() {
        // 准备测试数据
        String tableId = "12345";
        List<String> roadData = Arrays.asList("000", "111", "222", "313", "421", "512", "711", "822", "913");

        // 创建模拟的 BjTable 和 BjShoe
        BjTable table = createTableWithRounds(Arrays.asList(
                createRound("10", Pocker.fromCard(0)),
                createRound("20", Pocker.fromCard(1)),
                createRound("30", Pocker.fromCard(2)),
                createRound("40", Pocker.fromCard(7)),
                createRound("50", Pocker.fromCard(8)),
                createRound("60", Pocker.fromCard(9))
        ));

        // 调用方法
        checkResults(tableId, roadData, table);
    }

    @Test
    public void testUpdateRoadData_3Match2Missing2Match2Missing1Match() {
        // 准备测试数据
        String tableId = "12345";
        List<String> roadData = Arrays.asList("000", "111", "222","222","222", "421", "512", "711","711", "913");

        // 创建模拟的 BjTable 和 BjShoe
        BjTable table = createTableWithRounds(Arrays.asList(
                createRound("10", Pocker.fromCard(0)),
                createRound("20", Pocker.fromCard(1)),
                createRound("30", Pocker.fromCard(2)),
                createRound("40", Pocker.fromCard(4)),
                createRound("50", Pocker.fromCard(5)),
                createRound("70", Pocker.fromCard(9))
        ));

        // 调用方法
        checkResults(tableId, roadData, table);
    }

    @Test
    public void testUpdateRoadData_5Match5Missing() {
        // 准备测试数据
        String tableId = "12345";
        List<String> roadData = Arrays.asList("000", "111", "222", "313", "421", "512", "623", "711", "822", "913");

        // 创建模拟的 BjTable 和 BjShoe
        BjTable table = createTableWithRounds(Arrays.asList(
                createRound("10", Pocker.fromCard(0)),
                createRound("20", Pocker.fromCard(1)),
                createRound("30", Pocker.fromCard(2)),
                createRound("40", Pocker.fromCard(3)),
                createRound("50", Pocker.fromCard(4))
        ));

        // 调用方法
        checkResults(tableId, roadData, table);
    }

    @Test
    public void testUpdateRoadData_3Missing4Match3Missing() {
        // 准备测试数据
        String tableId = "12345";
        List<String> roadData = Arrays.asList("000", "111", "222", "313", "421", "512", "623", "711", "822", "913");

        // 创建模拟的 BjTable 和 BjShoe
        BjTable table = createTableWithRounds(Arrays.asList(
                createRound("10", Pocker.fromCard(7)),
                createRound("20", Pocker.fromCard(8)),
                createRound("30", Pocker.fromCard(9)),
                createRound("40", Pocker.fromCard(3)),
                createRound("50", Pocker.fromCard(4)),
                createRound("60", Pocker.fromCard(5)),
                createRound("70", Pocker.fromCard(6)),
                createRound("80", Pocker.fromCard(10)),
                createRound("90", Pocker.fromCard(11)),
                createRound("100", Pocker.fromCard(12))
        ));

        // 调用方法
        checkResults(tableId, roadData, table);
    }

    @Test
    public void testUpdateRoadData_2Missing3Match1Missing2Match1Missing() {
        // 准备测试数据
        String tableId = "12345";
        List<String> roadData = Arrays.asList("000", "111", "222", "313", "421", "421","421","512", "623", "711", "822", "913");

        // 创建模拟的 BjTable 和 BjShoe
        BjTable table = createTableWithRounds(Arrays.asList(
                createRound("10", Pocker.fromCard(7)),
                createRound("20", Pocker.fromCard(8)),
                createRound("30", Pocker.fromCard(0)),
                createRound("40", Pocker.fromCard(1)),
                createRound("50", Pocker.fromCard(2)),
                createRound("60", Pocker.fromCard(3)),
                createRound("70", Pocker.fromCard(4)),
                createRound("90", Pocker.fromCard(5)),
                createRound("100", Pocker.fromCard(6))
        ));

        checkResults(tableId, roadData, table);
    }


    @Test
    public void testEmptyRounds() {
        // 准备测试数据
        String tableId = "12345";
        List<String> roadData = Arrays.asList("000", "111", "222", "313", "421", "421","421","512", "623", "711", "822", "913");

        // 创建模拟的 BjTable 和 BjShoe
        BjTable table = createTableWithRounds(Arrays.asList(
                 ));

        checkResults(tableId, roadData, table);
    }


    private void checkResults(String tableId, List<String> roadData, BjTable table) {
        // 调用方法
        BjShoe shoe = bjService.getTable(tableId).getCurrentShoe();
        log.info("Source Rounds："+shoe.getRoundList().subList(Math.max(0,table.getCurrentShoe().getRoundList().size()-Math.max(roadData.size(),50)),table.getCurrentShoe().getRoundList().size()).stream().map(r->r.getBanker().isEmpty()?r.getRoundId()+":[]":r.getRoundId()+":"+r.getBanker().get(0).toString()).collect(Collectors.toList()));
        log.info("roadData :"+roadData.stream().map(rd->Pocker.fromCard(map.get(rd))).collect(Collectors.toList()));

        bjService.updateRoadData(tableId, roadData);
        log.info("Result Rounds："+shoe.getRoundList().subList(Math.max(0,table.getCurrentShoe().getRoundList().size()-Math.max(roadData.size(),50)),table.getCurrentShoe().getRoundList().size()).stream().map(r->r.getBanker().isEmpty()?r.getRoundId()+":[]":r.getRoundId()+":"+r.getBanker().get(0).toString()).collect(Collectors.toList()));

        // 验证结果
        List<BjRound> rounds = table.getCurrentShoe().getRoundList();
        for (int i = 0; i < roadData.size(); i++) {
            String roadValue = roadData.get(roadData.size()-1-i);
            Integer mapValue = map.get(roadValue);
            Pocker expectedPocker = Pocker.fromCard(mapValue);
            assertEquals(expectedPocker, rounds.get(rounds.size()-1-i).getBanker().get(0));
        }
     }

    private BjTable createTableWithRounds(List<BjRound> rounds) {
        BjTable table = bjService.getTable(tableId);
        table.setCurrentShoe(new BjShoe(tableId));
        BjShoe shoe = table.getCurrentShoe();
        rounds.stream().forEach(r-> shoe.addRound(r));
        return table;
    }

    private BjRound createRound(String roundId, Pocker pocker) {
        BjRound round = new BjRound(roundId);
        round.setBanker(Arrays.asList(pocker));
        return round;
    }




}
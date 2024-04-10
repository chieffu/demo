package com.chieffu.pocker.blackjack;

import com.chieffu.pocker.Pocker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bj")
@Slf4j
public class BjController {

    @Autowired
    BjService service;

    @GetMapping("/types")
    public List<BjTypeEnum> bjTypes(){
        return Arrays.asList(BjTypeEnum.values());
    }

    @GetMapping("/table/{tableId}/{roundId}")
    public BjRoundVO getRound(@PathVariable("tableId")String tableId,@PathVariable("roundId")String roundId ) throws NotFoundException {
        BjRoundVO vo = new BjRoundVO();
        BjRound round = service.getBjRound(tableId,roundId);
        vo.setRoundId(round.getRoundId());
        vo.setTableId(tableId);
        vo.setBanker(round.getBanker().stream().map(Pocker::toString).collect(Collectors.toList()));
        vo.setPlayers(round.getPlayers().stream().map(p->p.stream().map(Pocker::toString).collect(Collectors.toList())).collect(Collectors.toList()));
        return vo;
    }
    /**
     * 查询当前赢的概率
     * @return
     */
    @GetMapping("/table/{tableId}/odds")
    public Map<BjTypeEnum,String> queryOdds(@PathVariable("tableId")String tableId){
        BjTable bjTable = service.getTable(tableId);

        double luckyQueenExpectation = bjTable.getBlackjack().luckyQueenExpectation(1000, 125, 19, 9, 4);
        double hotThree = bjTable.getBlackjack().hotThreeExpectation(100, 20, 4, 2, 1);
        double luckyThree = bjTable.getBlackjack().luckThreeExpectation(100, 40, 30, 10, 5);
        double pair = bjTable.getBlackjack().pairExpectation(25, 8);
        double base = bjTable.getBlackjack().xWinExpectation();
        double bloom = bjTable.getBlackjack().bloomExpectation(1, 2, 9, 50, 100, 250);
        Map<BjTypeEnum,String> result = new HashMap<>();

        result.put(BjTypeEnum.LUCKY_QUEEN,String.format("%.4f",luckyQueenExpectation));
        result.put(BjTypeEnum.HOT_THREE,String.format("%.4f",hotThree));
        result.put(BjTypeEnum.LUCKY_THREE,String.format("%.4f",luckyThree));
        result.put(BjTypeEnum.PAIR,String.format("%.4f",pair));
        result.put(BjTypeEnum.BASE_PLAY,String.format("%.4f",base));
        result.put(BjTypeEnum.BLOOM,String.format("%.4f",bloom));
        return result;
    }

    /**
     *
     * @param cards
     * @return
     */
    @PutMapping("/table/{tableId}/remove-cards")
    public Map<String,Integer> removeCards(@PathVariable("tableId") String tableId,@RequestBody List<Integer> cards) throws NotFoundException {
        log.info("remove cards of table[{}]  : {}",tableId,cards);
        BjTable table = service.getTable(tableId);
        Blackjack blackjack = table.getBlackjack();
        for (Integer card : cards) {
            Pocker p = Pocker.fromCard(card);
            blackjack.removePocker(p);
        }
        Map<String, Integer> remainingPockerAsStrings = blackjack.getRemainingPocker()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().toString(),
                        Map.Entry::getValue
                ));

        return remainingPockerAsStrings;

    }

    /**
     *
     * @return
     */
    @PutMapping("/table/{tableId}/update-cards")
    public Map<String,Integer> updateCards(@PathVariable("tableId") String tableId,@RequestBody BjRoundDTO roundDTO) throws NotFoundException {
        log.info("update cards of table[{}]  : {}",tableId,roundDTO);
        BjTable table = service.getTable(tableId);
        Blackjack blackjack = table.getBlackjack();
        service.updateCards(tableId,roundDTO.getRoundId(),roundDTO.getBanker(),roundDTO.getPlayers());
        Map<String, Integer> remainingPockerAsStrings = blackjack.getRemainingPocker()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().toString(),
                        Map.Entry::getValue
                ));

        return remainingPockerAsStrings;
    }

    /**
     * 查询再加一次牌后获胜的概率
     * @return
     */
    @PostMapping("/table/{tableId}/win-rate-one-more")
    public Double calculateOneMoreCardWinRate(@PathVariable("tableId")String tableId,@RequestBody Play play ){
        BjTable table = service.getTable(tableId);

        List<Integer> xCards = play.getPlayer().stream().map(c->Blackjack.dot(Pocker.fromCard(c))).collect(Collectors.toList());
        Stage xStage = Stage.getXStage(xCards);

        Integer zCard =  Blackjack.dot(Pocker.fromCard(play.getBanker().get(0)));
        Map<Integer, Double> zRateMap = Stage.zRate(table.getBlackjack().getPai(), zCard);
        double oneMoreCardWinRate = xStage==null?0:Stage.xWinRate(zRateMap, xStage.oneMoreCardRateMap(table.getBlackjack().getPai()));
        return oneMoreCardWinRate;
    }

    @PostMapping("/table/{tableId}/win-rate")
    public double calculateWinRate(@PathVariable("tableId")String tableId,@RequestBody Play play ){
        Blackjack bj = service.getTable(tableId).getBlackjack();
        List<Integer> xCards = play.getPlayer().stream().map(c->Blackjack.dot(Pocker.fromCard(c))).collect(Collectors.toList());
        int[] dot = Blackjack.dots(xCards);
        Integer zCard =  Blackjack.dot(Pocker.fromCard(play.getBanker().get(0)));
        Map<Integer, Double> zRateMap = Stage.zRate(bj.getPai(), zCard);

        double currentWinRate =Stage.getCurrentWinRate(dot[dot.length - 1], zRateMap);
        return currentWinRate;
    }


}

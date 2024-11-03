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

    @GetMapping("/table/{tableId}/latestShoe")
    public String getLatestCards(@PathVariable("tableId")String tableId) throws NotFoundException {
        BjShoe shoe = service.getTable(tableId).getCurrentShoe();
        List<BjRound> rounds = shoe.getRoundList();
        StringBuffer sb = new StringBuffer();
        for(BjRound r:rounds){
            sb.append(String.format("%d、 庄:%s、闲:%s",r.getShoeNum(),r.getBanker(),r.getPlayers())).append("\n");
        }
        return sb.toString();
    }
    /**
     * 查询当前赢的概率
     * @return
     */
    @GetMapping("/table/{tableId}/odds")
    public Map<BjTypeEnum,Double> queryOdds(@PathVariable("tableId")String tableId) throws NotFoundException {
        return service.getTable(tableId).getCurrentShoe().odds();

    }

    /**
     * 查询再加一次牌后获胜的概率
     * @return
     */
    @GetMapping("/table/{tableId}/win-rate-one-more")
    public Double calculateOneMoreCardWinRate(@PathVariable("tableId")String tableId ) throws NotFoundException {
        return service.getTable(tableId).getCurrentShoe().oneMoreCardWinRate();
    }

    @GetMapping("/table/{tableId}/win-rate")
    public double calculateWinRate(@PathVariable("tableId")String tableId ) throws NotFoundException {
      return  service.getTable(tableId).getCurrentShoe().getCurrentWinRate();
    }


    @GetMapping("tables")
    public List<String> getBjTables(){
        return service.getBjTables();
    }

    @PutMapping("tables")
    public void updateBjTables(@RequestBody List<String> tables){
        service.setBjTables(tables);
    }
}

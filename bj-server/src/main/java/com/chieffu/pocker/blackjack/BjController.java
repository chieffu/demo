package com.chieffu.pocker.blackjack;

import com.chieffu.pocker.Pocker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;
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
        sb.append("<pre>");
        sb.append(service.getTable(tableId).getName());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sb.append(String.format("：\t%-20s  -  %-20s\n",format.format(shoe.getStartTime()) ,format.format(shoe.getEndTime()==null?new Date():shoe.getEndTime())));
        for(BjRound r:rounds){
            sb.append(String.format("\t%d\t庄:%-20s\t闲:%-20s\t%-15s\t%-5s\n",r.getShoeNum(),r.getBanker().toString(),r.getPlayers().toString(),r.getRoundId(), r.getStatus()));
        }
        sb.append("</pre>");

        return sb.toString();
    }

    @GetMapping("/table/{tableId}/statistic")
    public String getStatistic(@PathVariable("tableId")String tableId) throws NotFoundException {
        BjShoe shoe = service.getTable(tableId).getCurrentShoe();
        List<BjRound> rounds = shoe.getRoundList();
        Map<Pocker,Integer> statistics = new TreeMap<>();
        for(BjRound r:rounds){
            r.getBanker().stream().forEach(p->statistics.put(p,statistics.getOrDefault(p,0)+1));
            r.getPlayers().stream().flatMap(players->players.stream()).forEach(p->statistics.put(p,statistics.getOrDefault(p,0)+1));
        }
        StringBuffer sb = new StringBuffer();
        sb.append("<pre>");
        sb.append(service.getTable(tableId).getName());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sb.append(String.format("：\t%-20s  -  %-20s\n",format.format(shoe.getStartTime()) ,format.format(shoe.getEndTime()==null?new Date():shoe.getEndTime())));
        statistics.forEach((p,n)->sb.append(String.format("\t%-20s\t%-5d\n",p.toString(),n)));
        sb.append("</pre>");
        return sb.toString();
    }

    @GetMapping("/table/{tableId}/cards")
    public String getCards(@PathVariable("tableId")String tableId) throws NotFoundException {
        List<Integer> keys = service.getTable(tableId).getShoes().keys();
        StringBuffer sb = new StringBuffer();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sb.append("<pre>");
        sb.append(service.getTable(tableId).getName());
        for(Integer key:keys) {
            sb.append("SHOE:").append(key).append("\t");
            BjShoe shoe = service.getTable(tableId).getShoes().get(key);
            sb.append(String.format("\t%-20s  -  %-20s \n",format.format(shoe.getStartTime()) ,format.format(shoe.getEndTime()==null?new Date():shoe.getEndTime())));
            List<BjRound> rounds = shoe.getRoundList();
            for (BjRound r : rounds) {
                sb.append(String.format("\t%d\t庄:%-20s\t闲:%-20s\t%-15s\t%-5s\n",r.getShoeNum(),r.getBanker(),r.getPlayers(),r.getRoundId(), r.getStatus()));
            }
        }
        sb.append("</pre>");
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

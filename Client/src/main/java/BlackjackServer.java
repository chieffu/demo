
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlackjackServer {


    public String getServerHost(){
        return ConfigUtil.getSetting("server.host","http://localhost:8088");

    }

    /**
     * 查 下注 数学期望
     * @param tableId
     * @return
     */
    public Map<String,Double> getExpectations(String tableId){
        String url = getServerHost()+"/api/bj/table/"+tableId+"/odds";
        String content = HttpUtil.sendHttpGet(url);
        return JSON.parseObject(content, new TypeReference<Map<String,Double>>() {});
    }

    /**
     * 查所有下注类型
     * @return
     */
    public List<String> getBetTypes(){
        String url = getServerHost()+"/api/bj/table/types";
        String content = HttpUtil.sendHttpGet(url);
        return JSON.parseObject(content, new TypeReference<List<String>>() {});

    }

    public Round getRound(String tableId,String roundId){
        String url = getServerHost()+"/table/{tableId}/{roundId}";
        String content = HttpUtil.sendHttpGet(url);
        return JSON.parseObject(content, new TypeReference<Round>() {});
    }

    public Map<String,Integer> removeCards(String tableId,List<Integer> cards){
        String url = getServerHost()+"/api/bj/table/"+tableId+"/remove-cards";
        String response = HttpUtil.sendHttpPost(url,cards);
        return JSON.parseObject(response, new TypeReference<Map<String,Integer>>() {});
    }

    public Map<String,Integer> updateCards(String tableId,String roundId,List<Integer> bankerCards,List<List<Integer>> playersCards){
        String url = getServerHost()+"/api/bj/table/"+tableId+"/update-cards";

        Map<String,Object> data = new HashMap<>();
        data.put("roundId",roundId);
        data.put("banker",bankerCards);
        data.put("players",playersCards);

        String response = HttpUtil.sendHttpPut(url,data);
       return JSON.parseObject(response, new TypeReference<Map<String,Integer>>() {});
    }

    public Double getCurrentWinRate(String tableId,List<Integer> playerCards,Integer bankerCard){
        String url = getServerHost()+"/api/bj/table/"+tableId+"/win-rate";
        Map<String,Object> data = new HashMap<>();
        data.put("banker", Arrays.asList(bankerCard));
        data.put("players",playerCards);
        String response = HttpUtil.sendHttpPost(url,data);
        return Double.valueOf(response);
    }

    public Double getOneMoreWinRate(String tableId,List<Integer> playerCards,Integer bankerCard){
        String url = getServerHost()+"/api/bj/table/"+tableId+"/win-rate-one-more";
        Map<String,Object> data = new HashMap<>();
        data.put("banker", Arrays.asList(bankerCard));
        data.put("players",playerCards);
        String response = HttpUtil.sendHttpPost(url,data);
        return Double.valueOf(response);
    }

    //-------------------------------------------------------------------------------------------------


}

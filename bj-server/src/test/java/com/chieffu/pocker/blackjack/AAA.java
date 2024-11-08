package com.chieffu.pocker.blackjack;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.*;

public class AAA {
    public static void main(String[] args) {
        String json = "{\"a49\":36,\"000\":0,\"N35\":23,\"313\":3,\"A21\":10,\"H15\":17,\"E25\":14,\"C23\":12,\"M44\":22,\"623\":6,\"421\":4,\"X39\":33,\"913\":9,\"G24\":16,\"R39\":27,\"Q48\":26,\"F16\":15,\"111\":1,\"J34\":19,\"P37\":25,\"O46\":24,\"T38\":29,\"222\":2,\"U49\":30,\"K45\":20,\"512\":5,\"V37\":31,\"W48\":32,\"711\":7,\"313\":3,\"D14\":13,\"L36\":21,\"913\":9,\"B12\":11,\"711\":7,\"U49\":30,\"a49\":36,\"822\":8,\"Y47\":34,\"Z38\":35,\"A21\":10,\"V37\":31,\"Q48\":26,\"E25\":14,\"R39\":27,\"X39\":33,\"C23\":12,\"111\":1,\"G24\":16,\"J34\":19,\"I26\":18,\"F16\":15,\"H15\":17,\"S47\":28,\"W48\":32}";
        JSONObject jsonObject = JSONObject.parseObject(json);
        Map<Integer,String> map = new TreeMap<>();
        Map<String,Integer> map1 = new TreeMap<>();
        jsonObject.forEach((k, v) ->{ map.put(Integer.parseInt(v.toString()),k);map1.put(k,Integer.parseInt(v.toString()));});
        map.keySet().forEach(k->{System.out.println(k+"\t:\t"+map.get(k));});
        System.out.println(JSON.toJSONString(map));
        System.out.println(JSON.toJSONString(map1));
    }
}

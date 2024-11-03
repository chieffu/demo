package com.chieffu.pocker.blackjack.allbet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.chieffu.pocker.blackjack.BjAction;
import com.chieffu.pocker.blackjack.BjService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AllBetWebSocketHandler extends AbstractWebSocketHandler {

    private final Set<WebSocketSession> connectedSessions = new CopyOnWriteArraySet<>();

    @Autowired
    private BjService bjService;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
       JSONObject json = JSON.parseObject(message.getPayload());
       JSONObject p = json.getJSONObject("p");
       if(p!=null){
           String c = p.getString("c");
           JSONObject data = p.getJSONObject("p");
           if(c==null||data==null)return;
           BjAction action = dispatch(c,data);
           if(action!=null){
               session.sendMessage(new TextMessage(JSON.toJSONString(action)));
           }
           return;
       }
    }

    private BjAction dispatch(String action, JSONObject data) {
        try {
            switch (action) {
                case "pushGameStatus":
                    JSONArray A = data.getJSONArray("A");
                    if (A != null) {
                        for (int i = 0; i < A.size(); i++) {
                            JSONObject obj = A.getJSONObject(i);
                            String tableId = obj.getString("AA");
                            Integer num = obj.getInteger("BB");
                            String roundId = obj.getString("CC");
                            Integer status = obj.getInteger("DD");
                            return bjService.pushGameStatus(tableId, num, roundId, status);
                        }
                    }
                    break;
                case "getCountDown": // {"c":"getCountDown","p":{"A":0,"B":"","C":[{"AA":35,"BB":"451122007","CC":15,"DD":13}]}
//                    JSONArray c = data.getJSONArray("C");
//                    for (int i = 0; i < c.size(); i++) {
//                        JSONObject o = c.getJSONObject(i);
//                        bjService.getBjRound(o.getString("AA"), o.getString("BB"));
//                    }
                    break;
                case "pushRawCards":
                    log.info("<< {} {}", action, data);
                    // {"c":"pushRawCards","p":{"E":"451122007","A":35,"B":[["-2","-2"],["201","-2"]]}}
                    // {"c":"pushRawCards","p":{"E":"451122007","A":35,"B":[["-2","-2"],["201","108"]]}}
                    // {"c":"pushRawCards","p":{"E":"451122007","A":35,"B":[["310","-2"],["201","108"]]}}
                    // {"c":"pushRawCards","p":{"E":"451122007","A":35,"B":[["310","407"],["201","108"]]}
                    String roundId = data.getString("E");
                    String tableId = data.getString("A");
                    JSONArray B = data.getJSONArray("B");

                    if (B == null || B.size() < 2) {
                        return null;
                    }

                    List<Integer> bankCard = B.getJSONArray(0).toJavaList(Integer.class).stream()
                            .filter(i -> i > 0)
                            .collect(Collectors.toList());

                    List<List<Integer>> playCards = new ArrayList<>();
                    for (int i = 1; i < B.size(); i++) {
                        JSONArray obj = B.getJSONArray(i);
                        List<Integer> playCard = new ArrayList<>();
                        for (int j = 0; j < obj.size(); j++) {
                            Integer card = obj.getInteger(j);
                            if (card > 0) {
                                playCard.add(card);
                            }
                        }
                        if (!playCard.isEmpty()) {
                            playCards.add(playCard);
                        }
                    }

                    bjService.updateCards(tableId, roundId, bankCard, playCards);
                    break;
                case "getBriefGameTableList":
                    //{"c":"getBriefGameTableList","p":{"D":11,"A":0,"B":"","C":[{"AA":11,"BB":"B202","CC":101,"JJ":100,"MM":"468190283","FF":103},{"AA":73,"BB":"C003","CC":104,"JJ":100,"MM":"468190301","FF":100},{"AA":146,"BB":"B503","CC":101,"JJ":102,"MM":"468190298","FF":100},{"AA":161,"BB":"V911","CC":111,"JJ":102,"MM":"","FF":104},{"AA":56,"BB":"B302","CC":101,"JJ":103,"MM":"468190287","FF":100},{"AA":36,"BB":"Q002","CC":103,"JJ":105,"MM":"468190292","FF":103},{"AA":3,"BB":"B003","CC":101,"JJ":105,"MM":"468190305","FF":100},{"AA":4,"BB":"B004","CC":101,"JJ":105,"MM":"468190281","FF":103},{"AA":72,"BB":"C001","CC":104,"JJ":105,"MM":"468190257","FF":103},{"AA":158,"BB":"B506","CC":101,"JJ":108,"MM":"468190273","FF":103},{"AA":35,"BB":"Q001","CC":103,"JJ":0,"MM":"468190307","FF":100},{"AA":139,"BB":"F001","CC":202,"JJ":0,"MM":"468190309","FF":100},{"AA":141,"BB":"CW001","CC":703,"JJ":0,"MM":"468190316","FF":100},{"AA":144,"BB":"B501","CC":101,"JJ":0,"MM":"468190282","FF":103},{"AA":145,"BB":"B502","CC":101,"JJ":0,"MM":"468190294","FF":100},{"AA":148,"BB":"D501","CC":301,"JJ":0,"MM":"468190296","FF":103},{"AA":151,"BB":"BJ501","CC":704,"JJ":0,"MM":"468190254","FF":103},{"AA":154,"BB":"V901","CC":111,"JJ":0,"MM":"468190302","FF":103},{"AA":156,"BB":"B504","CC":101,"JJ":0,"MM":"","FF":104},{"AA":157,"BB":"B505","CC":101,"JJ":0,"MM":"","FF":102},{"AA":159,"BB":"V902","CC":111,"JJ":0,"MM":"468190312","FF":100},{"AA":162,"BB":"V912","CC":111,"JJ":0,"MM":"468190280","FF":103},{"AA":165,"BB":"R002","CC":401,"JJ":0,"MM":"468190288","FF":100},{"AA":168,"BB":"BJ001","CC":704,"JJ":0,"MM":"468190286","FF":103},{"AA":37,"BB":"Q003","CC":103,"JJ":0,"MM":"468190314","FF":100},{"AA":45,"BB":"Q202","CC":103,"JJ":0,"MM":"","FF":102},{"AA":46,"BB":"Q203","CC":103,"JJ":0,"MM":"468190315","FF":100},{"AA":1,"BB":"B001","CC":101,"JJ":0,"MM":"468190308","FF":100},{"AA":2,"BB":"B002","CC":101,"JJ":0,"MM":"468190299","FF":100},{"AA":5,"BB":"B005","CC":101,"JJ":0,"MM":"468190291","FF":100},{"AA":10,"BB":"B201","CC":101,"JJ":0,"MM":"","FF":104},{"AA":24,"BB":"IB001","CC":110,"JJ":0,"MM":"468190285","FF":103},{"AA":25,"BB":"IB002","CC":110,"JJ":0,"MM":"468190289","FF":100},{"AA":30,"BB":"B018","CC":101,"JJ":0,"MM":"","FF":102},{"AA":31,"BB":"B019","CC":101,"JJ":0,"MM":"468190279","FF":100},{"AA":32,"BB":"B219","CC":101,"JJ":0,"MM":"","FF":102},{"AA":55,"BB":"B301","CC":101,"JJ":0,"MM":"","FF":104},{"AA":57,"BB":"B303","CC":101,"JJ":0,"MM":"468190304","FF":100},{"AA":58,"BB":"B304","CC":101,"JJ":0,"MM":"468190310","FF":100},{"AA":60,"BB":"D001","CC":301,"JJ":0,"MM":"468190293","FF":103},{"AA":65,"BB":"D201","CC":301,"JJ":0,"MM":"468190313","FF":100},{"AA":74,"BB":"C002","CC":104,"JJ":0,"MM":"468190295","FF":100},{"AA":75,"BB":"C201","CC":104,"JJ":0,"MM":"468190311","FF":100},{"AA":100,"BB":"BB001","CC":801,"JJ":0,"MM":"468190317","FF":100},{"AA":101,"BB":"BB002","CC":801,"JJ":0,"MM":"468190284","FF":100},{"AA":102,"BB":"BB201","CC":801,"JJ":0,"MM":"","FF":104},{"AA":106,"BB":"P001","CC":501,"JJ":0,"MM":"468190306","FF":100},{"AA":108,"BB":"P201","CC":501,"JJ":0,"MM":"468190267","FF":103},{"AA":112,"BB":"W001","CC":901,"JJ":0,"MM":"468190303","FF":100},{"AA":113,"BB":"W002","CC":901,"JJ":0,"MM":"468190297","FF":100},{"AA":119,"BB":"S201","CC":201,"JJ":0,"MM":"468190275","FF":103},{"AA":121,"BB":"R001","CC":401,"JJ":0,"MM":"468190300","FF":100},{"AA":123,"BB":"R201","CC":401,"JJ":0,"MM":"468190290","FF":100},{"AA":127,"BB":"T201","CC":702,"JJ":0,"MM":"468190232","FF":103},{"AA":130,"BB":"AB001","CC":602,"JJ":0,"MM":"468190253","FF":103}]}}
                    JSONArray tabelList = data.getJSONArray("C");
                    for (int i = 0; i <tabelList.size() ; i++) {
                        JSONObject tableObj = tabelList.getJSONObject(i);
                        String theTableId = tableObj.getString("AA");
                        String theTableName = tableObj.getString("BB");
//                        String type = tableObj.getString("CC"); //704:blackjack  //101:baccarat
//                        String theRoundId= tableObj.getString("MM");
                        bjService.updateTableName(theTableId, theTableName);
                    }
                    break;
                // ... 其他 case 分支

                default:
                    // log.info("-- {} {}", action, data);
            }
        } catch (JSONException e) {
            log.error("Error while processing JSON data for action '{}': {}", action, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error while dispatching action '{}': {}", action, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        connectedSessions.add(session);
        log.info("WebSocket connection established: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        connectedSessions.remove(session);
        log.info("WebSocket connection closed: " + session.getId());
    }

    public void sendMessageToSession(String message) {
        connectedSessions.forEach(session->{
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                log.error(e.getMessage(),e);
            }
        });
    }
}



package com.chieffu.pocker.blackjack.allbet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.chieffu.pocker.blackjack.BjService;
import com.chieffu.pocker.blackjack.NotFoundException;
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
           dispatch(c,data);
           return;
       }
    }

    private void dispatch(String action, JSONObject data) {
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
                            Integer DD = obj.getInteger("DD");
                            bjService.pushGameStatus(tableId, num, roundId, DD);
                        }
                    }
                    break;
                case "getCountDown": // {"c":"getCountDown","p":{"A":0,"B":"","C":[{"AA":35,"BB":"451122007","CC":15,"DD":13}]}
                    JSONArray c = data.getJSONArray("C");
                    for (int i = 0; i < c.size(); i++) {
                        JSONObject o = c.getJSONObject(i);
                        bjService.getBjRound(o.getString("AA"), o.getString("BB"));
                    }
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
                        return;
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

                // ... 其他 case 分支

                default:
                    // log.info("-- {} {}", action, data);
            }
        } catch (JSONException e) {
            log.error("Error while processing JSON data for action '{}': {}", action, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error while dispatching action '{}': {}", action, e.getMessage(), e);
        }
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



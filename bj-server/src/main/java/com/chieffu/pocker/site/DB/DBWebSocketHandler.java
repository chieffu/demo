package com.chieffu.pocker.site.DB;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
public class DBWebSocketHandler extends AbstractWebSocketHandler {

    private final Set<WebSocketSession> connectedSessions = new CopyOnWriteArraySet<>();

    @Autowired
    private BjService bjService;


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
        connectedSessions.forEach(session -> {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        });
    }


    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        try {
            JSONObject jsonNode = JSON.parseObject(payload);
            int responseId = jsonNode.getIntValue("responseId");
            JSONObject data = jsonNode.getJSONObject("data");

            switch (responseId) {
                case DBConstant.ResponseId.NEW_INTER_GAME:
                case DBConstant.ResponseId.INTER_GAME:
                    handleInterGame(data);
                    break;
                case DBConstant.ResponseId.OUT_GAME:
                    handleOutGame(data);
                    break;
                case DBConstant.ResponseId.SINGLE_ROAD:
                    handleSingleRoad(data);
                    break;
                case DBConstant.ResponseId.OPEN_CARD:
                    handleOpenCard(data);
                    break;
                case DBConstant.ResponseId.SETTLEMENT:
                    handleSettlement(data);
                    break;
                case DBConstant.ResponseId.NEW_BOOT:
                    handleNewBoot(data);
                    break;
                case DBConstant.ResponseId.INTER_MULTIPLE:
                    handleInterMultiple(data);
                    break;
                case DBConstant.ResponseId.TABLE_LIST_CHANGE:
                    handleTableListChange(data);
                    break;
                case DBConstant.ResponseId.INTER_GOOD_ROAD:
                    handleInterGoodRoad(data);
                    break;
                case DBConstant.ResponseId.GOOD_ROAD_CHANGE_BOOT:
                    handleGoodRoadChangeBoot(data);
                    break;
                case DBConstant.ResponseId.ROAD_PAPER:
                    handleRoadPaper(data);
                    break;
                case DBConstant.ResponseId.GOOD_ROAD_SETTLEMENT:
                    handleGoodRoadSettlement(data);
                    break;
                case DBConstant.ResponseId.GOOD_ROAD_LIST:
                    handleGoodRoadList(data);
                    break;
                case DBConstant.ResponseId.TABLE_DATA_UPDATE:
                    handleTableDataUpdate(data);
                    break;
                case DBConstant.ResponseId.GOOD_ROAD_DATA_UPDATE:
                    handleGoodRoadDataUpdate(data);
                    break;
                case DBConstant.ResponseId.UPDATE_ONLINE_NUMBER:
                    handleUpdateOnlineNumber(data);
                    break;
                case DBConstant.ResponseId.TABLE_BASE_DATA:
                    handleTableBaseData(data);
                    break;
                case DBConstant.ResponseId.TABLE_ROAD:
                    handleTableRoad(data);
                    break;
                case DBConstant.ResponseId.TABLE_BOOT_REPORT:
                    handleTableBootReport(data);
                    break;
                case DBConstant.ResponseId.TABLE_BET_POINT_LIMIT:
                    handleTableBetPointLimit(data);
                    break;
                case DBConstant.ResponseId.TABLE_BOOT_NUMBER_LIMIT:
                    handleTableBootNumberLimit(data);
                    break;
                case DBConstant.ResponseId.TABLE_VERSION:
                    handleTableVersion(data);
                    break;
                case DBConstant.ResponseId.TABLE_BOOT_NUMBER_LIMIT_UPDATE:
                    handleTableBootNumberLimitUpdate(data);
                    break;
                case DBConstant.ResponseId.TABLE_LIST_NEW:
                    handleTableListNew(data);
                    break;
                case DBConstant.ResponseId.TABLE_BET_POINT_LIMIT_UPDATE:
                    handleTableBetPointLimitUpdate(data);
                    break;
                case DBConstant.ResponseId.TABLE_CACHE_BET_POINT_LIMIT_ID:
                    handleTableCacheBetPointLimitId(data);
                    break;
                case DBConstant.ResponseId.TABLE_CHANGE_INFO:
                    handleTableChangeInfo(data);
                    break;
                case DBConstant.ResponseId.TABLE_ITEM_INFO:
                    handleTableItemInfo(data);
                    break;
                case DBConstant.ResponseId.TABLE_ROUND_LIST:
                    handleTableRoundList(data);
                    break;
                case DBConstant.ResponseId.TABLE_GET_ROUND_INFO:
                    handleTableGetRoundInfo(data);
                    break;
                case DBConstant.ResponseId.TABLE_LIST_ALL:
                    handleTableListAll(data);
                    break;
                case DBConstant.ResponseId.SEARCH:
                    handleSearch(data);
                    break;
                case DBConstant.ResponseId.SEARCH_GET_HISTORY:
                    handleSearchGetHistory(data);
                    break;
                case DBConstant.ResponseId.SEARCH_CLEAR_HISTORY:
                    handleSearchClearHistory(data);
                    break;
                default:
                    log.warn("Unknown DBConstant.ResponseId: {}", responseId);
            }
        } catch (Exception e) {
            log.error("Error parsing message: {}", payload, e);
        }
    }

    private void handleSearchClearHistory(JSONObject data) {

    }

    private void handleSearchGetHistory(JSONObject data) {

    }

    private void handleSearch(JSONObject data) {

    }

    private void handleTableListAll(JSONObject data) {

    }

    private void handleTableGetRoundInfo(JSONObject data) {

    }

    private void handleTableRoundList(JSONObject data) {


    }

    private void handleTableItemInfo(JSONObject data) {

    }

    private void handleTableChangeInfo(JSONObject data) {

    }

    private void handleTableCacheBetPointLimitId(JSONObject data) {

    }

    private void handleTableBetPointLimitUpdate(JSONObject data) {

    }

    private void handleTableListNew(JSONObject data) {

    }

    private void handleTableBootNumberLimitUpdate(JSONObject data) {

    }

    private void handleTableVersion(JSONObject data) {

    }

    private void handleTableBootNumberLimit(JSONObject data) {

    }

    private void handleTableBetPointLimit(JSONObject data) {
    }

    private void handleTableBootReport(JSONObject data) {


    }


    private void handleTableRoad(JSONObject data) {

    }

    private void handleTableBaseData(JSONObject data) {

    }

    private void handleUpdateOnlineNumber(JSONObject data) {

    }

    private void handleInterGame(JSONObject data) {
        int errorId = data.getIntValue("errorId");
        if (errorId != 0) {
//                enterGameTask.handleInterGameError();
//                if (enterGameTask.isEmit()) {
//                    emit(EventConst.GAME_MAKE_ERROR, data);
//                }
            log.info("进桌失败: {}", data);
        } else {
            log.info("进桌成功: {}", data);
            int gameTypeId = data.getIntValue("gameTypeId");
            if (gameTypeId == DBConstant.GameType.BACCARAT_MATCH) {
//                    if (enterGameTask.isEmit()) {
//                        emit(EventConst.GAME_MAKE_END, data);
//                    }
            } else if (data.get("gameTableInfo") != null) {
                JSONObject gameTableInfo = data.getJSONObject("gameTableInfo");
                int tableId = gameTableInfo.getInteger("tableId");

            }
        }
    }

    private void handleOutGame(JSONObject data) {
        int tableId = data.getJSONObject("res").getInteger("tableId");
        log.info("局结果需求离开游戏桌子ID: {}", tableId);

    }

    private void handleSingleRoad(JSONObject data) {
        int tableId = data.getInteger("tableId");
        int roundId = data.getInteger("roundId");
    }

    private void handleOpenCard(JSONObject data) {
        int tableId = data.getJSONObject("res").getInteger("tableId");

    }

    private void handleSettlement(JSONObject data) {
    }


    private void handleNewBoot(JSONObject data) {


    }

    private void handleInterMultiple(JSONObject data) {

    }

    private void handleTableListChange(JSONObject data) {

    }

    private void handleInterGoodRoad(JSONObject data) {

    }

    private void handleGoodRoadChangeBoot(JSONObject data) {

    }

    private void handleRoadPaper(JSONObject data) {

    }

    private void handleGoodRoadSettlement(JSONObject data) {

    }

    private void handleGoodRoadList(JSONObject data) {

    }

    private void handleTableDataUpdate(JSONObject data) {

    }

    private void handleGoodRoadDataUpdate(JSONObject data) {
    }
}



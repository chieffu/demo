package com.chieffu.pocker.blackjack.allbet;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("")
@Slf4j
public class SendTestController {
    @Autowired
    private AllBetWebSocketHandler allBetWebSocketHandler;
    @PostMapping("/send")
    public Object calculateOneMoreCardWinRate(@RequestBody String message ){
        allBetWebSocketHandler.sendMessageToSession(message);
        log.info("test sending message:"+message);
        return JSONObject.parse("{\"data\":\"ok\"}");
    }

}

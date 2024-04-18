package com.chieffu.pocker.blackjack.allbet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class AllBetWebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private AllBetWebSocketHandler allBetWebSocketHandler;
    @Autowired
    private CustomOriginHandshakeInterceptor customOriginHandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(allBetWebSocketHandler, "/allbet")
                .addInterceptors(customOriginHandshakeInterceptor) // 添加自定义握手拦截器
                .setAllowedOrigins("*");  // 可选：保留原有的允许所有Origin的配置（根据实际需求调整）
    }
}

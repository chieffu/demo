package com.chieffu.pocker.site.allbet;

import com.chieffu.pocker.site.CustomOriginHandshakeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.websocket.server.ServerContainer;

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

    @Bean
    public ServerEndpointExporter allbetServerEndpointExporter() {
        return new ServerEndpointExporter() {
            @Override
            public void afterPropertiesSet() {
                super.afterPropertiesSet();
                ServerContainer container = (ServerContainer) getServletContext().getAttribute(ServerContainer.class.getName());
                if (container != null) {
                    container.setDefaultMaxSessionIdleTimeout(600000); // 设置会话超时时间
                    container.setDefaultMaxTextMessageBufferSize(1024 * 1024 * 10); // 设置最大文本消息大小为10MB
                    container.setDefaultMaxBinaryMessageBufferSize(1024 * 1024 * 10); // 设置最大二进制消息大小为10MB
                }
            }
        };
    }


}

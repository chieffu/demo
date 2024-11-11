
package com.chieffu.pocker.site;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class CustomOriginHandshakeInterceptor implements HandshakeInterceptor {


    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String originHeader = request.getHeaders().getFirst("Origin");
        if (isAllowedOrigin(originHeader)) {
            response.getHeaders().add("Access-Control-Allow-Origin", originHeader);
            return true; // 允许握手
        } else {
            return true; // 拒绝握手
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
        // 可选：在此处添加握手成功或失败后的处理逻辑
    }

    private boolean isAllowedOrigin(String originHeader) {
        // 根据您的需求判断originHeader是否被允许
        // 示例：允许来自特定Chrome扩展程序的连接
        return originHeader.startsWith("chrome-extension://") ;
    }

}

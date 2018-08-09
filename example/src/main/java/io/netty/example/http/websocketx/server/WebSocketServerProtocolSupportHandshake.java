package io.netty.example.http.websocketx.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

public class WebSocketServerProtocolSupportHandshake extends WebSocketServerProtocolHandler {
    public WebSocketServerProtocolSupportHandshake(String websocketPath) {
        super(websocketPath, null, true);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            WebSocketServerProtocolHandler.HandshakeComplete serverHandshakeComplete = (WebSocketServerProtocolHandler.HandshakeComplete) evt;
            System.out.println(serverHandshakeComplete.requestHeaders().get("user-name"));
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}

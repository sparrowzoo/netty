package io.netty.example.http.websocketx.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;

import java.util.List;

public class WebSocketServerProtocolSupportHandshake extends WebSocketServerProtocolHandler {
    /**
     * channel属性
     */
    public static final AttributeKey<String> USER_ID_KEY = AttributeKey.newInstance("userId");

    public WebSocketServerProtocolSupportHandshake(String websocketPath) {
        super(websocketPath, null, true);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            WebSocketServerProtocolHandler.HandshakeComplete serverHandshakeComplete = (WebSocketServerProtocolHandler.HandshakeComplete) evt;
            System.out.println(serverHandshakeComplete.requestHeaders().get("user-name"));
            ctx.channel().attr(USER_ID_KEY).set("user-id");
            //bind
        } else {
            if (evt instanceof IdleStateEvent) {
                // ctx.channel().attr(USER_ID_KEY)
                //remove bind
            }
            super.userEventTriggered(ctx, evt);
        }
    }


    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) throws Exception {
        if (frame instanceof CloseWebSocketFrame) {
            //unbind user
        }
        super.decode(ctx, frame, out);
    }
}

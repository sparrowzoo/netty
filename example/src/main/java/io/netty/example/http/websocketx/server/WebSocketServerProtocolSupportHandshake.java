package io.netty.example.http.websocketx.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import java.util.List;

/**
 * https://www.it1352.com/1677331.html
 *
 */
public class WebSocketServerProtocolSupportHandshake extends WebSocketServerProtocolHandler {




  /**
   * 子协议支持
   * https://www.it1352.com/1677331.html
   * @param websocketPath
   * @param maxLength
   */
  public WebSocketServerProtocolSupportHandshake(String websocketPath, Integer maxLength) {
    super(websocketPath, "*", true, maxLength);
  }

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
      //WebSocketServerProtocolHandler.HandshakeComplete serverHandshakeComplete = (WebSocketServerProtocolHandler.HandshakeComplete) evt;
      //String userId=serverHandshakeComplete.requestHeaders().get("sec-websocket-protocol");
     // System.out.println(serverHandshakeComplete.requestHeaders().get("sec-websocket-protocol"));
     // UserContainer.getContainer().online(ctx.channel(),userId);
    } else {
      if (evt instanceof IdleStateEvent) {
        UserContainer.getContainer().offline(ctx.channel());
      }
      super.userEventTriggered(ctx, evt);
    }
  }


  @Override
  protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out)
      throws Exception {
    if (frame instanceof CloseWebSocketFrame) {
      //unbind user
    }
    super.decode(ctx, frame, out);
  }
}

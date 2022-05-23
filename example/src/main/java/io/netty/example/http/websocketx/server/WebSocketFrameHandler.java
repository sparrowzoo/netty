/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.example.http.websocketx.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Echoes uppercase content of text frames.
 */
public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

  private static final Logger logger = LoggerFactory.getLogger(WebSocketFrameHandler.class);

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
    // ping and pong frames already handled

    if (frame instanceof TextWebSocketFrame) {
      // Send the uppercase string back.
      String request = ((TextWebSocketFrame) frame).text();
      System.out.println(request.length());
      System.out.println("received " + request);
      logger.info("{} received {}", ctx.channel(), request);
      ctx.channel().writeAndFlush(new TextWebSocketFrame(request.toUpperCase(Locale.US)));
    } else if (frame instanceof BinaryWebSocketFrame) {

      System.out.println("服务器接收到⼆进制消息.");
      BinaryWebSocketFrame msg = (BinaryWebSocketFrame) frame;
      ByteBuf content = msg.content();
      content.markReaderIndex();
      int flag = content.readInt();
      System.out.println(
          "IsFinal" + msg.isFinalFragment() + "Image Flag:" + flag + " length" + msg.content()
              .capacity());
      content.resetReaderIndex();
      ByteBuf byteBuf = Unpooled.directBuffer(msg.content().capacity());
      byteBuf.writeBytes(msg.content());
      ctx.writeAndFlush(new BinaryWebSocketFrame(byteBuf));
    } else if (frame instanceof ContinuationWebSocketFrame) {
      ContinuationWebSocketFrame msg = (ContinuationWebSocketFrame) frame;
      System.out.println(
          "IsFinal" + msg.isFinalFragment() + " length" + msg.content()
              .capacity());
    }
  }
}

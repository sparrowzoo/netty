package io.netty.example.echo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

public class OomClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws InterruptedException {
        while (true) {
            ByteBuf byteBuf = ByteBufAllocator.DEFAULT.directBuffer(1024);
            byteBuf.writeBytes("这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京这里是北京".getBytes());
            ctx.writeAndFlush(byteBuf).sync();
            //ReferenceCountUtil.release(byteBuf);//不允许 release
        }
    }
}

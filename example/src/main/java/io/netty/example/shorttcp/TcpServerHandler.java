package io.netty.example.shorttcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TcpServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(TcpServer.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        if (msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf) msg;
            byte[] dst = new byte[buf.capacity()];
            buf.readBytes(dst);
            logger.info("SERVER接收到消息:" + new String(dst));

            byte[] dest = (new String(dst) + ". yes, server is accepted you ,nice !").getBytes();
            ByteBuf destBuf = ctx.alloc().buffer(dest.length);
            destBuf.writeBytes(dest);
            ctx.channel().writeAndFlush(destBuf).addListener(ChannelFutureListener.CLOSE);

            ReferenceCountUtil.release(msg);
        } else {
            logger.warn("error object !");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) throws Exception {
        logger.warn("Unexpected exception from downstream.", cause);
        ctx.close();
    }
}


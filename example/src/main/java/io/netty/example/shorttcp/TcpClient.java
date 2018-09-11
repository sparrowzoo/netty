package io.netty.example.shorttcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TcpClient {
    private static final Logger logger = LoggerFactory.getLogger(TcpClient.class);
    public static String HOST = "127.0.0.1";
    public static int PORT = 9999;

    public static Bootstrap bootstrap = getBootstrap();

    /**
     * 初始化Bootstrap
     *
     * @return
     */
    public static final Bootstrap getBootstrap() {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group).channel(NioSocketChannel.class);
        b.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                //pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                pipeline.addLast("handler", new TcpClientHandler());
            }
        });
//	b.option(ChannelOption.SO_KEEPALIVE, true);
        return b;
    }


    public static void sendMsg(String host,Object msg){
        Channel channel= null;
        try {
            channel = bootstrap.connect(host,PORT).sync().channel();

        } catch (InterruptedException e) {
            logger.error("channel connection error",e);
            return;
        }
            try {
                channel.writeAndFlush(msg).sync();
            } catch (InterruptedException e) {
               logger.error("channel interrupt",e);
            }
    }

//    public static void main(String[] args) throws Exception {
//        try {
//            long t0 = System.nanoTime();
//            byte[] value = null;
//
//            Channel [] channels=new Channel[5000];
//            for (int i = 0; i < 5000; i++) {
//                Channel channel = getChannel(HOST, PORT);
//                channels[i]=channel;
//                value = (i + ",你好").getBytes();
//                ByteBufAllocator alloc = channel.alloc();
//                ByteBuf buf = alloc.buffer(value.length);
//                buf.writeBytes(value);
//                TcpClient.sendMsg(channel, buf);
//            }
//            long t1 = System.nanoTime();
//            System.out.println((t1 - t0) / 1000000.0);
//            Thread.sleep(5000);
//            System.exit(0);
//        } catch (Exception e) {
//// TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//    }
}


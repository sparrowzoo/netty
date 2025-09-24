package io.netty.example.http.websocketx.server;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

public class BinaryUtils {
    public static String toString(Object msg) {
        if (msg instanceof BinaryWebSocketFrame) {
            BinaryWebSocketFrame frame = (BinaryWebSocketFrame) msg;
            ByteBuf buf = frame.content();
            String result;
            if (!buf.isDirect()) {
                result = new String(buf.array(), buf.arrayOffset() + buf.readerIndex(), buf.readableBytes());
            } else {
                byte[] bytes = new byte[buf.readableBytes()];
                buf.getBytes(buf.readerIndex(), bytes);
                result = new String(bytes);
            }
            System.out.println("直接内存:" + buf.isDirect() + "result msg" + result);
            return result;
        }
        return null;
    }
}

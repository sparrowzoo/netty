package io.netty.example.shorttcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.util.ReferenceCountUtil;

public class PoolMemoryTest {
    public static void main(String[] args) {
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.buffer(1000);
        byteBuf.writeInt(1);
        ReferenceCountUtil.retain(byteBuf);
        //ReferenceCountUtil.release(byteBuf);
        System.out.println(ReferenceCountUtil.refCnt(byteBuf));

    }
}

package io.netty.example.http.websocketx.client;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;

public class EventLoopGroupTest {
    public static void main(String[] args) {
        /**
         * NioEventLoop(NioEventLoopGroup parent, Executor executor, SelectorProvider selectorProvider,
         *                  SelectStrategy strategy, RejectedExecutionHandler rejectedExecutionHandler) {
         *         super(parent, executor, false, DEFAULT_MAX_PENDING_TASKS, rejectedExecutionHandler);
         *
         */
        EventLoopGroup workder = new NioEventLoopGroup(1, new DefaultThreadFactory("websocket-workder", true));

        for (int i = 0; i < 100; i++) {
            workder.submit(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                    }
                }
            });
        }
        while (true) {
        }
    }
}

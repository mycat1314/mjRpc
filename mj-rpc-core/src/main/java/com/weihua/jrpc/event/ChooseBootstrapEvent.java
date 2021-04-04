package com.weihua.jrpc.event;

import com.weihua.jrpc.utils.OsUtils;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.ThreadFactory;

/**
 * @author xie wei hua
 * @version 1.0
 * @date 2021/4/3 10:02
 * @description
 */
public class ChooseBootstrapEvent {

    /**
     * choose a suitable eventLoopGroup
     * @param threadNaming naming thread
     * @return
     */
    public static EventLoopGroup choseEventLoop(String threadNaming) {
        ThreadFactory threadFactory = new DefaultThreadFactory(threadNaming, true);
        if (OsUtils.WINDOWS == Boolean.TRUE) {
            return new NioEventLoopGroup(4,threadFactory);
        }else if (OsUtils.LINUX) {
            return new EpollEventLoopGroup(4,threadFactory);
        }
        return new NioEventLoopGroup(4,threadFactory);
    }





}

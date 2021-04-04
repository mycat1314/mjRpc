package com.weihua.jrpc.client.abs;

import com.weihua.jrpc.client.MjRpcClientHandler;
import com.weihua.jrpc.event.ChooseBootstrapEvent;
import io.netty.channel.EventLoopGroup;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xie wei hua
 * @version 1.0
 * @date 2021/4/3 11:23
 * @description do somethings
 */
public abstract class AbstractConnect {
    /**
     * cache connect info
     */
    public final Map<InetSocketAddress, MjRpcClientHandler> connectedHandlerMap = new ConcurrentHashMap<InetSocketAddress, MjRpcClientHandler>(){
    };

    /**
     * connected list
     */
    public final CopyOnWriteArrayList<MjRpcClientHandler> connectedHandlerList = new CopyOnWriteArrayList<MjRpcClientHandler>();

    /**
     * netty client eventLoopGroup
     */
    public final EventLoopGroup eventLoopGroup = ChooseBootstrapEvent.choseEventLoop("mj-netty-client");

    /**
     * reentrant lock
     */
    public final ReentrantLock connectedLock = new ReentrantLock();

    /**
     * condition notice
     */
    public final Condition connectedCondition = connectedLock.newCondition();

    /**
     * connectTimeout
     */
    public final long connectTimeoutMills = 6000;

    /**
     * isRunning
     */
    public volatile boolean isRunning = true;

    public Map<InetSocketAddress, MjRpcClientHandler> getConnectedHandlerMap() {
        return connectedHandlerMap;
    }

    public CopyOnWriteArrayList<MjRpcClientHandler> getConnectedHandlerList() {
        return connectedHandlerList;
    }

    public EventLoopGroup getEventLoopGroup() {
        return eventLoopGroup;
    }

    public ReentrantLock getConnectedLock() {
        return connectedLock;
    }

    public Condition getConnectedCondition() {
        return connectedCondition;
    }

    public long getConnectTimeoutMills() {
        return connectTimeoutMills;
    }

    public boolean isRunning() {
        return isRunning;
    }

}

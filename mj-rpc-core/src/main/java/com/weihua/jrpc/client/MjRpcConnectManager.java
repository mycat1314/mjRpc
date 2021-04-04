package com.weihua.jrpc.client;

/**
 * @author xie wei hua
 * @version 1.0
 * @date 2021/4/1 11:07
 * @description rpc connect manager
 */
import com.weihua.jrpc.client.abs.AbstractConnect;
import com.weihua.jrpc.client.codec.MjRpcRequest;
import com.weihua.jrpc.client.invoke.entity.AsyncParams;
import com.weihua.jrpc.client.invoke.stragy.enums.LoadBalancingEnum;
import com.weihua.jrpc.client.prop.MjProperties;
import com.weihua.jrpc.pool.TaskExecutor;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
public class MjRpcConnectManager extends AbstractConnect {

    public MjRpcConnectManager(){};

    /**
     * retryTimes
     */
    private Map<String,Integer> retryTimes = new ConcurrentHashMap<>();

    
    /**
     * sendConnect
     * @param serverAddress
     */
    public void connect(final String serverAddress) {
        connectCheck(serverAddress);
        List<String> allServerAddr = Arrays.asList(serverAddress.split(","));
        connectionServer(allServerAddr);
    }

    /**
     * check empty
     * @param addr
     * @return
     */
    public boolean connectCheck(String addr) {
        if (StringUtils.isBlank(addr)) {
            log.error("server address can not empty");
            return false;
        }
        String[] arr = addr.split(",");
        if (arr.length == 0 ) {
            log.info("please check server whether or not right");
            return false;
        }
        return true;
    }

    /**
     * connect
     * @param serverAddress
     */
    public void connect(List<String> serverAddress) {
        connectionServer(serverAddress);
    }

    /**
     * connection
     * @param allServerAddr
     */
    public void connectionServer(List<String> allServerAddr) {
        if (CollectionUtils.isNotEmpty(allServerAddr)) {
            HashSet<InetSocketAddress> newAllServerNodeSet = new HashSet<InetSocketAddress>();
            for (int i = 0; i < allServerAddr.size(); i++) {
                String[] array = allServerAddr.get(i).split(":");
                if (array.length == 2) {
                    String host = array[0];
                    int port = Integer.parseInt(array[1]);
                    final InetSocketAddress remotePeer = new InetSocketAddress(host, port);
                    newAllServerNodeSet.add(remotePeer);
                }
            }
            for (InetSocketAddress serverNodeAddress : newAllServerNodeSet) {
                if (!connectedHandlerMap.keySet().contains(serverNodeAddress)) {
                    connectAsync(serverNodeAddress);
                }
            }


            for(int i = 0; i < connectedHandlerList.size(); i++) {
                MjRpcClientHandler mjRpcClientHandler = connectedHandlerList.get(i);
                SocketAddress remotePeer = mjRpcClientHandler.getRemotePeer();
                if (!newAllServerNodeSet.contains(remotePeer)) {
                    log.info(" remove invalid server node "+ remotePeer);
                    MjRpcClientHandler handler = connectedHandlerMap.get(remotePeer);
                    if (!Objects.isNull(handler)) {
                        handler.close();
                        connectedHandlerMap.remove(remotePeer);
                    }
                    connectedHandlerList.remove(mjRpcClientHandler);
                }
            }
        } else {
            log.error(" no available server address! ");
            clearConnected();
        }
    }


    /**
     * connect
     * @param remotePeer
     */
    private void connectAsync(InetSocketAddress remotePeer) {
        TaskExecutor.threadPoolExecutor().submit(new Runnable() {
            @Override
            public void run() {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(eventLoopGroup)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.TCP_NODELAY,true)
                        .handler(new MjRpcClientInitializer());
                connect(bootstrap,remotePeer);

            }
        });
    }


    private void connect(final Bootstrap b, InetSocketAddress remotePeer) {
        // 1. 真正建立连接
        final ChannelFuture channelFuture = b.connect(remotePeer);
        channelFuture.channel().closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                try {
                    String host = remotePeer.getHostName();
                    int port = remotePeer.getPort();
                    String namingRetryKey = host + ":" + port;
                    log.warn("connect fail， remote address ：" + remotePeer);
                    channelFuture.channel().eventLoop().schedule(new Runnable() {
                        @Override
                        public void run() {
                            //log.warn("{},connect fail，set reconnect ... first {} times", namingRetryKey, retryTimes.get(namingRetryKey));
                            if (retryTimes.containsKey(namingRetryKey)) {
                                Integer num = retryTimes.get(namingRetryKey);
                                if (num.equals(Integer.valueOf(3))) {
                                    clearConnected();
                                    retryTimes.remove(namingRetryKey);
                                    log.warn("clear connect address: {} ", namingRetryKey);
                                } else {
                                    retryTimes.put(namingRetryKey, retryTimes.get(namingRetryKey)+1);
                                    connect(b, remotePeer);
                                    log.error(" first {} times try connect server ... ",retryTimes.get(namingRetryKey));
                                }
                            } else {
                                retryTimes.put(namingRetryKey, 0);
                                connect(b,remotePeer);
                            }
                        }
                    }, 3, TimeUnit.SECONDS);
                } finally {

                };
            }
        });

        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    log.info(" connect success, server address :"+remotePeer);
                    MjRpcClientHandler mjRpcClientHandler = channelFuture.channel().pipeline().get(MjRpcClientHandler.class);
                    addHandler(mjRpcClientHandler);
                }
            }
        });
    }

    /**
     * clear cache
     */
    private void clearConnected() {
        for (final MjRpcClientHandler mjRpcClientHandler : connectedHandlerList) {
            SocketAddress remotePeer = mjRpcClientHandler.getRemotePeer();
            MjRpcClientHandler handler = connectedHandlerMap.get(remotePeer);
            if (handler != null) {
                handler.close();
                connectedHandlerMap.remove(remotePeer);
            }
        }
        connectedHandlerList.clear();
    }

    private void addHandler(MjRpcClientHandler handler) {
        connectedHandlerList.add(handler);
        InetSocketAddress socketAddress = (InetSocketAddress) handler.getChannel().remoteAddress();
        connectedHandlerMap.put(socketAddress,handler);
        signalAvailableHandler();
    }

    /**
     * notify thread weak
     */
    private void signalAvailableHandler() {
        connectedLock.lock();
        try {
            connectedCondition.signalAll();
        }finally {
            connectedLock.unlock();
        }
    }

    /**
     *  wait new connect
     * @return
     * @throws InterruptedException
     */
    private boolean waitingForAvailableHandler() throws InterruptedException{
        connectedLock.lock();
        try {
            return connectedCondition.await(this.connectTimeoutMills,TimeUnit.MICROSECONDS);
        }finally {
            connectedLock.unlock();
        }
    }

    /**
     * choose a handler
     * @return
     */
    public MjRpcClientHandler chooseHandler(MjProperties mjProperties) {
        CopyOnWriteArrayList<MjRpcClientHandler> handlers = (CopyOnWriteArrayList<MjRpcClientHandler>)this.connectedHandlerList.clone();
        int size = handlers.size();
        while (isRunning && size == 0) {
            try {
                boolean availableHandler = waitingForAvailableHandler();
                if (availableHandler) {
                    handlers = (CopyOnWriteArrayList<MjRpcClientHandler>)this.connectedHandlerList.clone();
                    size = handlers.size();
                }
            }catch (InterruptedException e) {
                throw new RuntimeException("no any handler ",e);
            }
        }
        if (!isRunning) {
            return null;
        }
        Map<InetSocketAddress, MjRpcClientHandler> connectedHandlerMap = this.getConnectedHandlerMap();
        Set<InetSocketAddress> list = new CopyOnWriteArraySet<>();
        for (Map.Entry<InetSocketAddress, MjRpcClientHandler> s : connectedHandlerMap.entrySet()) {
            InetSocketAddress key = s.getKey();
            list.add(key);
        }
        InetSocketAddress inetSocketAddress = null;
        String key = makeKey(mjProperties);
        if (mjProperties.getLoadBalancing() == null) {
           inetSocketAddress = LoadBalancingEnum.ROUND.loadBalancingStrategy.loadBalancing(key, list);
        }else {
            inetSocketAddress = mjProperties.getLoadBalancing().loadBalancing(key,list);
        }

        log.info(" choose server address :"+inetSocketAddress.getHostName() + ":" + inetSocketAddress.getPort());
        MjRpcClientHandler mjRpcClientHandler = connectedHandlerMap.get(inetSocketAddress);
        return mjRpcClientHandler;
    }

    private String makeKey(MjProperties properties) {
        String name = properties.getClazz().getName();
        String v = properties.getVersion();
        String finalName = name + "-" + v;
        return finalName;
    }

    /**
     * choose a handler
     * @return
     */
    public MjRpcClientHandler chooseHandler() {
        CopyOnWriteArrayList<MjRpcClientHandler> handlers = (CopyOnWriteArrayList<MjRpcClientHandler>)this.connectedHandlerList.clone();
        int size = handlers.size();
        while (isRunning && size == 0) {
            try {
                boolean availableHandler = waitingForAvailableHandler();
                if (availableHandler) {
                    handlers = (CopyOnWriteArrayList<MjRpcClientHandler>)this.connectedHandlerList.clone();
                    size = handlers.size();
                }
            }catch (InterruptedException e) {
                throw new RuntimeException("no any handler ",e);
            }
        }
        if (!isRunning) {
            return null;
        }
        Map<InetSocketAddress, MjRpcClientHandler> connectedHandlerMap = this.getConnectedHandlerMap();
        Set<InetSocketAddress> list = new CopyOnWriteArraySet<>();
        for (Map.Entry<InetSocketAddress, MjRpcClientHandler> s : connectedHandlerMap.entrySet()) {
            InetSocketAddress key = s.getKey();
            list.add(key);
        }
        InetSocketAddress inetSocketAddress = LoadBalancingEnum.ROUND.loadBalancingStrategy.loadBalancing("default", list);
        log.info(" choose server address :"+inetSocketAddress.getHostName() + ":" + inetSocketAddress.getPort());
        MjRpcClientHandler mjRpcClientHandler = connectedHandlerMap.get(inetSocketAddress);
        return mjRpcClientHandler;
    }


    /**
     * stop
     */
    public void stop() {
        isRunning = false;
        for (int i = 0; i < connectedHandlerList.size(); i++) {
            MjRpcClientHandler mjRpcClientHandler = connectedHandlerList.get(i);
            mjRpcClientHandler.close();
        }
        // notify
        signalAvailableHandler();
        TaskExecutor.threadPoolExecutor().shutdownNow();
        eventLoopGroup.shutdownGracefully();
    }

    public void reconnect(final MjRpcClientHandler handler, final SocketAddress remotePeer) {
        if (handler != null) {
            handler.close();
            connectedHandlerList.remove(handler);
            connectedHandlerMap.remove(remotePeer);
        }
        connectAsync((InetSocketAddress) remotePeer);
    }

    public MjRpcFuture remoteAsyncInvoke(MjRpcRequest request) {
        MjRpcClientHandler mjRpcClientHandler = chooseHandler();
        MjRpcFuture mjRpcFuture = mjRpcClientHandler.sendRequest(request);
        return mjRpcFuture;
    }

    public MjRpcFuture remoteAsyncInvoke(MjRpcRequest mjRpcRequest, AsyncParams asyncParams, MjProperties mjProperties) {
        mjProperties.setLoadBalancing(asyncParams.getLoadBalancingStrategy());
        MjRpcClientHandler mjRpcClientHandler = chooseHandler(mjProperties);
        MjRpcFuture mjRpcFuture = mjRpcClientHandler.sendRequest(mjRpcRequest);
        return mjRpcFuture;
    }
}

package com.weihua.jrpc.server;

import com.weihua.jrpc.client.codec.MjRpcDecoder;
import com.weihua.jrpc.client.codec.MjRpcRequest;
import com.weihua.jrpc.client.codec.MjRpcResponse;
import com.weihua.jrpc.client.codec.MjRpcEncoder;
import com.weihua.jrpc.config.provider.ProviderConfigMj;
import com.weihua.jrpc.server.abs.AbstractServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author xie wei hua
 * @version 1.0
 * @date 2021/4/1 15:13
 * @description do somethings
 */
@Slf4j
public class MjRpcServer extends AbstractServer {
    public MjRpcServer(String serverAddress) {
        this.serverAddress = serverAddress;
        this.start();
    }
   @Override
    public void start()  {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(new NioEventLoopGroup(), workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG,1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline cp = ch.pipeline();
                        cp.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0));
                        cp.addLast(new MjRpcDecoder(MjRpcRequest.class));
                        cp.addLast(new MjRpcEncoder(MjRpcResponse.class));
                        cp.addLast(new MjRpcServerHandler(handlerMap));
                    }
                });
        checkServerAddress();
        String[] array = serverAddress.split(":");
        String host = array[0];
        int port = Integer.parseInt(array[1]);
        try {
            ChannelFuture channelFuture = serverBootstrap.bind(host, port).sync();
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        log.info("stater success success bind to {}", serverAddress);
                    }else {
                        log.error("starter fail bing to " + serverAddress);
                        throw new Exception("starter fail, cause: " + future.cause());
                    }
                }
            });
            try {
                channelFuture.await(5000, TimeUnit.MILLISECONDS);
                if (channelFuture.isSuccess()) {
                    log.info("starter success !");
                }
            }catch (InterruptedException e) {
                log.error("starter occur interrupted, ex:" + e);
            }
        }catch (InterruptedException ex) {
            log.error("starter fail：{}",ex.getMessage());
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean status() {
        return false;
    }

    /**
     * 	register server
     */
    public void registerProcessor(ProviderConfigMj providerConfig) {
        handlerMap.put(providerConfig.getInterface(), providerConfig.getRef());
    }
}

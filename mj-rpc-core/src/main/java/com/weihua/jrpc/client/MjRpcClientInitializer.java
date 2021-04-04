package com.weihua.jrpc.client;

import com.weihua.jrpc.client.codec.MjRpcDecoder;
import com.weihua.jrpc.client.codec.MjRpcRequest;
import com.weihua.jrpc.client.codec.MjRpcResponse;
import com.weihua.jrpc.client.codec.MjRpcEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author xie wei hua
 * @version 1.0
 * @date 2021/4/1 12:19
 * @description rpc 初始化
 */
public class MjRpcClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline cp = ch.pipeline();
        // handler
        cp.addLast(new MjRpcEncoder(MjRpcRequest.class));
        cp.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0));
        cp.addLast(new MjRpcDecoder(MjRpcResponse.class));
        cp.addLast(new MjRpcClientHandler());
    }
}

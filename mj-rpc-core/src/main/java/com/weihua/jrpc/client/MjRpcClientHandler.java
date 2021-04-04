package com.weihua.jrpc.client;

import com.weihua.jrpc.client.codec.MjRpcRequest;
import com.weihua.jrpc.client.codec.MjRpcResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xie wei hua
 * @version 1.0
 * @date 2021/4/1 11:10
 * @description 实际业务处理器
 */
public class MjRpcClientHandler extends SimpleChannelInboundHandler<MjRpcResponse> {
    private Channel channel;

    private SocketAddress remotePeer;

    private Map<String, MjRpcFuture> pendingRpcTable = new ConcurrentHashMap<String, MjRpcFuture>();

    public Channel getChannel() {
        return channel;
    }


    /**
     * 通道激活的时候触发
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.remotePeer = this.channel.remoteAddress();
    }

    /**
     *
     * @param ctx
     * @param rpcResponse
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MjRpcResponse rpcResponse) throws Exception {
        String requestId = rpcResponse.getRequestId();
        MjRpcFuture mjRpcFuture = pendingRpcTable.get(requestId);
        if (mjRpcFuture != null) {
            pendingRpcTable.remove(requestId);
            mjRpcFuture.done(rpcResponse);
        }
    }

    public SocketAddress getRemotePeer() {
        return this.remotePeer;
    }

    /**
     * netty 提供一种主动关闭连接的方式，发送一个Unpooled.EMPTY_BUFFER 的close 事件
     */
    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }


    /**
     * request server
     * @param request
     * @return
     */
    public MjRpcFuture sendRequest(MjRpcRequest request) {
        MjRpcFuture mjRpcFuture = new MjRpcFuture(request);
        pendingRpcTable.put(request.getRequestId(), mjRpcFuture);
        channel.writeAndFlush(request);
        return mjRpcFuture;
    }

}

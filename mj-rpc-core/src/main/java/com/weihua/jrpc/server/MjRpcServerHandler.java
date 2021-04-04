package com.weihua.jrpc.server;

import com.weihua.jrpc.client.codec.MjRpcRequest;
import com.weihua.jrpc.client.codec.MjRpcResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author xie wei hua
 * @version 1.0
 * @date 2021/4/1 15:25
 * @description do somethings
 */
@Slf4j
public class MjRpcServerHandler extends SimpleChannelInboundHandler<MjRpcRequest> {
    private Map<String, Object> handlerMap;

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(16, 16, 600L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(65536));


    public MjRpcServerHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MjRpcRequest request) throws Exception {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                MjRpcResponse response = new MjRpcResponse();
                response.setRequestId(request.getRequestId());
                try {
                    Object result = handle(request);
                    response.setResult(result);
                }catch (Throwable e) {
                    response.setThrowable(e);
                    log.error("rpc server handle request throwable:" + e);
                }
                ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            //log.info("");
                        }
                    }
                });
            }
        });
    }


    /**
     * invoke local server by reflect
     * @param request
     * @return
     * @throws InvocationTargetException
     */
    private Object handle(MjRpcRequest request) throws InvocationTargetException {
        String className = request.getClassName();
        Object serviceRef = handlerMap.get(className);
        Class<?> serviceClass = serviceRef.getClass();
        String methodName = request.getMethodName();
        Class<?>[] paramterTypes = request.getParamterTypes();
        Object[] paramters = request.getParamters();


        // cglib proxy
        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serverFastMethod = serviceFastClass.getMethod(methodName,paramterTypes);
        return serverFastMethod.invoke(serviceRef,paramters);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("server caught throwable:"+ cause);
        ctx.close();
    }

}

package com.weihua.jrpc.client.invoke;

import com.weihua.jrpc.client.MjRpcConnectManager;
import com.weihua.jrpc.client.MjRpcFuture;
import com.weihua.jrpc.client.invoke.entity.AsyncParams;
import com.weihua.jrpc.client.prop.MjProperties;
import com.weihua.jrpc.client.proxy.RpcProxyImpl;

import java.lang.reflect.Proxy;


/**
 * @author xie wei hua
 * @version 1.0
 * @date 2021/4/3 15:02
 * @description invoke
 */
public class MjInvoke implements AsyncInvoke{
    private MjProperties properties;
    private MjRpcConnectManager mjRpcConnectManager;

//    public MjInvoke(RpcConnectManager rpcConnectManager){
//        this.rpcConnectManager = rpcConnectManager;
//    };
//
//    public MjInvoke(MjProperties properties,RpcConnectManager rpcConnectManager) {
//        this.properties = properties;
//        this.rpcConnectManager = rpcConnectManager;
//    }

    public MjInvoke set(MjProperties properties, MjRpcConnectManager mjRpcConnectManager) {
        this.properties = properties;
        this.mjRpcConnectManager = mjRpcConnectManager;
        return this;
    }

    @Override
    public <T> T getInstance() {
        Class clazz = this.properties.getClazz();
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class<?>[]{clazz},
                new RpcProxyImpl<>(mjRpcConnectManager,clazz,properties)
                );
    }

    @Override
    public MjRpcFuture async(String methodName, Object... params) {
        Class<?> clazz = this.properties.getClazz();
        RpcProxyImpl<?> rpcProxy = new RpcProxyImpl<>(mjRpcConnectManager, clazz, properties);
        Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class<?>[]{clazz},
                rpcProxy);
        MjRpcFuture call = rpcProxy.call(methodName, params);
        return call;
    }

    @Override
    public MjRpcFuture async(AsyncParams asyncParams) {
        Class<?> clazz = this.properties.getClazz();
        RpcProxyImpl<?> rpcProxy = new RpcProxyImpl<>(mjRpcConnectManager, clazz, properties);
        Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class<?>[]{clazz},
                rpcProxy);
        MjRpcFuture future = rpcProxy.callWithProp(asyncParams);
        return future;
    }
}

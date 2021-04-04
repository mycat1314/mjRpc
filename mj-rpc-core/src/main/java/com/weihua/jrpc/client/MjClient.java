package com.weihua.jrpc.client;

import com.weihua.jrpc.client.abs.AbstractClient;
import com.weihua.jrpc.client.invoke.MjInvoke;
import com.weihua.jrpc.client.invoke.entity.AsyncParams;
import com.weihua.jrpc.client.prop.MjProperties;
import com.weihua.jrpc.client.proxy.MjRpcAsyncInvoke;
import com.weihua.jrpc.client.proxy.RpcProxyImpl;
import com.weihua.jrpc.spi.invoke.AsyncInstance;

import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @author xie wei hua
 * @version 1.0
 * @date 2021/4/1 11:06
 * @description mjrpc client
 */
public class MjClient extends AbstractClient {

    private MjProperties properties;

    public MjClient() {
        this.mjRpcConnectManager = new MjRpcConnectManager();
    }

    public MjClient(int timeout) {
       this.timeout = timeout;
       this.mjRpcConnectManager = new MjRpcConnectManager();
    }

    @Override
    public void connect(String address) {
        mjRpcConnectManager.connect(address);
    }

    @Override
    public void connect(String address, int timeout) {
        this.timeout = timeout;
        mjRpcConnectManager.connect(address);
    }

    @Override
    public Object connectAndInvoke(MjProperties mjProperties) {
        reentrantLock.lock();
        try {
            connect(mjProperties.getAddress());
            this.properties = mjProperties;
            Object instance = ((MjInvoke) AsyncInstance.getInstance().getInstance(MjInvoke.class.getName()))
                    .set(this.properties, this.mjRpcConnectManager).getInstance();
            return instance;
        }finally {
            reentrantLock.unlock();
        }
    }

    @Override
    public MjRpcFuture connectAndAsyncInvoke(MjProperties mjProperties, String funName, Object... parmas) {
        reentrantLock.lock();
        try {
            connect(mjProperties.getAddress());
            this.properties = mjProperties;
            MjRpcFuture async = ((MjInvoke) AsyncInstance.getInstance().getInstance(MjInvoke.class.getName()))
                    .set(this.properties, this.mjRpcConnectManager).async(funName, parmas);
            return async;
        }finally {
            reentrantLock.unlock();
        }
    }

    public MjRpcFuture connectAndAsyncInvoke(MjProperties mjProperties, AsyncParams asyncParams) {
        reentrantLock.lock();
        try {
            connect(mjProperties.getAddress());
            this.properties = mjProperties;
            MjRpcFuture async = ((MjInvoke) AsyncInstance.getInstance().getInstance(MjInvoke.class.getName()))
                    .set(this.properties, this.mjRpcConnectManager).async(asyncParams);
            return async;
        }finally {
            reentrantLock.unlock();
        }
    }


    @Override
    public void connect(List<String> serverAddress) {
        mjRpcConnectManager.connect(serverAddress);
    }

    @Override
    public void connect(List<String> serverAddress, int timeout) {
        this.timeout = timeout;
        mjRpcConnectManager.connect(serverAddress);
    }

    @Override
    public void connect(String address, int timeout, String token) {
        this.token = token;
        this.timeout = timeout;
        mjRpcConnectManager.connect(serverAddress);
    }

    @Override
    public void connect(List<String> address, int timeout, String token) {
        this.token = token;
        this.timeout = timeout;
        mjRpcConnectManager.connect(address);
    }

    @Override
    public <T> T syncInvoke(Class<T> interfaceClass) {
        if (syncProxyInstanceMap.containsKey(interfaceClass)) {
            return (T) syncProxyInstanceMap.get(interfaceClass);
        }else {
            Object proxy = Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                    new Class<?>[]{interfaceClass},
                    new RpcProxyImpl<>(mjRpcConnectManager, interfaceClass, timeout, this.token));
            syncProxyInstanceMap.put(interfaceClass,proxy);
            return (T) proxy;
        }
    }

    @Override
    public <T> MjRpcAsyncInvoke asyncInvoke(Class<T> interfaceClass) {
        if (asyncProxyInstanceMap.containsKey(interfaceClass)) {
            return ((RpcProxyImpl<T>) asyncProxyInstanceMap.get(interfaceClass));
        }else {
            RpcProxyImpl<T> rpcProxy = new RpcProxyImpl<>(mjRpcConnectManager,interfaceClass,timeout,token);
            asyncProxyInstanceMap.put(interfaceClass,rpcProxy);
            return rpcProxy;
        }
    }

    public void stop() {
        this.mjRpcConnectManager.stop();
    }

    public MjProperties getProperties() {
        return properties;
    }

    public void setProperties(MjProperties properties) {
        this.properties = properties;
    }
}


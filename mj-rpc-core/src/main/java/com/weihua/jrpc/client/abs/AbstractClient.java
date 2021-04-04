package com.weihua.jrpc.client.abs;

import com.weihua.jrpc.client.MjRpcConnectManager;
import com.weihua.jrpc.client.MjRpcFuture;
import com.weihua.jrpc.client.init.ClientInit;
import com.weihua.jrpc.client.prop.MjProperties;
import com.weihua.jrpc.client.proxy.MjRpcAsyncInvoke;
import com.weihua.jrpc.client.proxy.RpcProxyImpl;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xie wei hua
 * @version 1.0
 * @date 2021/4/3 9:57
 * @description client
 */
public abstract class AbstractClient implements ClientInit {

    public MjRpcConnectManager mjRpcConnectManager;

    public AbstractClient(){};

    public AbstractClient(MjRpcConnectManager mjRpcConnectManager){
        this.mjRpcConnectManager = mjRpcConnectManager;
    }

    /**
     * synchronous proxy
     */
    public final Map<Class<?>, Object> asyncProxyInstanceMap = new ConcurrentHashMap<>();

    /**
     * asynchronous proxy
     */
    public final Map<Class<?>, Object> syncProxyInstanceMap = new ConcurrentHashMap<>();

    /**
     * synchronous connect server
     */
    public abstract <T> T syncInvoke(Class<T> interfaceClazz);

    /**
     * connect server only address
     * @param address
     */
    public abstract void connect(String address);

    /**
     * connect server need address , timeout of params
     * @param address
     * @param timeout
     */
    public abstract void connect(String address, int timeout);

    /**
     * connect server params is serverAddress List
     * @param serverAddress
     */
    public abstract void connect(List<String> serverAddress);

    /**
     * connect  server addressList
     * @param serverAddress
     * @param timeout
     */
    public abstract void connect(List<String> serverAddress, int timeout);

    /**
     * check take token
     * @param address
     * @param timeout
     * @param token
     */
    public abstract void connect(String address, int timeout, String token);

    /**
     * take token check
     * @param address
     * @param timeout
     * @param token
     */
    public abstract void connect(List<String> address, int timeout, String token);

    /**
     * connect and invoke of synchronize invoke
     * @param mjProperties
     * @return
     */
    public abstract Object connectAndInvoke(MjProperties mjProperties);

    /**
     * connect and async invoke
     * @param mjProperties
     * @return
     */
    public abstract MjRpcFuture connectAndAsyncInvoke(MjProperties mjProperties, String funName, Object... params);

    /**
     * asynchronous request
     */
    public abstract <T> MjRpcAsyncInvoke asyncInvoke(Class<T> interfaceClazz);

    /**
     * connect timeout if not value default 6 seconds
     */
    public int timeout = 6;

    /**
     * serverAddress
     */
    public String serverAddress;

    /**
     * list serverAdd
     */
    private List<String> serverAddressList;

    /**
     * token
     */
    public String token;

    public ReentrantLock reentrantLock = new ReentrantLock();

    @Override
    public void init() {
        this.serverAddress = serverAddress;
        this.timeout = timeout;
        this.mjRpcConnectManager = new MjRpcConnectManager();
    }

    @Override
    public <T> T init(List<String> serverAddressList, int timeout, Class<T> interfaceClazz) {
        this.serverAddressList = serverAddressList;
        this.timeout = timeout;
        this.mjRpcConnectManager = new MjRpcConnectManager();
        this.mjRpcConnectManager.connect(this.serverAddressList);
        return (T) Proxy.newProxyInstance(interfaceClazz.getClassLoader(),
                new Class<?>[]{interfaceClazz},
                new RpcProxyImpl<>(mjRpcConnectManager,interfaceClazz,timeout,token));
    }
}

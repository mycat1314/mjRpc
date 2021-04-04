package com.weihua.jrpc.client.proxy;

import com.weihua.jrpc.client.MjRpcClientHandler;
import com.weihua.jrpc.client.MjRpcConnectManager;
import com.weihua.jrpc.client.MjRpcFuture;
import com.weihua.jrpc.client.codec.MjRpcRequest;
import com.weihua.jrpc.client.invoke.entity.AsyncParams;
import com.weihua.jrpc.client.prop.MjProperties;
import com.weihua.jrpc.utils.UuidUtils;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author xie wei hua
 * @version 1.0
 * @date 2021/4/1 13:26
 * @description do somethings
 */
public class RpcProxyImpl<T> implements InvocationHandler, MjRpcAsyncInvoke {

    private Class<T> clazz;

    private long timeout;

    private MjRpcConnectManager mjRpcConnectManager;

    private String token;

    private MjProperties properties;

    public RpcProxyImpl(MjRpcConnectManager mjRpcConnectManager, Class<T> clazz, MjProperties properties) {
        this.clazz = clazz;
        this.timeout = properties.getTimeout();
        this.mjRpcConnectManager = mjRpcConnectManager;
        this.token = properties.getAccessToken();
        this.properties = properties;
    }

    public RpcProxyImpl(MjRpcConnectManager mjRpcConnectManager, Class<T> clazz, int timeout, String token) {
        this.clazz = clazz;
        this.timeout = timeout;
        this.mjRpcConnectManager = mjRpcConnectManager;
        this.token = token;
    }



    /**
     * invoke
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // build Request obj
        MjRpcRequest request = new MjRpcRequest();
        request.buildRequestId(UUID.randomUUID().toString())
                .buildClassName(method.getDeclaringClass().getName())
                .buildMethodName(method.getName())
                .buildParamsTypes(method.getParameterTypes())
                .buildParamters(args)
                .buildAccessToken(token);

        // 2. 选择一个合适client任务处理器
        MjRpcClientHandler mjRpcClientHandler = this.mjRpcConnectManager.chooseHandler(properties);
        // 3. 发送真正的客户端请求 返回结果
        MjRpcFuture mjRpcFuture = mjRpcClientHandler.sendRequest(request);
        return mjRpcFuture.get(timeout, TimeUnit.SECONDS);
    }

    /**
     *
     * @param funcName
     * @param args
     * @return
     */
    @Override
    public MjRpcFuture call(String funcName, Object... args) {
        Class<?>[] parameterTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            parameterTypes[i] = getClassType(args[i]);
        }
        // build Request obj
        MjRpcRequest mjRpcRequest = new MjRpcRequest();
        mjRpcRequest.buildRequestId(UuidUtils.getUuid())
                .buildClassName(this.clazz.getName())
                .buildMethodName(funcName)
                .buildParamters(args)
                .buildParamsTypes(parameterTypes);
        MjRpcFuture mjRpcFuture = this.mjRpcConnectManager.remoteAsyncInvoke(mjRpcRequest);
        return mjRpcFuture;
    }

    @Override
    public MjRpcFuture callWithProp(AsyncParams asyncParams) {
        Object[] args = asyncParams.getArgs();
        String methodName = asyncParams.getMethodName();
        Class<?>[] parameterTypes = null;
        if (args.length > 0 ) {
            parameterTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                parameterTypes[i] = getClassType(args[i]);
            }
        }
        // build Request obj
        MjRpcRequest mjRpcRequest = new MjRpcRequest();
        mjRpcRequest.buildRequestId(UuidUtils.getUuid())
                .buildClassName(this.clazz.getName())
                .buildMethodName(methodName)
                .buildParamters(args)
                .buildParamsTypes(parameterTypes);
        MjRpcFuture mjRpcFuture = mjRpcConnectManager.remoteAsyncInvoke(mjRpcRequest,asyncParams,this.properties);
        return mjRpcFuture;
    }

    private Class<?> getClassType(Object obj) {
        Class<?> classType = obj.getClass();
        String typeName = classType.getName();
        if (typeName.equals("java.lang.Integer")) {
            return Integer.TYPE;
        } else if (typeName.equals("java.lang.Long")) {
            return Long.TYPE;
        } else if (typeName.equals("java.lang.Float")) {
            return Float.TYPE;
        } else if (typeName.equals("java.lang.Double")) {
            return Double.TYPE;
        } else if (typeName.equals("java.lang.Character")) {
            return Character.TYPE;
        } else if (typeName.equals("java.lang.Boolean")) {
            return Boolean.TYPE;
        } else if (typeName.equals("java.lang.Short")) {
            return Short.TYPE;
        } else if (typeName.equals("java.lang.Byte")) {
            return Byte.TYPE;
        }
        return classType;
    }


}

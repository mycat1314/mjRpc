package com.weihua.jrpc.client.invoke;

import com.weihua.jrpc.client.MjRpcFuture;
import com.weihua.jrpc.client.invoke.entity.AsyncParams;

public interface AsyncInvoke {
    <T> T getInstance();

    MjRpcFuture async(String methodName, Object... params);

    MjRpcFuture async(AsyncParams asyncParams);

}

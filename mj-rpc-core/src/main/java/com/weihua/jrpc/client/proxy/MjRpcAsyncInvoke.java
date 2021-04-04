package com.weihua.jrpc.client.proxy;

import com.weihua.jrpc.client.MjRpcFuture;
import com.weihua.jrpc.client.invoke.entity.AsyncParams;

/**
 * 	async
 * @author xie wei hua
 *
 */
public interface MjRpcAsyncInvoke {

	/**
	 * async remote invoke
	 * @param funcName
	 * @param args
	 * @return
	 */
	MjRpcFuture call(String funcName, Object... args);

	/**
	 * async remote invoke but can choose loadBalancing
	 * @param asyncParams
	 * @return
	 */
	MjRpcFuture callWithProp(AsyncParams asyncParams);
	
}

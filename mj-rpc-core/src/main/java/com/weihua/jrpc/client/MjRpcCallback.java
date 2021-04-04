package com.weihua.jrpc.client;

public interface MjRpcCallback {

	void success(Object result);
	
	void failure(Throwable cause);
	
}

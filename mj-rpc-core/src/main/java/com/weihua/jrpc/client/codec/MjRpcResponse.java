package com.weihua.jrpc.client.codec;


import java.io.Serializable;

/**
 * response entity
 */
public class MjRpcResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private String requestId;
	
	private Object result;
	
	private Throwable throwable;

	public MjRpcResponse buildRequestId(String requestId) {
		this.requestId = requestId;
		return this;
	}

	public MjRpcResponse buildResult(String result) {
		this.result = result;
		return this;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}

	@Override
	public String toString() {
		return "RpcResponse{" +
				"requestId='" + requestId + '\'' +
				", result=" + result +
				", throwable=" + throwable +
				'}';
	}
}

package com.weihua.jrpc.config;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class MjRpcConfigAbstract {
	
	private AtomicInteger generator = new AtomicInteger(0);
	
	protected String id;
	
	protected String interfaceClass = null;

	protected String token;

	protected Class<?> proxyClass = null;
	
	public String getId() {
		if(StringUtils.isBlank(id)) {
			id = "mjrpc-gen-" + generator.getAndIncrement();
		}
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setInterface(String interfaceClass) {
		this.interfaceClass = interfaceClass;
	}
	
	public String getInterface() {
		return this.interfaceClass;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}

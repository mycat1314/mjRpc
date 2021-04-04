package com.weihua.jrpc.client.codec;


import java.io.Serializable;
import java.util.Arrays;

/**
 * 	request entity
 * @author xieweihua
 *
 */
public class MjRpcRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * requestId
	 */
	private String requestId;

	/**
	 * clazzName
	 */
	private String className;

	/**
	 * methodName
	 */
	private String methodName;

	/**
	 * paramsType
	 */
	private Class<?>[] paramterTypes;

	/**
	 * parameters
	 */
	private Object[] paramters;


	/**
	 * version
	 */
	private String version;

	/**
	 * accessToken
	 */
	private String accessToken;


	public MjRpcRequest buildRequestId(String requestId) {
		this.requestId = requestId;
		return this;
	}

	public MjRpcRequest buildClassName(String className) {
		this.className = className;
		return this;
	}

	public MjRpcRequest buildMethodName(String methodName) {
		this.methodName = methodName;
		return this;
	}

	public MjRpcRequest buildParamsTypes(Class<?>[] paramterTypes) {
		if (paramterTypes.length > 0) {
			this.paramterTypes = paramterTypes;
		}
		return this;
	}

	public MjRpcRequest buildParamters(Object[] paramters) {
		this.paramters = paramters;
		return this;
	}

	public MjRpcRequest buildVersion(String version) {
		this.version = version;
		return this;
	}

	public MjRpcRequest buildAccessToken(String accessToken) {
		this.accessToken = accessToken;
		return this;
	}

	public MjRpcRequest build(){
		MjRpcRequest mjRpcRequest = new MjRpcRequest();
		mjRpcRequest.setAccessToken(this.accessToken);
		mjRpcRequest.setRequestId(this.requestId);
		mjRpcRequest.setClassName(this.className);
		mjRpcRequest.setMethodName(this.methodName);
		mjRpcRequest.setParamters(this.paramters);
		mjRpcRequest.setParamterTypes(this.paramterTypes);
		mjRpcRequest.setVersion(this.version);
		return mjRpcRequest;
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

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Class<?>[] getParamterTypes() {
		return paramterTypes;
	}

	public void setParamterTypes(Class<?>[] paramterTypes) {
		this.paramterTypes = paramterTypes;
	}

	public Object[] getParamters() {
		return paramters;
	}

	public void setParamters(Object[] paramters) {
		this.paramters = paramters;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	@Override
	public String toString() {
		return "MjRpcRequestMjRpcRequest{" +
				"requestId='" + requestId + '\'' +
				", className='" + className + '\'' +
				", methodName='" + methodName + '\'' +
				", paramterTypes=" + Arrays.toString(paramterTypes) +
				", paramters=" + Arrays.toString(paramters) +
				", version='" + version + '\'' +
				", accessToken='" + accessToken + '\'' +
				'}';
	}
}

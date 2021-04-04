package com.weihua.jrpc.client.prop;

import com.weihua.jrpc.client.invoke.stragy.LoadBalancingStrategy;
import com.weihua.jrpc.client.invoke.stragy.enums.LoadBalancingEnum;

/**
 * @author xie wei hua
 * @version 1.0
 * @date 2021/4/3 14:53
 * @description properties config
 */
public class MjProperties {
    private static final long serialVersionUID = 1L;

    /**
     * invoke interface
     */
    private Class<?> clazz;

    /**
     * version
     */
    private String version;

    /**
     * accessToken
     */
    private String accessToken;

    /**
     * server address, if multipart server address please use ','
     */
    private String address;

    /**
     * request timeout time
     */
    private int timeout = 6;

    /**
     * default loadBalancingStrategy
     */
    private LoadBalancingStrategy loadBalancing = LoadBalancingEnum.ROUND.loadBalancingStrategy;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public LoadBalancingStrategy getLoadBalancing() {
        return loadBalancing;
    }

    public void setLoadBalancing(LoadBalancingStrategy loadBalancing) {
        this.loadBalancing = loadBalancing;
    }
}

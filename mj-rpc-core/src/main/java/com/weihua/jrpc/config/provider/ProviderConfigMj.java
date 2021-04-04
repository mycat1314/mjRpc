package com.weihua.jrpc.config.provider;

import com.weihua.jrpc.config.MjRpcConfigAbstract;

/**
 * @author xie wei hua
 * @version 1.0
 * @date 2021/4/1 15:44
 * @description do somethings
 */
public class ProviderConfigMj extends MjRpcConfigAbstract {
    protected Object ref;

    /**
     * address
     */
    protected String address;

    protected String version = "1.0.0";

    /**
     * weight
     */
    protected int weight = 1;

    public Object getRef() {
        return ref;
    }

    public void setRef(Object ref) {
        this.ref = ref;
    }

    public String getAddress() {
        return address;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}

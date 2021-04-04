package com.weihua.jrpc.client.invoke.entity;

import com.weihua.jrpc.client.invoke.stragy.LoadBalancingStrategy;
import com.weihua.jrpc.client.invoke.stragy.enums.LoadBalancingEnum;

/**
 * @author xie wei hua
 * @version 1.0
 * @date 2021/4/4 17:45
 * @description async params
 */
public class AsyncParams {
   private String methodName;
   private Object[] args;

    private LoadBalancingStrategy loadBalancingStrategy = LoadBalancingEnum.ROUND.loadBalancingStrategy;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public LoadBalancingStrategy getLoadBalancingStrategy() {
        return loadBalancingStrategy;
    }

    public void setLoadBalancingStrategy(LoadBalancingStrategy loadBalancingStrategy) {
        this.loadBalancingStrategy = loadBalancingStrategy;
    }
}

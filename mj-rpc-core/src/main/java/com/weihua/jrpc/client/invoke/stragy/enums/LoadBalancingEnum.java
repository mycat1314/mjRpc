package com.weihua.jrpc.client.invoke.stragy.enums;

import com.weihua.jrpc.client.invoke.stragy.LoadBalancingStrategy;
import com.weihua.jrpc.client.invoke.stragy.impl.HashStrategy;
import com.weihua.jrpc.client.invoke.stragy.impl.RandomStrategy;
import com.weihua.jrpc.client.invoke.stragy.impl.RoundStrategy;

import java.net.InetSocketAddress;
import java.util.TreeSet;

/**
 * loadBalancing enum
 */
public enum LoadBalancingEnum {

    ROUND(new RoundStrategy()),
    RANDOM(new RandomStrategy()),
    HASH(new HashStrategy())
    ;
    public final LoadBalancingStrategy loadBalancingStrategy;
    private LoadBalancingEnum(LoadBalancingStrategy loadBalancingStrategy) {
        this.loadBalancingStrategy = loadBalancingStrategy;
    }



}

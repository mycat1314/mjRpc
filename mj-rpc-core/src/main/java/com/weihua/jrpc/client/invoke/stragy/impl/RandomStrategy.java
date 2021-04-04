package com.weihua.jrpc.client.invoke.stragy.impl;

import com.weihua.jrpc.client.invoke.stragy.LoadBalancingStrategy;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * @author xie wei hua
 * @version 1.0
 * @date 2021/4/3 16:55
 * @description randomStrategy
 */
public class RandomStrategy implements LoadBalancingStrategy {
    private final Random random = new Random();

    @Override
    public InetSocketAddress loadBalancing(String key, Set<InetSocketAddress> list) {
        int i = random.nextInt(list.size());
        InetSocketAddress[] arr = list.toArray(new InetSocketAddress[list.size()]);
        return arr[i];
    }


}

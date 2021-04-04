package com.weihua.jrpc.client.invoke.stragy.impl;

import com.weihua.jrpc.client.invoke.stragy.LoadBalancingStrategy;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xie wei hua
 * @version 1.0
 * @date 2021/4/3 16:34
 * @description roundStrategy
 */
public class RoundStrategy implements LoadBalancingStrategy {
    private Map<String,Integer> map = new ConcurrentHashMap<>();
    @Override
    public InetSocketAddress loadBalancing(String key, Set<InetSocketAddress> list) {
        Integer count = map.get(key);
        if (count == null) {
            count = new Random().nextInt(1000);
        }else {
            ++count;
        }
        map.put(key,count);
        InetSocketAddress[] arr = list.toArray(new InetSocketAddress[list.size()]);
        return arr[count % list.size()];
    }
}

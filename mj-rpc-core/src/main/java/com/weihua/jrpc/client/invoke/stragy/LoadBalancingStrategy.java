package com.weihua.jrpc.client.invoke.stragy;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author xie wei hua
 * @version 1.0
 * @date 2021/4/3 16:32
 * @description loadBalancingStrategy
 */
public interface LoadBalancingStrategy {

    public InetSocketAddress loadBalancing(String key, Set<InetSocketAddress> list);
}

package com.weihua.jrpc.client.invoke.stragy.impl;

import com.weihua.jrpc.client.invoke.stragy.LoadBalancingStrategy;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author xie wei hua
 * @version 1.0
 * @date 2021/4/3 17:18
 * @description hashStrategy
 */
public class HashStrategy implements LoadBalancingStrategy {
    @Override
    public InetSocketAddress loadBalancing(String key, Set<InetSocketAddress> list) {
        TreeMap<Long, InetSocketAddress> map = new TreeMap<Long, InetSocketAddress>();
        for (InetSocketAddress server : list) {
            Long hash = hash(server.toString());
            map.put(hash,server);
        }
        if (key != null) {
            Long hash = hash(key);
            SortedMap<Long, InetSocketAddress> lastRing = map.tailMap(hash);
            if (!lastRing.isEmpty()) {
                return lastRing.get(lastRing.firstKey());
            }
        }
        return map.firstEntry().getValue();
    }

    /**
     * MurMurHash算法,性能高,碰撞率低
     * @param key String
     * @return Long
     */
    public Long hash(String key) {
        ByteBuffer buf = ByteBuffer.wrap(key.getBytes());
        int seed = 0x1234ABCD;

        ByteOrder byteOrder = buf.order();
        buf.order(ByteOrder.LITTLE_ENDIAN);

        long m = 0xc6a4a7935bd1e995L;
        int r = 47;

        long h = seed ^ (buf.remaining() * m);

        long k;
        while (buf.remaining() >= 8) {
            k = buf.getLong();

            k *= m;
            k ^= k >>> r;
            k *= m;

            h ^= k;
            h *= m;
        }

        if (buf.remaining() > 0) {
            ByteBuffer finish = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
            finish.put(buf).rewind();
            h ^= finish.getLong();
            h *= m;
        }

        h ^= h >>> r;
        h *= m;
        h ^= h >>> r;

        buf.order(byteOrder);
        return h;

    }


}

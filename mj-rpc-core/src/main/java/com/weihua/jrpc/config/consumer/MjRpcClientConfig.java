package com.weihua.jrpc.config.consumer;

import com.weihua.jrpc.registry.MjRpcRegistryConsumerService;

/**
 * @author xie wei hua
 * @version 1.0
 * @date 2021/4/2 9:23
 * @description do somethings
 */
public class MjRpcClientConfig {
    private MjRpcRegistryConsumerService rpcRegistryConsumerService;

    public MjRpcClientConfig(MjRpcRegistryConsumerService rpcRegistryConsumerService) {
        this.rpcRegistryConsumerService = rpcRegistryConsumerService;
    }

    /**
     * getConsumer
     * @param <T>
     * @param clazz
     * @param version
     * @return ConsumerConfig
     */
    public <T> ConsumerConfigMj<?> getConsumer(Class<T> clazz, String version){
        return rpcRegistryConsumerService.getConsumer(clazz.getName(), version);
    }
}

package com.weihua.jrpc.invoke.provider;

import com.weihua.jrpc.config.provider.ProviderConfigMj;
import com.weihua.jrpc.config.provider.MjRpcServerConfig;
import com.weihua.jrpc.registry.MjRpcRegistryProviderService;
import com.weihua.jrpc.zookeeper.CuratorImpl;
import com.weihua.jrpc.zookeeper.ZookeeperClient;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xie wei hua
 * @version 1.0
 * @date 2021/4/2 9:25
 * @description do somethings
 */
public class ZookeeperProvider {
    public static void main(String[] args) {
        new Thread(()->{
            try {
                ProviderConfigMj providerConfig = new ProviderConfigMj();
                providerConfig.setInterface("com.weihua.jrpc.invoke.consumer.HelloService");
                HelloServiceImpl hellpHelloServiceImpl = HelloServiceImpl.class.newInstance();
                providerConfig.setRef(hellpHelloServiceImpl);

                List<ProviderConfigMj> providerConfigs = new ArrayList<ProviderConfigMj>();
                providerConfigs.add(providerConfig);

                ZookeeperClient zookeeperClient = new CuratorImpl("127.0.0.1:2181", 10000);
                MjRpcRegistryProviderService registryProviderService = new MjRpcRegistryProviderService(zookeeperClient);
                MjRpcServerConfig mjRpcServerConfig = new MjRpcServerConfig(providerConfigs, registryProviderService);
                mjRpcServerConfig.setPort(8766);
                mjRpcServerConfig.exporter();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}

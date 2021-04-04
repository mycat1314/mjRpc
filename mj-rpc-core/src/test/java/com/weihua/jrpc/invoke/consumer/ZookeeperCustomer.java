package com.weihua.jrpc.invoke.consumer;

import com.weihua.jrpc.config.consumer.ConsumerConfigMj;
import com.weihua.jrpc.config.consumer.MjRpcClientConfig;
import com.weihua.jrpc.registry.MjRpcRegistryConsumerService;
import com.weihua.jrpc.zookeeper.CuratorImpl;
import com.weihua.jrpc.zookeeper.ZookeeperClient;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xie wei hua
 * @version 1.0
 * @date 2021/4/2 9:35
 * @description do somethings
 */
@Slf4j
public class ZookeeperCustomer {
    public static void main(String[] args) {
        try {
            ZookeeperClient zookeeperClient = new CuratorImpl("127.0.0.1:2181", 10000);
            MjRpcRegistryConsumerService rpcRegistryConsumerService = new MjRpcRegistryConsumerService(zookeeperClient);
            MjRpcClientConfig mjRpcClientConfig = new MjRpcClientConfig(rpcRegistryConsumerService);

            Thread.sleep(1000);

            ConsumerConfigMj<HelloService> consumerConfig = (ConsumerConfigMj<HelloService>) mjRpcClientConfig.getConsumer(HelloService.class, "1.0.0");
            HelloService helloService = consumerConfig.getProxyInstance();
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < 10000; i++) {
                String result1 = helloService.hello("hello"+i);
                log.info("rs:"+result1);
            }
            long endTime = System.currentTimeMillis();
            log.info("耗时:"+(endTime-startTime)/1000+"秒");
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

}

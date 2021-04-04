package com.weihua.jrpc.invoke.consumer;

import com.weihua.jrpc.client.MjClient;
import com.weihua.jrpc.client.MjRpcFuture;
import com.weihua.jrpc.client.invoke.entity.AsyncParams;
import com.weihua.jrpc.client.invoke.stragy.enums.LoadBalancingEnum;
import com.weihua.jrpc.client.prop.MjProperties;
import com.weihua.jrpc.invoke.provider.HelloServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;


/**
 * @author xie wei hua
 * @version 1.0
 * @date 2021/4/1 15:38
 * @description do somethings
 */
@Slf4j
public class ConsumerStarter {
    public static void sync() {
        // rpcClient
//        MjClient rpcClient = new MjClient();
//        rpcClient.connect("127.0.0.1:8765", 3000);
//        long beginTime = System.currentTimeMillis();
//        for (int i = 0; i < 10000; i++) {
//            HelloService helloService = rpcClient.syncInvoke(HelloService.class);
//            String result = helloService.hello2("zhang3");
//            log.info(result);
//        }
//        RpcAsyncProxy rpcAsyncProxy = rpcClient.asyncInvoke(HelloService.class);
//        RpcFuture call = rpcAsyncProxy.call("hello", "洗洗碗");
//        try {
//            Object o = call.get();
//            log.info(""+o);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//
//        long endTime = System.currentTimeMillis();
//        log.info("耗时:"+(endTime-beginTime));
        MjProperties mjProperties = new MjProperties();
        mjProperties.setAddress("127.0.0.1:8765,127.0.0.1:8766,127.0.0.1:8767");
        mjProperties.setTimeout(3000);
        mjProperties.setClazz(HelloService.class);
        mjProperties.setVersion("1.0");
        mjProperties.setLoadBalancing(LoadBalancingEnum.HASH.loadBalancingStrategy);
        MjClient mjClient = new MjClient();
        //mjClient.connect
        AsyncParams asyncParams = new AsyncParams();
        asyncParams.setMethodName("hello");
        asyncParams.setArgs(new Object[]{"哈啊哈哈"});
        asyncParams.setLoadBalancingStrategy(LoadBalancingEnum.ROUND.loadBalancingStrategy);
        MjRpcFuture mjRpcFuture = mjClient.connectAndAsyncInvoke(mjProperties, asyncParams);
        try {
            Object o = mjRpcFuture.get();
            System.out.println(o);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }

    public static void local() {
        HelloService helloService = new HelloServiceImpl();
        long beginTime = System.currentTimeMillis();
        for(int i = 0; i < 10000; i++) {
            String hello = helloService.hello("zhang" + i);
            log.info(hello);
        }
        long endTime = System.currentTimeMillis();
        log.info("耗时:"+(endTime-beginTime));
    }

    public static void main(String[] args) {
        sync();
    }
}

package com.weihua.jrpc.invoke.provider;

import com.weihua.jrpc.config.provider.ProviderConfigMj;
import com.weihua.jrpc.config.provider.MjRpcServerConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xie wei hua
 * @version 1.0
 * @date 2021/4/1 15:43
 * @description do somethings
 */
public class ProviderStarter {
    public static void main(String[] args) {

        //	服务端启动
        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    // 每一个具体的服务提供者的配置类
                    ProviderConfigMj providerConfig = new ProviderConfigMj();
                    providerConfig.setInterface("com.weihua.jrpc.invoke.consumer.HelloService");
                    HelloServiceImpl hellpHelloServiceImpl = HelloServiceImpl.class.newInstance();
                    providerConfig.setRef(hellpHelloServiceImpl);

                    //	把所有的ProviderConfig 添加到集合中
                    List<ProviderConfigMj> providerConfigs = new ArrayList<ProviderConfigMj>();
                    providerConfigs.add(providerConfig);

                   MjRpcServerConfig mjRpcServerConfig = new MjRpcServerConfig(providerConfigs);
                   mjRpcServerConfig.setPort(8766);
                   mjRpcServerConfig.exporter();
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

    }
}

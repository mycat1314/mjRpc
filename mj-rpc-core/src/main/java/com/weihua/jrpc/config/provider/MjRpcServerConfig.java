package com.weihua.jrpc.config.provider;

import java.util.List;

import com.weihua.jrpc.registry.MjRpcRegistryProviderService;
import com.weihua.jrpc.server.MjRpcServer;

public class MjRpcServerConfig {

	private final String host = "127.0.0.1";

	protected int port;

	private List<ProviderConfigMj> providerConfigs ;

	private MjRpcServer mjRpcServer = null;

	private MjRpcRegistryProviderService rpcRegistryProviderService;


	public MjRpcServerConfig(List<ProviderConfigMj> providerConfigs) {
		this.providerConfigs = providerConfigs;
	}

	/**
	 * RpcServerConfig
	 * @param providerConfigs 服务提供方的元数据信息列表
	 * @param rpcRegistryProviderService 注册服务
	 */
	public MjRpcServerConfig(List<ProviderConfigMj> providerConfigs,
							 MjRpcRegistryProviderService rpcRegistryProviderService) {
		this.providerConfigs = providerConfigs;
		this.rpcRegistryProviderService = rpcRegistryProviderService;
	}


	public void exporter() {
		if(mjRpcServer == null) {
			try {
				mjRpcServer = new MjRpcServer(host + ":" + port);
			} catch (Exception e) {

			}
		}
		for(ProviderConfigMj providerConfig: providerConfigs) {
			try {
				mjRpcServer.registerProcessor(providerConfig);
				//rpcServer.start();
			} catch (Exception e) {
				e.printStackTrace();
			}


			//	引入注册中心：

			//	设置当前注册服务(providerConfig)的服务地址
			providerConfig.setAddress(host + ":" + port);

			if(rpcRegistryProviderService != null) {
				try {
					//	把当前的providerConfig里面的元数据信息注册到zookeeper上去
					rpcRegistryProviderService.registry(providerConfig);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}

	public MjRpcServer getMjRpcServer() {
		return mjRpcServer;
	}

	public void setMjRpcServer(MjRpcServer mjRpcServer) {
		this.mjRpcServer = mjRpcServer;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}



}

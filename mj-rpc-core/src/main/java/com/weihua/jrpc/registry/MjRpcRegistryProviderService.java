package com.weihua.jrpc.registry;


import com.weihua.jrpc.config.provider.ProviderConfigMj;
import com.weihua.jrpc.utils.FastJsonConvertUtil;
import com.weihua.jrpc.zookeeper.ZookeeperClient;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * mjrpc register
 */
public class MjRpcRegistryProviderService extends MjRpcRegistryAbstract {

	private ZookeeperClient zookeeperClient;
	
	public MjRpcRegistryProviderService(ZookeeperClient zookeeperClient) throws Exception {
		this.zookeeperClient = zookeeperClient;
		if(!zookeeperClient.checkExists(ROOT_PATH)) {
			zookeeperClient.addPersistentNode(ROOT_PATH, ROOT_VALUE);
		}
	}
	

	public void registry(ProviderConfigMj providerConfig) throws Exception {
		String interfaceClass = providerConfig.getInterface();
		Object ref = providerConfig.getRef();
		String version = providerConfig.getVersion();
		String registeryKey = ROOT_PATH + "/" + interfaceClass + ":" + version ;
		
		if(!zookeeperClient.checkExists(registeryKey)) {
			Method[] methods = ref.getClass().getDeclaredMethods();
			Map<String, String> methodMap = new HashMap<String, String>();
			
			for(Method method : methods) {
				String methodName = method.getName();
				Class<?>[] parameterTypes = method.getParameterTypes();
				String methodParameterTypes = "";
				for(Class<?> clazz : parameterTypes) {
					String parameterTypeName = clazz.getName();
					methodParameterTypes += parameterTypeName + ",";
				}
				
				String key = methodName + "@" + methodParameterTypes.substring(0, methodParameterTypes.length()-1);
				methodMap.put(key, key);
			}
			
			zookeeperClient.addPersistentNode(registeryKey,
					FastJsonConvertUtil.convertObjectToJSON(methodMap));
			
			zookeeperClient.addPersistentNode(registeryKey + PROVIDERS_PATH, "");
		} 
		
		String address = providerConfig.getAddress();
		String registerInstanceKey = registeryKey + PROVIDERS_PATH + "/" + address;
		
		Map<String, String> instanceMap = new HashMap<String, String>();
		instanceMap.put("weight", providerConfig.getWeight() + "");
		
		this.zookeeperClient.addEphemeralNode(registerInstanceKey,
				FastJsonConvertUtil.convertObjectToJSON(instanceMap));
		
	}
	
}

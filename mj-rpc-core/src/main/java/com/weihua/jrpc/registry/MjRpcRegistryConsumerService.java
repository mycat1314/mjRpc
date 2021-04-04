package com.weihua.jrpc.registry;

import com.weihua.jrpc.client.MjClient;
import com.weihua.jrpc.config.consumer.CachedService;
import com.weihua.jrpc.config.consumer.ConsumerConfigMj;
import com.weihua.jrpc.utils.FastJsonConvertUtil;
import com.weihua.jrpc.zookeeper.ChangedEvent;
import com.weihua.jrpc.zookeeper.NodeListener;
import com.weihua.jrpc.zookeeper.ZookeeperClient;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xie wei hua
 * @version 1.0
 * @date 2021/4/1 19:19
 * @description do somethings
 */
public class MjRpcRegistryConsumerService extends MjRpcRegistryAbstract implements NodeListener {
    private ZookeeperClient zookeeperClient;

    private ConcurrentHashMap<String , List<CachedService>> CACHED_SERVICES = new ConcurrentHashMap<String, List<CachedService>>();

    private ConcurrentHashMap<String , ConsumerConfigMj<?>> CACHED_CONSUMERCONFIGS = new ConcurrentHashMap<String, ConsumerConfigMj<?>>();

    private final ReentrantLock LOCK = new ReentrantLock();

    public MjRpcRegistryConsumerService(ZookeeperClient zookeeperClient) throws Exception {
        this.zookeeperClient = zookeeperClient;
        if(!zookeeperClient.checkExists(ROOT_PATH)) {
            zookeeperClient.addPersistentNode(ROOT_PATH, ROOT_VALUE);
        }
        this.zookeeperClient.listener4ChildrenPath(ROOT_PATH, this);
    }

    public ConsumerConfigMj<?> getConsumer(String interfaceClass, String version){
        return CACHED_CONSUMERCONFIGS.get(interfaceClass + ":" + version);
    }

    @Override
    public void nodeChanged(ZookeeperClient client, ChangedEvent event) throws Exception {

        String path = event.getPath();
        String data = event.getData();
        ChangedEvent.Type type = event.getType();
        if(ChangedEvent.Type.CHILD_ADDED == type) {

            String[] pathArray = null;
            if(!StringUtils.isBlank(path) && (pathArray = path.substring(1).split("/")).length == 2) {
                this.zookeeperClient.listener4ChildrenPath(path,  this);
            }
            if(!StringUtils.isBlank(path) && (pathArray = path.substring(1).split("/")).length == 3) {
                this.zookeeperClient.listener4ChildrenPath(path,  this);
            }
            if(!StringUtils.isBlank(path) && (pathArray = path.substring(1).split("/")).length == 4) {
                try {
                    LOCK.lock();
                    String interfaceClassWithV = pathArray[1];
                    String[] arrays = interfaceClassWithV.split(":");
                    String interfaceClass = arrays[0];
                    String version = arrays[1];

                    String address = pathArray[3];
                    @SuppressWarnings("unchecked")
                    Map<String, String> map = FastJsonConvertUtil.convertJSONToObject(data, Map.class);
                    int weight = Integer.parseInt(map.get("weight"));
                    CachedService cs = new CachedService(address, weight);

                    List<CachedService> addresses = CACHED_SERVICES.get(interfaceClass + ":" + version);
                    if(addresses == null) {
                        CopyOnWriteArrayList<CachedService> newAddresses = new CopyOnWriteArrayList<CachedService>();
                        newAddresses.add(cs);
                        CACHED_SERVICES.put(interfaceClassWithV, newAddresses);
                        ConsumerConfigMj<?> consumerConfig = new ConsumerConfigMj<>();
                        consumerConfig.setInterface(interfaceClass);
                        CopyOnWriteArrayList<String> urls = new CopyOnWriteArrayList<String>();
                        for(int i = 0; i< weight; i ++) {
                            urls.add(address);
                        }
                        consumerConfig.setUrl(urls);
                        consumerConfig.setToken("");
                        consumerConfig.initRpcClient();
                        CACHED_CONSUMERCONFIGS.put(interfaceClass + ":" + version, consumerConfig);

                    } else {
                        addresses.add(cs);
                        ConsumerConfigMj<?> consumerConfig = CACHED_CONSUMERCONFIGS.get(interfaceClassWithV);
                        MjClient rpcClient = consumerConfig.getClient();
                        CopyOnWriteArrayList<String> urls = new CopyOnWriteArrayList<String>();
                        for(CachedService cachedService: addresses) {
                            int cWeight = cachedService.getWeight();
                            for(int i = 0 ; i < cWeight; i ++) {
                                urls.add(cachedService.getAddress());
                            }
                        }
                        consumerConfig.setUrl(urls);
                        rpcClient.connect(urls);
                    }
                } finally {
                    LOCK.unlock();
                }
            }

        }

    }
}

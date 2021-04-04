package com.weihua.jrpc.zookeeper;

/**
 *	NodeListener
 */
public interface NodeListener {
    void nodeChanged(ZookeeperClient client, ChangedEvent event) throws Exception;
}

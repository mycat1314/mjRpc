package com.weihua.jrpc.zookeeper;

/**
 *	ConnectionStateListener
 *	connect state manage
 */
public interface ConnectionStateListener {
    void stateChanged(ZookeeperClient client, ConnectionState state);
}


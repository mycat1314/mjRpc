package com.weihua.jrpc.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * zookeeper
 */
public interface ZookeeperClient {
    /**
     * 	addPersistentNode
     * @param path
     * @param data
     * @throws Exception
     */
    void addPersistentNode(String path, String data) throws Exception ;

    /**
     * @throws Exception
     */
    String addEphemeralNode(String path, String data) throws Exception;

    /**
     * @return
     * @throws Exception
     */
    Stat setData(String path, String data) throws Exception;

    /**
     * @param path
     * @return
     * @throws Exception
     */
    String getData(String path) throws Exception;

    /**
     * @param path
     * @throws Exception
     */
    void deletePath(String path) throws Exception;

    /**
     * @return
     */
    boolean isConnected();

    /**
     */
    List<String> getNodes(String path);

    /**
     * @param path
     * @return
     * @throws Exception
     */
    boolean checkExists(String path) throws Exception;

    /**
     * @param parent   		/parent
     * @param listener		/aaa, /bbb
     * @throws Exception
     *
     */
    void listener4ChildrenPath(final String parent, final NodeListener listener) throws Exception;

    void close();

    CuratorFramework getClient();
}

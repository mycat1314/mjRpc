package com.weihua.jrpc.server.abs;

import com.weihua.jrpc.event.ChooseBootstrapEvent;
import io.netty.channel.EventLoopGroup;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xie wei hua
 * @version 1.0
 * @date 2021/4/3 9:59
 * @description server top defined
 */
public abstract class AbstractServer {

    private static final String THREAD_BOSS_FACTORY_NAME = "mjRpc-boos-server";
    private static final String THREAD_WORK_FACTORY_NAME = "mjRpc-work-server";
    public String serverAddress;
    public EventLoopGroup bossGroup = ChooseBootstrapEvent.choseEventLoop(THREAD_BOSS_FACTORY_NAME);
    public EventLoopGroup workerGroup = ChooseBootstrapEvent.choseEventLoop(THREAD_WORK_FACTORY_NAME);
    public volatile Map<String, Object> handlerMap = new HashMap<String, Object>(){
    };

    /**
     * start server
     */
    public abstract void start();

    /**
     * stop server
     */
    public abstract void stop();

    /**
     * current status of server
     * @return
     */
    public abstract boolean status();

    public void checkServerAddress () {
        if (StringUtils.isBlank(serverAddress)) {
            throw new RuntimeException(" starter fail cause: server ip:port can not empty ");
        }
        String[] split = serverAddress.split(",");
        if (split == null || split.length == 0) {
            throw new RuntimeException(" starter fail cause: server format can not right ");
        }
    }


}

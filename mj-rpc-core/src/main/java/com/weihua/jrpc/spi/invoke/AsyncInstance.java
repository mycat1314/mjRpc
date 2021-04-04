package com.weihua.jrpc.spi.invoke;

import com.weihua.jrpc.client.invoke.AsyncInvoke;

import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author xie wei hua
 * @version 1.0
 * @date 2021/4/4 18:24
 * @description spi instance
 */
public class AsyncInstance {
    private Map<String, AsyncInvoke> instanceMap = new ConcurrentHashMap<>();
    /**
     * 24h cache time
     */
    private final Long cacheTime = (24L * 60L * 60L * 1000L);
    private long lastTime = 0;
    private static volatile AsyncInstance asyncInstance = null;
    public AsyncInvoke getInstance(String clazzName) {
        if (System.currentTimeMillis() > lastTime) {
            instanceMap.clear();
            lastTime = System.currentTimeMillis() + cacheTime;
        }
        if (instanceMap.containsKey(clazzName)) {
            return instanceMap.get(clazzName);
        }
        ServiceLoader<AsyncInvoke> load = ServiceLoader.load(AsyncInvoke.class);
        Iterator<AsyncInvoke> iterator = load.iterator();
        while (iterator.hasNext()) {
            AsyncInvoke next = iterator.next();
            instanceMap.put(next.getClass().getName(), next);
        }
        return instanceMap.get(clazzName);
    }
    private AsyncInstance(){
    }
    public static AsyncInstance getInstance(){
        if(asyncInstance == null){
            synchronized (AsyncInstance.class){
                if(asyncInstance == null){
                    asyncInstance = new AsyncInstance();
                }
            }
        }
        return asyncInstance;
    }
}

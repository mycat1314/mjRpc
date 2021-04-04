package com.weihua.jrpc.client.init;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author xie wei hua
 * @version 1.0
 * @date 2021/4/3 10:59
 * @description do somethings
 */
public interface ClientInit {
    void init();

    <T> T init(List<String> urls, int timeout, Class<T> interfaceClazz);
}

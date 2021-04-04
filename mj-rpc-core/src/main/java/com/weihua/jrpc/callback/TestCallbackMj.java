package com.weihua.jrpc.callback;

import com.weihua.jrpc.client.MjRpcCallback;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xie wei hua
 * @version 1.0
 * @date 2021/4/4 17:11
 * @description do somethings
 */
@Slf4j
public class TestCallbackMj implements MjRpcCallback {
    @Override
    public void success(Object result) {
        log.info("执行成功！");
        log.info(result.toString());
    }

    @Override
    public void failure(Throwable cause) {
        log.info("执行失败！");
        log.info(cause.getMessage());
    }
}

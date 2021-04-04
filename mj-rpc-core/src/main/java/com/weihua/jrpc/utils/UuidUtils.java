package com.weihua.jrpc.utils;

import java.util.UUID;

/**
 * @author xie wei hua
 * @version 1.0
 * @date 2021/4/4 17:36
 * @description do somethings
 */
public class UuidUtils {
    public static String getUuid() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    public static void main(String[] args) {
        String uuid = getUuid();
        System.out.println(uuid);
    }
}

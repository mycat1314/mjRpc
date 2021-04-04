package com.weihua.jrpc.invoke.provider;


import com.weihua.jrpc.invoke.consumer.HelloService;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class HelloServiceImpl implements HelloService {

	@Override
	public String hello(String name) {
		System.out.println("---------服务调用-------------");
		return "hello! " + name;
	}

	@Override
	public String hello2(String name) {
		System.out.println("----------dsad-----------------");
		return "hello2" + name;
	}


}

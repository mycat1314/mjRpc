package com.weihua.jrpc.config.consumer;

import com.weihua.jrpc.client.MjClient;
import com.weihua.jrpc.config.MjRpcConfigAbstract;
import org.apache.commons.lang3.StringUtils;

import java.util.List;


public class ConsumerConfigMj<T> extends MjRpcConfigAbstract {

    /**
     * 	url
     */
    protected volatile List<String> url;

    /**
     * 	connectTimeout
     */
    protected int connectTimeout = 3000;

    /**
     * 	proxyInstance
     */
    private volatile transient T proxyInstance;
    
    private MjClient client ;

    @SuppressWarnings("unchecked")
	public void initRpcClient() {
        this.client = new MjClient();
        this.proxyInstance = (T) this.client.init(url, connectTimeout, getProxyClass());
    }

    protected Class<?> getProxyClass() {
        if (proxyClass != null) {
            return proxyClass;
        }
        try {
            if (StringUtils.isNotBlank(interfaceClass)) {
                this.proxyClass = Class.forName(interfaceClass);
            } else {
                throw new Exception("consumer.interfaceId, null, interfaceId must be not null");
            }
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        return proxyClass;
    }

    public List<String> getUrl() {
		return url;
	}

	public void setUrl(List<String> url) {
		this.url = url;
	}

	public int getTimeout() {
        return connectTimeout;
    }
    
    public void setTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

	public T getProxyInstance() {
		return proxyInstance;
	}


	public MjClient getClient() {
		return client;
	}


	public void setClient(MjClient client) {
		this.client = client;
	}

}

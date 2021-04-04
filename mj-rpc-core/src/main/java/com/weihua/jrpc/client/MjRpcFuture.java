package com.weihua.jrpc.client;

import com.weihua.jrpc.client.codec.MjRpcRequest;
import com.weihua.jrpc.client.codec.MjRpcResponse;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xie wei hua
 * @version 1.0
 * @date 2021/4/1 13:27
 * @description do somethings
 */
@Slf4j
public class MjRpcFuture implements Future<Object> {

    private MjRpcRequest request;

    private MjRpcResponse response;

    private long startTime;

    private static final long TIME_THRESHOLD = 5000;

    private List<MjRpcCallback> pendingCallbacks = new CopyOnWriteArrayList<>();

    private Sync sync;

    private ReentrantLock lock = new ReentrantLock();

    TimeUnit unit = TimeUnit.SECONDS;
    BlockingQueue workQueue = new ArrayBlockingQueue(70000);
    private ThreadPoolExecutor executor = new ThreadPoolExecutor(16, 16, 60, unit, workQueue);


    public MjRpcFuture(MjRpcRequest request) {
        this.request = request;
        this.startTime = System.currentTimeMillis();
        this.sync = new Sync();
    }


    public void done(MjRpcResponse response) {
        this.response = response;
        boolean success = sync.release(1);
        if (success) {
            invokeCallbacks();
        }
        // cost time
        long costTime = System.currentTimeMillis() - startTime;
        if (TIME_THRESHOLD < costTime) {
            log.warn(" slow request, request id = " + this.request.getRequestId() + " cost time: " + costTime);
        }
    }

    private void invokeCallbacks() {
        lock.lock();
        try {
            for (final MjRpcCallback callback : pendingCallbacks) {
                runCallback(callback);
            }
        }finally {
            lock.unlock();
        }
    }

    private void runCallback(MjRpcCallback callback) {
        final MjRpcResponse response = this.response;
        executor.submit(new Runnable() {
            @Override
            public void run() {
                if (response.getThrowable() == null) {
                    callback.success(response.getResult());
                }else {
                    callback.failure(response.getThrowable());
                }
            }
        });
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDone() {
        return sync.isDone();
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        sync.acquire(0);
        if (this.response != null) {
            return this.response.getResult();
        }
        return null;
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        boolean success = sync.tryAcquireNanos(0,unit.toNanos(timeout));
        if (success) {
            if (this.response != null) {
                return this.response.getResult();
            }
            return null;
        }else {
            throw new RuntimeException("timeout exception requestId: "
            +this.request.getRequestId()
            +",className:"+this.request.getClassName()
            +",methodName:" + this.request.getMethodName());
        }
    }

    class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 1L;
        private final int done = 1;
        private final int pending = 0;


        @Override
        protected boolean tryAcquire(int acquires) {
            return getState() == done ? true : false;
        }

        @Override
        protected boolean tryRelease(int arg) {
            if (getState() == pending) {
                if (compareAndSetState(pending,done)) {
                    return true;
                }
            }
            return false;
        }

        public boolean isDone() {
            return getState() == done;
        }
    }

    /**
     * customer callback
     * @param callback
     * @return
     */
    public MjRpcFuture addCallback(MjRpcCallback callback) {
        lock.lock();
        try {
            if (isDone()) {
                runCallback(callback);
            }else {
                this.pendingCallbacks.add(callback);
            }
        }finally {
            lock.unlock();
        }
        return this;
    }

}

package com.weihua.jrpc.pool;

import java.util.concurrent.*;

/**
 * @创建人 谢伟华
 * @创建时间 2020/12/4
 * @描述
 **/
public class TaskExecutor {
    private static LinkedBlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>(Integer.MAX_VALUE);

    private static Thread t;

    public void set(Thread t) {
        TaskExecutor.t = t;
    }


    public static ThreadPoolExecutor threadPoolExecutor() {
        return new ThreadPoolExecutor(
                coreNum(),
                maxNum(),
                keepAliveTime(),
                TimeUnit.SECONDS,
                tasks,
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r,"work thread:"+r.hashCode());
                    }
                },
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        throw new RuntimeException("the thread is bad, please retry ");
                    }
                }
        );
    }

    private static Integer coreNum() {
        return cpuNum() - 1;
    }

    private static Integer maxNum() {
        return cpuNum();
    }

    private static Integer cpuNum() {
        int cpuNum = Runtime.getRuntime().availableProcessors();
        int threadNum = cpuNum * 2 + 1;
        return threadNum;
    }

    private static Long keepAliveTime() {
        return 60L;
    }






}

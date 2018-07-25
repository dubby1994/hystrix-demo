package cn.dubby.hystrix.demo.configure;

import com.netflix.hystrix.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ConfigureCommand1 extends HystrixCommand<String> {

    private String name;

    public ConfigureCommand1(Setter setter, String name) {
        super(setter);
        this.name = name;
    }

    @Override
    protected String run() throws Exception {
        System.out.println("run:\t[thread:" + Thread.currentThread().getName() + "]");
        try {
            Thread.sleep(1000 * 2);
        } catch (InterruptedException e) {
            System.out.println("Interrupted");
            e.printStackTrace();
        }
        return "result:" + name + ",\ttime:" + System.currentTimeMillis() + "\t[thread:" + Thread.currentThread().getName() + "]";
    }

    @Override
    protected String getFallback() {
        return "fallback:" + name + ",\ttime:" + System.currentTimeMillis() + "\t[thread:" + Thread.currentThread().getName() + "]";
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //信号量隔离
        Setter semaphoreSetter = Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("TestGroup"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("TestCommand1"))
                //参数
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                        .withExecutionIsolationSemaphoreMaxConcurrentRequests(1)
                        .withExecutionTimeoutInMilliseconds(100)
                        .withFallbackIsolationSemaphoreMaxConcurrentRequests(1)
                        .withFallbackEnabled(true)
                );

        for (int i = 0; i < 5; ++i) {
            System.out.println(new ConfigureCommand1(semaphoreSetter, "semaphoreSetter").execute());
        }

        //线程隔离
//        Setter threadSetter = Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("TestGroup"))
//                .andCommandKey(HystrixCommandKey.Factory.asKey("TestCommand2"))
//                //参数
//                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
//                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
//                        .withExecutionTimeoutInMilliseconds(1000)
//                        .withExecutionTimeoutEnabled(true)
//                        .withExecutionIsolationThreadInterruptOnTimeout(true)
//                        .withExecutionIsolationThreadInterruptOnFutureCancel(true)
//                )
//                //线程池
//                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("TestThreadPool"))
//                //线程池参数
//                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(2));
//
//
//        //System.out.println(new ConfigureCommand1(threadSetter, "threadSetter").execute());
//        Future<String> future = new ConfigureCommand1(threadSetter, "threadSetter").queue();
//        Thread.sleep(10);
//        System.out.println(future.cancel(true));

        System.out.println("main:\t[" + Thread.currentThread().getName() + "]");
        new CountDownLatch(1).await();
    }
}

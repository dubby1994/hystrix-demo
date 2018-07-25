package cn.dubby.hystrix.demo.configure;

import com.netflix.hystrix.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ConfigureThreadPool1 extends HystrixCommand<String> {

    protected ConfigureThreadPool1(Setter setter) {
        super(setter);
    }

    @Override
    protected String run() throws Exception {
        System.out.println(System.currentTimeMillis() + "\t" + Thread.currentThread().getName() + "\trun");
        try {
            Thread.sleep(1000 * 2);
        } catch (InterruptedException e) {
            System.out.println(System.currentTimeMillis() + "\t" + Thread.currentThread().getName() + "\tInterrupted");
            throw e;
        }
        return System.currentTimeMillis() + "\t" + Thread.currentThread().getName() + "\tsuccess";
    }

    @Override
    protected String getFallback() {
        return System.currentTimeMillis() + "\t" + Thread.currentThread().getName() + "\tfallback";
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //线程隔离
        Setter threadSetter = Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("TestGroup"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("TestCommand"))
                //参数
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
                        .withExecutionTimeoutInMilliseconds(1000 * 10)
                )
                //线程池
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("TestThreadPool"))
                //线程池参数
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                        //10
                        .withCoreSize(1)
                        //10
                        .withMaximumSize(1)
                        //默认是-1，那么使用的是SynchronousQueue，如果>0，使用的是LinkedBlockingQueue
                        .withMaxQueueSize(2)
                        //拒绝的阈值，即使没有达到queue的上限，这个配置的意义是queue的大小不能动态改变，所以用这个来控制
                        .withQueueSizeRejectionThreshold(2)
                );

        List<Future<String>> futures = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            try {
                futures.add(new ConfigureThreadPool1(threadSetter).queue());
            } catch (Exception e) {
                System.out.println("reject\t" + (i + 1));
            }
        }

        futures.forEach(f -> {
            try {
                System.out.println(f.get());
            } catch (Exception e) {
                System.out.println("exception:\t" + e.toString());
            }
        });

        System.out.println("main\t" + Thread.currentThread().getName());
        new CountDownLatch(1).await();
    }

}

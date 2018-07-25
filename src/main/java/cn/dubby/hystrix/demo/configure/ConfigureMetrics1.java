package cn.dubby.hystrix.demo.configure;

import com.netflix.hystrix.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class ConfigureMetrics1 extends HystrixCommand<String> {

    private String name;

    public ConfigureMetrics1(Setter setter, String name) {
        super(setter);
        this.name = name;
    }

    @Override
    protected String run() throws Exception {
        System.out.println(System.currentTimeMillis() + "\t" + Thread.currentThread().getName() + "\trun");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println(System.currentTimeMillis() + "\t" + Thread.currentThread().getName() + "\tInterrupted");
            throw e;
        }
        return System.currentTimeMillis() + "\t" + Thread.currentThread().getName() + "\t" + name;
    }

    @Override
    protected String getFallback() {
        return System.currentTimeMillis() + "\t" + Thread.currentThread().getName() + "\tfallback";
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //线程隔离
        Setter threadSetter = Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("TestGroup"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("TestCommand2"))
                //参数
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
                        .withExecutionTimeoutInMilliseconds(1000)
                        .withExecutionTimeoutEnabled(true)
                        .withExecutionIsolationThreadInterruptOnTimeout(true)
                        .withExecutionIsolationThreadInterruptOnFutureCancel(true)
                        //熔断器配置
                        .withCircuitBreakerEnabled(true)
                        //请求的总量最低要求，如果达不到就不会触发熔断
                        .withCircuitBreakerRequestVolumeThreshold(0)
                        //熔断恢复的等待时间，在这个时间段内，不会尝试恢复
                        .withCircuitBreakerSleepWindowInMilliseconds(1000)
                        //错误百分比
                        .withCircuitBreakerErrorThresholdPercentage(0)
                )
                //线程池
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("TestThreadPool"))
                //线程池参数
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(2));

        for (int i = 0; i < 10; ++i) {
            System.out.println(new ConfigureMetrics1(threadSetter, "threadSetter").execute() + "\t" + (i + 1));
        }

        System.out.println("main\t" + Thread.currentThread().getName());
        new CountDownLatch(1).await();
    }
}

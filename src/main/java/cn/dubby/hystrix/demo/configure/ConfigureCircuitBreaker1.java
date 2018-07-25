package cn.dubby.hystrix.demo.configure;

import com.netflix.hystrix.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class ConfigureCircuitBreaker1 extends HystrixCommand<String> {

    private String name;

    public ConfigureCircuitBreaker1(Setter setter, String name) {
        super(setter);
        this.name = name;
    }

    @Override
    protected String run() throws Exception {
        System.out.println(System.currentTimeMillis() + "\t" + Thread.currentThread().getName() + "\trun");
        throw new EmptyException();
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
                        //1、熔断器配置
                        .withCircuitBreakerEnabled(true)
                        //请求的总量最低要求，如果达不到就不会触发熔断
                        .withCircuitBreakerRequestVolumeThreshold(0)
                        //熔断恢复的等待时间，在这个时间段内，不会尝试恢复
                        .withCircuitBreakerSleepWindowInMilliseconds(1000 * 10)
                        //错误百分比
                        .withCircuitBreakerErrorThresholdPercentage(0)
                        //2、统计配置，窗口时长默认10000
                        .withMetricsRollingStatisticalWindowInMilliseconds(1000 * 10)
                        //窗口个数
                        .withMetricsRollingStatisticalWindowBuckets(10)
                        //比例统计时长
                        .withMetricsRollingPercentileWindowInMilliseconds(1000 * 10)
                        //比例统计的桶数
                        .withMetricsRollingPercentileWindowBuckets(10)
                        //最多只统计100个，超过就只统计最新的100个
                        .withMetricsRollingPercentileBucketSize(100)
                        //统计成功和失败的比例的时间间隔，如果太短，那可能会导致cpu被占用很多
                        .withMetricsHealthSnapshotIntervalInMilliseconds(500)
                )
                //线程池
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("TestThreadPool"))
                //线程池参数
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(2));

        for (int i = 0; i < 20; ++i) {
            System.out.println(new ConfigureCircuitBreaker1(threadSetter, "threadSetter").execute() + "\t" + (i + 1));
            Thread.sleep(1000);
        }

        System.out.println("main\t" + Thread.currentThread().getName());
        new CountDownLatch(1).await();
    }

    static class EmptyException extends RuntimeException {
        public EmptyException() {
        }

        @Override
        public synchronized Throwable fillInStackTrace() {
            return this;
        }
    }
}

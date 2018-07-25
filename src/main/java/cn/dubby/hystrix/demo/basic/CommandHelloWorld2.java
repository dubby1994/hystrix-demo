package cn.dubby.hystrix.demo.basic;

import com.netflix.hystrix.*;

public class CommandHelloWorld2 extends HystrixCommand<String> {

    private final String name;

    public CommandHelloWorld2(String name) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("HelloWorld"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("HelloWorldPool"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withCircuitBreakerEnabled(true)
                )
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                        .withCoreSize(2)
                ));
        this.name = name;
    }


    @Override
    protected String run() {
        return "Hello " + name + "!\t(ThreadName is:)" + Thread.currentThread().getName();
    }
}
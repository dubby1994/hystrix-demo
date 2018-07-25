package cn.dubby.hystrix.demo.basic;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public class CommandHelloWorld1 extends HystrixCommand<String> {

    private final String name;

    public CommandHelloWorld1(String name) {
        super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
        this.name = name;
    }

    @Override
    protected String run() {
        return "Hello " + name + "!\t(ThreadName is:)" + Thread.currentThread().getName();
    }

    @Override
    protected String getFallback() {
        return "Fallback";
    }
}
package cn.dubby.hystrix.demo.basic;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

public class CommandUsingRequestCache1Test {

    public static void main(String[] args) {
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        System.out.println(new CommandUsingRequestCache1(1).execute());
        System.out.println(new CommandUsingRequestCache1(2).execute());
        System.out.println(new CommandUsingRequestCache1(1).execute());
        System.out.println(new CommandUsingRequestCache1(1).execute());
    }

}

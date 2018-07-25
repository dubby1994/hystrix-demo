package cn.dubby.hystrix.demo.basic;

import rx.Observable;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class CommandHelloWorld1Test {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String s = new CommandHelloWorld1("Dubby").execute();
        System.out.println(s);
        Future<String> fs = new CommandHelloWorld1("Dubby").queue();
        System.out.println(fs.get());
        Observable<String> hotValue = new CommandHelloWorld1("Dubby").observe();
        System.out.println(hotValue.toBlocking().toFuture().get());
        Observable<String> oldValue = new CommandHelloWorld1("Dubby").toObservable();
//        System.out.println(oldValue.toBlocking().toFuture().get());
        oldValue.subscribe(System.out::println);

        new CountDownLatch(1).await();
    }

}

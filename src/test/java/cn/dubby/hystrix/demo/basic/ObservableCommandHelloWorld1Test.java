package cn.dubby.hystrix.demo.basic;

import rx.Observable;
import rx.Observer;

import java.util.concurrent.CountDownLatch;

public class ObservableCommandHelloWorld1Test {

    public static void main(String[] args) throws InterruptedException {
        Observable<String> observable = new ObservableCommandHelloWorld1("Hot Test").observe();
        observable.toBlocking().getIterator().forEachRemaining(System.out::println);

        Observable<String> ho = new ObservableCommandHelloWorld1("Hot World").observe();
        ho.subscribe(System.out::println);

        Observable<String> co = new ObservableCommandHelloWorld1("Cold World").toObservable();
        co.subscribe(System.out::println);

        new ObservableCommandHelloWorld1("Dubby").toObservable().subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {
                System.out.println("onCompleted");
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("onError:" + throwable);
            }

            @Override
            public void onNext(String s) {
                System.out.println("onNext:" + s);
            }
        });

        new CountDownLatch(1).await();
    }

}

package cn.dubby.hystrix.demo.basic;

public class CommandHelloFailure1Test {

    public static void main(String[] args) {
        System.out.println(new CommandHelloFailure1("Dubby").execute());
    }

}

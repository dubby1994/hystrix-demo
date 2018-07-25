package cn.dubby.hystrix.demo.basic;

public class CommandHelloWorld2Test {

    public static void main(String[] args)  {
        String s = new CommandHelloWorld2("Dubby").execute();
        System.out.println(s);
    }

}

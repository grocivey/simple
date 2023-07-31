package com.simple.designpattern.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JavaDynamicProxy2 {
    interface Eat{
        String slow();
    }

    static class SlowEat implements  Eat{


        @Override
        public String slow() {
            System.out.println("慢点吃");
            return "re 慢点吃";
        }
    }

    static class EatProxy implements InvocationHandler{

        private Eat eat;

        public EatProxy(Eat eat) {
            this.eat = eat;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("我准备慢点吃");
            Object invoke = method.invoke(eat, args);
            System.out.println("我慢点吃完了");
            return invoke;
        }
    }

    public static void main(String[] args) {
        Eat realEat = new SlowEat();
        EatProxy eatProxy = new EatProxy(realEat);
        Eat proxyEat = (Eat) Proxy.newProxyInstance(Eat.class.getClassLoader(), new Class[]{Eat.class}, eatProxy);
        System.out.println(proxyEat.slow());
    }

}

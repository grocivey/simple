package com.simple.designpattern.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * java动态代理2
 */
public class JavaDynamicProxy2 {
    interface Eat{
        String slow(String food);
        String quick(String food);
    }

    static class SlowEat implements  Eat{


        @Override
        public String slow(String food) {
            System.out.println("慢点吃---"+food);
            return "re---慢点吃---";
        }

        @Override
        public String quick(String food) {
            System.out.println("快点吃==="+food);
            return "re===快点吃===";
        }
    }

    static class EatProxy implements InvocationHandler{

        private Eat eat;

        public EatProxy(Eat eat) {
            this.eat = eat;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("我准备"+method.getName()+"吃了");
            Object invoke = method.invoke(eat, args);
            System.out.println("我"+method.getName()+"吃完了");
            return invoke;
        }
    }

    public static void main(String[] args) {
        Eat realEat = new SlowEat();
        EatProxy eatProxy = new EatProxy(realEat);
        Eat proxyEat = (Eat) Proxy.newProxyInstance(Eat.class.getClassLoader(), new Class[]{Eat.class}, eatProxy);
        System.out.println(proxyEat.slow("鱼"));
        System.out.println(proxyEat.quick("火锅"));
    }

}
